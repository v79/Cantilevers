package org.liamjd.cantilevers.controllers

import com.github.salomonbrys.kodein.instance
import org.liamjd.cantilevers.annotations.SparkController
import org.liamjd.cantilevers.services.sparql.SparqlService
import spark.ModelAndView
import spark.Spark
import spark.kotlin.get
import spark.kotlin.post

@SparkController
class HomeController : AbstractController(path = "/") {

	lateinit var wikiDataService: SparqlService

	companion object Validator {

		fun validateSearchRequestSplat(splatArray: Array<String>): Map<String, String> {
			val errorMap = mutableMapOf<String, String>()

			if (splatArray != null && splatArray.isNotEmpty()) {
				val name: String? = splatArray[0]
				name?.let {
					if (name.length < 3) {
						errorMap.put("name", "Name must contain at least three characters")
					}
				}
			} else {
				errorMap.put("name", "Cannot search for an empty string")
			}

			return errorMap
		}
	}

	init {
		wikiDataService = injectServices.instance("wikidata")

		get(path) {
			model.put("title", "Cantilevers Bridge Database")
			engine.render(ModelAndView(model, "home"))
		}

		Spark.path(path + "ajax/") {
			post("validate/") {
				val name: String? = request.queryParams("add-bridge-name")
				val errorMap = validateName(name)
				if (errorMap.isEmpty()) {
					logger.info("No errors found")
					response.status(200)
					response.body("")
					flash(request,"bridgeName",name!!)
				} else {
					model.put("errors", errorMap)
					model.put("name",name!!)
					engine.render(ModelAndView(model, "fragments/home-add-search"))
				}
			}
		}
	}

	private fun validateName(bridgeName: String?): Map<String, String> {
		val errors: MutableMap<String, String> = mutableMapOf()
		if (bridgeName == null || bridgeName.isEmpty()) {
			errors.put("name", "Name must not be blank")
		} else {
			if (bridgeName.length < 4 || bridgeName.length > 50) {
				errors.put("name", "Name must be between 3 and 50 characters long")
			}
		}

		return errors
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