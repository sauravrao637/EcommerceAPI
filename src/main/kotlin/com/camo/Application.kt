package main.kotlin.com.camo
/* ktlint-disable no-wildcard-imports */
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import main.kotlin.com.camo.auth.JwtConfig
import main.kotlin.com.camo.config.ModulesConfig
import main.kotlin.com.camo.controllers.Controller
import org.kodein.di.generic.instance
import kotlin.io.println
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

public val jwtConfig = JwtConfig("FCS_JWT_SECRET")

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    // val db = DatabaseManager()
    install(CORS) {
        anyHost()
        allowCredentials = true
        header(HttpHeaders.ContentType)
        header(HttpHeaders.AccessControlAllowOrigin)
        header(HttpHeaders.Authorization)
    }
    val controller by ModulesConfig.kodein.instance<Controller>()

    install(Authentication) { jwt { jwtConfig.configureKtorFeature(this) } }

    install(ContentNegotiation) { gson { setPrettyPrinting() } }

    // install(CallLogging) {}

    routing {

        route("/api/v1") {
            get { call.respondText("Api for FCS Project") }
            route("login") {
                post {
                    println("routed login")
                    controller.login(this.context, jwtConfig)
                }
            }
            route("signup") { post { controller.signup(this.context) } }
            route("products") {
                get() {
                    println("sort_id: ${call.parameters["sort_id"]} and category: ${call.parameters["category"]}")
                    controller.getProducts(call)
                }
            }
        }

        authenticate {
            route("/api/v1") {
                route("valid") { get { call.respond(HttpStatusCode.OK) } }
                route("me") {
                    get {
                        val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                        controller.getMe(this.context, jwtUser)
                    }
                }

                route("wallet") {
                    get {
                        val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                        controller.getWallet(this.context, jwtUser)
                    }

                    post("add") {
                        val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                        controller.addToWallet(this.context, jwtUser)
                    }

                    post("withdraw") {
                        val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                        controller.withdrawFromWallet(this.context, jwtUser)
                    }

                    get("withdrawals") {
                        val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                        controller.getWithdrawals(this.context, jwtUser)
                    }
                }

                route("seller") {
                    post("proposal") {
                        val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                        controller.productProposal(this.context, jwtUser)
                    }
                    route("orders") {
                        get {
                            val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                            controller.getOrdersForSeller(this.context, jwtUser, null)
                        }
                        get("approved") {
                            val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                            controller.getOrdersForSeller(this.context, jwtUser, true)
                        }
                        get("unapproved") {
                            val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                            controller.getOrdersForSeller(this.context, jwtUser, false)
                        }
                        get("{order_id}") {
                            val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                            controller.getOrderForSeller(this.context, jwtUser)
                        }
                        post("approve") {
                            // req order_id
                            //
                            val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                            controller.approveOrder(this.context, jwtUser)
                        }
                        post("cancel") {
                            // req order_id
                            val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                            controller.sellerCancelOrder(this.context, jwtUser)
                        }
                    }
                }

                route("buyer") {
                    post("addtocart") {
                        val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                        controller.addToCart(this.context, jwtUser)
                    }
                    post("removefromcart") {
                        val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                        controller.removeFromCart(this.context, jwtUser)
                    }
                    post("checkout") {
                        val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                        controller.checkout(this.context, jwtUser)
                    }
                    get("cart") {
                        val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                        controller.getCart(this.context, jwtUser)
                    }
                    post("clearcart") {
                        val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                        controller.clearCart(this.context, jwtUser)
                    }
                    get("orders") {
                        val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                        controller.getOrders(this.context, jwtUser)
                    }
                }

                route("admin") {
                    route("proposals") {
                        get {
                            val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                            controller.getProposals(this.context, jwtUser, null)
                        }
                        get("approved") {
                            val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                            controller.getProposals(this.context, jwtUser, true)
                        }
                        get("unapproved") {
                            val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                            controller.getProposals(this.context, jwtUser, false)
                        }
                    }
                    route("transactions") {
                        get {
                            val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                            controller.getTransactions(this.context, jwtUser, null)
                        }
                        get("approved") {
                            val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                            controller.getTransactions(this.context, jwtUser, true)
                        }
                        get("unapproved") {
                            val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                            controller.getTransactions(this.context, jwtUser, false)
                        }
                        post("approve") {
                            val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                            controller.approveTransaction(this.context, jwtUser)
                        }

                        get("{transaction_id}") {
                            val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                            controller.getTransaction(this.context, jwtUser)
                        }
                    }
                    route("product") {
                        post("approve") {
                            val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                            controller.approveProposal(this.context, jwtUser)
                        }
                        post("remove") {
                            val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                            controller.removeProduct(this.context, jwtUser)
                        }
                    }
                    route("signup") {
                        post {
                            val jwtUser = call.authentication.principal as JwtConfig.JwtUser
                            controller.signupAdmin(this.context, jwtUser)
                        }
                    }
                }
                route("debug") {}
            }
        }
    }
}
