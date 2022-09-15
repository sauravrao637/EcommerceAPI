package main.kotlin.com.camo.services
/* ktlint-disable no-wildcard-imports */
import main.kotlin.com.camo.models.OrderSeller
import main.kotlin.com.camo.repos.SellerRepoIF

class SellerService(
    private val repo: SellerRepoIF
) {
    // fun getAllUsers(): List<UserProfile> = repo.getAllUsers()
    fun proposeProduct(category: String, name: String, inventory: Int, details: String, image1: ByteArray, image2: ByteArray, catalog: ByteArray, username: String, price: Int): Int? = repo.proposeProduct(category, name, inventory, details, image1, image2, catalog, username, price)

    fun getOrdersForSeller(uid: String, filter: Boolean?): ArrayList<OrderSeller>? = repo.getOrdersForSeller(uid, filter)

    fun getOrderForSeller(uid: String, orderId: Int): OrderSeller? = repo.getOrderForSeller(uid, orderId)

    fun approveOrder(uid: String, orderId: Int): Boolean? = repo.approveOrder(uid, orderId)

    fun sellerCancelOrder(uid: String, orderId: Int): Boolean? = repo.sellerCancelOrder(uid, orderId)
}
