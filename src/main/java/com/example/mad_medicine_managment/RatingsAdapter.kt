package com.example.mad_medicine_managment

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class RatingsAdapter(
    private val ratings: List<Rating>,
    private val currentUserRef: DocumentReference,
    private val db: FirebaseFirestore
) : RecyclerView.Adapter<RatingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rating, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rating = ratings[position]

        holder.commentTextView.text = rating.comment
        holder.ratingBar.rating = rating.rating?.toFloat() ?: 0f
        holder.userTextView.text = rating.username

        if (currentUserRef == rating.userRef) {
            holder.deleteButton.visibility = View.VISIBLE
        } else {
            holder.deleteButton.visibility = View.GONE
        }
        holder.deleteButton.setOnClickListener {
            val documentId = rating.documentId

            // delete the rating from the database
            db.collection("ratings").document(documentId)
                .delete()
                .addOnSuccessListener {
                    // remove the rating from the adapter
                    ratings.toMutableList().removeAt(position)
                    notifyItemRemoved(position)

                    // show toast message
                    Toast.makeText(holder.itemView.context, "Rating deleted successfully", Toast.LENGTH_LONG).show()

                    // navigate to main page
                    val intent = Intent(holder.itemView.context, RatingMainPage::class.java)
                    holder.itemView.context.startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(holder.itemView.context, "Error deleting rating", Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun getItemCount(): Int {
        return ratings.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val userTextView: TextView = itemView.findViewById(R.id.userTextView)
        val deleteButton: Button = itemView.findViewById(R.id.rating_delete_button)

    }
}
