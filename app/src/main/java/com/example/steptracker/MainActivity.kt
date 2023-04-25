package com.example.steptracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private lateinit var btnCountSteps: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCountSteps = findViewById(R.id.btn_count_steps)

        btnCountSteps.setOnClickListener {
            val intent = Intent(this, CountSteps::class.java)
            startActivity(intent)
        }
    }
}