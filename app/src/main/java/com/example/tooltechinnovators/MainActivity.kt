package com.example.tooltechinnovators

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tooltechinnovators.ui.theme.ToolTechInnovatorsTheme

// --- Data model for a product ---
data class Product(
    val id: Int,
    val name: String,
    val price: String
)

// --- Sample product list ---
val sampleProducts = listOf(
    Product(1, "Power Drill", "₹3,499"),
    Product(2, "Angle Grinder", "₹2,199"),
    Product(3, "Electric Saw", "₹4,999"),
    Product(4, "Impact Wrench", "₹5,499")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToolTechInnovatorsTheme {
                Scaffold { innerPadding ->
                    ProductList(
                        products = sampleProducts,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

// --- Product list screen ---
@Composable
fun ProductList(products: List<Product>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(products) { product ->
            ProductCard(product)
        }
    }
}

// --- Single product card ---
@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Placeholder image (default Android icon)
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                contentDescription = product.name,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = product.price,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Button(
                    onClick = { /* TODO: Handle Buy Action */ },
                    modifier = Modifier.padding(top = 6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Buy Now", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProductList() {
    ToolTechInnovatorsTheme {
        ProductList(products = sampleProducts)
    }
}
