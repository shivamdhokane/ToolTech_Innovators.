package com.example.tooltechinnovators

data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val imageResId: Int,
    val description: String,
    val brandModel: String,
    val powerWatts: String,
    val weightKg: String,
    val category: String = "General"
)
