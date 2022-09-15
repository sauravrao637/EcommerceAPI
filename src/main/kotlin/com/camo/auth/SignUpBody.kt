package main.kotlin.com.camo.auth
/* ktlint-disable no-wildcard-imports */
data class SignUpBody(
    var username: String,
    var password: String,
    var type: String,
    var firstName: String,
    var lastName: String,
    var email: String
)
