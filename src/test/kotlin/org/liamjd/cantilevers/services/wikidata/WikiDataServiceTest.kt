package org.liamjd.cantilevers.services.wikidata

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueItemId

/**
 * This is a live, integration test. For now!
 */
class WikiDataServiceTest {

	val connelBridge = "Q5161719"
	val britishEnglish = "en-gb"

	var mJacksonValueItemId = Mockito.mock(JacksonValueItemId::class.java)

	@Before
	fun setup() {
		Mockito.`when`(mJacksonValueItemId.id).thenReturn(connelBridge)
	}

	@Test
	fun `should return british english label for entity Q5161719`() {
		// setup
		val service = WikiDataService()

		// execute
		val result = service.getValueIdLabel(mJacksonValueItemId,britishEnglish)

		// verify
		val expected = "Connel Bridge"
		Assert.assertEquals(result,expected)
	}

	@Test
	fun `should get all statements about entity Q5161719`() {
		// setup
		val service = WikiDataService()

		// execute
		val result = service.getStatementsForDocument(connelBridge,britishEnglish)

		// verify
		Assert.assertNotNull(result)
		Assert.assertTrue(result.size > 0)
		println("Total statements: ${result.size}")
		val instanceOf = result["P31"] as WikiDataStrings
		Assert.assertTrue(4 == instanceOf.value.size)
		println(instanceOf)
		val materialUsed = result["P186"] as WikiDataStrings
		Assert.assertTrue(1 == materialUsed.value.size)
		val steelExpected = materialUsed.value.first().get("en")
		Assert.assertEquals(steelExpected,"steel")
		println(materialUsed)

		val coords = result["P625"]
		println(coords)
	}

}