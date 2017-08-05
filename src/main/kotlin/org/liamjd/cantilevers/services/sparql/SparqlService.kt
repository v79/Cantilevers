package org.liamjd.cantilevers.services.sparql

interface SparqlService {

	fun query(queryMap: Map<String,String>): String
}