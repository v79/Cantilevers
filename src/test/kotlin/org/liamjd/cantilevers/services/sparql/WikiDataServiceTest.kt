package org.liamjd.cantilevers.services.sparql

import org.junit.Test

class WikiDataServiceTest {

	val service = WikiDataSparqlService()

	@Test
	fun `should make call to wikidata`() {
		// setup

		// execute
		val result = service.query(mapOf())

		// verify
		println(result.length)
	}
}