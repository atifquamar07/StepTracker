package com.example.steptracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private lateinit var btnCountSteps: Button
    private lateinit var etHeight: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCountSteps = findViewById(R.id.btn_count_steps)
        etHeight = findViewById(R.id.et_height)
        btnCountSteps.setOnClickListener {
            var height = 170
            if(etHeight.text.toString() != ""){
                height = etHeight.text.toString().toInt()
            }
            val bundle = Bundle()
            bundle.putInt("height", height)
            val intent = Intent(this, CountSteps::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }


    }
}