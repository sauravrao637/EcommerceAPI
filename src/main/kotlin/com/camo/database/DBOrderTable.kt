package main.kotlin.com.camo.database
/* ktlint-disable no-wildcard-imports */
import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.time.LocalDateTime

object DBOrderTable : Table<DBOrderEntity>("_order") {
    val orderId = int("order_id").primaryKey().bindTo { it.orderId }
    val buyerUid = varchar("buyer_uid").bindTo { it.buyerUid }
    val productId = int("product_id").bindTo { it.productId }
    val quantity = int("quantity").bindTo { it.quantity }
    val paid = boolean("paid").bindTo { it.paid }
    val amount = int("amount").bindTo { it.amount }
    val status = text("_status").bindTo { it.status }
    val createdAt = datetime("_time").bindTo { it.createdAt }
}

interface DBOrderEntity : Entity<DBOrderEntity> {
    companion object : Entity.Factory<DBOrderEntity>()
    val orderId: Int
    val buyerUid: String
    val productId: Int
    val quantity: Int
    val paid: Boolean
    val amount: Int
    val status: String
    val createdAt: LocalDateTime
}
