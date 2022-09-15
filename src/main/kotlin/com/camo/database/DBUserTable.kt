package main.kotlin.com.camo.database
/* ktlint-disable no-wildcard-imports */
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.varchar

open class DBUserTable(alias: String?) : Table<DBUserEntity>("_user", alias) {
    companion object : DBUserTable(null)
    override fun aliased(alias: String) = DBUserTable(alias)
    val uid = varchar("uid").primaryKey().bindTo { it.uid }
    val type = varchar("_type").bindTo { it.type }
    val username = varchar("username").bindTo { it.username }
    val email = varchar("email").bindTo { it.email }
    val firstName = varchar("first_name").bindTo { it.firstName }
    val lastName = varchar("last_name").bindTo { it.lastName }
}

interface DBUserEntity : Entity<DBUserEntity> {
    companion object : Entity.Factory<DBUserEntity>()

    val uid: String
    val type: String
    val email: String
    val firstName: String
    val lastName: String
    val username: String
}
