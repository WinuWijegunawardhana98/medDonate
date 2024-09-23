package com.example.mad_medicine_managment

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class UserAccountDetails : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUserRef: DocumentReference

    private lateinit var accountIdTextView: TextView
    private lateinit var accountNoTextView: TextView
    private lateinit var bankTextView: TextView
    private lateinit var branchTextView: TextView
    private lateinit var mobileNoTextView: TextView

    private lateinit var acc_update_button: Button

    lateinit var account_id: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_account_details)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        accountIdTextView = findViewById(R.id.account_id_textview)
        accountNoTextView = findViewById(R.id.account_no_textview)
        bankTextView = findViewById(R.id.bank_textview)
        branchTextView = findViewById(R.id.branch_textview)
        mobileNoTextView = findViewById(R.id.mobile_no_textview)

        acc_update_button = findViewById(R.id.update_button)

        // Get the current user's UID
        val uid = auth.currentUser!!.uid

// Get a reference to the current user's document
        val userDocRef = db.collection("users").document(uid)

// Query the accounts collection using the current user's reference
        val currentUserRef = db.collection("accounts")
            .whereEqualTo("current_user_ref", userDocRef)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    val account = documentSnapshot.toObject(Account::class.java)
                    accountIdTextView.text = "Account ID: ${account?.account_id}"
                    accountNoTextView.text = "Account No: ${account?.account_no}"
                    bankTextView.text = "Bank: ${account?.bank}"
                    branchTextView.text = "Branch: ${account?.branch}"
                    mobileNoTextView.text = "Mobile No: ${account?.mobile_no}"

                    account_id = account?.account_id.toString()
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting account details", e)
            }

        acc_update_button.setOnClickListener {
            val intent = Intent(this, AccountDetailsUpdate::class.java)
            intent.putExtra("accountID", account_id)
            startActivity(intent)
        }
    }
}