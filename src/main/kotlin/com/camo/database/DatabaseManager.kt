package main.kotlin.com.camo.database
/* ktlint-disable no-wildcard-imports */
// import com.camo.models.*

import main.kotlin.com.camo.CryptoHash
import main.kotlin.com.camo.models.*
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel
import java.util.*

const val HOST_NAME = "localhost"
// const val HOST_NAME = "host.docker.internal"
const val DATABASE_NAME = "fcs"
const val USERNAME = "root"
const val PASSWORD = "12345678"

class DatabaseManager {

    private val database: Database

    init {
        val jdbcUrl =
            "jdbc:mysql://$HOST_NAME:3306/$DATABASE_NAME?user=$USERNAME&password=$PASSWORD&allowPublicKeyRetrieval=true&useSSL=false"
        database =
            Database.connect(url = jdbcUrl, logger = ConsoleLogger(threshold = LogLevel.INFO))
    }

    fun isAuthValid(username: String, passHash: String): Boolean {
        val query =
            database.from(DBAuthTable)
                .select()
                .where({
                    (DBAuthTable.username like username) and
                        (DBAuthTable.passHash like passHash)
                })
        if (query.totalRecords != 1) return false
        for (row in query) {
            if (row[DBAuthTable.uid] == null) return false else return true
        }
        return false
    }

    fun getUser(username: String, passHash: String): DBUserEntity? {
        val uid = getUid(username, passHash) ?: return null
        return getUserByUid(uid)
    }
    private fun getUserByUid(uid: String): DBUserEntity? {
        val temp = database.from(DBUserTable).select().where({ DBUserTable.uid like uid })
        if (temp.totalRecords != 1) return null
        for (user in temp) {
            return DBUserTable.createEntity(user)
        }
        return null
    }
    private fun getUid(username: String, passHash: String): String? {
        val query =
            database.from(DBAuthTable)
                .select()
                .where({
                    (DBAuthTable.username like username) and
                        (DBAuthTable.passHash like passHash)
                })
        if (query.totalRecords != 1) return null
        for (row in query) {
            return row[DBAuthTable.uid]
        }
        return null
    }
    fun signup(
        username: String,
        password: String,
        type: String,
        firstName: String,
        lastName: String,
        email: String
    ): String? {
        try {
            if (type.lowercase() == "admin") throw Exception("Admin Signup is not allowed")
            val uiD = UUID.randomUUID()
            val uid = "$uiD"
            val passHash = CryptoHash.getHex(password)
            database.useTransaction() {
                database.insert(DBAuthTable) {
                    set(DBAuthTable.uid, uid)
                    set(DBAuthTable.username, username)
                    set(DBAuthTable.passHash, passHash)
                }

                database.insert(DBUserTable) {
                    set(it.uid, uid)
                    set(it.type, type)
                    set(it.email, email)
                    set(it.firstName, firstName)
                    set(it.lastName, lastName)
                    set(it.username, username)
                }

                database.insert(DBWalletTable) {
                    set(DBWalletTable.uid, uid)
                }
            }
            return uid
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun signupAdmin(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String
    ): String? {
        try {
            val uiD = UUID.randomUUID()
            val uid = "$uiD"
            val passHash = CryptoHash.getHex(password)
            database.useTransaction() {
                database.insert(DBAuthTable) {
                    set(DBAuthTable.uid, uid)
                    set(DBAuthTable.username, username)
                    set(DBAuthTable.passHash, passHash)
                }

                database.insert(DBUserTable) {
                    set(it.uid, uid)
                    set(it.type, "admin")
                    set(it.email, email)
                    set(it.firstName, firstName)
                    set(it.lastName, lastName)
                    set(it.username, username)
                }

                database.insert(DBWalletTable) {
                    set(DBWalletTable.uid, uid)
                }
            }
            return uid
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun proposeProduct(
        category: String,
        name: String,
        inventory: Int,
        details: String,
        image1: ByteArray,
        image2: ByteArray,
        catalog: ByteArray,
        username: String,
        price: Int
    ): Int? {
        try {
            val query =
                database.from(DBUserTable)
                    .select()
                    .where({
                        (DBUserTable.username like username) and
                            (DBUserTable.type like "seller")
                    })
            if (query.totalRecords == 0) {
                throw Exception("No Such Seller")
            }
            val productId =
                database.insertAndGenerateKey(DBProductTable) {
                    set(DBProductTable.category, category)
                    set(DBProductTable.name, name)
                    set(DBProductTable.inventory, inventory)
                    set(DBProductTable.catalogg, catalog)
                    set(DBProductTable.image1, image1)
                    set(DBProductTable.image2, image2)
                    set(DBProductTable.details, details)
                    set(DBProductTable.seller, username)
                    set(DBProductTable.price, price)
                }
            return productId as Int
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getProposals(filter: Boolean?): ArrayList<Product>? {
        try {
            val query =
                if (filter == null) {
                    database.from(DBProductTable).select()
                } else if (filter) {
                    database.from(DBProductTable)
                        .select()
                        .where({ DBProductTable.approved eq true })
                } else {
                    database.from(DBProductTable)
                        .select()
                        .where({ DBProductTable.approved eq false })
                }
            val result = ArrayList<Product>()
            for (row in query) {
                result.add(
                    Product(
                        row[DBProductTable.productId]!!,
                        row[DBProductTable.category]!!,
                        row[DBProductTable.name]!!,
                        row[DBProductTable.inventory]!!,
                        row[DBProductTable.catalogg]!!,
                        row[DBProductTable.approved]!!,
                        row[DBProductTable.image1]!!,
                        row[DBProductTable.image2]!!,
                        row[DBProductTable.details]!!,
                        row[DBProductTable.seller]!!,
                        row[DBProductTable.price]!!
                    )
                )
            }
            return result
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun approveProposal(productId: Int): Boolean {
        try {
            val query =
                database.update(DBProductTable) {
                    set(DBProductTable.approved, true)
                    where { (DBProductTable.productId eq productId) }
                }
            if (query == 0) {
                throw Exception("Product Not Found")
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun removeProduct(productId: Int): Boolean {
        try {
            val query = database.delete(DBProductTable) { (DBProductTable.productId eq productId) }
            if (query == 0) {
                throw Exception("Product Not Found")
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun getProducts(sortId: Int?, category: String?): ArrayList<Product>? {
        try {

            var query =
                if (category == null) {
                    database.from(DBProductTable).select().where {
                        DBProductTable.approved eq true
                    }
                } else {
                    database.from(DBProductTable).select().where {
                        (DBProductTable.category like category.lowercase()) and
                            (DBProductTable.approved eq true)
                    }
                }
            when (sortId) {
                1 -> query = query.orderBy(DBProductTable.name.asc())
                2 -> query = query.orderBy(DBProductTable.name.desc())
                3 -> query = query.orderBy(DBProductTable.price.asc())
                4 -> query = query.orderBy(DBProductTable.price.desc())
                else -> query = query.orderBy(DBProductTable.productId.asc())
            }

            val result = ArrayList<Product>()
            for (row in query) {
                result.add(
                    Product(
                        row[DBProductTable.productId]!!,
                        row[DBProductTable.category]!!,
                        row[DBProductTable.name]!!,
                        row[DBProductTable.inventory]!!,
                        row[DBProductTable.catalogg]!!,
                        row[DBProductTable.approved]!!,
                        row[DBProductTable.image1]!!,
                        row[DBProductTable.image2]!!,
                        row[DBProductTable.details]!!,
                        row[DBProductTable.seller]!!,
                        row[DBProductTable.price]!!
                    )
                )
            }
            return result
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun addToCart(userId: String, productId: Int, quantity: Int): Boolean? {
        try {
            val productQ = database.from(DBProductTable).select().where {
                (DBProductTable.productId eq productId) and (DBProductTable.approved eq true)
            }
            if (productQ.totalRecords == 0) throw Exception("Product Not Found")
            for (row in productQ) {
                if (quantity > row[DBProductTable.inventory]!!) throw Exception("Not Enough Inventory")
                break
            }
            val update =
                database.update(DBCartTable) {
                    set(DBCartTable.quantity, DBCartTable.quantity + quantity)
                    where {
                        (DBCartTable.uid like userId) and (DBCartTable.productId eq productId)
                    }
                }
            if (update == 0) {
                val query =
                    database.insert(DBCartTable) {
                        set(DBCartTable.uid, userId)
                        set(DBCartTable.productId, productId)
                        set(DBCartTable.quantity, quantity)
                    }
                if (query == 0) return false
                return true
            } else return true
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun removeFromCart(userId: String, productId: Int): Boolean? {
        try {
            val query =
                database.delete(DBCartTable) {
                    (DBCartTable.uid like userId) and (DBCartTable.productId eq productId)
                }
            if (query == 0) {
                throw Exception("Product Not Found")
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getCart(userId: String): Cart? {
        try {
            val query = database.from(DBCartTable).select().where { (DBCartTable.uid like userId) }
            val items = ArrayList<CartItem>()
            var total = 0
            for (row in query) {
                val products = database.from(DBProductTable).select().where { (DBProductTable.productId eq row[DBCartTable.productId]!!) and (DBProductTable.approved eq true) }
                if (products.totalRecords == 0) {
                    database.delete(DBCartTable) { (DBCartTable.uid like userId) and (DBCartTable.productId eq row[DBCartTable.productId]!!) }
                    continue
                } else {
                    for (product in products) {
                        total += product[DBProductTable.price]!! * row[DBCartTable.quantity]!!
                        break
                    }
                    items.add(
                        CartItem(
                            productId = row[DBCartTable.productId]!!,
                            quantity = row[DBCartTable.quantity]!!,
                            uid = row[DBCartTable.uid]!!
                        )
                    )
                }
            }
            return Cart(items, total)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun checkout(userId: String): Boolean? {
        try {
            val cart = getCart(userId) ?: throw Exception("Cart Null")
            if (cart.items.isEmpty()) throw Exception("Cart Empty")
            database.useTransaction() {
                val pay = database.update(DBWalletTable) {
                    set(DBWalletTable.balance, DBWalletTable.balance - cart.total)
                    set(DBWalletTable.hold, DBWalletTable.hold + cart.total)
                    where({ DBWalletTable.uid like userId })
                }
                if (pay == 0) throw Exception("Payment issues")
                val query = database.delete(DBCartTable) { (DBCartTable.uid like userId) }
                if (query == 0) throw Exception("Cart Empty")
                for (item in cart.items) {
                    val products = database.from(DBProductTable).select().where {
                        DBProductTable.productId eq item.productId
                    }
                    if (products.totalRecords == 0) { continue }
                    var cartProductPrice = 0
                    for (product in products) {
                        cartProductPrice = product[DBProductTable.price]!!
                        break
                    }

                    val query2 =
                        database.insertAndGenerateKey(DBOrderTable) {
                            set(DBOrderTable.buyerUid, item.uid)
                            set(DBOrderTable.productId, item.productId)
                            set(DBOrderTable.quantity, item.quantity)
                            set(DBOrderTable.paid, true)
                            set(DBOrderTable.amount, cartProductPrice * item.quantity)
                            set(DBOrderTable.status, "waiting")
                        }
                    if (query2 == 0) throw Exception("Could not proceed with product $item.productId")
                }
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getOrders(userId: String): ArrayList<Order>? {
        try {
            val user = getUserByUid(userId) ?: throw Exception("User Not Found")
            val query = if (user.type == "admin") {
                database.from(DBOrderTable).select()
            } else {
                database.from(DBOrderTable).select().where { DBOrderTable.buyerUid like userId }
            }
            val result = ArrayList<Order>()
            for (row in query) {
                result.add(
                    Order(
                        row[DBOrderTable.orderId]!!,
                        row[DBOrderTable.buyerUid]!!,
                        row[DBOrderTable.productId]!!,
                        row[DBOrderTable.quantity]!!,
                        row[DBOrderTable.paid]!!,
                        row[DBOrderTable.amount]!!,
                        row[DBOrderTable.status]!!,
                        row[DBOrderTable.createdAt]!!
                    )
                )
            }
            return result
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getWallet(uid: String): Wallet? {
        try {
            val query = database.from(DBWalletTable).select().where({ DBWalletTable.uid like uid })
            if (query.totalRecords == 0) throw Exception("User not found")
            for (row in query) {
                return Wallet(row[DBWalletTable.uid]!!, row[DBWalletTable.balance]!!, row[DBWalletTable.hold]!!)
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun addToWallet(uid: String, transactionId: String): Boolean? {
        try {
            val query = database.insert(DBTransactionTable) {
                set(DBTransactionTable.uid, uid)
                set(DBTransactionTable.transactionId, transactionId)
            }
            if (query == 0) throw Exception("Transaction not added")
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun withdrawFromWallet(uid: String, amount: Int): Int? {
        try {
            database.useTransaction() {
                database.update(DBWalletTable) {
                    set(DBWalletTable.balance, DBWalletTable.balance - amount)
                    where {
                        DBWalletTable.uid like uid
                    }
                }
                val withdrawalId = database.insertAndGenerateKey(DBWithdrawalTable) {
                    set(DBWithdrawalTable.uid, uid)
                    set(DBWithdrawalTable.amount, amount)
                    set(DBWithdrawalTable.status, "hold")
                } as Int
                return withdrawalId
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getWithdrawals(uid: String): ArrayList<Withdrawal>? {
        try {
            val user = getUserByUid(uid) ?: throw Exception("User Not Found")
            val query = if (user.type == "admin") {
                database.from(DBWithdrawalTable).select()
            } else {
                database.from(DBWithdrawalTable).select().where({ DBWithdrawalTable.uid like uid })
            }
            val withdrawals = ArrayList<Withdrawal>()
            for (row in query) {
                withdrawals.add(Withdrawal(row[DBWithdrawalTable.id]!!, row[DBWithdrawalTable.uid]!!, row[DBWithdrawalTable.amount]!!, row[DBWithdrawalTable.status]!!))
            }
            return withdrawals
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getOrdersForSeller(uid: String, filter: Boolean?): ArrayList<OrderSeller>? {
        try {
            val user = getUserByUid(uid) ?: throw Exception("User Not Found")
            if (user.type != "seller") throw Exception("User is not a seller")
            val query = when (filter) {
                null -> {
                    database.from(DBOrderTable).leftJoin(DBProductTable, on = DBProductTable.productId eq DBOrderTable.productId).select().where({ (DBOrderTable.productId eq DBProductTable.productId) and (DBProductTable.seller like user.username) })
                }
                true -> {
                    database.from(DBOrderTable).leftJoin(DBProductTable, on = DBProductTable.productId eq DBOrderTable.productId).select().where({ (DBOrderTable.productId eq DBProductTable.productId) and (DBProductTable.seller like user.username) and (DBOrderTable.status like "approved") })
                }
                false -> {
                    database.from(DBOrderTable).leftJoin(DBProductTable, on = DBProductTable.productId eq DBOrderTable.productId).select().where({ (DBOrderTable.productId eq DBProductTable.productId) and (DBProductTable.seller like user.username) and (DBOrderTable.status like "waiting") })
                }
            }
            val orders = ArrayList<OrderSeller>()
            for (row in query) {
                orders.add(OrderSeller(row[DBOrderTable.orderId]!!, row[DBOrderTable.productId]!!, row[DBOrderTable.quantity]!!, row[DBOrderTable.paid]!!, row[DBOrderTable.amount]!!, row[DBOrderTable.status]!!, row[DBOrderTable.createdAt]!!))
            }
            return orders
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getOrderForSeller(uid: String, orderId: Int): OrderSeller? {
        try {
            val user = getUserByUid(uid) ?: throw Exception("User Not Found")
            if (user.type != "seller") throw Exception("User is not a seller")
            val query = database.from(DBOrderTable).leftJoin(DBProductTable, on = DBProductTable.productId eq DBOrderTable.productId).select().where({ (DBOrderTable.productId eq DBProductTable.productId) and (DBProductTable.seller like user.username) and (DBOrderTable.orderId eq orderId) })
            for (row in query) {
                return OrderSeller(row[DBOrderTable.orderId]!!, row[DBOrderTable.productId]!!, row[DBOrderTable.quantity]!!, row[DBOrderTable.paid]!!, row[DBOrderTable.amount]!!, row[DBOrderTable.status]!!, row[DBOrderTable.createdAt]!!)
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun approveOrder(uid: String, orderId: Int): Boolean? {
        try {
            val user = getUserByUid(uid) ?: throw Exception("User Not Found")
            if (user.type != "seller") throw Exception("User is not a seller")
            database.useTransaction() {
                val query = database.from(DBOrderTable).leftJoin(DBProductTable, on = DBProductTable.productId eq DBOrderTable.productId).select().where({ (DBOrderTable.productId eq DBProductTable.productId) and (DBProductTable.seller like user.username) and (DBOrderTable.orderId eq orderId) })
                if (query.totalRecords == 0) throw Exception("Order Not Found")
                for (row in query) {
                    if (row[DBOrderTable.status]!! != "waiting") throw Exception("Order is not waiting")
                    database.update(DBOrderTable) {
                        set(DBOrderTable.status, "approved")
                        where {
                            (DBOrderTable.orderId eq orderId)
                        }
                    }
                    val holdQ = database.from(DBWalletTable).select().where({ DBWalletTable.uid like row[DBOrderTable.buyerUid]!! })
                    if (holdQ.totalRecords == 0) throw Exception("User Not Found")
                    for (hold in holdQ) {
                        database.update(DBWalletTable) {
                            set(DBWalletTable.hold, hold[DBWalletTable.hold]!! - row[DBOrderTable.amount]!! * row[DBOrderTable.quantity]!!)
                            where {
                                (DBWalletTable.uid like row[DBOrderTable.buyerUid]!!)
                            }
                        }
                        database.update(DBWalletTable) {
                            set(DBWalletTable.balance, hold[DBWalletTable.balance]!! + row[DBOrderTable.amount]!! * row[DBOrderTable.quantity]!!)
                            where {
                                DBWalletTable.uid like uid
                            }
                        }
                        break
                    }
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun sellerCancelOrder(uid: String, orderId: Int): Boolean? {
        try {
            val user = getUserByUid(uid) ?: throw Exception("User Not Found")
            if (user.type != "seller") throw Exception("User is not a seller")
            database.useTransaction() {
                val query = database.from(DBOrderTable).leftJoin(DBProductTable, on = DBProductTable.productId eq DBOrderTable.productId).select().where({ (DBOrderTable.productId eq DBProductTable.productId) and (DBProductTable.seller like user.username) and (DBOrderTable.orderId eq orderId) })
                if (query.totalRecords == 0) throw Exception("Order Not Found")
                for (row in query) {
                    if (row[DBOrderTable.status]!! == "delivered") throw Exception("Order has already been delivered")
                    else if (row[DBOrderTable.status]!! == "cancelled") throw Exception("Order has already been cancelled")
                    if (row[DBOrderTable.status]!! == "waiting") {
                        database.update(DBWalletTable) {
                            set(DBWalletTable.hold, DBWalletTable.hold - row[DBOrderTable.amount]!! * row[DBOrderTable.quantity]!!)
                            set(DBWalletTable.balance, DBWalletTable.balance + row[DBOrderTable.amount]!! * row[DBOrderTable.quantity]!!)
                            where {
                                DBWalletTable.uid like row[DBOrderTable.buyerUid]!!
                            }
                        }
                    } else {
                        database.update(DBWalletTable) {
                            set(DBWalletTable.balance, DBWalletTable.balance - row[DBOrderTable.amount]!! * row[DBOrderTable.quantity]!!)
                            where {
                                (DBWalletTable.uid like uid)
                            }
                        }
                        database.update(DBWalletTable) {
                            set(DBWalletTable.balance, DBWalletTable.balance + row[DBOrderTable.amount]!! * row[DBOrderTable.quantity]!!)
                            where {
                                DBWalletTable.uid like row[DBOrderTable.buyerUid]!!
                            }
                        }
                    }
                    database.update(DBOrderTable) {
                        set(DBOrderTable.status, "cancelled")
                        where {
                            (DBOrderTable.orderId eq orderId)
                        }
                    }
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun clearCart(uid: String): Boolean? {
        return try {
            val query = database.delete(DBCartTable) { DBCartTable.uid like uid }
            query != 0
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getTransactions(uid: String, filter: Boolean?): ArrayList<Transaction>? {
        try {
            val user = getUserByUid(uid) ?: throw Exception("User Not Found")
            val query = if (user.type == "admin") {
                if (filter != null) {
                    database.from(DBTransactionTable).select().where({ DBTransactionTable.approved eq filter })
                } else {
                    database.from(DBTransactionTable).select()
                }
            } else {
                if (filter != null) {
                    database.from(DBTransactionTable).select().where({ (DBTransactionTable.uid like uid) and (DBTransactionTable.approved eq filter) })
                } else {
                    database.from(DBTransactionTable).select().where({ DBTransactionTable.uid like uid })
                }
            }
            val transactions = ArrayList<Transaction>()
            for (row in query) {
                transactions.add(Transaction(row[DBTransactionTable.transactionId]!!, row[DBTransactionTable.uid]!!, row[DBTransactionTable.approved]!!))
            }
            return transactions
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getTransaction(uid: String, transactionId: String): Transaction? {
        try {
            val user = getUserByUid(uid) ?: throw Exception("User Not Found")
            val query = if (user.type == "admin") {
                database.from(DBTransactionTable).select().where({ DBTransactionTable.transactionId like transactionId })
            } else {
                database.from(DBTransactionTable).select().where({ (DBTransactionTable.transactionId like transactionId) and (DBTransactionTable.uid like uid) })
            }
            if (query.totalRecords == 0) throw Exception("Transaction Not Found")
            for (row in query) {
                return Transaction(row[DBTransactionTable.transactionId]!!, row[DBTransactionTable.uid]!!, row[DBTransactionTable.approved]!!)
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun approveTransaction(transactionId: String, amount: Int): Boolean {
        try {
            val query = database.from(DBTransactionTable).select().where({ DBTransactionTable.transactionId like transactionId })
            if (query.totalRecords == 0) throw Exception("Transaction Not Found")
            for (row in query) {
                if (row[DBTransactionTable.approved]!!) throw Exception("Transaction has already been approved")
                database.useTransaction {
                    database.update(DBTransactionTable) {
                        set(DBTransactionTable.approved, true)
                        where {
                            (DBTransactionTable.transactionId like transactionId)
                        }
                    }

                    database.update(DBWalletTable) {
                        set(DBWalletTable.balance, DBWalletTable.balance + amount)
                        where {
                            (DBWalletTable.uid like row[DBTransactionTable.uid]!!)
                        }
                    }
                }
                return true
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun getMe(uid: String): Me? {
        return try {
            val user = getUserByUid(uid) ?: throw Exception("User Not Found")
            val wallet = getWallet(uid) ?: throw Exception("Wallet Not Found")
            Me(user.uid, user.type, user.email, user.firstName, user.lastName, user.username, wallet)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
