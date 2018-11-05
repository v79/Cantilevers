package org.liamjd.cantilevers.services.sparql

import com.beust.klaxon.*
import org.liamjd.cantilevers.viewmodel.Bridge
import org.slf4j.LoggerFactory
import java.net.URL


class WikiDataSparqlService : SparqlService {

	val logger = LoggerFactory.getLogger(WikiDataSparqlService::class.java)

	// how might a make a DSL of this???
	fun buildQuery(itemKey: String, propertyNames: Array<String>, countryKey: String, name: String, limit: Int): String {
		val sBuilder = StringBuilder()

		sBuilder.append("SELECT DISTINCT ")
		sBuilder.append("?").append(itemKey).append(" ")
		propertyNames.forEach {
			sBuilder.append("?").append(it).append(" ")
		}
		sBuilder.append("WHERE {").appendln()
		sBuilder.append("?").append(itemKey)
		// instance of/subclass of bridge
		sBuilder.append(" wdt:P31/wdt:P279* ").append("wd:Q12280.").appendln()
		// get country - doesn't really work with my propertyNames array. A map perhaps?
		sBuilder.append("?").append(itemKey)
		sBuilder.append(" wdt:P17 ?country.").appendln()
		// cooordinates
		sBuilder.append("?").append(itemKey)
		sBuilder.append(" wdt:P625 ?coord.").appendln()
		// bridge label
		sBuilder.append("?").append(itemKey)
		sBuilder.append(" rdfs:label ?bridgeName .").appendln()
		// in country
		sBuilder.append("?country wdt:P17 wd:").append(countryKey).appendln(".")
		// label service
		sBuilder.append("SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],en\". }").appendln()
		// option values
		sBuilder.append("OPTIONAL {").appendln()
		sBuilder.append("?").append(itemKey)
		sBuilder.append(" wdt:P2043 ?length .").appendln()
		sBuilder.appendln("}")

		// name filter
		sBuilder.append("FILTER(STRSTARTS (?bridgeName, \"").append(name).append("\")).").appendln()

		sBuilder.appendln("}")

		// limit
		sBuilder.append("LIMIT ").appendln(limit)

		return sBuilder.toString()
	}

	override fun queryList(queryMap: Map<String, String>): List<Bridge> {
		if (queryMap.get("name") == null) {
			throw NullPointerException("Expression 'queryMap.get(\"name\")' must not be null")
		} else {
			val nameToFind =
					queryMap.getOrDefault("name", "") // this can never be true, but hey ho, I want a String not a String?

			logger.info("Searching wikidata for $nameToFind")

			val propArray = arrayOf("bridgeLabel", "bridgeDescription", "countryLabel", "length", "coord")
			val url = java.net.URLEncoder.encode(buildQuery("bridge", propArray, "Q145", nameToFind, 10), "ISO-8859-1")

			logger.info("https://query.wikidata.org/sparql?query=" + url + "?format=json")

			val result = URL("https://query.wikidata.org/sparql?format=json&query=" + url).readText()

			val parser: Parser = Parser()
			val json: JsonObject = parser.parse(StringBuilder(result)) as JsonObject
			val bridgeList: MutableList<Bridge> = mutableListOf()
			val jsonArray = json.obj("results")?.array<JsonObject>("bindings")


			jsonArray?.forEach {
				val name = it.obj("bridgeLabel")?.string("value")
				val description = it.obj("bridgeDescription")?.string("value")
				val length = it.obj("length")?.string("value")?.toFloat()
				val coords = it.obj("coord")?.string("value")
//			val types = it.obj("typeLabel")?.get("value") as String
				val wikidataURI = it.obj("bridge")?.string("value")
				val wikidataID: String? = wikidataURI?.split("/")?.last()
				val wikidatajsonurl = URL("https://www.wikidata.org/wiki/Special:EntityData/" + wikidataID + ".json")
				val wikidatajson = wikidatajsonurl.readText()
				val fullJson = parser.parse(StringBuilder(wikidatajson)) as JsonObject
				val bridge: Bridge = Bridge(wikiDataID = wikidataID!!, name = name!!, description = description, length = length, coords = coords, wikiDataJSON = fullJson.toJsonString(true))
				logger.info("Bridge found: ${bridge}")
				bridgeList.add(bridge)
			}
			return bridgeList
		}

	}

	companion object {
		private val exampleName = "Forth"
		val sparqlQueryString = java.net.URLEncoder.encode("""SELECT DISTINCT ?bridge ?bridgeLabel ?bridgeDescription ?countryLabel ?length ?coord WHERE {
?bridge wdt:P31/wdt:P279* wd:Q12280.
?bridge wdt:P17 ?country .
?bridge wdt:P625 ?coord.
# ?bridge wdt:P31 ?type.
?bridge rdfs:label ?bridgeName .
?country wdt:P17 wd:Q145 .

SERVICE wikibase:label { bd:serviceParam wikibase:language "[AUTO_LANGUAGE],en". }

OPTIONAL {
?bridge wdt:P2043 ?length .
}

FILTER(STRSTARTS (?bridgeName, """" + exampleName + """")).

}
LIMIT 100
""", "ISO-8859-1")
	}
}
