package com.example.tooltechinnovators

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeScreen : AppCompatActivity() {


    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_home_screen)

        listView = findViewById(R.id.myList)
        val templist = listOf("abc", "pqr", "mnl")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            templist
        )
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, i, _ ->
            val item = templist[i]
            Toast.makeText(this, "Clicked: $item", Toast.LENGTH_SHORT).show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.optionsmenu, menu)
        return true
    }
}
