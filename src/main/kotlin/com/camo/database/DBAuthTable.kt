package main.kotlin.com.camo.database
/* ktlint-disable no-wildcard-imports */
import org.ktorm.entity.Entity
import org.ktorm.schema.*

object DBAuthTable : Table<DBAuthEntity>("_auth") {
    val username = varchar("username").bindTo { it.username }
    val uid = varchar("uid").primaryKey().bindTo { it.uid }
    val passHash = varchar("pass_hash").bindTo { it.passHash }
}

interface DBAuthEntity : Entity<DBAuthEntity> {
    companion object : Entity.Factory<DBAuthEntity>()
    val username: String
    val uid: String
    val passHash: String
}
