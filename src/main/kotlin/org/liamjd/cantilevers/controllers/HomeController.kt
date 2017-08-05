package org.liamjd.cantilevers.controllers

import com.github.salomonbrys.kodein.instance
import org.eclipse.jetty.http.HttpStatus
import org.liamjd.cantilevers.annotations.SparkController
import org.liamjd.cantilevers.services.sparql.SparqlService
import spark.ModelAndView
import spark.Spark
import spark.kotlin.get
import spark.kotlin.post

@SparkController
class HomeController : AbstractController(path = "/") {

	lateinit var wikiDataSerice: SparqlService

	init {
		wikiDataSerice = injectServices.instance("wikidata")
		get(path) {
			model.put("title","Cantilevers Bridge Database")
			engine.render(ModelAndView(model,"home"))
		}

		Spark.path("/ajax/") {
			get("add-search/*") {

				val bridgeName: String? = request.splat()[0]
				if (bridgeName != null) {
					model.put("add-search-name",bridgeName)
				}
				logger.info("Search for bridge with name ${bridgeName} triggered")
//				engine.render(ModelAndView(model, "modals/home-add-search"))
				if(bridgeName != null) {
					val queryMap = mutableMapOf<String, String>()
					queryMap.put("name", bridgeName)
					val wikidata = wikiDataSerice.query(queryMap)
					response.body(wikidata)
					response.status(HttpStatus.OK_200)
				} else {

					response.body("here's some information about bridge $bridgeName")
					response.status(HttpStatus.OK_200)
				}
			}
		}
	}
}