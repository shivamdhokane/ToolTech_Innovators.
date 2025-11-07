package com.example.tooltechinnovators

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tooltechinnovators.databinding.ActivityPaymentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var amount = "100" // Example — you can pass this dynamically
    
    private var productId: Int = -1
    private var productName: String = ""
    private var productPrice: String = ""
    private var productImageResId: Int = 0
    private var customerName: String = ""
    private var customerMobile: String = ""
    private var customerAddress: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        productId = intent.getIntExtra("productId", -1)
        productName = intent.getStringExtra("productName") ?: "Product"
        productPrice = intent.getStringExtra("productPrice") ?: "₹100"
        productImageResId = intent.getIntExtra("productImageResId", 0)
        customerName = intent.getStringExtra("customerName") ?: ""
        customerMobile = intent.getStringExtra("customerMobile") ?: ""
        customerAddress = intent.getStringExtra("customerAddress") ?: ""
        
        amount = productPrice.replace("₹", "").replace(",", "")

        binding.paymentProductName.text = productName
        binding.paymentProductPrice.text = productPrice

        // Google Pay
        binding.gpayOption.setOnClickListener {
            payUsingUpi("com.google.android.apps.nbu.paisa.user", "Google Pay")
        }

        // PhonePe
        binding.phonepeOption.setOnClickListener {
            payUsingUpi("com.phonepe.app", "PhonePe")
        }

        // Paytm
        binding.paytmOption.setOnClickListener {
            payUsingUpi("net.one97.paytm", "Paytm")
        }

        // Generic UPI
        binding.upiOption.setOnClickListener {
            payUsingUpi(null, "UPI App")
        }
    }

    private fun payUsingUpi(packageName: String?, appName: String) {
        val uri = Uri.parse(
            "upi://pay?pa=yourmerchant@upi&pn=ToolTech%20Innovators&am=$amount&cu=INR&tn=Payment%20for%20$amount"
        )

        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (packageName != null) {
            intent.setPackage(packageName)
        }

        try {
            startActivity(intent)
            // Save order to Firebase after opening payment app
            // Note: In a real app, you'd verify payment completion via a callback/webhook
            saveOrderToFirebase("Online Payment")
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "$appName not installed", Toast.LENGTH_SHORT).show()
            if (packageName != null) {
                // Open Play Store if app missing
                try {
                    val playIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                    startActivity(playIntent)
                } catch (ex: Exception) {
                    Toast.makeText(this, "Unable to open Play Store", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun saveOrderToFirebase(paymentMethod: String) {
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

        // Convert to list format for Firebase
        val itemsList = listOf(cartItem)
        
        // Create order data
        val orderData = mapOf(
            "orderId" to orderId,
            "userId" to userId,
            "items" to itemsList.map { item ->
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
                android.util.Log.d("PaymentActivity", "Order saved successfully!")
                Toast.makeText(this, "Order placed! Payment initiated.", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                android.util.Log.e("PaymentActivity", "Error saving order: ${e.message}")
                Toast.makeText(this, "Error placing order: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
