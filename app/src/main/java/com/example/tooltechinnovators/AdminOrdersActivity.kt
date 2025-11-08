package com.example.tooltechinnovators

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tooltechinnovators.databinding.ActivityAdminOrdersBinding
import com.google.firebase.database.*

class AdminOrdersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminOrdersBinding
    private lateinit var database: DatabaseReference
    private lateinit var adminOrdersAdapter: AdminOrdersAdapter
    private val orders = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference

        binding.backBtn.setOnClickListener {
            finish()
        }

        setupRecyclerView()
        loadAllOrders()
    }

    private fun setupRecyclerView() {
        adminOrdersAdapter = AdminOrdersAdapter(orders) { order, newStatus ->
            updateOrderStatus(order, newStatus)
        }
        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.ordersRecyclerView.adapter = adminOrdersAdapter
    }

    private fun loadAllOrders() {
        val allOrdersRef = database.child("allOrders")
        
        allOrdersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()
                
                if (!snapshot.exists() || !snapshot.hasChildren()) {
                    updateEmptyState()
                    return
                }
                
                for (orderSnapshot in snapshot.children) {
                    try {
                        val orderData = orderSnapshot.value as? Map<*, *>
                        if (orderData != null) {
                            val itemsList = mutableListOf<CartItem>()
                            val itemsData = orderData["items"]
                            
                            when (itemsData) {
                                is List<*> -> {
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
                            
                            val order = Order(
                                orderId = orderSnapshot.key ?: "",
                                userId = orderData["userId"] as? String ?: "",
                                items = itemsList,
                                totalAmount = orderData["totalAmount"] as? String ?: "",
                                orderDate = orderData["orderDate"] as? String ?: "",
                                status = orderData["status"] as? String ?: "Pending"
                            )
                            orders.add(order)
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AdminOrdersActivity", "Error parsing order: ${e.message}", e)
                    }
                }
                
                orders.sortByDescending { it.orderDate }
                adminOrdersAdapter.notifyDataSetChanged()
                updateEmptyState()
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("AdminOrdersActivity", "Error loading orders: ${error.message}")
                Toast.makeText(this@AdminOrdersActivity, "Error loading orders: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateOrderStatus(order: Order, newStatus: String) {
        android.util.Log.d("AdminOrdersActivity", "Updating order ${order.orderId} status to: $newStatus")
        
        // Update in allOrders (admin view) and user-specific location simultaneously
        val allOrdersRef = database.child("allOrders").child(order.orderId).child("status")
        val userOrderRef = database.child("orders").child(order.userId).child(order.orderId).child("status")
        
        // Use updateChildren for atomic update to both locations
        val updates = mapOf(
            "allOrders/${order.orderId}/status" to newStatus,
            "orders/${order.userId}/${order.orderId}/status" to newStatus
        )
        
        database.updateChildren(updates)
            .addOnSuccessListener {
                android.util.Log.d("AdminOrdersActivity", "Order status updated successfully to: $newStatus")
                Toast.makeText(this, "Order status updated to $newStatus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                android.util.Log.e("AdminOrdersActivity", "Error updating order status: ${e.message}")
                Toast.makeText(this, "Error updating order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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

