package com.example.tooltechinnovators

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tooltechinnovators.databinding.ActivityCheckoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var savedAddressAdapter: SavedAddressAdapter

    private var isFromCart = false
    private var cartTotalAmount = 0.0
    private val savedAddresses = mutableListOf<SavedAddress>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        isFromCart = intent.getBooleanExtra("isFromCart", false)
        cartTotalAmount = intent.getDoubleExtra("totalAmount", 0.0)

        val productId = intent.getIntExtra("productId", -1)
        val productName = intent.getStringExtra("productName")
        val productPrice = intent.getStringExtra("productPrice")
        val productImageResId = intent.getIntExtra("productImageResId", 0)

        setupSavedAddressesRecyclerView()
        loadSavedAddressesAndDetails()

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

            // Save address and personal details if checkbox is checked
            if (binding.saveAddressCheckbox.isChecked) {
                saveAddressAndPersonalDetails(name, mobile, address)
            }

            if (isFromCart) {
                // Handle cart checkout
                if (paymentMethod == "Cash on Delivery") {
                    saveCartOrderToFirebase(name, mobile, address, paymentMethod)
                } else if (paymentMethod == "Online Payment") {
                    val intent = Intent(this, PaymentActivity::class.java)
                    intent.putExtra("isFromCart", true)
                    intent.putExtra("totalAmount", cartTotalAmount)
                    intent.putExtra("customerName", name)
                    intent.putExtra("customerMobile", mobile)
                    intent.putExtra("customerAddress", address)
                    startActivity(intent)
                }
            } else {
                // Handle single product checkout
                if (paymentMethod == "Cash on Delivery") {
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
    }

    private fun setupSavedAddressesRecyclerView() {
        savedAddressAdapter = SavedAddressAdapter(savedAddresses) { address ->
            // Fill form with selected address
            binding.fullName.setText(address.fullName)
            binding.mobileNumber.setText(address.mobileNumber)
            binding.address.setText(address.address)
        }
        binding.savedAddressesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.savedAddressesRecyclerView.adapter = savedAddressAdapter
    }

    private fun loadSavedAddressesAndDetails() {
        val userId = auth.currentUser?.uid ?: return

        // Load saved addresses
        val addressesRef = database.child("savedAddresses").child(userId)
        addressesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                savedAddresses.clear()
                if (snapshot.exists() && snapshot.hasChildren()) {
                    for (addressSnapshot in snapshot.children) {
                        val address = addressSnapshot.getValue(SavedAddress::class.java)
                        address?.let {
                            val addressWithId = it.copy(addressId = addressSnapshot.key ?: "")
                            savedAddresses.add(addressWithId)
                        }
                    }
                    // Show RecyclerView and label if addresses exist
                    if (savedAddresses.isNotEmpty()) {
                        binding.savedAddressesLabel.visibility = View.VISIBLE
                        binding.savedAddressesRecyclerView.visibility = View.VISIBLE
                        binding.noSavedAddressesTv.visibility = View.GONE
                        savedAddressAdapter.notifyDataSetChanged()
                        android.util.Log.d("CheckoutActivity", "Loaded ${savedAddresses.size} saved addresses")
                    } else {
                        binding.savedAddressesLabel.visibility = View.GONE
                        binding.savedAddressesRecyclerView.visibility = View.GONE
                        binding.noSavedAddressesTv.visibility = View.GONE
                    }
                } else {
                    binding.savedAddressesLabel.visibility = View.GONE
                    binding.savedAddressesRecyclerView.visibility = View.GONE
                    binding.noSavedAddressesTv.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("CheckoutActivity", "Error loading addresses: ${error.message}")
            }
        })

        // Load personal details (name, mobile) from user profile - load once on activity start
        val userDetailsRef = database.child("userDetails").child(userId)
        userDetailsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("fullName").getValue(String::class.java) ?: ""
                    val mobile = snapshot.child("mobileNumber").getValue(String::class.java) ?: ""
                    
                    android.util.Log.d("CheckoutActivity", "Loaded personal details - Name: $name, Mobile: $mobile")
                    
                    // Pre-fill if fields are empty (only pre-fill on initial load)
                    val currentName = binding.fullName.text.toString()
                    val currentMobile = binding.mobileNumber.text.toString()
                    
                    if (currentName.isEmpty() && name.isNotEmpty()) {
                        binding.fullName.setText(name)
                        android.util.Log.d("CheckoutActivity", "Pre-filled name: $name")
                    }
                    if (currentMobile.isEmpty() && mobile.isNotEmpty()) {
                        binding.mobileNumber.setText(mobile)
                        android.util.Log.d("CheckoutActivity", "Pre-filled mobile: $mobile")
                    }
                } else {
                    android.util.Log.d("CheckoutActivity", "No personal details found - will be saved after first order")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("CheckoutActivity", "Error loading user details: ${error.message}")
            }
        })
    }

    private fun saveAddressAndPersonalDetails(name: String, mobile: String, address: String) {
        val userId = auth.currentUser?.uid ?: return

        // Save personal details
        val userDetailsRef = database.child("userDetails").child(userId)
        userDetailsRef.child("fullName").setValue(name)
        userDetailsRef.child("mobileNumber").setValue(mobile)

        // Check if address already exists
        val addressesRef = database.child("savedAddresses").child(userId)
        var addressExists = false
        var existingAddressId: String? = null

        addressesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (addressSnapshot in snapshot.children) {
                        val savedAddress = addressSnapshot.getValue(SavedAddress::class.java)
                        if (savedAddress?.address == address && 
                            savedAddress.fullName == name && 
                            savedAddress.mobileNumber == mobile) {
                            addressExists = true
                            existingAddressId = addressSnapshot.key
                            break
                        }
                    }
                }

                if (!addressExists) {
                    // Save new address
                    val addressId = addressesRef.push().key
                    if (addressId != null) {
                        // Check if this is the first address (make it default)
                        val isDefault = !snapshot.exists() || !snapshot.hasChildren()
                        
                        val newAddress = SavedAddress(
                            addressId = addressId,
                            fullName = name,
                            mobileNumber = mobile,
                            address = address,
                            isDefault = isDefault
                        )
                        
                        addressesRef.child(addressId).setValue(newAddress)
                            .addOnSuccessListener {
                                android.util.Log.d("CheckoutActivity", "Address saved successfully")
                            }
                    }
                } else {
                    android.util.Log.d("CheckoutActivity", "Address already exists, skipping save")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("CheckoutActivity", "Error checking addresses: ${error.message}")
            }
        })
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

        // Save order to Firebase in both user-specific location and allOrders for admin
        val userOrderPath = database.child("orders").child(userId).child(orderId)
        val allOrdersPath = database.child("allOrders").child(orderId)
        
        // Save to user-specific location
        userOrderPath.setValue(orderData)
            .addOnSuccessListener {
                // Also save to allOrders for admin access
                allOrdersPath.setValue(orderData)
                    .addOnSuccessListener {
                        android.util.Log.d("CheckoutActivity", "Order saved successfully!")
                        Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("CheckoutActivity", "Error saving to allOrders: ${e.message}")
                        // Still show success since user order was saved
                        Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("CheckoutActivity", "Error saving order: ${e.message}")
                Toast.makeText(this, "Error placing order: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveCartOrderToFirebase(
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

        // Load cart items from Firebase
        val cartRef = database.child("carts").child(userId)
        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists() || !snapshot.hasChildren()) {
                    Toast.makeText(this@CheckoutActivity, "Your cart is empty", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this@CheckoutActivity, "Your cart is empty", Toast.LENGTH_SHORT).show()
                    return
                }

                val orderId = database.child("orders").push().key
                if (orderId == null) {
                    Toast.makeText(this@CheckoutActivity, "Error creating order. Please try again.", Toast.LENGTH_SHORT).show()
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
                                android.util.Log.d("CheckoutActivity", "Cart order saved successfully!")
                                Toast.makeText(this@CheckoutActivity, "Order placed successfully!", Toast.LENGTH_LONG).show()
                                val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                android.util.Log.e("CheckoutActivity", "Error saving to allOrders: ${e.message}")
                                // Still clear cart and show success since user order was saved
                                cartRef.removeValue()
                                Toast.makeText(this@CheckoutActivity, "Order placed successfully!", Toast.LENGTH_LONG).show()
                                val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("CheckoutActivity", "Error saving cart order: ${e.message}")
                        Toast.makeText(this@CheckoutActivity, "Error placing order: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("CheckoutActivity", "Error loading cart: ${error.message}")
                Toast.makeText(this@CheckoutActivity, "Error loading cart: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
