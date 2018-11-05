package org.liamjd.cantilevers.viewmodel

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.obj
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Bridge(val wikiDataID: String, val name: String, val description: String?, val length: Float?, val wikiDataJSON: String?, val coords: String?) {

	var lastModified: LocalDate? = null

	val json: JsonObject
		get() = wikiDataJSON?.let { parseJsonString(it) } as JsonObject

	companion object {
		val parser: Parser = Parser()
		val dateFormat: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
	}



	override fun toString(): String {
		val sb: StringBuilder = StringBuilder()
		sb.append(name).append(", ")
		if(description != null) {
			sb.append("${description}, ")
		}
		if(length != null) {
			sb.append(length).append("m,")
		}

		sb.append(" ($wikiDataID)")

		return sb.toString()
	}

	fun parseJsonString(jsonString: String): JsonObject {
		return parser.parse(StringBuilder(jsonString)) as JsonObject
	}

	fun extractBasicFields() {
		val qBridge = json.obj("entities")?.get(wikiDataID) as JsonObject
		val lastModifiedDateString: String? = qBridge["modified"] as String?
		// formatted as 2017-03-20T20:39:57Z
		lastModified = LocalDate.parse(lastModifiedDateString, dateFormat)

		val claims = qBridge.obj("claims")

	}

	fun getPropertyFromClaims(claims: JsonObject, property: String): String {
		claims[property]

		return ""
	}
}

