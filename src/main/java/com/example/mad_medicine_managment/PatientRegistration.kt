package com.example.mad_medicine_managment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PatientRegistration : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var birthdayEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var designationSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_registration)

        nameEditText = findViewById(R.id.name_edit_text)
        emailEditText = findViewById(R.id.email_edit_text)
        locationEditText = findViewById(R.id.location_edit_text)
        birthdayEditText = findViewById(R.id.birthday_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text)
        registerButton = findViewById(R.id.registerButton)
        designationSpinner = findViewById<Spinner>(R.id.designation_spinner)

        auth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val location = locationEditText.text.toString().trim()
            val birthday = birthdayEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val designation = designationSpinner.selectedItem.toString().trim()

            if (name.isEmpty()) {
                nameEditText.error = "Please enter your name"
                nameEditText.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                emailEditText.error = "Please enter your email"
                emailEditText.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.error = "Please enter a valid email"
                emailEditText.requestFocus()
                return@setOnClickListener
            }

            if (location.isEmpty()) {
                locationEditText.error = "Please enter your location"
                locationEditText.requestFocus()
                return@setOnClickListener
            }

            if (birthday.isEmpty()) {
                birthdayEditText.error = "Please enter your birthday"
                birthdayEditText.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordEditText.error = "Please enter your password"
                passwordEditText.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                passwordEditText.error = "Password should be at least 6 characters long"
                passwordEditText.requestFocus()
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                confirmPasswordEditText.error = "Please confirm your password"
                confirmPasswordEditText.requestFocus()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                confirmPasswordEditText.error = "Passwords do not match"
                confirmPasswordEditText.requestFocus()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //Log.d("myTag", "This is my message");
                        val user = auth.currentUser
                        val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        user?.updateProfile(userProfileChangeRequest)
                            ?.addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    val db = Firebase.firestore
                                    val userDocRef = db.collection("users").document(user!!.uid)
                                    val newUser = hashMapOf(
                                        "name" to name,
                                        "email" to email,
                                        "location" to location,
                                        "birthday" to birthday,
                                        "designation" to designation
                                    )
                                    userDocRef.set(newUser)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                this,
                                                "User registered successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            if(designation == "Patient" || designation == "Pharmacist"){
                                                val intent = Intent(this, AccountDetailPage::class.java)
                                                startActivity(intent)
                                                finish()
                                            }else{
                                                val intent = Intent(this, RatingMainPage::class.java)
                                                startActivity(intent)
                                                finish()
                                            }

                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(
                                                this,
                                                "Registration failed. ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }


                                    Toast.makeText(
                                        this,
                                        "User registered successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Registration failed. ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
