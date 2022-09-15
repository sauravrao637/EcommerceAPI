package main.kotlin.com.camo.database
/* ktlint-disable no-wildcard-imports */
import org.ktorm.entity.Entity
import org.ktorm.schema.*

object DBWalletTable : Table<DBWalletEntity>("wallet") {
    val uid = varchar("uid").primaryKey().bindTo { it.uid }
    val balance = int("balance").bindTo { it.balance }
    val hold = int("hold").bindTo { it.hold }
}

interface DBWalletEntity : Entity<DBWalletEntity> {
    companion object : Entity.Factory<DBWalletEntity>()
    val uid: String
    val balance: Int
    val hold: Int
}
