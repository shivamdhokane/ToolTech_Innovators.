package com.example.tooltechinnovators

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginScreen : AppCompatActivity() {

    lateinit var getUsername: EditText
    lateinit var getPassword: EditText
    lateinit var loginAction: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_login)

        val myPreference = applicationContext.getSharedPreferences("myPref", MODE_PRIVATE)

        getUsername = findViewById(R.id.username)
        getPassword = findViewById(R.id.password)
        loginAction = findViewById(R.id.login)

        loginAction.setOnClickListener {
            if (getUsername.text.toString() == myPreference.getString("username", "") &&
                getPassword.text.toString() == myPreference.getString("password", "")
            ) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeScreen::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_LONG).show()
            }
        }
    }
}
