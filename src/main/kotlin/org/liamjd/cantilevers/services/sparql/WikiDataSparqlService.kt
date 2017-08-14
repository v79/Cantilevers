package org.liamjd.cantilevers.services.sparql

import com.beust.klaxon.*
import org.liamjd.cantilevers.viewmodel.Bridge
import org.slf4j.LoggerFactory
import java.net.URL


class WikiDataSparqlService : SparqlService {

	val logger = LoggerFactory.getLogger(WikiDataSparqlService::class.java)

	override fun queryList(queryMap: Map<String, String>): List<Bridge> {
		val nameToFind = queryMap.get("name")
		logger.info("Searching wikidata for $nameToFind")
		val url = java.net.URLEncoder.encode("""SELECT DISTINCT ?bridge ?bridgeLabel ?countryLabel ?length ?coord WHERE {
  ?bridge wdt:P31/wdt:P279* wd:Q12280.
  ?bridge wdt:P17 ?country .
  ?bridge wdt:P625 ?coord.

  ?bridge rdfs:label ?bridgeName .

  ?country wdt:P17 wd:Q145 .

  SERVICE wikibase:label { bd:serviceParam wikibase:language "[AUTO_LANGUAGE],en". }

  OPTIONAL {
    ?bridge wdt:P2043 ?length .
    }

  FILTER(STRSTARTS (?bridgeName, """" + nameToFind!! + """")).

}
LIMIT 100
""", "ISO-8859-1")


		logger.info("https://query.wikidata.org/sparql?query=" + url + "?format=json")

		val result = URL("https://query.wikidata.org/sparql?format=json&query=" + url).readText()


		val parser: Parser = Parser()
		val json: JsonObject = parser.parse(StringBuilder(result)) as JsonObject


		val bridgeList: MutableList<Bridge> = mutableListOf()
		val jsonArray = json.obj("results")?.array<JsonObject>("bindings")


		jsonArray?.forEach {
			val name = it.obj("bridgeLabel")?.string("value")
			val length = it.obj("length")?.string("value")
			val coords = it.obj("coord")?.string("value")
			val wikidataURI = it.obj("bridge")?.string("value")
			val wikidataID: String? = wikidataURI?.split("/")?.last()
			val wikidatajsonurl = URL("https://www.wikidata.org/wiki/Special:EntityData/" + wikidataID + ".json")
			val wikidatajson = wikidatajsonurl.readText()
			val fullJson = parser.parse(StringBuilder(wikidatajson)) as JsonObject
			val bridge: Bridge = Bridge(wikiDataID = wikidataID!!, name = name!!, length = length?.toInt(), coords = coords, wikiDataJSON = fullJson.toJsonString(true))
			logger.info("Bridge found: ${bridge}")
			bridgeList.add(bridge)
		}




//
//		println(json.toJsonString(true))
//
//		println("wikidataID: " + wikidataID)
//		println("Name: " + name)
//		println("Length: " + length)
//		println("Coords: " + coords)

//		val bridgeInfo = name + ", " + length + " metres, at  " + coords
//
//		val wikidatajsonurl = URL("https://www.wikidata.org/wiki/Special:EntityData/" + wikidataID + ".json")
//		val wikidatajson = wikidatajsonurl.readText()
//		val fullJson = parser.parse(StringBuilder(wikidatajson)) as JsonObject
//		println(fullJson.toJsonString(true))


//		val bridge = Bridge(wikidataID!!,name!!,length?.toInt(),fullJson.toJsonString(false) )


//		println(result)


		return bridgeList
	}
}
