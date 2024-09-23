package com.example.mad_medicine_managment

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class RatingListView : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val currentUserRef: DocumentReference = FirebaseAuth.getInstance().currentUser?.let {
        FirebaseFirestore.getInstance().collection("users").document(it.uid)
    } ?: throw IllegalStateException("Current user is null")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating_list_view)

        recyclerView = findViewById(R.id.recyclerView)

        // Set the layout manager for the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set the adapter for the RecyclerView (adapter is not set yet)
        recyclerView.adapter = null

        // Retrieve the ratings from Firestore and pass them to the RecyclerView adapter
        retrieveRatings()


    }

    private fun retrieveRatings() {
        val db = FirebaseFirestore.getInstance()

        db.collection("ratings")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(this, "Error fetching ratings", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                if (value != null) {
                    val ratings = mutableListOf<Rating>()
                    for (document in value) {
                        val userRef = document.getDocumentReference("userRef")
                        userRef?.get()?.addOnSuccessListener { userDocument ->
                            val rating = Rating(
                                documentId = document.getString("documentId") ?: "",
                                comment = document.getString("comment") ?: "",
                                rating = document.getLong("rating")?.toInt() ?: 0,
                                userRef = userRef,
                                username = document?.getString("username") ?: ""
                            )

                            ratings.add(rating)
                            // Update the ListView adapter with the retrieved ratings
                            recyclerView.adapter = RatingsAdapter(ratings,currentUserRef,db)
                        }
                    }
                }
            }
    }
}