package com.example.tooltechinnovators

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tooltechinnovators.databinding.ActivityCheckoutBinding

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productName = intent.getStringExtra("productName")
        val productPrice = intent.getStringExtra("productPrice")

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

            if (paymentMethod == "Cash on Delivery") {
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else if (paymentMethod == "Online Payment") {
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("productName", productName)
                intent.putExtra("productPrice", productPrice)
                startActivity(intent)
            }
        }
    }
}
