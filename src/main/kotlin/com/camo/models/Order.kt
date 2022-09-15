package main.kotlin.com.camo.models

import java.time.LocalDateTime

data class Order(
    val orderId: Int,
    val buyerUid: String,
    val productId: Int,
    val quantity: Int,
    val paid: Boolean,
    val amount: Int,
    val status: String,
    val createdAt: LocalDateTime
)
