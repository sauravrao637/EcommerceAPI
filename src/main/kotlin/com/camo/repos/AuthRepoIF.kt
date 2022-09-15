package main.kotlin.com.camo.repos
/* ktlint-disable no-wildcard-imports */
import main.kotlin.com.camo.database.DBUserEntity

interface AuthRepoIF {
    fun getUser(username: String, passwordHash: String): DBUserEntity?
    fun signUp(
        username: String,
        password: String,
        type: String,
        firstName: String,
        lastName: String,
        email: String
    ): String?

    fun signUpAdmin(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String
    ): String?
}
