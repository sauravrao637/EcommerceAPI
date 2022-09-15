package main.kotlin.com.camo.database
/* ktlint-disable no-wildcard-imports */
import org.ktorm.entity.Entity
import org.ktorm.schema.*

object DBTransactionTable : Table<DBTransactionEntity>("_transaction") {
    val uid = varchar("uid").bindTo { it.uid }
    val transactionId = varchar("id").primaryKey().bindTo { it.transactionId }
    val approved = boolean("approved").bindTo { it.approved }
}

interface DBTransactionEntity : Entity<DBTransactionEntity> {
    companion object : Entity.Factory<DBTransactionEntity>()
    val uid: String
    val transactionId: String
    val approved: Boolean
}
