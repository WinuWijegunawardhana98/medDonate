package com.example.mad_medicine_managment

import android.content.ContentValues.TAG
import android.content.Intent
import android.media.Rating
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class RatingMainPage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userDocRef: DocumentReference
    private lateinit var ratingBar: RatingBar
    private lateinit var commentEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var addPrescription: Button
    private lateinit var viewPrescription: Button
    private lateinit var viewPharmacyPrescription: Button
    private lateinit var viewDonarPrescription: Button
    private lateinit var viewRatings: TextView
    private val ratings = mutableListOf<Rating>()

    private lateinit var listView: ListView
    private lateinit var adapter: RatingsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating_main_page)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        userDocRef = db.collection("users").document(auth.currentUser!!.uid)

        ratingBar = findViewById(R.id.rating_bar)
        commentEditText = findViewById(R.id.comment_rating_text)
        submitButton = findViewById(R.id.submit_button)
        addPrescription = findViewById(R.id.add_new_prescription_btn)
        viewPrescription = findViewById(R.id.view_prescription_btn)
        viewPharmacyPrescription = findViewById(R.id.View_Pharmacy_pres_button)
        viewDonarPrescription = findViewById(R.id.View_donor_pres_button)
        viewRatings = findViewById(R.id.seeAllText)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        val nav = findViewById<BottomNavigationView>(R.id.nav)

        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // handle home click
                    val intent = Intent(this, RatingMainPage::class.java)
                    startActivity(intent)
                    true
                }
                R.id.profile -> {
                    // handle profile click
                    val intent = Intent(this, userMainProfile::class.java)
                    startActivity(intent)
                    true
                }
                R.id.setting -> {
                    // handle setting click
                    true
                }
                else -> false
            }
        }

        Log.d(TAG, "User designation: $uid")

        if (uid != null) {
            db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val designation = document.getString("designation")
                        Log.d(TAG, "User designation: $designation")
                        if(designation == "Patient"){
                            // To set the button visibility to visible:
                            Log.d(TAG, "Patient designation found, showing addPrescription and viewPrescription buttons")

                            addPrescription.visibility = View.VISIBLE
                            viewPrescription.visibility = View.VISIBLE

                        }else if(designation == "Pharmacist"){
                            Log.d(TAG, "Pharmacist designation found, showing viewPharmacyPrescription button")

                            viewPharmacyPrescription.visibility = View.VISIBLE
                        }else{
                            Log.d(TAG, "No matching designation found, showing viewDonarPrescription button")

                            viewDonarPrescription.visibility = View.VISIBLE
                        }

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

        viewRatings.setOnClickListener {
            val intent = Intent(this, RatingListView::class.java)
            startActivity(intent)
        }

        addPrescription.setOnClickListener {
            val intent = Intent(this, PatientMainPage::class.java)
            startActivity(intent)
        }

        viewPharmacyPrescription.setOnClickListener {
            val intent = Intent(this, PharmacyListView::class.java)
            startActivity(intent)
        }

        viewPrescription.setOnClickListener {
            val intent = Intent(this, PatientPrescriptionList::class.java)
            startActivity(intent)
        }

        viewDonarPrescription.setOnClickListener {
            val intent = Intent(this, DonarPrescriptionList::class.java)
            startActivity(intent)
        }

        submitButton.setOnClickListener {
            val ratingValue = ratingBar.rating.toInt()
            val commentText = commentEditText.text.toString()

            if (ratingValue <= 0 || commentText.isBlank()) {
                Toast.makeText(this, "Please provide rating and comment", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("ratings").add(
                hashMapOf(
                    "rating" to ratingValue,
                    "comment" to commentText,
                    "userRef" to userDocRef,
                    "username" to auth.currentUser?.displayName,
                    "documentId" to ""
                )
            ).addOnSuccessListener { documentReference ->
                // Update the document with the ID
                documentReference.update("documentId", documentReference.id)
                    .addOnSuccessListener {
                        // Document ID added successfully
                        Toast.makeText(this, "Rating submitted successfully", Toast.LENGTH_SHORT)
                            .show()
                        ratingBar.rating = 0.0f
                        commentEditText.text.clear()
                    }
                    .addOnFailureListener { e ->
                        // Failed to add document ID
                        Log.e(TAG, "Error adding document ID", e)
                        Toast.makeText(this, "Error submitting rating", Toast.LENGTH_SHORT).show()
                    }
            }
        }


    }
}