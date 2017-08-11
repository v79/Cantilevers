package org.liamjd.cantilevers.controllers

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.github.salomonbrys.kodein.instance
import org.liamjd.cantilevers.annotations.SparkController
import org.liamjd.cantilevers.services.sparql.SparqlService
import org.liamjd.cantilevers.viewmodel.Bridge
import spark.ModelAndView
import spark.Spark
import spark.kotlin.get
import spark.kotlin.post

@SparkController
class BridgeController: AbstractController("/bridge") {

	lateinit var wikiDataService: SparqlService
	var suggestions: List<Bridge> = mutableListOf()

	init {
		wikiDataService = injectServices.instance("wikidata")

		get(path) {
			"Shouldn't be here - TODO"
		}
		get(path + "/search") {
			model.put("title", "Edit Bridge | Cantilevers Bridge Database")
			engine.render(ModelAndView(model, "bridge/view-edit"))
		}

		Spark.path(path + "/ajax") {
			get("/") {
				"ajax"
			}
			post("/triggerSearch") {
				var bridgeName: String? = request.queryParams("bridgeName")
				if(bridgeName == null) {
					bridgeName = model["bridgeName"] as String
				}
				suggestions = getWikidataSuggestions(bridgeName)
				model.put("results",suggestions)

				engine.render(ModelAndView(model,"bridge/fragments/search-edit-results"))
			}

			get("/getPreview") {
				val wikiDataID: String? = request.queryParams("wikiDataID")
				val previewBridge = suggestions.find { it.wikiDataID == wikiDataID }
				previewBridge?.wikiDataJSON.toString()
			}

		}
	}

	private fun getWikidataSuggestions(bridgeName: String): List<Bridge> {
		val queryMap = mutableMapOf<String, String>()
		queryMap.put("name", bridgeName)
		val wikidata = wikiDataService.queryList(queryMap)
		return wikidata
	}

	private fun getPreviewHTML(wikiDataID: String): String {
		val stringBuilder: StringBuilder = StringBuilder()
		val outputBuilder: StringBuilder = StringBuilder()

		val parser: Parser = Parser()
		stringBuilder.append(wikiDataID)
		val json: JsonObject = parser.parse(stringBuilder) as JsonObject
		outputBuilder.append(json.toJsonString(true))
		outputBuilder.append("""<br/><a href="#">Close</a>""")
		return outputBuilder.toString()
	}
}