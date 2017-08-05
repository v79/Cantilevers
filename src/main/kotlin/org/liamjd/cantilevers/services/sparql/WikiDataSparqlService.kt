package org.liamjd.cantilevers.services.sparql

import com.beust.klaxon.*
import java.net.URL
import java.util.*


class WikiDataSparqlService : SparqlService {

	override fun query(queryMap: Map<String, String>): String {

		val nameToFind = queryMap.get("name")
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
""","ISO-8859-1")



		println(url)
		println("https://query.wikidata.org/sparql?query=" + url + "?format=json")

		val result = URL("https://query.wikidata.org/sparql?format=json&query=" + url).readText()


		val parser: Parser = Parser()
		val json: JsonObject = parser.parse(StringBuilder(result)) as JsonObject

		val name = json.obj("results")?.array<JsonObject>("bindings")?.obj("bridgeLabel")?.string("value")?.singleOrNull()
		val length = json.obj("results")?.array<JsonObject>("bindings")?.obj("length")?.string("value")?.singleOrNull()
		val coords = json.obj("results")?.array<JsonObject>("bindings")?.obj("coord")?.string("value")?.singleOrNull()
//
		println(json.toJsonString(true))

		println("Name: " + name)
		println("Length: " + length)
		println("Coords: " + coords)

		val bridgeInfo = name + ", " + length + " metres, at  " + coords

//		println(result)


	return bridgeInfo
	}
}