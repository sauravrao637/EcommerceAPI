package main.kotlin.com.camo.database
/* ktlint-disable no-wildcard-imports */
import org.ktorm.entity.Entity
import org.ktorm.schema.*

object DBProductTable : Table<DBProductEntity>("product") {
    val productId = int("product_id").primaryKey().bindTo { it.productId }
    val category = varchar("category").bindTo { it.category }
    val name = varchar("_name").bindTo { it.name }
    val inventory = int("_inventory").bindTo { it.inventory }
    val approved = boolean("approved").bindTo { it.approved }
    val image1 = blob("image_1").bindTo { it.image1 }
    val image2 = blob("image_2").bindTo { it.image2 }
    val details = varchar("details").bindTo { it.details }
    val seller = varchar("seller_username").bindTo { it.seller }
    val catalogg = blob("catalog").bindTo { it.catalog }
    val price = int("price").bindTo { it.price }
}

interface DBProductEntity : Entity<DBProductEntity> {
    companion object : Entity.Factory<DBProductEntity>()
    val productId: Int
    val category: String
    val name: String
    val inventory: Int
    val catalog: ByteArray
    val approved: Boolean
    val image1: ByteArray
    val image2: ByteArray
    val details: String
    val seller: String
    val price: Int
}
/*
(`product_id`,
`category`,
`_name`,
`_inventory`,
`catalog`,
`approved`,
`image_1`,
`image_2`,
`details`,
`seller_username`
`price`) */
