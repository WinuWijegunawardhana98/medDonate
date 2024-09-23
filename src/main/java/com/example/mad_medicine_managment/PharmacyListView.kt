package com.example.mad_medicine_managment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class PharmacyListView : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var prescriptionAdapter: PrescriptionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pharmacy_list_view)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val listView = findViewById<ListView>(R.id.prescriptionListView)
        prescriptionAdapter = PrescriptionAdapter(this)
        listView.adapter = prescriptionAdapter

        // Fetch prescriptions with "not accepted" status
        db.collection("prescriptions")
            .whereEqualTo("status", "not accepted")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(this, "Error fetching prescriptions", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                if (value != null) {
                    val prescriptions = value.toObjects(Prescription::class.java)
                    prescriptionAdapter.updatePrescriptions(prescriptions)
                }
            }

        listView.setOnItemClickListener { parent, view, position, id ->
            val prescription = prescriptionAdapter.getItem(position) as Prescription
            acceptPrescription(prescription)
        }
    }

    private fun acceptPrescription(prescription: Prescription) {
        val prescriptionRef = db.collection("prescriptions").document(prescription.prescriptionId)
        prescriptionRef.update("status", "accepted")
            .addOnSuccessListener {
                Toast.makeText(this, "Prescription accepted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error accepting prescription", Toast.LENGTH_SHORT).show()
            }
    }

}