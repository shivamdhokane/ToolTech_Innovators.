package com.example.tooltechinnovators

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tooltechinnovators.databinding.ActivityCheckoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val productId = intent.getIntExtra("productId", -1)
        val productName = intent.getStringExtra("productName")
        val productPrice = intent.getStringExtra("productPrice")
        val productImageResId = intent.getIntExtra("productImageResId", 0)

        binding.confirmOrderBtn.setOnClickListener {
            val name = binding.fullName.text.toString()
            val mobile = binding.mobileNumber.text.toString()
            val address = binding.address.text.toString()
            val selectedId = binding.paymentOptions.checkedRadioButtonId

            if (name.isEmpty() || mobile.isEmpty() || address.isEmpty() || selectedId == -1) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Find which payment option is selected
            val selectedButton = findViewById<RadioButton>(selectedId)
            val paymentMethod = selectedButton.text.toString()

            if (paymentMethod == "Cash on Delivery") {
                // Save order to Firebase
                saveOrderToFirebase(productId, productName ?: "", productPrice ?: "", productImageResId, name, mobile, address, paymentMethod)
            } else if (paymentMethod == "Online Payment") {
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("productId", productId)
                intent.putExtra("productName", productName)
                intent.putExtra("productPrice", productPrice)
                intent.putExtra("productImageResId", productImageResId)
                intent.putExtra("customerName", name)
                intent.putExtra("customerMobile", mobile)
                intent.putExtra("customerAddress", address)
                startActivity(intent)
            }
        }
    }

    private fun saveOrderToFirebase(
        productId: Int,
        productName: String,
        productPrice: String,
        productImageResId: Int,
        customerName: String,
        customerMobile: String,
        customerAddress: String,
        paymentMethod: String
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Please login to place order", Toast.LENGTH_SHORT).show()
            return
        }

        val orderId = database.child("orders").push().key
        if (orderId == null) {
            Toast.makeText(this, "Error creating order. Please try again.", Toast.LENGTH_SHORT).show()
            return
        }

        val orderDate = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        
        // Create cart item for the order
        val cartItem = CartItem(
            productId = productId,
            productName = productName,
            productPrice = productPrice,
            productImageResId = productImageResId,
            quantity = 1,
            cartItemId = ""
        )

        // Convert to map format for Firebase
        val itemsList = listOf(cartItem)
        
        // Create order data
        val orderData = mapOf(
            "orderId" to orderId,
            "userId" to userId,
            "items" to itemsList.mapIndexed { index, item ->
                mapOf(
                    "productId" to item.productId,
                    "productName" to item.productName,
                    "productPrice" to item.productPrice,
                    "productImageResId" to item.productImageResId,
                    "quantity" to item.quantity,
                    "cartItemId" to item.cartItemId
                )
            },
            "totalAmount" to productPrice,
            "orderDate" to orderDate,
            "status" to "Pending",
            "customerName" to customerName,
            "customerMobile" to customerMobile,
            "customerAddress" to customerAddress,
            "paymentMethod" to paymentMethod
        )

        // Save order to Firebase
        val orderPath = database.child("orders").child(userId).child(orderId)
        orderPath.setValue(orderData)
            .addOnSuccessListener {
                android.util.Log.d("CheckoutActivity", "Order saved successfully!")
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                android.util.Log.e("CheckoutActivity", "Error saving order: ${e.message}")
                Toast.makeText(this, "Error placing order: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
