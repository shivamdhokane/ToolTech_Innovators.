package com.example.tooltechinnovators

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tooltechinnovators.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private var amount = "100" // Example — you can pass this dynamically

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productName = intent.getStringExtra("productName") ?: "Product"
        val productPrice = intent.getStringExtra("productPrice") ?: "₹100"
        amount = productPrice.replace("₹", "")

        binding.paymentProductName.text = productName
        binding.paymentProductPrice.text = productPrice

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
            payUsingUpi(null, "UPI App")
        }
    }

    private fun payUsingUpi(packageName: String?, appName: String) {
        val uri = Uri.parse(
            "upi://pay?pa=yourmerchant@upi&pn=ToolTech%20Innovators&am=$amount&cu=INR&tn=Payment%20for%20$amount"
        )

        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (packageName != null) {
            intent.setPackage(packageName)
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "$appName not installed", Toast.LENGTH_SHORT).show()
            if (packageName != null) {
                // Open Play Store if app missing
                try {
                    val playIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                    startActivity(playIntent)
                } catch (ex: Exception) {
                    Toast.makeText(this, "Unable to open Play Store", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
