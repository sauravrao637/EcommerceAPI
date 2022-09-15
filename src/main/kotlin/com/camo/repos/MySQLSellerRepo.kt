package main.kotlin.com.camo.repos
/* ktlint-disable no-wildcard-imports */
import main.kotlin.com.camo.database.DatabaseManager
import main.kotlin.com.camo.models.OrderSeller

class MySQLSellerRepo(private val db: DatabaseManager) : SellerRepoIF {
    override fun proposeProduct(category: String, name: String, inventory: Int, details: String, image1: ByteArray, image2: ByteArray, catalog: ByteArray, username: String, price: Int): Int? = db.proposeProduct(category, name, inventory, details, image1, image2, catalog, username, price)

    override fun getOrdersForSeller(uid: String, filter: Boolean?): ArrayList<OrderSeller>? = db.getOrdersForSeller(uid, filter)

    override fun getOrderForSeller(uid: String, orderId: Int): OrderSeller? = db.getOrderForSeller(uid, orderId)

    override fun approveOrder(uid: String, orderId: Int): Boolean? = db.approveOrder(uid, orderId)

    override fun sellerCancelOrder(uid: String, orderId: Int): Boolean? = db.sellerCancelOrder(uid, orderId)
}
