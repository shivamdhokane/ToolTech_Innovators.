package com.example.tooltechinnovators

import android.os.Bundle
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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

// ---------- Main ----------
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

    var registeredEmail by remember { mutableStateOf("") }
    var registeredPassword by remember { mutableStateOf("") }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("product") { ProductListScreen() }
        composable("signin") { SignInScreen(navController) }
        composable("signup") {
            SignUpScreen(navController) { email, password ->
                registeredEmail = email
                registeredPassword = password
            }
        }
        composable("login") {
            LoginScreen(navController, registeredEmail, registeredPassword)
        }
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


@Composable
fun SignInScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to Sign In", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Log In")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("signup") },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Sign Up")
        }
    }
}


@Composable
fun LoginScreen(
    navController: NavHostController,
    registeredEmail: String,
    registeredPassword: String
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Log In", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (email == registeredEmail && password == registeredPassword) {
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    navController.navigate("home")
                } else {
                    Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
    }
}


@Composable
fun SignUpScreen(
    navController: NavHostController,
    onRegister: (String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign Up", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile Number") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    onRegister(email, password)   // save values
                    Toast.makeText(context, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                    navController.navigate("login")
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Contact Us") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Contact Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Contact Us",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Owner: Sachin Desai",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Phone",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Phone: +91 98225 45478",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Address",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Address: Desai Complex, Pimplas Road,\nRahata, Ahmednagar 423107, Maharashtra, India",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Email: support@tooltech.com",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { /* TODO: Open email app */ },
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text(
                    "Send Us an Email",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp)
                )
            }
        }
    }
}