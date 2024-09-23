package com.example.mad_medicine_managment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AccountDetailPage : AppCompatActivity() {
    private lateinit var accountNoEditText: EditText
    private lateinit var bankEditText: EditText
    private lateinit var branchEditText: EditText
    private lateinit var mobileNoEditText: EditText
    private lateinit var saveButton: Button

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_detail_page)

        accountNoEditText = findViewById(R.id.account_no_edit_text)
        bankEditText = findViewById(R.id.bank_edit_text)
        branchEditText = findViewById(R.id.branch_edit_text)
        mobileNoEditText = findViewById(R.id.mobile_no_edit_text)
        saveButton = findViewById(R.id.account_save_button)

        saveButton.setOnClickListener {
            if (validateFields()) {
                saveAccountDetails()
            }
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true

        val accountNo = accountNoEditText.text.toString().trim()
        val bank = bankEditText.text.toString().trim()
        val branch = branchEditText.text.toString().trim()
        val mobileNo = mobileNoEditText.text.toString().trim()

        if (accountNo.isEmpty()) {
            accountNoEditText.error = "Account No. is required"
            isValid = false
        }

        if (bank.isEmpty()) {
            bankEditText.error = "Bank is required"
            isValid = false
        }

        if (branch.isEmpty()) {
            branchEditText.error = "Branch is required"
            isValid = false
        }

        if (mobileNo.isEmpty()) {
            mobileNoEditText.error = "Mobile No. is required"
            isValid = false
        } else if (mobileNo.length != 10) {
            mobileNoEditText.error = "Invalid Mobile No."
            isValid = false
        }

        return isValid
    }

    private fun saveAccountDetails() {
        val accountNo = accountNoEditText.text.toString().trim()
        val bank = bankEditText.text.toString().trim()
        val branch = branchEditText.text.toString().trim()
        val mobileNo = mobileNoEditText.text.toString().trim()
        val currentUserRef = db.collection("users").document(Firebase.auth.currentUser?.uid ?: "")

        val accountDetails = hashMapOf(
            "account_no" to accountNo,
            "bank" to bank,
            "branch" to branch,
            "mobile_no" to mobileNo,
            "current_user_ref" to currentUserRef
        )

        db.collection("accounts")
            .add(accountDetails)
            .addOnSuccessListener { documentReference ->
                db.collection("accounts")
                    .document(documentReference.id)
                    .update("account_id", documentReference.id)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Account details saved successfully!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, RatingMainPage::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error updating account details: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving account details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}