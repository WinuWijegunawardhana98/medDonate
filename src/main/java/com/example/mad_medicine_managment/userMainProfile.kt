package com.example.mad_medicine_managment

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

class userMainProfile : AppCompatActivity() {
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var designationTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var birthdayTextView: TextView
    private lateinit var updateButton: Button
    private lateinit var logoutButton: Button
    private lateinit var viewAccountButton: Button

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserRef: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_main_profile)

        nameTextView = findViewById(R.id.userNameTextView)
        emailTextView = findViewById(R.id.userEmailTextView)
        designationTextView = findViewById(R.id.userDesignationTextView)
        locationTextView = findViewById(R.id.userLocationTextView)
        birthdayTextView = findViewById(R.id.userBirthdayTextView)
        updateButton = findViewById(R.id.userUpdateButton)
        logoutButton = findViewById(R.id.userLogoutButton)
        viewAccountButton = findViewById(R.id.userAccountButton)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserRef = db.collection("users").document(auth.currentUser!!.uid)

        // set click listener for update button
        updateButton.setOnClickListener {
            // navigate to update profile page
            val intent = Intent(this, UserProfileUpdate::class.java)
            startActivity(intent)
        }

        // set click listener for view account button
        viewAccountButton.setOnClickListener {
            // navigate to update profile page
            val intent = Intent(this, UserAccountDetails::class.java)
            startActivity(intent)
        }

        // set click listener for logout button
        logoutButton.setOnClickListener {
            // sign out user and navigate to login page
            auth.signOut()
            val intent = Intent(this, WelcomePage::class.java)
            startActivity(intent)
            finish()
        }

        // get user details from database
        currentUserRef.get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(com.example.mad_medicine_managment.User::class.java)
            if (user != null) {
                nameTextView.text = user.name
                emailTextView.text = "Email: ${user.email}"
                designationTextView.text = "User Type: ${user.designation}"
                locationTextView.text = "Location: ${user.location}"
                birthdayTextView.text = "Birthday: ${user.birthday}"
            }
        }
}
}