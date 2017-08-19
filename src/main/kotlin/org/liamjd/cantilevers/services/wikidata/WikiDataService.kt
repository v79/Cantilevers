package org.liamjd.cantilevers.services.wikidata

import com.github.salomonbrys.kodein.instance
import org.liamjd.cantilevers.db.caches.PropertyCacheDao
import org.liamjd.cantilevers.services.AbstractService
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument
import org.wikidata.wdtk.datamodel.interfaces.Statement
import org.wikidata.wdtk.datamodel.json.jackson.JacksonPropertyDocument
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.*
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher
import java.time.LocalDateTime

typealias QCode = String
typealias I18nLabel = MutableMap<String,String>
typealias Coordinates = Pair<Float,Float>

interface WikiDataResult {
	override fun toString(): String
}

class WikiDataStrings(val value: MutableSet<I18nLabel>) : WikiDataResult {
	override fun toString(): String {
		val sb = StringBuilder()
		value.forEach {
			it.forEach {
				sb.append(it.value).appendln()
			}
		}
		return sb.toString()
	}
}
class WikiDataCoords(val value: Coordinates) : WikiDataResult {
	override fun toString(): String {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}
class WikiDataTime(val value: LocalDateTime) : WikiDataResult {
	override fun toString(): String {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}


class WikiDataService : AbstractService() {

	val propertyCacheDao = injectCache.instance<PropertyCacheDao>()
	// TODO: put these in a proper cache, or on the DB
	val propNameCache: MutableMap<String,String> = mutableMapOf()
	val valueCache: MutableMap<String,String> = mutableMapOf()
	val statementCache: MutableMap<String,EntityDocument> = mutableMapOf()

	companion object {
		val fetcher: WikibaseDataFetcher = WikibaseDataFetcher.getWikidataDataFetcher()
		val defaultWiki = "enwiki"
		val defaultLanguage = "en"
	}

	fun getStatementsForDocument(wikiDataID: String, wiki: String = defaultWiki, language: String = defaultLanguage): MutableMap<QCode, WikiDataResult> {
		val entityDocument: EntityDocument
		val statementCacheKey = "$wikiDataID:$language"
		if(statementCache.contains(statementCacheKey)) {
			entityDocument = statementCache[statementCacheKey]!!
			logger.debug("sCache hit ${entityDocument.entityId.id}")
		} else {
			logger.info("Calling WikiData to get document $statementCacheKey")
			entityDocument = fetcher.getEntityDocument(wikiDataID)
			statementCache.put(statementCacheKey,entityDocument)
		}

		val claimMap: MutableMap<QCode,WikiDataResult> = mutableMapOf()
		if(entityDocument is ItemDocument) {
			val item: ItemDocument = entityDocument
			val statements = item.allStatements
			while (statements.hasNext()) {
				val s: Statement = statements.next()
				val value: JacksonValue = s.claim.value as JacksonValue
				val propertyId = s.claim.mainSnak.propertyId.id
				when(value) {
					is JacksonValueString -> {
						extractStringValue(language, value, claimMap, propertyId)
					}
					is JacksonValueItemId -> {
						extractIdValue(language, value, claimMap, propertyId)
					}
					is JacksonValueQuantity -> {

					}
					is JacksonValueTime -> {

					}
					is JacksonValueGlobeCoordinates -> {
						// TODO: does it really make sense for coordinates to be strings, or have languages
					}
					// etc
				}
			}
		} else {
			// wrong type of document; failure
		}
		return claimMap
	}

	/**
	 * Extract a string and store it on the claim map
	 */
	private fun extractStringValue(language: String, value: JacksonValueString, claimMap: MutableMap<QCode, WikiDataResult>, propertyId: String) {
		val langMap: I18nLabel = mutableMapOf()
		langMap.put(language, value.string)
		if (claimMap.containsKey(propertyId)) {
			val set: WikiDataStrings = claimMap[propertyId] as WikiDataStrings
			set.value.add(langMap)
		} else {
			claimMap.put(propertyId, WikiDataStrings(mutableSetOf<I18nLabel>(langMap)))
		}
	}

	/**
	 * Extract a wikidata item and store its label on the claim map
	 */
	private fun extractIdValue(language: String, value: JacksonValue, claimMap: MutableMap<QCode, WikiDataResult>, propertyId: String) {
		val langMap: I18nLabel = mutableMapOf()
		langMap.put(language, getValueIdLabel(value as JacksonValueItemId, language))
		if (claimMap.containsKey(propertyId)) {
			val set: WikiDataStrings = claimMap[propertyId] as WikiDataStrings
			set.value.add(langMap)
		} else {
			claimMap.put(propertyId, WikiDataStrings(mutableSetOf<I18nLabel>(langMap)))
		}
	}

	fun getValueIdLabel(valueItemId: JacksonValueItemId, lang: String): String {
		val cacheKey = "${valueItemId.id}:$lang"
		if(valueCache.containsKey(cacheKey)) {
			valueCache[cacheKey]?.let {
				logger.debug("vCache hit $it")
				return it}
		}
		logger.info("fetching $cacheKey from WikiData")
		val itemDocument = fetcher.getEntityDocument(valueItemId.id) as ItemDocument
		if(itemDocument.labels.get(lang) != null) {
			// TODO: cache all this
			val text = itemDocument.labels.get(lang)?.text
			valueCache.put("${valueItemId.id}:$lang",text!!)
			return text
		} else if (itemDocument.labels.get(defaultLanguage) != null) {
			val text = itemDocument.labels.get(defaultLanguage)?.text
			valueCache.put("${valueItemId.id}:$lang",text!!)
			return text
		} else {
			val text = itemDocument.labels.get(itemDocument.labels.keys.first())?.text
			if(text != null) {
				return text
			} else {
				// just return the WikiData Q number
				return valueItemId.id
			}
		}
	}

	fun getPropertyLabel(propertyKey: String, lang: String): String {
		val cacheKey = "$propertyKey:$lang"

		val text = propertyCacheDao.getText(cacheKey)

//		val text = propNameCache.get(cacheKey)
		if(text != null) {
			logger.debug("pCache hit $propertyKey:$lang")
			return text
		}

		logger.info("fetching $cacheKey from WikiData")
		val propertyDoc = fetcher.getEntityDocument(propertyKey) as JacksonPropertyDocument
		if(propertyDoc.labels[lang] != null) {
			val text: String? = propertyDoc.labels[lang]?.text
			propertyCacheDao.add("$propertyKey:$lang", text!!)
//			propNameCache.put("$propertyKey:$lang",text!!)
			return text!!
		} else if (propertyDoc.labels[defaultLanguage] != null) {
			val text: String? = propertyDoc.labels[defaultLanguage]?.text
			propertyCacheDao.add("$propertyKey:$lang", text!!)
//			propNameCache.put("$propertyKey:$lang",text!!)
			return text!!
		} else {
			return propertyDoc.datatype.toString()
		}
	}
}