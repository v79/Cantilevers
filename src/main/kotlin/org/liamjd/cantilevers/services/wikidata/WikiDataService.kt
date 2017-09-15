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
import java.math.BigDecimal
import java.time.Month
import java.time.format.TextStyle
import java.util.*

typealias QCode = String
typealias I18nLabel = MutableMap<String,String>
typealias Coordinates = Pair<Double,Double>


interface WikiDataResult {
	override fun toString(): String
}

data class WikiDataStrings(val value: MutableSet<I18nLabel>) : WikiDataResult {
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
data class WikiDataCoords(val value: Coordinates) : WikiDataResult {
	override fun toString(): String {
		return "(${value.first},${value.second})"
	}
}

// TODO: this is really ugly
data class WikiDataTime(val wikiTimeValue: JacksonValueTime, val year: Long?, val month: Month?, val day: Int? ) : WikiDataResult {
	override fun toString(): String {
		// TODO: make this much much cleverer and formatted
		val sb = StringBuilder()
		if(day != null) {
			sb.append(day).append(" ")
		}
		if(month != null) {
			sb.append(month.getDisplayName(TextStyle.FULL, Locale.UK)).append(" ")
		}
		if(year != null) {
			sb.append(year)
		}
		sb.append(" [$wikiTimeValue]")

		return sb.toString()
	}
}

data class WikiDataQuantity(val value: BigDecimal, val units: String) : WikiDataResult {

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
//						extractQuantityValue(language, value, claimMap, propertyId)
					}
					is JacksonValueTime -> {
						extractTimeValue(value,claimMap,propertyId)
					}
					is JacksonValueGlobeCoordinates -> {
						// TODO: does it really make sense for coordinates to be strings, or have languages
						extractCoordinateValue(value,claimMap,propertyId)
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
	 * The JacksonValueTime class from WikiData doesn't neatly map on to a Java Date or LocalTime, as it can represent pretty vague concepts like 1800 +- 2 years", or "186million years ago"
	 * I think we will store the JacksonValueTime as-is, and separately store day/month/year where possible as it better matches our dataset.
	 * Haven't decided how to store this in the DB!
	 */
	// TODO: this is all really ugly
	private fun extractTimeValue(value: JacksonValueTime, claimMap: MutableMap<QCode, WikiDataResult>, propertyId: String) {
		val zeroByte: Byte = 0
		var year: Long?
		var month: Month?
		var day: Int?
		// undefined days and months etc are stored as 0 in the JacksonValueTime.InnerTime object
		if(value.value.year.equals(0)) {
			year = null
		} else {
			year = value.value.year
		}
		if( value.value.month.equals(zeroByte)) {
			month = null
		} else {
			month = Month.of(value.value.month.toInt())
		}
		if(value.value.day.equals(zeroByte)) {
			day = null
		} else {
			day = value.value.day.toInt()
		}
		val date = WikiDataTime(value,year, month, day)
		claimMap.put(propertyId,date)
	}

	private fun extractCoordinateValue(value: JacksonValueGlobeCoordinates, claimMap: MutableMap<QCode, WikiDataResult>, propertyId: String) {
		if(claimMap.containsKey(propertyId)) {
			// can't remember what this is for; I don't think it applies to languageless properties
		} else {
			val coords: Coordinates = Coordinates(value.latitude,value.longitude)
			claimMap.put(propertyId,WikiDataCoords(coords))
		}
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

	// TODO: these are a bugger to extract because the units (miles, pence etc) are WikiData entities in themselves
	private fun extractQuantityValue(language: String, value: JacksonValueQuantity, claimMap: MutableMap<QCode, WikiDataResult>, propertyId: String) {
		val amount = value.value.amount
//		val unitKey = value.value.unit.(regex = Regex("(Q[0-9]*)\$"))
		val unitKey = value.value.unit.substring(value.value.unit.lastIndexOf('Q'))
		var jacksonValueItemId: JacksonValueItemId = JacksonValueItemId()
//		jacksonValueItemId.id = unitKey
//		val units = getValueIdLabel(unitKey,language)
//		claimMap.put(propertyId,WikiDataQuantity(amount, units))
	}

	fun getValueIdLabel(valueItemId: JacksonValueItemId, lang: String): String {
		val cacheKey = "${valueItemId.id}:$lang"
		if(valueCache.containsKey(cacheKey)) {
			valueCache[cacheKey]?.let {
				logger.debug("vCache hit $it")
				return it}
		}
		logger.info("fetching item $cacheKey from WikiData")
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
			logger.info("pCache hit $propertyKey:$lang")
			return text
		}

		logger.info("fetching property $cacheKey from WikiData")
		val propertyDoc = fetcher.getEntityDocument(propertyKey) as JacksonPropertyDocument
		if(propertyDoc.labels[lang] != null) {
			val text: String? = propertyDoc.labels[lang]?.text
			propertyCacheDao.add("$propertyKey:$lang", text!!)
			logger.info("Caching property $propertyKey:$lang to database")
//			propNameCache.put("$propertyKey:$lang",text!!)
			return text
		} else if (propertyDoc.labels[defaultLanguage] != null) {
			val text: String? = propertyDoc.labels[defaultLanguage]?.text
			propertyCacheDao.add("$propertyKey:$lang", text!!)
			logger.info("Caching property $propertyKey:$lang to database")
//			propNameCache.put("$propertyKey:$lang",text!!)
			return text
		} else {
			return propertyDoc.datatype.toString()
		}
	}
}