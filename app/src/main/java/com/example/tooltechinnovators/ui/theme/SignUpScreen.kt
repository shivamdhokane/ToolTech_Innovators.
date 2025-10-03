package com.example.tooltechinnovators.ui.theme
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.tooltechinnovators.LoginScreen
import com.example.tooltechinnovators.R
class SignUpScreen : AppCompatActivity() {
    lateinit var getName: EditText
    lateinit var getEmail: EditText
    lateinit var getPhone: EditText
    lateinit var getPassword: EditText
    lateinit var signUpClick: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.layout_signup)

        val myPreference = applicationContext.getSharedPreferences("myPref",
            MODE_PRIVATE)
        val editor = myPreference.edit()

        getName = findViewById(R.id.userName)
        getEmail = findViewById(R.id.userEmail)
        getPhone = findViewById(R.id.userPhone)
        getPassword = findViewById(R.id.userPassword)
        signUpClick = findViewById(R.id.signUpAction)

        signUpClick.setOnClickListener {
            editor.putString("username",getName.text.toString())
            editor.putString("email",getEmail.text.toString())
            editor.putString("phone",getPhone.text.toString())
            editor.putString("password",getPassword.text.toString())
            editor.apply()

            Toast.makeText(this,"Data Saved", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)
        }
    }

}