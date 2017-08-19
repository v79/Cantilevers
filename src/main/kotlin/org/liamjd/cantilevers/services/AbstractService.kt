package org.liamjd.cantilevers.services

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.provider
import org.liamjd.cantilevers.db.caches.PropertyCacheDao
import org.liamjd.cantilevers.db.dbModule
import org.slf4j.LoggerFactory

abstract class AbstractService() {

	open val logger = LoggerFactory.getLogger(AbstractService::class.java)

	val injectCache = Kodein {
		import(dbModule)
		bind<PropertyCacheDao>() with provider { PropertyCacheDao() }
	}
}