package org.liamjd.cantilevers.controllers

import org.liamjd.cantilevers.annotations.SparkController
import spark.ModelAndView
import spark.Spark
import spark.kotlin.get
import spark.kotlin.post

@SparkController
class HomeController : AbstractController(path = "/") {

	init {

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

}