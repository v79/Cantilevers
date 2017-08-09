package org.liamjd.cantilevers.controllers

import com.github.salomonbrys.kodein.instance
import org.eclipse.jetty.http.HttpStatus
import org.liamjd.cantilevers.annotations.SparkController
import org.liamjd.cantilevers.services.sparql.SparqlService
import spark.ModelAndView
import spark.Spark
import spark.kotlin.get

@SparkController
class HomeController : AbstractController(path = "/") {

	lateinit var wikiDataService: SparqlService

	companion object Validator {

		fun validateSearchRequestSplat(splatArray: Array<String>): Map<String, String> {
			val errorMap = mutableMapOf<String, String>()

			if(splatArray != null && splatArray.isNotEmpty()) {
				val name: String? = splatArray[0]
				name?.let {
					if(name.length < 3) {
						errorMap.put("name","Name must contain at least three characters")
					}
				}
			} else {
				errorMap.put("name","Cannot search for an empty string")
			}

			return errorMap
		}
	}

	init {
		wikiDataService = injectServices.instance("wikidata")
		get(path) {
			model.put("title","Cantilevers Bridge Database")
			engine.render(ModelAndView(model,"home"))
		}

		Spark.path("/ajax/") {
			get("add-search/*") {

				val errors = validateSearchRequestSplat(request.splat())
				if(errors.isNotEmpty()) {
					flash(request,"errors",errors)
					response.header("spark-error-redirect","/")
				} else {
					val bridgeName: String? = request.splat()[0]
					if (bridgeName != null) {
						model.put("add-search-name", bridgeName)
					}
					logger.info("Search for bridge with name ${bridgeName} triggered")
//				engine.render(ModelAndView(model, "modals/home-add-search"))
					if (bridgeName != null) {
						val responseString = getWikidataSuggestions(bridgeName)
						response.body(responseString)
						response.status(HttpStatus.OK_200)
					} else {
						response.body("Could not find a bridge with name $bridgeName")
						response.status(HttpStatus.OK_200)
					}
				}
			}
			get("refine-search/*") {
				val bridgeName: String? = request.splat()[0]
				if (bridgeName != null) {
					model.put("add-search-name",bridgeName)
				}
				logger.info("Search for bridge with name ${bridgeName} triggered")
//				engine.render(ModelAndView(model, "modals/home-add-search"))
				if(bridgeName != null) {
					val responseString = getWikidataSuggestions(bridgeName)
					response.body(responseString)
					response.status(HttpStatus.OK_200)
				} else {
					response.body("Could not find a bridge with name $bridgeName")
					response.status(HttpStatus.OK_200)
				}
			}
		}
	}

	private fun getWikidataSuggestions(bridgeName: String): String {
		val queryMap = mutableMapOf<String, String>()
		queryMap.put("name", bridgeName)
		val wikidata = wikiDataService.queryList(queryMap)

		val responseString = StringBuilder()
		responseString.append("<ol>")
		wikidata.forEach {
			responseString.append("<li>")
			responseString.append("$it")
			responseString.append("</li>")
		}
		responseString.append("</ol>")
		return responseString.toString()
	}
}