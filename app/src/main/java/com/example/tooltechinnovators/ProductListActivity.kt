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
    private lateinit var productAdapter: ProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private var allProducts = listOf<Product>()
    private var filteredProducts = mutableListOf<Product>()

    val sampleProducts = listOf(
        // Category 1: Cutting Tools
        Product(1, "Angle Grinder", "₹2,199", R.drawable.anglegrinder, "Compact angle grinder for cutting and grinding metal or tiles.", "Stanley STGS6100", "750 W", "1.8 kg", "Cutting Tools"),
        Product(2, "Electric Saw", "₹4,999", R.drawable.electricsaw, "Powerful electric saw for precision wood cutting.", "Makita M5802", "1050 W", "3.2 kg", "Cutting Tools"),
        Product(3, "Jigsaw", "₹3,299", R.drawable.ic_product, "Versatile jigsaw for curved and straight cuts in wood, metal, and plastic.", "Bosch PST 700", "700 W", "2.3 kg", "Cutting Tools"),
        Product(4, "Circular Saw", "₹5,799", R.drawable.ic_product, "Heavy-duty circular saw for fast and accurate cutting of wood and metal sheets.", "Makita HS7601", "1600 W", "4.1 kg", "Cutting Tools"),
        Product(5, "Reciprocating Saw", "₹4,899", R.drawable.ic_product, "Heavy-duty reciprocating saw for demolition and cutting through tough materials.", "Makita JR3050T", "1010 W", "3.9 kg", "Cutting Tools"),
        Product(6, "Chainsaw", "₹8,499", R.drawable.ic_product, "Electric chainsaw for cutting trees, logs, and large branches safely.", "Black+Decker CS1240", "2400 W", "5.2 kg", "Cutting Tools"),
        Product(7, "Tile Cutter", "₹3,799", R.drawable.ic_product, "Precision tile cutter for clean cuts on ceramic and porcelain tiles.", "Bosch GDC 120", "1200 W", "3.2 kg", "Cutting Tools"),
        Product(8, "Bench Grinder", "₹3,299", R.drawable.ic_product, "Heavy-duty bench grinder for sharpening tools and metal grinding.", "Bosch GWS 6-100", "670 W", "1.75 kg", "Cutting Tools"),
        Product(9, "Rotary Tool", "₹2,499", R.drawable.ic_product, "Compact rotary tool for grinding, cutting, polishing, and engraving.", "Dremel 3000", "175 W", "0.5 kg", "Cutting Tools"),
        Product(10, "Band Saw", "₹7,299", R.drawable.ic_product, "Precision band saw for straight and curved cuts in wood and metal.", "Bosch GCB 18V", "18V", "3.5 kg", "Cutting Tools"),
        Product(11, "Miter Saw", "₹9,999", R.drawable.ic_product, "Compound miter saw for accurate angle cuts in wood and trim work.", "Dewalt DWS780", "15A", "22.7 kg", "Cutting Tools"),
        Product(12, "Table Saw", "₹12,499", R.drawable.ic_product, "Heavy-duty table saw for precise rip cuts and crosscuts.", "Bosch GTS 10", "1800 W", "25.5 kg", "Cutting Tools"),
        
        // Category 2: Drilling Tools
        Product(13, "Power Drill", "₹2,499", R.drawable.powerdrill, "High-performance power drill for all household and professional tasks.", "Bosch GSB 550", "600 W", "2.0 kg", "Drilling Tools"),
        Product(14, "Hammer Drill", "₹4,299", R.drawable.powerdrill, "Powerful hammer drill for drilling into concrete, brick, and stone.", "Bosch GBH 2-26", "800 W", "2.8 kg", "Drilling Tools"),
        Product(15, "Impact Wrench", "₹5,499", R.drawable.impactwrench, "Heavy-duty impact wrench with high torque for mechanical use.", "Dewalt DCF899", "800 W", "2.7 kg", "Drilling Tools"),
        Product(16, "Cordless Drill", "₹3,799", R.drawable.powerdrill, "Compact cordless drill for convenient drilling without power cord limitations.", "Makita DHP481", "18V", "2.1 kg", "Drilling Tools"),
        Product(17, "Drill Press", "₹8,999", R.drawable.ic_product, "Precision drill press for accurate vertical drilling operations.", "Bosch PBD 40", "710 W", "18.5 kg", "Drilling Tools"),
        Product(18, "Screwdriver", "₹1,599", R.drawable.ic_product, "Electric screwdriver for fast and efficient screw driving tasks.", "Black+Decker AS6NG", "4.8V", "0.5 kg", "Drilling Tools"),
        Product(19, "Impact Driver", "₹4,599", R.drawable.impactwrench, "High-torque impact driver for driving screws and bolts effortlessly.", "Dewalt DCF887", "20V", "1.4 kg", "Drilling Tools"),
        Product(20, "Right Angle Drill", "₹5,299", R.drawable.powerdrill, "Compact right angle drill for tight spaces and corner drilling.", "Bosch GSR 120-LI", "12V", "1.2 kg", "Drilling Tools"),
        Product(21, "Magnetic Drill", "₹15,999", R.drawable.ic_product, "Heavy-duty magnetic drill for steel and metal drilling applications.", "Bosch GBM 10", "1010 W", "11.5 kg", "Drilling Tools"),
        
        // Category 3: Finishing Tools
        Product(22, "Power Planer", "₹4,999", R.drawable.powerplaner, "Durable and smooth power planer for fine finishing.", "Black+Decker KW712", "650 W", "2.6 kg", "Finishing Tools"),
        Product(23, "Router", "₹6,499", R.drawable.ic_product, "Precision router for edge profiling, dado cutting, and decorative work.", "Bosch GOF 1250", "1250 W", "3.5 kg", "Finishing Tools"),
        Product(24, "Orbital Sander", "₹2,899", R.drawable.ic_product, "Smooth orbital sander for perfect finishing on wood and metal surfaces.", "Black+Decker BDERO100", "225 W", "1.4 kg", "Finishing Tools"),
        Product(25, "Belt Sander", "₹5,299", R.drawable.ic_product, "Powerful belt sander for rapid material removal and surface preparation.", "Bosch PBS 75", "750 W", "3.8 kg", "Finishing Tools"),
        Product(26, "Polisher", "₹4,599", R.drawable.ic_product, "Professional polisher for car detailing and surface polishing tasks.", "Makita PO5000C", "1200 W", "2.4 kg", "Finishing Tools"),
        Product(27, "Detail Sander", "₹2,199", R.drawable.ic_product, "Compact detail sander for reaching tight corners and small areas.", "Bosch PSM 100", "100 W", "0.9 kg", "Finishing Tools"),
        Product(28, "Random Orbital Sander", "₹3,899", R.drawable.ic_product, "Dual-action random orbital sander for swirl-free finishing.", "Dewalt DWE6423", "200 W", "1.8 kg", "Finishing Tools"),
        Product(29, "Edge Sander", "₹6,799", R.drawable.ic_product, "Specialized edge sander for perfect edge finishing on wood projects.", "Makita BO5041", "300 W", "2.1 kg", "Finishing Tools"),
        Product(30, "Buffer", "₹3,499", R.drawable.ic_product, "Heavy-duty buffer for polishing and waxing large surfaces efficiently.", "Black+Decker WP900", "900 W", "2.7 kg", "Finishing Tools"),
        Product(31, "Heat Gun", "₹1,899", R.drawable.ic_product, "Professional heat gun for paint stripping, shrink wrapping, and thawing.", "Bosch GHG 660", "2000 W", "0.7 kg", "Finishing Tools"),
        Product(32, "Multi-Tool", "₹3,599", R.drawable.ic_product, "Versatile oscillating multi-tool for cutting, sanding, and scraping tasks.", "Dewalt DWE315K", "250 W", "1.5 kg", "Finishing Tools"),
        Product(33, "Nail Gun", "₹6,999", R.drawable.ic_product, "Electric nail gun for fast and efficient nailing in construction projects.", "Stanley TRE550", "550 W", "2.9 kg", "Finishing Tools"),
        Product(34, "Staple Gun", "₹2,499", R.drawable.ic_product, "Electric staple gun for upholstery and construction fastening tasks.", "Bosch Tacker", "18V", "1.1 kg", "Finishing Tools")
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        allProducts = sampleProducts

        // Get unique categories
        val categories = sampleProducts.map { it.category }.distinct()

        // Setup Category RecyclerView (Horizontal)
        binding.categoryRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )
        categoryAdapter = CategoryAdapter(categories) { category ->
            filterProductsByCategory(category)
        }
        binding.categoryRecyclerView.adapter = categoryAdapter

        // Setup Product RecyclerView (Vertical)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(filteredProducts) { product ->
            val intent = Intent(this, ProductDetailsActivity::class.java)
            intent.putExtra("productId", product.id)
            startActivity(intent)
        }
        binding.recyclerView.adapter = productAdapter

        // Show first category by default
        if (categories.isNotEmpty()) {
            filterProductsByCategory(categories[0])
        }
    }

    private fun filterProductsByCategory(category: String) {
        filteredProducts.clear()
        filteredProducts.addAll(allProducts.filter { it.category == category })
        productAdapter.notifyDataSetChanged()
        categoryAdapter.selectedCategory = category
    }
}
