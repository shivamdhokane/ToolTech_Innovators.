package com.example.tooltechinnovators

data class CartItem(
    val productId: Int,
    val productName: String,
    val productPrice: String,
    val productImageResId: Int,
    val quantity: Int = 1,
    val cartItemId: String = ""
) {
    constructor() : this(0, "", "", 0, 1, "")
}





