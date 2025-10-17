package com.example.tooltechinnovators

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.tooltechinnovators.ui.theme.ToolTechInnovatorsTheme

// ---------- Data ----------
data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val imageResId: Int,
    val description: String,
    val brandModel: String,
    val powerWatts: String,
    val weightKg: String
)

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

        composable("signin") {
            SignUpScreen(navController) { email, password ->
                registeredEmail = email
                registeredPassword = password
            }
        }

        composable("login") {
            LoginScreen(navController, registeredEmail, registeredPassword)
        }

        composable("dashboard") { DashboardScreen(navController) }

        composable("product") { ProductListScreen(navController) }

        composable(
            "product/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            val product = sampleProducts.find { it.id == id }
            product?.let { ProductDetailsScreen(navController, it) }
        }

        composable(
            "checkout/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            val product = sampleProducts.find { it.id == id }
            product?.let { CheckoutScreen(navController, it) }
        }

        composable(
            "payment/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            val product = sampleProducts.find { it.id == id }
            product?.let { PaymentScreen(navController, it) }
        }

        composable("contact") { ContactScreen() }
    }
}

// ---------- HOME ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(topBar = { TopAppBar(title = { Text("Samarth Electricals") }) }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFEFF3FF))
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // circular logo - ensure R.drawable.logo exists
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Company Logo",
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text("Welcome to Samarth Electricals", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = { navController.navigate("signin") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Sign In") }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Already signed in? Log In",
                color = Color.Blue,
                modifier = Modifier.clickable { navController.navigate("login") }
            )
        }
    }
}

// ---------- SIGN UP ----------
@Composable
fun SignUpScreen(navController: NavHostController, onRegister: (String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign Up", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = username, onValueChange = { username = it },
            label = { Text("Username") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Email") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    onRegister(email.trim(), password)
                    Toast.makeText(context, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                    navController.navigate("login")
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Register") }
    }
}

// ---------- LOGIN ----------
@Composable
fun LoginScreen(navController: NavHostController, registeredEmail: String, registeredPassword: String) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Log In", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            if (registeredEmail.isNotEmpty() && email.trim() == registeredEmail && password == registeredPassword) {
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                navController.navigate("dashboard") {
                    popUpTo("home") { inclusive = false }
                }
            } else {
                Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT).show()
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Login")
        }
    }
}

// ---------- DASHBOARD ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Dashboard",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E88E5)
                ),
                actions = {
                    IconButton(onClick = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFE3F2FD), Color(0xFFFFFFFF))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // 🔹 Company Logo & Welcome Text
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Company Logo",
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color(0xFF90CAF9), CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Welcome to Samarth Electricals ⚡",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(30.dp))

                // 🔹 Dashboard Buttons
                DashboardCard(
                    title = "Product Section",
                    subtitle = "Explore our tools & equipment",
                    icon = Icons.Default.Home,
                    color = Color(0xFF3F51B5)
                ) {
                    navController.navigate("product")
                }

                Spacer(modifier = Modifier.height(20.dp))

                DashboardCard(
                    title = "Contact Us",
                    subtitle = "Reach out for support & info",
                    icon = Icons.Default.Info,
                    color = Color(0xFF43A047)
                ) {
                    navController.navigate("contact")
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Powered by Samarth Electricals ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A)
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray
                    )
                )
            }
        }
    }
}

// ---------- PRODUCT LIST ----------
@Composable
fun ProductListScreen(navController: NavHostController) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        items(sampleProducts) { product ->
            ProductCard(product) { navController.navigate("product/${product.id}") }
        }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth().clickable { onClick() }, shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)) {
        Row(modifier = Modifier.padding(12.dp)) {
            Image(painter = painterResource(id = product.imageResId), contentDescription = product.name, modifier = Modifier.size(80.dp).padding(end = 12.dp), contentScale = ContentScale.Crop)
            Column {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text(product.price, color = Color(0xFF1E88E5))
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onClick, modifier = Modifier.padding(top = 6.dp)) { Text("Buy Now") }
            }
        }
    }
}

// ---------- PRODUCT DETAILS ----------
@Composable
fun ProductDetailsScreen(navController: NavHostController, product: Product) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Image(
                painter = painterResource(id = product.imageResId),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(product.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(product.price, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))
            Text(product.description)

            Spacer(modifier = Modifier.height(16.dp))

            // Specifications Section
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Specifications", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Brand / Model: ${product.brandModel}", fontSize = 16.sp)
                    Text("Power Rating: ${product.powerWatts}", fontSize = 16.sp)
                    Text("Weight: ${product.weightKg}", fontSize = 16.sp)
                }
            }
        }

        Button(
            onClick = { navController.navigate("checkout/${product.id}") },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Proceed to Buy", fontSize = 18.sp)
        }
    }
}


// ---------- CHECKOUT ----------
@Composable
fun CheckoutScreen(navController: NavHostController, product: Product) {
    val ctx = LocalContext.current
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf("COD") }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Top) {
        Text("Checkout", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Mobile Number") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Delivery Address") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))
        Text("Payment Options", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth().clickable { paymentMode = "COD" }.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = paymentMode == "COD", onClick = { paymentMode = "COD" })
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cash on Delivery")
        }

        Row(modifier = Modifier.fillMaxWidth().clickable { paymentMode = "Online" }.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = paymentMode == "Online", onClick = { paymentMode = "Online" })
            Spacer(modifier = Modifier.width(8.dp))
            Text("Online Payment")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            if (name.isBlank() || phone.isBlank() || address.isBlank()) {
                Toast.makeText(ctx, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@Button
            }
            if (paymentMode == "COD") {
                Toast.makeText(ctx, "Order placed successfully (COD)", Toast.LENGTH_SHORT).show()
                navController.navigate("dashboard")
            } else {
                navController.navigate("payment/${product.id}")
            }
        }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp)) {
            Text("Confirm Order")
        }
    }
}

// ---------- PAYMENT ----------
@Composable
fun PaymentScreen(navController: NavHostController, product: Product) {
    val ctx = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Pay Securely", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))
        Text("${product.name}", fontWeight = FontWeight.Bold)
        Text(product.price, color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)

        Spacer(modifier = Modifier.height(20.dp))
        Text("Choose a payment method", fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(16.dp))
        // each option one below each
        val paymentOptions = listOf(
            "Google Pay" to R.drawable.gpay,
            "PhonePe" to R.drawable.phonepe,
            "Paytm" to R.drawable.paytm,
            "UPI" to R.drawable.upi,
            "Credit/Debit Card" to R.drawable.card
        )

        paymentOptions.forEach { (label, iconRes) ->
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable {
                    Toast.makeText(ctx, "$label selected. Processing payment...", Toast.LENGTH_SHORT).show()
                    // Simulate payment success
                    Toast.makeText(ctx, "Payment Successful! 🎉", Toast.LENGTH_SHORT).show()
                    navController.navigate("dashboard") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(id = iconRes), contentDescription = label, modifier = Modifier.size(44.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(label, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Secured by Samarth Electricals 🔒", color = MaterialTheme.colorScheme.primary)
    }
}

// ---------- CONTACT ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Contact Us",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E88E5),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFE3F2FD), Color(0xFFFFFFFF))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Contact Info Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Owner",
                                tint = Color(0xFF1E88E5),
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Owner: Sachin Desai", fontWeight = FontWeight.Medium)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Phone",
                                tint = Color(0xFF43A047),
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Phone: +91 98225 45478", fontWeight = FontWeight.Medium)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = Color(0xFFFB8C00),
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Email: support@samarthelectricals.com", fontWeight = FontWeight.Medium)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Address",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Desai Complex, Pimplas Road,\nRahata, Ahmednagar 423107,\nMaharashtra, India",
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Send Message Button
                Button(
                    onClick = { /* TODO: integrate email intent later */ },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Send Message",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
