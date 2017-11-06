package org.liamjd.cantilevers.controllers

import com.github.salomonbrys.kodein.instance
import org.liamjd.cantilevers.annotations.SparkController
import org.liamjd.cantilevers.services.sparql.SparqlService
import org.liamjd.cantilevers.services.wikidata.WikiDataService
import org.liamjd.cantilevers.services.wikidata.WikiMediaImage
import org.liamjd.cantilevers.viewmodel.Bridge
import spark.ModelAndView
import spark.Spark
import spark.kotlin.get
import spark.kotlin.post

@SparkController
class BridgeController: AbstractController("/bridge") {

	val sparqlService: SparqlService
	val wikiDataService: WikiDataService
	var suggestions: List<Bridge> = mutableListOf()
	val viewPath = "bridge/view-edit"

	init {
		sparqlService = injectServices.instance("sparql")
		wikiDataService = injectServices.instance("wikidata")

		get(path) {
			"Shouldn't be here - TODO"
		}

		get(path + "/search") {
			model.put("title", "Choose Bridge | Cantilevers Bridge Database")
			model.remove("results")
			engine.render(ModelAndView(model, viewPath))
		}

		Spark.path(path + "/ajax") {
			get("/") {
				"ajax"
			}
			post("/triggerSearch") {
				// TODO: Validate the bridge name
				var bridgeName: String? = request.queryParams("bridgeName")
				if(bridgeName == null) {
					bridgeName = model["bridgeName"] as String
				}
				model.remove("results")
				suggestions = getWikidataSuggestions(bridgeName)
				model.put("results",suggestions)

				engine.render(ModelAndView(model, viewPath),"results")
			}

			get("/getPreview") {
				model.remove("imageUrl")
				val wikiDataID: String? = request.queryParams("wikiDataID")
				wikiDataID?.let {
					val bridgePreviewStatements = wikiDataService.getStatementsForDocument(wikiDataID)
					val simplePreview: MutableMap<String,String> = mutableMapOf()
					bridgePreviewStatements.forEach {
						simplePreview.put(wikiDataService.getPropertyLabel(it.key,"en-gb"),it.value.toString() )
						if(it.key.equals("P18")) {
							val image  = it.value as WikiMediaImage
							model.put("imageUrl", image.url)
						}
					}
					model.put("bridgePreview",simplePreview)
				}
				engine.render(ModelAndView(model, viewPath),"preview")
			}

		}
	}

	private fun getWikidataSuggestions(bridgeName: String): List<Bridge> {
		val queryMap = mutableMapOf<String, String>()
		queryMap.put("name", bridgeName)
		val wikidata = sparqlService.queryList(queryMap)
		return wikidata
	}

}