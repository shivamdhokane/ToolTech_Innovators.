package com.example.tooltechinnovators

import android.content.Context
import android.os.Bundle
import androidx.compose.ui.platform.LocalContext

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tooltechinnovators.ui.theme.ToolTechInnovatorsTheme

// ---------------- Product Data ----------------
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

// ---------------- Main Activity ----------------
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

// ---------------- Navigation Setup ----------------
@Composable
fun MainApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("product") { ProductListScreen() }
        composable("signup") { SignUpScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("contact") { ContactScreen() }
    }
}

// ---------------- Home Screen ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("ToolTech Innovators") }) }
    ) { innerPadding ->
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
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Product Section") }

            Button(
                onClick = { navController.navigate("signup") },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Sign Up") }

            Button(
                onClick = { navController.navigate("contact") },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Contact Us") }
        }
    }
}

// ---------------- Product List ----------------
@Composable
fun ProductListScreen() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(sampleProducts) { product ->
            ProductCard(product)
        }
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
                Button(
                    onClick = { /* TODO: Handle Buy */ },
                    modifier = Modifier.padding(top = 6.dp)
                ) { Text("Buy Now") }
            }
        }
    }
}

// ---------------- Sign Up ----------------
@Composable
fun SignUpScreen(navController: NavHostController) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign Up Form", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                with(sharedPref.edit()) {
                    putString("username", name)
                    putString("email", email)
                    putString("phone", phone)
                    putString("password", password)
                    apply()
                }

                Toast.makeText(context, "Data Saved", Toast.LENGTH_SHORT).show()
                navController.navigate("login")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }
    }
}

// ---------------- Login ----------------
@Composable
fun LoginScreen(navController: NavHostController) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login Screen", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(20.dp))
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val savedUser = sharedPref.getString("username", "")
                val savedPass = sharedPref.getString("password", "")
                if (username == savedUser && password == savedPass) {
                    navController.navigate("home")
                } else {
                    Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Login") }
    }
}


@Composable
fun ContactScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Mr.Sachin Desai " +
                "  Mob. 9822545478" , style = MaterialTheme.typography.headlineMedium)
    }
}
