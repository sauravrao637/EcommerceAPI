package main.kotlin.com.camo.repos

import main.kotlin.com.camo.database.DBUserEntity
/* ktlint-disable no-wildcard-imports */
import main.kotlin.com.camo.models.*

interface UserRepoIF {
    fun getUser(username: String, passHash: String): DBUserEntity?
    fun getProducts(sortId: Int?, category: String?): ArrayList<Product>?
    fun addToCart(userId: String, productId: Int, quantity: Int): Boolean?
    fun removeFromCart(userId: String, productId: Int): Boolean?
    fun getCart(userId: String): Cart?
    fun clearCart(userId: String): Boolean?
    fun checkout(userId: String): Boolean?
    fun getOrders(userId: String): ArrayList<Order>?
    fun getWallet(uid: String): Wallet?
    fun addToWallet(uid: String, transactionId: String): Boolean?
    fun withdrawFromWallet(uid: String, amount: Int): Int?
    fun getWithdrawals(uid: String): ArrayList<Withdrawal>?
    fun getTransactions(uid: String, filter: Boolean?): ArrayList<Transaction>?
    fun getTransaction(uid: String, transactionId: String): Transaction?
    fun getMe(uid: String): Me?
}
