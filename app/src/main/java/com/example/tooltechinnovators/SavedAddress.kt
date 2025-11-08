package com.example.tooltechinnovators

data class SavedAddress(
    val addressId: String = "",
    val fullName: String = "",
    val mobileNumber: String = "",
    val address: String = "",
    val isDefault: Boolean = false
) {
    constructor() : this("", "", "", "", false)
}

