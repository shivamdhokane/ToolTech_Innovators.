package com.example.tooltechinnovators

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tooltechinnovators.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val productName = intent.getStringExtra("productName")
        val productPrice = intent.getStringExtra("productPrice")

        binding.paymentProductName.text = productName
        binding.paymentProductPrice.text = "Total Amount: $productPrice"


        binding.payButton.text = "Pay $productPrice"

        binding.payButton.setOnClickListener {
            Toast.makeText(this, "Payment Successful 🎉", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }
}
