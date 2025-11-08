package com.example.tooltechinnovators

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tooltechinnovators.databinding.ActivityCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.backBtn.setOnClickListener {
            finish()
        }

        setupRecyclerView()
        loadCartItems()

        binding.checkoutBtn.setOnClickListener {
            if (cartItems.isNotEmpty()) {
                android.util.Log.d("CartActivity", "Checkout button clicked. Cart has ${cartItems.size} items")
                proceedToCheckout()
            } else {
                android.util.Log.w("CartActivity", "Checkout attempted with empty cart")
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(cartItems) { item, action ->
            when (action) {
                CartAdapter.CartAction.INCREASE -> updateQuantity(item, item.quantity + 1)
                CartAdapter.CartAction.DECREASE -> {
                    if (item.quantity > 1) {
                        updateQuantity(item, item.quantity - 1)
                    }
                }
                CartAdapter.CartAction.REMOVE -> removeFromCart(item)
            }
        }
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.cartRecyclerView.adapter = cartAdapter
    }

    private fun loadCartItems() {
        val userId = auth.currentUser?.uid ?: return
        val cartRef = database.child("carts").child(userId)

        cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartItems.clear()
                for (itemSnapshot in snapshot.children) {
                    val cartItem = itemSnapshot.getValue(CartItem::class.java)
                    cartItem?.let {
                        val itemWithId = it.copy(cartItemId = itemSnapshot.key ?: "")
                        cartItems.add(itemWithId)
                    }
                }
                cartAdapter.notifyDataSetChanged()
                updateTotal()
                updateEmptyState()
            }

            override fun onCancelled(error: DatabaseError) {
                val errorMsg = if (error.message.contains("Permission denied")) {
                    "Permission denied. Please check Firebase Database rules. See FIREBASE_SETUP.md"
                } else {
                    "Error loading cart: ${error.message}"
                }
                Toast.makeText(this@CartActivity, errorMsg, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateQuantity(item: CartItem, newQuantity: Int) {
        val userId = auth.currentUser?.uid ?: return
        val cartRef = database.child("carts").child(userId).child(item.cartItemId)
        cartRef.child("quantity").setValue(newQuantity)
    }

    private fun removeFromCart(item: CartItem) {
        val userId = auth.currentUser?.uid ?: return
        val cartRef = database.child("carts").child(userId).child(item.cartItemId)
        cartRef.removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTotal() {
        var total = 0.0
        cartItems.forEach { item ->
            val priceStr = item.productPrice.replace("₹", "").replace(",", "")
            val price = priceStr.toDoubleOrNull() ?: 0.0
            total += price * item.quantity
        }
        binding.totalAmountTv.text = "₹${String.format("%.2f", total)}"
    }

    private fun updateEmptyState() {
        if (cartItems.isEmpty()) {
            binding.emptyCartTv.visibility = View.VISIBLE
            binding.cartRecyclerView.visibility = View.GONE
            binding.checkoutBtn.isEnabled = false
        } else {
            binding.emptyCartTv.visibility = View.GONE
            binding.cartRecyclerView.visibility = View.VISIBLE
            binding.checkoutBtn.isEnabled = true
        }
    }

    private fun proceedToCheckout() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            android.util.Log.e("CartActivity", "User not logged in!")
            Toast.makeText(this, "Please login to place order", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (cartItems.isEmpty()) {
            android.util.Log.e("CartActivity", "Cart is empty!")
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Navigate to CheckoutActivity with cart flag
        val intent = Intent(this, CheckoutActivity::class.java)
        intent.putExtra("isFromCart", true)
        intent.putExtra("totalAmount", calculateTotal())
        startActivity(intent)
    }

    private fun calculateTotal(): Double {
        var total = 0.0
        cartItems.forEach { item ->
            val priceStr = item.productPrice.replace("₹", "").replace(",", "")
            val price = priceStr.toDoubleOrNull() ?: 0.0
            total += price * item.quantity
        }
        return total
    }
}

