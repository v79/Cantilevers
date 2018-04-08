package org.liamjd.cantilevers.controllers

import org.liamjd.caisson.controllers.AbstractController
import org.liamjd.caisson.webforms.Form
import org.liamjd.cantilevers.annotations.SparkController
import org.liamjd.cantilevers.controllers.validators.BridgeValidator
import org.liamjd.cantilevers.viewmodel.BridgeName
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
				val bridge = Form(request, BridgeName::class).get() as BridgeName

				val errorMap = BridgeValidator.validateName(bridge.name)
				if (errorMap.isEmpty()) {
					logger.info("No errors found")
					flash(request, response, "bridgeName", bridge.name)
				} else {
					model.put("errors", errorMap)
					model.put("name", bridge.name)
					engine.render(ModelAndView(model, "fragments/home-add-search"))
				}
			}
		}
	}

}