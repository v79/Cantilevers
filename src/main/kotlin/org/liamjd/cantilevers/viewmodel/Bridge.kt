package org.liamjd.cantilevers.viewmodel

data class Bridge(val wikiDataID: String, val name: String, val length: Int?, val wikiDataJSON: String?, val coords: String?) {

	override fun toString(): String {
		val sb: StringBuilder = StringBuilder()
		sb.append(name).append(", ")
		if(length != null) {
			sb.append(length).append("m,")
		}
		if(coords != null) {
			sb.append(" at $coords,")
		}
		sb.append(" ($wikiDataID)")

		return sb.toString()
	}
}