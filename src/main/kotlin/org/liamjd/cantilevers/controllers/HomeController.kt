package org.liamjd.cantilevers.controllers

import org.liamjd.cantilevers.annotations.SparkController
import org.liamjd.cantilevers.controllers.validators.BridgeValidator
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
				val errorMap = BridgeValidator.validateName(name)
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



}