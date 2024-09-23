package com.example.mad_medicine_managment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class CardPaymentPage : AppCompatActivity() {
    private lateinit var etCardNumber: EditText
    private lateinit var etExpiryDate: EditText
    private lateinit var etCVV: EditText
    private lateinit var amount: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_payment_page)

        etCardNumber = findViewById(R.id.etCardNumber)
        etExpiryDate = findViewById(R.id.etExpiryDate)
        etCVV = findViewById(R.id.etCVV)
        amount = findViewById(R.id.donar_amount)

        val btnSaveCard = findViewById<Button>(R.id.btnSaveCard)
        btnSaveCard.setOnClickListener {
            saveCardDetails()
        }
    }

    private fun saveCardDetails() {
        val cardNumber = etCardNumber.text.toString()
        val expiryDate = etExpiryDate.text.toString()
        val cvv = etCVV.text.toString()
        val total = amount.text.toString()

        val payment = hashMapOf(
            "cardNumber" to cardNumber,
            "expiryDate" to expiryDate,
            "cvv" to cvv,
            "amount" to total
        )

        FirebaseFirestore.getInstance()
            .collection("payment")
            .document()
            .set(payment)
            .addOnSuccessListener {
                Toast.makeText(this, "Card details saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save card details", Toast.LENGTH_SHORT).show()
            }
    }
}