package main.kotlin.com.camo.services
/* ktlint-disable no-wildcard-imports */
import main.kotlin.com.camo.database.DBUserEntity
import main.kotlin.com.camo.models.*
import main.kotlin.com.camo.repos.UserRepoIF

class UserService(
    private val repo: UserRepoIF
) {
    // fun getAllUsers(): List<UserProfile> = repo.getAllUsers()
    fun getUser(username: String, passHash: String): DBUserEntity? = repo.getUser(username, passHash)
    fun getProducts(sortId: Int?, category: String?): ArrayList<Product>? = repo.getProducts(sortId, category)
    fun addToCart(userId: String, productId: Int, quantity: Int): Boolean? = repo.addToCart(userId, productId, quantity)
    fun removeFromCart(userId: String, productId: Int): Boolean? = repo.removeFromCart(userId, productId)
    fun getCart(userId: String): Cart? = repo.getCart(userId)
    fun clearCart(userId: String): Boolean? = repo.clearCart(userId)
    fun checkout(userId: String): Boolean? = repo.checkout(userId)
    fun getOrders(userId: String): ArrayList<Order>? = repo.getOrders(userId)
    fun getWallet(uid: String): Wallet? = repo.getWallet(uid)
    fun addToWallet(uid: String, transactionId: String): Boolean? = repo.addToWallet(uid, transactionId)
    fun withdrawFromWallet(uid: String, amount: Int): Int? = repo.withdrawFromWallet(uid, amount)
    fun getWithdrawals(uid: String): ArrayList<Withdrawal>? = repo.getWithdrawals(uid)
    fun getTransactions(uid: String, filter: Boolean?): ArrayList<Transaction>? = repo.getTransactions(uid, filter)
    fun getTransaction(uid: String, transactionId: String): Transaction? = repo.getTransaction(uid, transactionId)
    fun getMe(uid: String): Me? = repo.getMe(uid)
}
