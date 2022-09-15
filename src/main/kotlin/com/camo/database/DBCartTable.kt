package main.kotlin.com.camo.database
/* ktlint-disable no-wildcard-imports */
import org.ktorm.entity.Entity
import org.ktorm.schema.*

object DBCartTable : Table<DBCartEntity>("cart") {
    val uid = varchar("uid").bindTo { it.uid }
    val productId = int("product_id").bindTo { it.productId }
    val quantity = int("quantity").bindTo { it.quantity }
}

interface DBCartEntity : Entity<DBCartEntity> {
    companion object : Entity.Factory<DBCartEntity>()
    val uid: String
    val productId: Int
    val quantity: Int
}
