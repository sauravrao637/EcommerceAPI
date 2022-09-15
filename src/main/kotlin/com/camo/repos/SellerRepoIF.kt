package main.kotlin.com.camo.repos
/* ktlint-disable no-wildcard-imports */
import main.kotlin.com.camo.models.OrderSeller
interface SellerRepoIF {
    fun proposeProduct(category: String, name: String, inventory: Int, details: String, image1: ByteArray, image2: ByteArray, catalog: ByteArray, username: String, price: Int): Int?
    fun getOrdersForSeller(uid: String, filter: Boolean?): ArrayList<OrderSeller>?

    fun getOrderForSeller(uid: String, orderId: Int): OrderSeller?

    fun approveOrder(uid: String, orderId: Int): Boolean?

    fun sellerCancelOrder(uid: String, orderId: Int): Boolean?
}
