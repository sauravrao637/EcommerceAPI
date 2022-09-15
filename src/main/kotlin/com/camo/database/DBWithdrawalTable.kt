package main.kotlin.com.camo.database
/* ktlint-disable no-wildcard-imports */
import org.ktorm.entity.Entity
import org.ktorm.schema.*

object DBWithdrawalTable : Table<DBWithdrawalEntity>("Withdrawal") {
    val id = int("id").primaryKey().bindTo { it.id }
    val uid = varchar("uid").bindTo { it.uid }
    val amount = int("amount").bindTo { it.amount }
    val status = text("_status").bindTo { it.status }
}

interface DBWithdrawalEntity : Entity<DBWithdrawalEntity> {
    companion object : Entity.Factory<DBWithdrawalEntity>()
    val id: Int
    val uid: String
    val amount: Int
    val status: String
}
