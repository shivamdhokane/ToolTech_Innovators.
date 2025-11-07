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
        
        // Add debug logging
        android.util.Log.d("MyOrdersActivity", "Loading orders for userId: $userId")
        android.util.Log.d("MyOrdersActivity", "Database path: orders/$userId")
        android.util.Log.d("MyOrdersActivity", "Current user: ${auth.currentUser?.email}")
        
        // First, let's check if the path exists at all
        database.child("orders").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                android.util.Log.d("MyOrdersActivity", "Root orders node exists: ${snapshot.exists()}")
                android.util.Log.d("MyOrdersActivity", "Root orders has children: ${snapshot.hasChildren()}")
                if (snapshot.exists()) {
                    android.util.Log.d("MyOrdersActivity", "All user IDs in orders: ${snapshot.children.map { it.key }}")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MyOrdersActivity", "Error checking root orders: ${error.message}")
            }
        })

        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()
                
                android.util.Log.d("MyOrdersActivity", "Snapshot exists: ${snapshot.exists()}")
                android.util.Log.d("MyOrdersActivity", "Snapshot has children: ${snapshot.hasChildren()}")
                
                if (!snapshot.exists()) {
                    android.util.Log.d("MyOrdersActivity", "No orders found in database")
                    updateEmptyState()
                    return
                }
                
                if (!snapshot.hasChildren()) {
                    android.util.Log.d("MyOrdersActivity", "Snapshot exists but has no children")
                    updateEmptyState()
                    return
                }
                
                android.util.Log.d("MyOrdersActivity", "Found ${snapshot.childrenCount} orders")
                
                for (orderSnapshot in snapshot.children) {
                    try {
                        android.util.Log.d("MyOrdersActivity", "Processing order: ${orderSnapshot.key}")
                        android.util.Log.d("MyOrdersActivity", "Order value: ${orderSnapshot.value}")
                        
                        // Get order data as a map
                        val orderData = orderSnapshot.value as? Map<*, *>
                        if (orderData != null) {
                            android.util.Log.d("MyOrdersActivity", "Order data keys: ${orderData.keys}")
                            
                            // Extract items from the map - Firebase can return items as List or Map
                            val itemsList = mutableListOf<CartItem>()
                            val itemsData = orderData["items"]
                            
                            android.util.Log.d("MyOrdersActivity", "Items data type: ${itemsData?.javaClass?.simpleName}")
                            android.util.Log.d("MyOrdersActivity", "Items data: $itemsData")
                            
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
                                else -> {
                                    android.util.Log.w("MyOrdersActivity", "Items data is neither List nor Map: ${itemsData?.javaClass}")
                                }
                            }
                            
                            // Create Order object
                            val order = Order(
                                orderId = orderSnapshot.key ?: "",
                                userId = orderData["userId"] as? String ?: "",
                                items = itemsList,
                                totalAmount = orderData["totalAmount"] as? String ?: "",
                                orderDate = orderData["orderDate"] as? String ?: "",
                                status = orderData["status"] as? String ?: "Pending"
                            )
                            orders.add(order)
                            android.util.Log.d("MyOrdersActivity", "Added order: ${order.orderId} with ${order.items.size} items")
                        } else {
                            // Fallback: try to get as Order object (for backward compatibility)
                            val order = orderSnapshot.getValue(Order::class.java)
                            order?.let {
                                val orderWithId = it.copy(orderId = orderSnapshot.key ?: "")
                                orders.add(orderWithId)
                                android.util.Log.d("MyOrdersActivity", "Added order (fallback): ${orderWithId.orderId}")
                            }
                        }
                    } catch (e: Exception) {
                        // Log error but continue processing other orders
                        android.util.Log.e("MyOrdersActivity", "Error parsing order: ${e.message}", e)
                        e.printStackTrace()
                    }
                }
                
                android.util.Log.d("MyOrdersActivity", "Total orders loaded: ${orders.size}")
                
                // Sort orders by date (newest first)
                orders.sortByDescending { it.orderDate }
                ordersAdapter.notifyDataSetChanged()
                updateEmptyState()
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MyOrdersActivity", "Error loading orders: ${error.message}")
                android.util.Log.e("MyOrdersActivity", "Error code: ${error.code}")
                val errorMsg = if (error.message.contains("Permission denied")) {
                    "Permission denied. Please check Firebase Database rules. See FIREBASE_SETUP.md"
                } else {
                    "Error loading orders: ${error.message}"
                }
                Toast.makeText(this@MyOrdersActivity, errorMsg, Toast.LENGTH_LONG).show()
            }
        })
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
}

