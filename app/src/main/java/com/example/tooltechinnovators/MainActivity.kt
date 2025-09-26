package com.example.tooltechinnovators

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.tooltechinnovators.ui.theme.ToolTechInnovatorsTheme


data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val imageResId: Int
)

val sampleProducts = listOf(
    Product(1, "Power Drill", "₹2,499", R.drawable.powerdrill),
    Product(2, "Angle Grinder", "₹2,199", R.drawable.anglegrinder),
    Product(3, "Electric Saw", "₹4,999", R.drawable.electricsaw),
    Product(4, "Impact Wrench", "₹5,499", R.drawable.impactwrench),
    Product(5, "Power Planer", "₹4,999", R.drawable.powerplaner)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToolTechInnovatorsTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("product") { ProductListScreen() }
        composable("signin") { SignInScreen(navController) }
        composable("contact") { ContactScreen() }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(topBar = { TopAppBar(title = { Text("ToolTech Innovators") }) }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("product") },
                modifier = Modifier.fillMaxWidth().height(70.dp),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Product Section") }

            Button(
                onClick = { navController.navigate("signin") },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Sign In") }

            Button(
                onClick = { navController.navigate("contact") },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Contact Us") }
        }
    }
}

@Composable
fun ProductListScreen() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(sampleProducts) { product -> ProductCard(product) }
    }
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = painterResource(id = product.imageResId),
                contentDescription = product.name,
                modifier = Modifier.size(80.dp).padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                Text(text = product.price, color = MaterialTheme.colorScheme.primary)
                Button(onClick = { }, modifier = Modifier.padding(top = 6.dp)) {
                    Text("Buy Now")
                }
            }
        }
    }
}

// sign in
@Composable
fun SignInScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign In", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = mobile,
            onValueChange = { mobile = it },
            label = { Text("Mobile Number") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                // TODO: Add validation later
                navController.navigate("home")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
    }
}


@Composable
fun ContactScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Contact Us")
    }
}
