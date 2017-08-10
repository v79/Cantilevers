package org.liamjd.cantilevers.controllers

import com.github.salomonbrys.kodein.instance
import org.liamjd.cantilevers.annotations.SparkController
import org.liamjd.cantilevers.services.sparql.SparqlService
import spark.ModelAndView
import spark.Spark
import spark.kotlin.get

@SparkController
class BridgeController: AbstractController("/bridge") {

	lateinit var wikiDataService: SparqlService

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
		}
	}
}