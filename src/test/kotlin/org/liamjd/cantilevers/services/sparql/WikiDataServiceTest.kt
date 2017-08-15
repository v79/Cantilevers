package org.liamjd.cantilevers.services.sparql

import org.junit.Assert
import org.junit.Test
import org.liamjd.cantilevers.viewmodel.Bridge

class WikiDataServiceTest {

	val service = WikiDataSparqlService()

	@Test
	fun `should make call to wikidata and get multiple results`() {
		// setup
		val paramMap = mutableMapOf<String,String>()
		paramMap.put("name","Forth")

		// execute
		val result: List<Bridge> = service.queryList(paramMap)

		// verify
//		Assert.assertEquals("Q933000",thingy)
		Assert.assertNotNull(result)
		Assert.assertTrue(result.size > 1)
	}

	@Test
	fun `builder should match sparql string`() {
		// setup
		val propArray = arrayOf("bridgeLabel","countryLabel","length","coord")
		val expected = """SELECT DISTINCT ?bridge ?bridgeLabel ?countryLabel ?length ?coord WHERE {
?bridge wdt:P31/wdt:P279* wd:Q12280.
?bridge wdt:P17 ?country.
?bridge wdt:P625 ?coord.
?bridge rdfs:label ?bridgeName .
?country wdt:P17 wd:Q145.
SERVICE wikibase:label { bd:serviceParam wikibase:language "[AUTO_LANGUAGE],en". }
OPTIONAL {
?bridge wdt:P2043 ?length .
}
FILTER(STRSTARTS (?bridgeName, "Forth")).
}
LIMIT 100
""".replace("\\r\\n?", "\n").trimIndent()
		// execute
		val result: String = service.buildQuery("bridge",propArray,"Q145","Forth",100).replace("\\r\\n?", "\n")

		// verify
		Assert.assertEquals(expected.trim().replace("\n","").replace("\r",""), result.replace("\n","").replace("\r","").trim())

		val s: String = """
asda
"""

	}



}