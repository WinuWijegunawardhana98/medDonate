package com.example.mad_medicine_managment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import androidx.navigation.NavController

class WelcomePage : AppCompatActivity() {

    private lateinit var login: Button
    private lateinit var signUp: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_page)

        // Initialize the buttons
        login = findViewById(R.id.btn_sign_in)
        signUp = findViewById(R.id.btn_sign_up)

        // Set click listeners for each button
        login.setOnClickListener {
            val intent = Intent(this, Main_loging::class.java)
            //intent.putExtra("EXTRA_KEY", "value")
            startActivity(intent)
        }

        signUp.setOnClickListener {
            val intent = Intent(this, PatientRegistration::class.java)
            //intent.putExtra("ANOTHER_EXTRA_KEY", 42)
            startActivity(intent)
        }
    }
}