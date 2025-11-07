package com.example.tooltechinnovators

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: String = "",
    val orderDate: String = "",
    val status: String = "Pending"
) {
    constructor() : this("", "", emptyList(), "", "", "Pending")
}





