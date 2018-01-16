package org.liamjd.cantilevers

import org.liamjd.cantilevers.annotations.SparkController
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.slf4j.LoggerFactory
import spark.kotlin.port
import spark.kotlin.staticFiles
import spark.servlet.SparkApplication
import java.util.*


class CantileverServer : SparkApplication {
	val logger = LoggerFactory.getLogger(CantileverServer::class.java)
	val thisPackage = this.javaClass.`package`

	constructor(args: Array<String>) {
		val portNumber: String? = System.getProperty("server.port")
		port(number = portNumber?.toInt() ?: 4568)

		staticFiles.location("/public")

		// initialize controllers
		val reflections = Reflections(thisPackage.name, MethodAnnotationsScanner(), TypeAnnotationsScanner(), SubTypesScanner())
		val controllers = reflections.getTypesAnnotatedWith(SparkController::class.java)
		controllers.forEach {
			logger.info("Instantiating controller " + it.simpleName)
			it.newInstance()
		}

		displayStartupMessage(portNumber?.toInt())
	}

	override fun init() {
		TODO("Not really necessary; the work is done in the constructor")
	}

	private fun displayStartupMessage(portNumber: Int?) {
		logger.info("=============================================================")
		logger.info("Cantilevers Bridge Database Started")
		logger.info("Date: " + Date().toString())
		logger.info("OS: " + System.getProperty("os.name"))
		logger.info("Port: " + if (portNumber != null) portNumber else "4568")
		logger.info("JDBC URL: " + System.getenv("JDBC_DATABASE_URL"))
		logger.info("=============================================================")
	}
}