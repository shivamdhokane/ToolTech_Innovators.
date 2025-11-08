package com.example.tooltechinnovators

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tooltechinnovators.databinding.ActivityMyOrdersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyOrdersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyOrdersBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var ordersAdapter: OrdersAdapter
    private val orders = mutableListOf<Order>()
    private var ordersListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.backBtn.setOnClickListener {
            finish()
        }

        setupRecyclerView()
        loadOrders()
    }

    private fun setupRecyclerView() {
        ordersAdapter = OrdersAdapter(orders)
        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.ordersRecyclerView.adapter = ordersAdapter
    }

    private fun loadOrders() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Please login to view orders", Toast.LENGTH_SHORT).show()
            updateEmptyState()
            return
        }
        
        val ordersRef = database.child("orders").child(userId)
        
        android.util.Log.d("MyOrdersActivity", "Setting up real-time listener for orders. UserId: $userId")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()
                
                android.util.Log.d("MyOrdersActivity", "Real-time update received. Snapshot exists: ${snapshot.exists()}")
                
                if (!snapshot.exists() || !snapshot.hasChildren()) {
                    android.util.Log.d("MyOrdersActivity", "No orders found")
                    updateEmptyState()
                    return
                }
                
                android.util.Log.d("MyOrdersActivity", "Found ${snapshot.childrenCount} orders")
                
                for (orderSnapshot in snapshot.children) {
                    try {
                        // Get order data as a map
                        val orderData = orderSnapshot.value as? Map<*, *>
                        if (orderData != null) {
                            // Extract items from the map - Firebase can return items as List or Map
                            val itemsList = mutableListOf<CartItem>()
                            val itemsData = orderData["items"]
                            
                            when (itemsData) {
                                is List<*> -> {
                                    // Items are stored as a List/Array
                                    itemsData.forEach { itemValue ->
                                        val itemMap = itemValue as? Map<*, *>
                                        itemMap?.let {
                                            val cartItem = CartItem(
                                                productId = (it["productId"] as? Long)?.toInt() ?: (it["productId"] as? Int) ?: 0,
                                                productName = it["productName"] as? String ?: "",
                                                productPrice = it["productPrice"] as? String ?: "",
                                                productImageResId = (it["productImageResId"] as? Long)?.toInt() ?: (it["productImageResId"] as? Int) ?: 0,
                                                quantity = (it["quantity"] as? Long)?.toInt() ?: (it["quantity"] as? Int) ?: 1,
                                                cartItemId = it["cartItemId"] as? String ?: ""
                                            )
                                            itemsList.add(cartItem)
                                        }
                                    }
                                }
                                is Map<*, *> -> {
                                    // Items are stored as a Map (our new format)
                                    itemsData.forEach { (_, itemValue) ->
                                        val itemMap = itemValue as? Map<*, *>
                                        itemMap?.let {
                                            val cartItem = CartItem(
                                                productId = (it["productId"] as? Long)?.toInt() ?: (it["productId"] as? Int) ?: 0,
                                                productName = it["productName"] as? String ?: "",
                                                productPrice = it["productPrice"] as? String ?: "",
                                                productImageResId = (it["productImageResId"] as? Long)?.toInt() ?: (it["productImageResId"] as? Int) ?: 0,
                                                quantity = (it["quantity"] as? Long)?.toInt() ?: (it["quantity"] as? Int) ?: 1,
                                                cartItemId = it["cartItemId"] as? String ?: ""
                                            )
                                            itemsList.add(cartItem)
                                        }
                                    }
                                }
                            }
                            
                            // Create Order object with current status
                            val currentStatus = orderData["status"] as? String ?: "Pending"
                            val order = Order(
                                orderId = orderSnapshot.key ?: "",
                                userId = orderData["userId"] as? String ?: "",
                                items = itemsList,
                                totalAmount = orderData["totalAmount"] as? String ?: "",
                                orderDate = orderData["orderDate"] as? String ?: "",
                                status = currentStatus
                            )
                            orders.add(order)
                            android.util.Log.d("MyOrdersActivity", "Order ${order.orderId} - Status: ${order.status}")
                        } else {
                            // Fallback: try to get as Order object (for backward compatibility)
                            val order = orderSnapshot.getValue(Order::class.java)
                            order?.let {
                                val orderWithId = it.copy(orderId = orderSnapshot.key ?: "")
                                orders.add(orderWithId)
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MyOrdersActivity", "Error parsing order: ${e.message}", e)
                    }
                }
                
                // Sort orders by date (newest first)
                orders.sortByDescending { it.orderDate }
                ordersAdapter.notifyDataSetChanged()
                updateEmptyState()
                
                android.util.Log.d("MyOrdersActivity", "Orders list updated. Total: ${orders.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MyOrdersActivity", "Error loading orders: ${error.message}")
                val errorMsg = if (error.message.contains("Permission denied")) {
                    "Permission denied. Please check Firebase Database rules."
                } else {
                    "Error loading orders: ${error.message}"
                }
                Toast.makeText(this@MyOrdersActivity, errorMsg, Toast.LENGTH_LONG).show()
            }
        }
        
        ordersListener = listener
        ordersRef.addValueEventListener(listener)
    }

    private fun updateEmptyState() {
        if (orders.isEmpty()) {
            binding.emptyOrdersTv.visibility = View.VISIBLE
            binding.ordersRecyclerView.visibility = View.GONE
        } else {
            binding.emptyOrdersTv.visibility = View.GONE
            binding.ordersRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove listener to prevent memory leaks
        ordersListener?.let {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                database.child("orders").child(userId).removeEventListener(it)
                android.util.Log.d("MyOrdersActivity", "Removed orders listener")
            }
        }
    }
}

