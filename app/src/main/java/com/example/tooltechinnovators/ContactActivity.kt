package com.example.tooltechinnovators

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tooltechinnovators.databinding.ActivityContactBinding

class ContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set contact details
        binding.contactPhone.text = "Phone: +91 98225 454787"
        binding.contactAddress.text =
            "Desai Complex, Pimplas Road, Rahata, Ahmednagar 423107, Maharashtra, India"
        binding.contactEmail.text = "samarth_elect@rediffmail.com"

        // Handle Email Button
        binding.emailBtn.setOnClickListener {
            sendEmail()
        }

        // Optional: Click phone to open dialer
        binding.contactPhone.setOnClickListener {
            val phoneNumber = "+9198225454787"
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(intent)
        }

        // Optional: Click email text to open email app
        binding.contactEmail.setOnClickListener {
            sendEmail()
        }
    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:samarth_elect@rediffmail.com")
            putExtra(Intent.EXTRA_SUBJECT, "Inquiry from ToolTech App")
        }

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email via..."))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
        }
    }
}
