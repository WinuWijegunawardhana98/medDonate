package com.example.mad_medicine_managment

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class AccountDetailsUpdate : AppCompatActivity() {

    private lateinit var accountNo: EditText
    private lateinit var mobile: EditText
    private lateinit var bank: EditText
    private lateinit var branch: EditText
    private lateinit var accountID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details_update)

        accountNo = findViewById(R.id.accountNoEditText)
        mobile = findViewById(R.id.mobileNoEditText)
        bank = findViewById(R.id.bankEditText)
        branch = findViewById(R.id.branchEditText)

        // get the accountID passed from the previous activity
        accountID = intent.getStringExtra("accountID").toString()

        // get the Firestore document reference for the account with the given accountID
        val accountRef = FirebaseFirestore.getInstance().collection("accounts").document(accountID)

        // fetch the data from Firestore and populate the EditTexts with the retrieved data
        accountRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // retrieve the data from the document and populate the EditTexts
                    accountNo.setText(document.getString("account_no"))
                    mobile.setText(document.getString("mobile_no"))
                    bank.setText(document.getString("bank"))
                    branch.setText(document.getString("branch"))
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        // set onClickListener for the update button
        val updateButton = findViewById<Button>(R.id.account_updateButton)
        updateButton.setOnClickListener {
            // get the new values entered by the user
            val newAccountNo = accountNo.text.toString()
            val newMobile = mobile.text.toString()
            val newBank = bank.text.toString()
            val newBranch = branch.text.toString()

            // update the Firestore document with the new values
            accountRef.update(
                "account_no", newAccountNo,
                "mobile_no", newMobile,
                "bank", newBank,
                "branch", newBranch
            )
                .addOnSuccessListener {
                    Toast.makeText(this, "Account details updated successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, UserAccountDetails::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating document", e)
                    Toast.makeText(this, "Failed to update account details", Toast.LENGTH_SHORT).show()
                }
        }

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


    }
}