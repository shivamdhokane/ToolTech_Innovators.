package com.example.tooltechinnovators

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tooltechinnovators.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle logout icon click
        binding.logoutBtn.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.productSectionBtn.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }

        binding.contactUsBtn.setOnClickListener {
            startActivity(Intent(this, ContactActivity::class.java))
        }
    }
}
