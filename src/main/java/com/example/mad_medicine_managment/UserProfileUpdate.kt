package com.example.mad_medicine_managment

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileUpdate : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var birthdayEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_update)

        // Retrieve the user's current details from the database
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        // Initialize UI elements
        nameEditText = findViewById(R.id.nameEditText)
        birthdayEditText = findViewById(R.id.birthdayEditText)
        locationEditText = findViewById(R.id.locationEditText)
        saveButton = findViewById(R.id.saveButton)

        if (uid != null) {
            db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name")
                        val birthday = document.getString("birthday")
                        val location = document.getString("location")

                        // Populate the EditTexts with the values
                        nameEditText.setText(name)
                        birthdayEditText.setText(birthday)
                        locationEditText.setText(location)
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        } else {
            // User is not logged in
        }
        saveButton.setOnClickListener {
            // Retrieve the values from the EditTexts
            val name = nameEditText.text.toString()
            val birthday = birthdayEditText.text.toString()
            val location = locationEditText.text.toString()

            // Update the user's details in the database
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val db = FirebaseFirestore.getInstance()

            if (uid != null) {
                db.collection("users")
                    .document(uid)
                    .update(
                        "name", name,
                        "birthday", birthday,
                        "location", location
                    )
                    .addOnSuccessListener {
                        // Display a message indicating success
                        Toast.makeText(
                            this, "User profile updated.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this, userMainProfile::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "Update failed with ", exception)
                        // Display a message indicating failure
                        Toast.makeText(
                            this, "User profile update failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                // User is not logged in
            }
        }

    }
}