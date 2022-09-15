package main.kotlin.com.camo.auth
/* ktlint-disable no-wildcard-imports */
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import main.kotlin.com.camo.database.DatabaseManager

class JwtConfig(jwtSecret: String) {
    val db = DatabaseManager()
    companion object Constants {
        // jwt config
        private const val jwtIssuer = "com.camo"
        private const val jwtRealm = "com.camo.fcs"

        // claims
        private const val CLAIM_USERNAME = "username"
        private const val CLAIM_PASS = "password"
    }

    private val jwtAlgorithm = Algorithm.HMAC512(jwtSecret)
    private val jwtVerifier: JWTVerifier = JWT.require(jwtAlgorithm).withIssuer(jwtIssuer).build()

    /** Generate a token for a authenticated user */
    fun generateToken(user: JwtUser): String =
        JWT.create()
            .withSubject("Authentication")
            .withIssuer(jwtIssuer)
            .withClaim(CLAIM_USERNAME, user.username)
            .withClaim(CLAIM_PASS, user.passHash)
            .sign(jwtAlgorithm)

    /** Configure the jwt ktor authentication feature */
    fun configureKtorFeature(config: JWTAuthenticationProvider.Configuration) =
        with(config) {
            verifier(jwtVerifier)
            realm = jwtRealm
            validate { it ->
                val username = it.payload.getClaim(CLAIM_USERNAME).asString()
                val passHash = it.payload.getClaim(CLAIM_PASS).asString()
                if (username != null && passHash != null) {
                    if (db.isAuthValid(username, passHash)) JwtUser(username, passHash) else null
                } else {
                    null
                }
            }
        }

    /** POKO, that contains information of an authenticated user that is authenticated via jwt */
    data class JwtUser(val username: String, val passHash: String) : Principal
}
