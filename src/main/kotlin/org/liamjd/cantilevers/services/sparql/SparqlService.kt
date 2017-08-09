package org.liamjd.cantilevers.services.sparql

import org.liamjd.cantilevers.viewmodel.Bridge

interface SparqlService {

	fun query(queryMap: Map<String,String>): String

	fun queryList(queryMap: Map<String,String>): List<Bridge>
}