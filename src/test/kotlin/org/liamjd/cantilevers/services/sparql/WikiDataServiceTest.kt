package org.liamjd.cantilevers.services.sparql

import org.junit.Assert
import org.junit.Test
import org.liamjd.cantilevers.viewmodel.Bridge

class WikiDataServiceTest {

	val service = WikiDataSparqlService()

	@Test
	fun `should make call to wikidata`() {
		// setup
		val paramMap = mutableMapOf<String,String>()
		paramMap.put("name","Forth Road")

		// execute
		val result = service.query(paramMap)

		// verify
//		Assert.assertEquals("Q933000",thingy)
		Assert.assertNotNull(result)
	}

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

}