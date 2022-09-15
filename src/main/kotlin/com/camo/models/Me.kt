package main.kotlin.com.camo.models

data class Me(
    val uid: String,
    val type: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val wallet: Wallet
)
