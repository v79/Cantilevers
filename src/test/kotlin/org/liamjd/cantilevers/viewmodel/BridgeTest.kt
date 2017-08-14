package org.liamjd.cantilevers.viewmodel

import com.beust.klaxon.JsonObject
import com.beust.klaxon.obj
import org.junit.Assert
import org.junit.Test
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BridgeTest {

	companion object TayBridge {
		val wikiDataID: String = "Q6480690"
		val name: String = "Tay Rail Bridge"
		val coords: String = "Point(-2.988444444 56.437333333)"
		val length: Int = 1234
		private val wikidatajsonurl = URL("https://www.wikidata.org/wiki/Special:EntityData/" + wikiDataID + ".json")
		val wikiDataJSON: String? = wikidatajsonurl.readText()
//		val lastModifiedDate: LocalDate = LocalDate.parse("2017-03-20T20:39:57Z", DateTimeFormatter.ISO_INSTANT)
	}


	@Test
	fun `should build string for basic values`() {
		// setup
		var bridge: Bridge = Bridge(name = TayBridge.name, wikiDataID = TayBridge.wikiDataID, coords = TayBridge.coords, length = TayBridge.length, wikiDataJSON = null)
		val expected: String = """Tay Rail Bridge, 1234m, at Point(-2.988444444 56.437333333), (Q6480690)"""
		// execute
		val result = bridge.toString()

		// verify
		Assert.assertEquals(expected, result)
	}

	@Test
	fun `random tests to see outputs`() {
		// setup
		var bridge: Bridge = Bridge(name = TayBridge.name, wikiDataID = TayBridge.wikiDataID, coords = TayBridge.coords, length = TayBridge.length, wikiDataJSON = TayBridge.wikiDataJSON)

		// execute
		val json = bridge.json

		// verify
		println(json.toJsonString(prettyPrint = true))

		val entities = json.obj("entities")
		val qBridge = entities?.get(TayBridge.wikiDataID) as JsonObject

		println(qBridge)
	}

	@Test
	fun `should extract date as LocalDate object`() {
		// setup
		val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy MM dd")
		var bridge: Bridge = Bridge(name = TayBridge.name, wikiDataID = TayBridge.wikiDataID, coords = TayBridge.coords, length = TayBridge.length, wikiDataJSON = TayBridge.wikiDataJSON)
		val expectedDate: LocalDate = LocalDate.parse("2017 03 20", dateFormat)

		// execute
		bridge.extractBasicFields()

		// verify
		println(bridge.lastModified)
		Assert.assertEquals(expectedDate.year,bridge.lastModified?.year)
		Assert.assertEquals(expectedDate.month,bridge.lastModified?.month)
		Assert.assertEquals(expectedDate.dayOfYear,bridge.lastModified?.dayOfYear)
	}

}