package org.liamjd.cantilevers.db.caches

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.liamjd.cantilevers.db.AbstractDao
import org.liamjd.cantilevers.db.Dao

object PropertyCache : IntIdTable() {
	val pCode = varchar("pCode",length = 30).index(isUnique = true)
//	val language = varchar(name = "language", length = 10)
	val text = varchar(name = "text", length = 1023)
}

class PropertyCacheDB(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<PropertyCacheDB>(PropertyCache)

	var pCode by PropertyCache.pCode
//	var language by PropertyCache.language
	var text by PropertyCache.text
}

class PropertyCacheDao : AbstractDao(), Dao {

	init {
		transaction {
			SchemaUtils.create(PropertyCache)
			}
	}

	fun add(pCode: String, text: String): Int {
		val newId: Int = transaction {
			val newPCache: PropertyCacheDB = PropertyCacheDB.new {
				this.pCode = pCode
//				this.language = lang
				this.text = text
			}
			newPCache.id.value
		}
		return newId
	}

	fun getText(codeToFind: String): String? {
		val result = get(codeToFind)
		if(result != null) {
			return result.text
		}
		return null
	}

	private fun get(codeToFind: String): PropertyCacheDB? {
		val result = transaction {
			PropertyCacheDB.find {
				PropertyCache.pCode eq codeToFind
			}.firstOrNull()
		}
		return result
	}

	fun delete(codeToDelete: String) {
		val result = get(codeToDelete)
		if(result != null) {
			transaction { result.delete() }
		}
	}
}