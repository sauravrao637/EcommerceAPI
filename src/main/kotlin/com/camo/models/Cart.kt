package main.kotlin.com.camo.models

data class Cart(
    val items: List<CartItem>,
    val total: Int
)

data class CartItem(
    val uid: String,
    val productId: Int,
    val quantity: Int
)
