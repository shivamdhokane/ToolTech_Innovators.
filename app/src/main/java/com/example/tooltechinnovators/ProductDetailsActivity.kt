package com.example.tooltechinnovators

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tooltechinnovators.databinding.ActivityProductDetailsBinding

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getIntExtra("productId", -1)
        val product = getProductById(productId)

        product?.let { p ->
            binding.productImage.setImageResource(p.imageResId)
            binding.productName.text = p.name
            binding.productPrice.text = p.price
            binding.productDescription.text = p.description

            binding.buyNowBtn.setOnClickListener {
                Intent(this, CheckoutActivity::class.java).apply {
                    putExtra("productName", p.name)
                    putExtra("productPrice", p.price)
                    startActivity(this)
                }
            }
        }
    }

    private fun getProductById(id: Int): Product? {
        return ProductListActivity().sampleProducts.find { it.id == id }
    }
}
