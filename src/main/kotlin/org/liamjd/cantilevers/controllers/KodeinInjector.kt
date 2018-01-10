package org.liamjd.cantilevers.controllers

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.provider
import org.liamjd.cantilevers.services.sparql.SparqlService
import org.liamjd.cantilevers.services.sparql.WikiDataSparqlService
import org.liamjd.cantilevers.services.wikidata.WikiDataService

interface KodeinInjector {
	val injectServices: Kodein
}