package com.example.tooltechinnovators

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tooltechinnovators.databinding.ActivityProductListBinding

/*data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val imageResId: Int,
    val brandModel: String,
    val powerWatts: String,
    val weightKg: String
)*/

class ProductListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductListBinding

    val sampleProducts = listOf(
        Product(
            1,
            "Power Drill",
            "₹2,499",
            R.drawable.powerdrill,
            "High-performance power drill for all household and professional tasks.",
            "Bosch GSB 550",
            "600 W",
            "2.0 kg"
        ),
        Product(
            2,
            "Angle Grinder",
            "₹2,199",
            R.drawable.anglegrinder,
            "Compact angle grinder for cutting and grinding metal or tiles.",
            "Stanley STGS6100",
            "750 W",
            "1.8 kg"
        ),
        Product(
            3,
            "Electric Saw",
            "₹4,999",
            R.drawable.electricsaw,
            "Powerful electric saw for precision wood cutting.",
            "Makita M5802",
            "1050 W",
            "3.2 kg"
        ),
        Product(
            4,
            "Impact Wrench",
            "₹5,499",
            R.drawable.impactwrench,
            "Heavy-duty impact wrench with high torque for mechanical use.",
            "Dewalt DCF899",
            "800 W",
            "2.7 kg"
        ),
        Product(
            5,
            "Power Planer",
            "₹4,999",
            R.drawable.powerplaner,
            "Durable and smooth power planer for fine finishing.",
            "Black+Decker KW712",
            "650 W",
            "2.6 kg"
        )
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ProductAdapter(sampleProducts) { product ->
            val intent = Intent(this, ProductDetailsActivity::class.java)
            intent.putExtra("productId", product.id)
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter
    }
}
