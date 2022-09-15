package main.kotlin.com.camo.models

data class Product(
    val productId: Int,
    val category: String,
    val name: String,
    val inventory: Int,
    val catalog: ByteArray,
    val approved: Boolean,
    val image1: ByteArray,
    val image2: ByteArray,
    val details: String,
    val seller: String,
    val price: Int
)
