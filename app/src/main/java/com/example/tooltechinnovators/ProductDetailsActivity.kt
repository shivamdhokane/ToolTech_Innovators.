package com.example.tooltechinnovators

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tooltechinnovators.databinding.ActivityProductDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val productId = intent.getIntExtra("productId", -1)
        val product = getProductById(productId)

        product?.let { p ->
            binding.productImage.setImageResource(p.imageResId)
            binding.productName.text = p.name
            binding.productPrice.text = p.price
            binding.productDescription.text = p.description

            binding.addToCartBtn.setOnClickListener {
                addToCart(p)
            }

            binding.buyNowBtn.setOnClickListener {
                Intent(this, CheckoutActivity::class.java).apply {
                    putExtra("productId", p.id)
                    putExtra("productName", p.name)
                    putExtra("productPrice", p.price)
                    putExtra("productImageResId", p.imageResId)
                    startActivity(this)
                }
            }
        }
    }

    private fun getProductById(id: Int): Product? {
        return ProductListActivity().sampleProducts.find { it.id == id }
    }

    private fun addToCart(product: Product) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Please login to add items to cart", Toast.LENGTH_SHORT).show()
            return
        }

        val cartItem = CartItem(
            productId = product.id,
            productName = product.name,
            productPrice = product.price,
            productImageResId = product.imageResId,
            quantity = 1
        )

        // Check if item already exists in cart by loading all items
        val cartRef = database.child("carts").child(userId)
        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var existingItem: CartItem? = null
                var existingItemKey: String? = null
                
                // Check all cart items to see if product already exists
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(CartItem::class.java)
                    if (item?.productId == product.id) {
                        existingItem = item
                        existingItemKey = itemSnapshot.key
                        break
                    }
                }
                
                if (existingItem != null && existingItemKey != null) {
                    // Item exists, update quantity
                    val itemRef = cartRef.child(existingItemKey)
                    val newQuantity = existingItem.quantity + 1
                    itemRef.child("quantity").setValue(newQuantity)
                        .addOnSuccessListener {
                            Toast.makeText(this@ProductDetailsActivity, "Cart updated", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            val errorMsg = if (e.message?.contains("Permission denied") == true) {
                                "Permission denied. Please check Firebase Database rules. See FIREBASE_SETUP.md"
                            } else {
                                "Error updating cart: ${e.message}"
                            }
                            Toast.makeText(this@ProductDetailsActivity, errorMsg, Toast.LENGTH_LONG).show()
                        }
                } else {
                    // Item doesn't exist, add new item
                    val newCartItemRef = cartRef.push()
                    newCartItemRef.setValue(cartItem)
                        .addOnSuccessListener {
                            Toast.makeText(this@ProductDetailsActivity, "Added to cart", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            val errorMsg = if (e.message?.contains("Permission denied") == true) {
                                "Permission denied. Please check Firebase Database rules. See FIREBASE_SETUP.md"
                            } else {
                                "Error: ${e.message}"
                            }
                            Toast.makeText(this@ProductDetailsActivity, errorMsg, Toast.LENGTH_LONG).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                val errorMsg = if (error.message.contains("Permission denied")) {
                    "Permission denied. Please check Firebase Database rules. See FIREBASE_SETUP.md"
                } else {
                    "Error: ${error.message}"
                }
                Toast.makeText(this@ProductDetailsActivity, errorMsg, Toast.LENGTH_LONG).show()
            }
        })
    }
}
