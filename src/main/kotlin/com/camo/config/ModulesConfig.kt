package main.kotlin.com.camo.config
/* ktlint-disable no-wildcard-imports */

import main.kotlin.com.camo.controllers.Controller
import main.kotlin.com.camo.database.DatabaseManager
import main.kotlin.com.camo.repos.*
import main.kotlin.com.camo.services.*
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

object ModulesConfig {
    private val mainModule =
        Kodein.Module("MAIN") {
            bind() from
                singleton {
                    Controller(
                        instance(),
                        instance(),
                        instance(),
                        instance(),
                        instance()
                    )
                }
            bind() from singleton { UserService(instance()) }
            bind() from singleton { MySQLUserRepo(instance()) }
            bind() from singleton { AuthService(instance()) }
            bind() from singleton { MySQLAuthRepo(instance()) }
            bind() from singleton { AdminService(instance()) }
            bind() from singleton { MySQLAdminRepo(instance()) }
            bind() from singleton { BuyerService(instance()) }
            bind() from singleton { MySQLBuyerRepo(instance()) }
            bind() from singleton { SellerService(instance()) }
            bind() from singleton { MySQLSellerRepo(instance()) }

            bind() from singleton { DatabaseManager() }
        }
    internal val kodein = Kodein { import(mainModule) }
}
