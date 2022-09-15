package main.kotlin.com.camo.services
/* ktlint-disable no-wildcard-imports */
import main.kotlin.com.camo.auth.JwtConfig
import main.kotlin.com.camo.database.DBUserEntity
import main.kotlin.com.camo.repos.AuthRepoIF

class AuthService(private val repo: AuthRepoIF) {
    fun authenticate(email: String, passHash: String): DBUserEntity {
        val userFound = repo.getUser(email, passHash)
        userFound ?: throw Exception()
        return userFound
    }

    private fun generateJwtToken(jwtProvider: JwtConfig, user: JwtConfig.JwtUser): String? {
        return jwtProvider.generateToken(user)
    }

    fun signUp(
        username: String,
        password: String,
        type: String,
        firstName: String,
        lastName: String,
        email: String
    ): String {
        val uid = repo.signUp(username, password, type, firstName, lastName, email)
        uid ?: throw Exception()
        return uid
    }

    fun signUpAdmin(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String
    ): String {
        val uid = repo.signUpAdmin(username, password, firstName, lastName, email)
        uid ?: throw Exception()
        return uid
    }
}
