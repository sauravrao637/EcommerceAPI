package main.kotlin.com.camo.repos
/* ktlint-disable no-wildcard-imports */

import main.kotlin.com.camo.database.DBUserEntity
import main.kotlin.com.camo.database.DatabaseManager
import main.kotlin.com.camo.models.*

class MySQLUserRepo(private val db: DatabaseManager) : UserRepoIF {
    override fun getUser(username: String, passHash: String): DBUserEntity? = db.getUser(username, passHash)
    override fun getProducts(sortId: Int?, category: String?): ArrayList<Product>? = db.getProducts(sortId, category)
    override fun addToCart(userId: String, productId: Int, quantity: Int): Boolean? = db.addToCart(userId, productId, quantity)
    override fun removeFromCart(userId: String, productId: Int): Boolean? = db.removeFromCart(userId, productId)
    override fun getCart(userId: String): Cart? = db.getCart(userId)
    override fun clearCart(userId: String): Boolean? = db.clearCart(userId)
    override fun checkout(userId: String): Boolean? = db.checkout(userId)
    override fun getOrders(userId: String): ArrayList<Order>? = db.getOrders(userId)
    override fun getWallet(uid: String): Wallet? = db.getWallet(uid)
    override fun addToWallet(uid: String, transactionId: String): Boolean? = db.addToWallet(uid, transactionId)
    override fun withdrawFromWallet(uid: String, amount: Int): Int? = db.withdrawFromWallet(uid, amount)
    override fun getWithdrawals(uid: String): ArrayList<Withdrawal>? = db.getWithdrawals(uid)
    override fun getTransactions(uid: String, filter: Boolean?): ArrayList<Transaction>? = db.getTransactions(uid, filter)
    override fun getTransaction(uid: String, transactionId: String): Transaction? = db.getTransaction(uid, transactionId)
    override fun getMe(uid: String): Me? = db.getMe(uid)
}
