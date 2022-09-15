package main.kotlin.com.camo.repos
/* ktlint-disable no-wildcard-imports */
import main.kotlin.com.camo.database.DBUserEntity
import main.kotlin.com.camo.database.DatabaseManager

class MySQLAuthRepo(private val db: DatabaseManager) : AuthRepoIF {
    override fun getUser(username: String, passwordHash: String): DBUserEntity? =
        db.getUser(username, passwordHash)

    override fun signUp(username: String, password: String, type: String, firstName: String, lastName: String, email: String): String? = db.signup(username, password, type, firstName, lastName, email)

    override fun signUpAdmin(username: String, password: String, firstName: String, lastName: String, email: String): String? = db.signupAdmin(username, password, firstName, lastName, email)
}
