package org.liamjd.cantilevers.services.sparql

import org.liamjd.cantilevers.viewmodel.Bridge

interface SparqlService {

	fun queryList(queryMap: Map<String,String>): List<Bridge>
}