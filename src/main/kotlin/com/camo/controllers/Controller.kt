package main.kotlin.com.camo.controllers
/* ktlint-disable no-wildcard-imports */
import com.camo.auth.*
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.*
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import main.kotlin.com.camo.Utils
import main.kotlin.com.camo.auth.JwtConfig
import main.kotlin.com.camo.auth.SignUpBody
import main.kotlin.com.camo.auth.SignUpBodyAdmin
import main.kotlin.com.camo.services.*
class Controller(
    private val userService: UserService,
    private val authService: AuthService,
    private val adminService: AdminService,
    private val buyerService: BuyerService,
    private val sellerService: SellerService
) {
    suspend fun login(context: ApplicationCall, jwtConfig: JwtConfig) {
        // println(context.receive<String>())
        context.receive<LoginBody>().apply {
            try {
                authService.authenticate(username, passHash)
                context.respond(jwtConfig.generateToken(JwtConfig.JwtUser(username, passHash)))
            } catch (e: Exception) {
                context.respond(HttpStatusCode.Unauthorized, "Wrong Credentials")
            }
        }
    }

    suspend fun signup(context: ApplicationCall) {
        try {
            context.receive<SignUpBody>().apply {
                username = username.trim()
                type = type.trim().lowercase()
                email = email.trim().lowercase()
                firstName = firstName.trim()
                Utils.validateSignUp(username, password, type, email, firstName)
                val uid = authService.signUp(username, password, type, firstName, lastName, email)
                context.respond(HttpStatusCode.Created, uid)
            }
        } catch (e: Exception) {
            context.respond(HttpStatusCode.Conflict, e.message ?: "User Could Not Be Created")
        }
    }

    suspend fun productProposal(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            var category: String? = null
            var name: String? = null
            var inventory: Int? = null
            var details: String? = null
            var image1: ByteArray? = null
            var image2: ByteArray? = null
            var catalog: ByteArray? = null
            var price: Int? = null
            val multipartData = context.receiveMultipart()
            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "category" -> {
                                category = part.value
                            }
                            "name" -> {
                                name = part.value
                            }
                            "inventory" -> {
                                inventory = part.value.toInt()
                            }
                            "details" -> {
                                details = part.value
                            }
                            "price" -> {
                                price = part.value.toInt()
                            }
                        }
                    }
                    is PartData.FileItem -> {
                        println(part.contentType)
                        when (part.name) {
                            "image1" -> {
                                if (part.contentType?.toString() == "image/png") {
                                    image1 = part.streamProvider().readBytes()
                                    var sz = image1?.size?.div(1024) // returns in mb
                                    if (sz == null || sz > 2048) {
                                        throw Exception("Image1 is too large")
                                    }
                                }
                            }
                            "image2" -> {
                                if (part.contentType?.toString() == "image/png") {
                                    image2 = part.streamProvider().readBytes()
                                    var sz = image2?.size?.div(1024) // returns in mb
                                    if (sz == null || sz > 2048) {
                                        throw Exception("Image2 is too large")
                                    }
                                }
                            }
                            "catalog" -> {
                                if (part.contentType?.toString() == "application/pdf") {
                                    catalog = part.streamProvider().readBytes()
                                    var sz = catalog?.size?.div(1024 * 1024) // returns in mb
                                    if (sz == null || sz > 2048) {
                                        throw Exception("Catalog is too large")
                                    }
                                }
                            }
                        }
                        // fileName = part.originalFileName as String
                        // var fileBytes = part.streamProvider().readBytes()
                        // File("uploads/$fileName").writeBytes(fileBytes)
                    }
                    else -> {}
                }
            }
            if (image1 == null ||
                image2 == null ||
                catalog == null ||
                price == null ||
                category == null ||
                name == null ||
                inventory == null ||
                details == null
            ) {
                throw Exception("One or more invalid feilds")
            } else {
                val productId =
                    sellerService.proposeProduct(
                        category!!,
                        name!!,
                        inventory!!,
                        details!!,
                        image1!!,
                        image2!!,
                        catalog!!,
                        jwtUser.username,
                        price!!
                    )
                if (productId == null) {
                    throw Exception("Null ProductId")
                } else context.respond(HttpStatusCode.OK, productId)
            }
        } catch (e: Exception) {
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something went wrong")
        }
    }

    /*
        true for approved
        false for unapproved
        null otherwise
    */
    suspend fun getProposals(
        context: ApplicationCall,
        jwtUser: JwtConfig.JwtUser,
        filter: Boolean?
    ) {
        try {
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")

            if (user.type == "admin") {
                val proposals = adminService.getProposals(filter) ?: throw Exception("Huehuehuehue")
                for (proposal in proposals) {}
                context.respond(HttpStatusCode.OK, proposals)
            } else throw Exception("U ain't no admin")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Oops camo's bad")
        }
    }
    suspend fun approveProposal(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val productId =
                context.parameters["product_id"]?.toInt() ?: throw Exception("Pass product_id")
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")

            if (user.type == "admin") {
                val result = adminService.approveProposal(productId)
                if (result) context.respond(HttpStatusCode.OK, result)
                else context.respond(HttpStatusCode.Conflict, "Could not approve")
            } else throw Exception("U ain't no admin")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Oops camo's bad")
        }
    }

    suspend fun removeProduct(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val productId =
                context.parameters["product_id"]?.toInt() ?: throw Exception("Pass product_id")
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")

            if (user.type == "admin") {
                val result = adminService.removeProduct(productId)
                if (result) context.respond(HttpStatusCode.OK, result)
                else context.respond(HttpStatusCode.Conflict, "Could not remove")
            } else throw Exception("U ain't no admin")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Oops camo's bad")
        }
    }

    suspend fun getProducts(context: ApplicationCall) {
        try {
            val sortId = context.parameters["sort_id"]?.toInt()
            val category = context.parameters["category"]
            val result = userService.getProducts(sortId, category)
            if (result != null) context.respond(HttpStatusCode.OK, result)
            else context.respond(HttpStatusCode.Conflict, "Could not get products")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Oops camo's bad")
        }
    }

    suspend fun addToCart(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val productId =
                context.parameters["product_id"]?.toInt() ?: throw Exception("Pass product_id")
            var quantity = context.parameters["quantity"]?.toInt() ?: 1
            if (quantity < 1) quantity = 1
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")
            val res = userService.addToCart(user.uid, productId, quantity)
            if (res == true) context.respond(HttpStatusCode.OK, res)
            else context.respond(HttpStatusCode.Conflict, "Could not add to cart")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something Went Wrong")
        }
    }
    suspend fun removeFromCart(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val productId =
                context.parameters["product_id"]?.toInt() ?: throw Exception("Pass product_id")
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")
            val res = userService.removeFromCart(user.uid, productId)
            if (res == true) context.respond(HttpStatusCode.OK, res)
            else context.respond(HttpStatusCode.Conflict, "Could not remove from cart")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something Went Wrong")
        }
    }

    suspend fun getCart(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")
            val res = userService.getCart(user.uid)
            if (res != null) context.respond(HttpStatusCode.OK, res)
            else context.respond(HttpStatusCode.Conflict, "Could not get cart")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something Went Wrong")
        }
    }

    suspend fun clearCart(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")
            val res = userService.clearCart(user.uid)
            if (res != null) context.respond(HttpStatusCode.OK, "Cart Cleared")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something Went Wrong")
        }
    }

    suspend fun checkout(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")
            val res = userService.checkout(user.uid)
            if (res != null) context.respond(HttpStatusCode.OK, res)
            else context.respond(HttpStatusCode.Conflict, "Could not checkout")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something Went Wrong")
        }
    }

    suspend fun getOrders(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")
            val res = userService.getOrders(user.uid)
            if (res != null) context.respond(HttpStatusCode.OK, res)
            else context.respond(HttpStatusCode.Conflict, "Could not get orders")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something Went Wrong")
        }
    }

    suspend fun getWallet(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")
            val res = userService.getWallet(user.uid)
            if (res != null) context.respond(HttpStatusCode.OK, res)
            else context.respond(HttpStatusCode.Conflict, "Could not get Wallet")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something Went Wrong")
        }
    }

    suspend fun addToWallet(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val transactionId =
                context.parameters["transaction_id"]
                    ?: throw Exception("transaction_id required")
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")
            val res = userService.addToWallet(user.uid, transactionId)
            if (res != null) context.respond(HttpStatusCode.OK, res)
            else context.respond(HttpStatusCode.Conflict, "Could not add to Wallet")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something Went Wrong")
        }
    }

    suspend fun withdrawFromWallet(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val amount = context.parameters["amount"]?.toInt() ?: throw Exception("amount required")
            if (amount <= 0) throw Exception("Amount must be greater than 0")
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")
            val res = userService.withdrawFromWallet(user.uid, amount)
            if (res != null) context.respond(HttpStatusCode.OK, res)
            else context.respond(HttpStatusCode.Conflict, "Could not get Wallet")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something Went Wrong")
        }
    }

    suspend fun getWithdrawals(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")
            val res = userService.getWithdrawals(user.uid)
            if (res != null) context.respond(HttpStatusCode.OK, res)
            else context.respond(HttpStatusCode.Conflict, "Could not get withdrawals")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something Went Wrong")
        }
    }

    suspend fun getOrdersForSeller(
        context: ApplicationCall,
        jwtUser: JwtConfig.JwtUser,
        filter: Boolean?
    ) {
        try {
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")
            if (user.type != "seller") throw Exception("Only sellers can access this endpoint")
            val res = sellerService.getOrdersForSeller(user.uid, filter)
            if (res != null) context.respond(HttpStatusCode.OK, res)
            else context.respond(HttpStatusCode.Conflict, "Could not get orders")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something Went Wrong")
        }
    }

    suspend fun getOrderForSeller(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val orderId =
                context.parameters["order_id"]?.toInt() ?: throw Exception("order_id required")
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")
            if (user.type != "seller") throw Exception("Only sellers can access this endpoint")
            val res = sellerService.getOrderForSeller(user.uid, orderId)
            if (res != null) context.respond(HttpStatusCode.OK, res)
            else context.respond(HttpStatusCode.Conflict, "Could not get order")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something Went Wrong")
        }
    }
    suspend fun approveOrder(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val orderId =
                context.parameters["order_id"]?.toInt() ?: throw Exception("order_id required")
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")
            if (user.type != "seller") throw Exception("Only sellers can access this endpoint")
            val res = sellerService.approveOrder(user.uid, orderId)
            if (res != null) context.respond(HttpStatusCode.OK, res)
            else context.respond(HttpStatusCode.Conflict, "Could not approve order")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something Went Wrong")
        }
    }
    suspend fun sellerCancelOrder(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val orderId =
                context.parameters["order_id"]?.toInt() ?: throw Exception("order_id required")
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")
            if (user.type != "seller") throw Exception("Only sellers can access this endpoint")
            val res = sellerService.sellerCancelOrder(user.uid, orderId)
            if (res != null) context.respond(HttpStatusCode.OK, res)
            else context.respond(HttpStatusCode.Conflict, "Could not cancel order")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something Went Wrong")
        }
    }

    suspend fun getTransactions(
        context: ApplicationCall,
        jwtUser: JwtConfig.JwtUser,
        filter: Boolean?
    ) {
        try {
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")
            val res = userService.getTransactions(user.uid, filter)
            if (res != null) context.respond(HttpStatusCode.OK, res)
            else context.respond(HttpStatusCode.Conflict, "Could not get transactions")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something Went Wrong")
        }
    }

    suspend fun getTransaction(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val transactionId =
                context.parameters["transaction_id"]
                    ?: throw Exception("transaction_id required")
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")
            val res = userService.getTransaction(user.uid, transactionId)
            if (res != null) context.respond(HttpStatusCode.OK, res)
            else context.respond(HttpStatusCode.Conflict, "Could not get transaction")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something Went Wrong")
        }
    }

    suspend fun approveTransaction(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val transactionId =
                context.parameters["transaction_id"]
                    ?: throw Exception("transaction_id required")
            val amount = context.parameters["amount"]?.toInt() ?: throw Exception("amount required")
            if (amount <= 0) throw Exception("Amount must be greater than 0")
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")
            if (user.type != "admin") throw Exception("Only admins can access this endpoint")
            val res = adminService.approveTransaction(transactionId, amount)
            if (res) context.respond(HttpStatusCode.OK, res)
            else context.respond(HttpStatusCode.Conflict, "Could not approve transaction")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something Went Wrong")
        }
    }

    suspend fun getMe(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val user =
                userService.getUser(jwtUser.username, jwtUser.passHash)
                    ?: throw Exception("Wait a minute, who r u??")
            val res = userService.getMe(user.uid)
            if (res != null) context.respond(HttpStatusCode.OK, res)
            else context.respond(HttpStatusCode.Conflict, "Could not Account")
        } catch (e: Exception) {
            e.printStackTrace()
            context.respond(HttpStatusCode.Conflict, e.message ?: "Something Went Wrong")
        }
    }

    suspend fun signupAdmin(context: ApplicationCall, jwtUser: JwtConfig.JwtUser) {
        try {
            val user = userService.getUser(jwtUser.username, jwtUser.passHash) ?: throw Exception("Wait a minute, who r u??")
            if (user.type != "admin") throw Exception("Only admins can access this endpoint")
            val form = context.receive<SignUpBodyAdmin>()
            form.username = form.username.trim()
            form.email = form.email.trim().lowercase()
            form.firstName = form.firstName.trim()
            form.lastName = form.lastName.trim()
            Utils.validateSignUpAdmin(form.username, form.password, form.email, form.firstName)
            val uid = authService.signUpAdmin(form.username, form.password, form.firstName, form.lastName, form.email)
            context.respond(HttpStatusCode.Created, uid)
        } catch (e: Exception) {
            context.respond(HttpStatusCode.Conflict, e.message ?: "User Could Not Be Created")
        }
    }
}
