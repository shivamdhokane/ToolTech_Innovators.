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
    
    private var isFromCart = false
    private var cartTotalAmount = 0.0
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

        isFromCart = intent.getBooleanExtra("isFromCart", false)
        cartTotalAmount = intent.getDoubleExtra("totalAmount", 0.0)
        
        productId = intent.getIntExtra("productId", -1)
        productName = intent.getStringExtra("productName") ?: "Product"
        productPrice = intent.getStringExtra("productPrice") ?: "₹100"
        productImageResId = intent.getIntExtra("productImageResId", 0)
        customerName = intent.getStringExtra("customerName") ?: ""
        customerMobile = intent.getStringExtra("customerMobile") ?: ""
        customerAddress = intent.getStringExtra("customerAddress") ?: ""
        
        if (isFromCart) {
            amount = String.format("%.2f", cartTotalAmount)
            binding.paymentProductName.text = "Cart Items"
            binding.paymentProductPrice.text = "₹${String.format("%.2f", cartTotalAmount)}"
        } else {
            amount = productPrice.replace("₹", "").replace(",", "")
            binding.paymentProductName.text = productName
            binding.paymentProductPrice.text = productPrice
        }

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
            payUsingUpi(null, "UPI")
        }
        
        // Credit/Debit Card (optional - can show a message or implement card payment)
        binding.cardOption.setOnClickListener {
            Toast.makeText(this, "Card payment coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun payUsingUpi(packageName: String?, appName: String) {
        // Format amount for UPI (remove any formatting, use plain number)
        val cleanAmount = amount.replace(",", "").trim()
        
        // Create UPI payment URI
        // Note: Replace 'yourmerchant@upi' with your actual UPI ID
        val upiId = "yourmerchant@upi" // Replace with actual merchant UPI ID
        val merchantName = "ToolTech%20Innovators"
        val transactionNote = "Payment%20for%20order"
        
        val uriString = "upi://pay?pa=$upiId&pn=$merchantName&am=$cleanAmount&cu=INR&tn=$transactionNote"
        val uri = Uri.parse(uriString)

        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (packageName != null) {
            intent.setPackage(packageName)
        }

        try {
            startActivity(intent)
            // Save order to Firebase with specific payment method name
            // Note: In a real app, you'd verify payment completion via a callback/webhook
            saveOrderToFirebase(appName)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "$appName is not installed", Toast.LENGTH_SHORT).show()
            if (packageName != null) {
                // Open Play Store if app missing
                try {
                    val playStoreUri = Uri.parse("market://details?id=$packageName")
                    val playIntent = Intent(Intent.ACTION_VIEW, playStoreUri)
                    startActivity(playIntent)
                } catch (ex: Exception) {
                    // If Play Store app is not available, try browser
                    try {
                        val browserUri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                        val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
                        startActivity(browserIntent)
                    } catch (ex2: Exception) {
                        Toast.makeText(this, "Unable to open Play Store", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // For generic UPI, try opening any UPI app
                try {
                    val genericUpiIntent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(genericUpiIntent)
                } catch (ex: Exception) {
                    Toast.makeText(this, "No UPI app found. Please install a UPI payment app.", Toast.LENGTH_LONG).show()
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

        if (isFromCart) {
            saveCartOrderToFirebase(paymentMethod)
        } else {
            saveSingleProductOrderToFirebase(paymentMethod)
        }
    }

    private fun saveSingleProductOrderToFirebase(paymentMethod: String) {
        val userId = auth.currentUser?.uid ?: return

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

        // Save order to Firebase in both user-specific location and allOrders for admin
        val userOrderPath = database.child("orders").child(userId).child(orderId)
        val allOrdersPath = database.child("allOrders").child(orderId)
        
        // Save to user-specific location
        userOrderPath.setValue(orderData)
            .addOnSuccessListener {
                // Also save to allOrders for admin access
                allOrdersPath.setValue(orderData)
                    .addOnSuccessListener {
                        android.util.Log.d("PaymentActivity", "Order saved successfully!")
                        Toast.makeText(this, "Order placed! Payment initiated.", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("PaymentActivity", "Error saving to allOrders: ${e.message}")
                        // Still show success since user order was saved
                        Toast.makeText(this, "Order placed! Payment initiated.", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("PaymentActivity", "Error saving order: ${e.message}")
                Toast.makeText(this, "Error placing order: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveCartOrderToFirebase(paymentMethod: String) {
        val userId = auth.currentUser?.uid ?: return

        // Load cart items from Firebase
        val cartRef = database.child("carts").child(userId)
        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists() || !snapshot.hasChildren()) {
                    Toast.makeText(this@PaymentActivity, "Your cart is empty", Toast.LENGTH_SHORT).show()
                    return
                }

                val cartItems = mutableListOf<CartItem>()
                for (itemSnapshot in snapshot.children) {
                    val cartItem = itemSnapshot.getValue(CartItem::class.java)
                    cartItem?.let {
                        val itemWithId = it.copy(cartItemId = itemSnapshot.key ?: "")
                        cartItems.add(itemWithId)
                    }
                }

                if (cartItems.isEmpty()) {
                    Toast.makeText(this@PaymentActivity, "Your cart is empty", Toast.LENGTH_SHORT).show()
                    return
                }

                val orderId = database.child("orders").push().key
                if (orderId == null) {
                    Toast.makeText(this@PaymentActivity, "Error creating order. Please try again.", Toast.LENGTH_SHORT).show()
                    return
                }

                val orderDate = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                
                // Convert cart items to map format for Firebase
                val itemsList = cartItems.mapIndexed { index, item ->
                    mapOf(
                        "productId" to item.productId,
                        "productName" to item.productName,
                        "productPrice" to item.productPrice,
                        "productImageResId" to item.productImageResId,
                        "quantity" to item.quantity,
                        "cartItemId" to item.cartItemId
                    )
                }
                
                // Create order data
                val orderData = mapOf(
                    "orderId" to orderId,
                    "userId" to userId,
                    "items" to itemsList,
                    "totalAmount" to "₹${String.format("%.2f", cartTotalAmount)}",
                    "orderDate" to orderDate,
                    "status" to "Pending",
                    "customerName" to customerName,
                    "customerMobile" to customerMobile,
                    "customerAddress" to customerAddress,
                    "paymentMethod" to paymentMethod
                )

                // Save order to Firebase in both user-specific location and allOrders for admin
                val userOrderPath = database.child("orders").child(userId).child(orderId)
                val allOrdersPath = database.child("allOrders").child(orderId)
                
                // Save to user-specific location
                userOrderPath.setValue(orderData)
                    .addOnSuccessListener {
                        // Also save to allOrders for admin access
                        allOrdersPath.setValue(orderData)
                            .addOnSuccessListener {
                                // Clear cart after successful order
                                cartRef.removeValue()
                                android.util.Log.d("PaymentActivity", "Cart order saved successfully!")
                                Toast.makeText(this@PaymentActivity, "Order placed! Payment initiated.", Toast.LENGTH_LONG).show()
                            }
                            .addOnFailureListener { e ->
                                android.util.Log.e("PaymentActivity", "Error saving to allOrders: ${e.message}")
                                // Still clear cart and show success since user order was saved
                                cartRef.removeValue()
                                Toast.makeText(this@PaymentActivity, "Order placed! Payment initiated.", Toast.LENGTH_LONG).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("PaymentActivity", "Error saving cart order: ${e.message}")
                        Toast.makeText(this@PaymentActivity, "Error placing order: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("PaymentActivity", "Error loading cart: ${error.message}")
                Toast.makeText(this@PaymentActivity, "Error loading cart: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
