package com.example.mad_medicine_managment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DonarPrescriptionList : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var prescriptionAdapter: DonarPrescriptionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donar_prescription_list)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val currentUserRef = if (auth.currentUser != null) {
            db.collection("users").document(auth.currentUser!!.uid)
        } else {
            // handle the case where currentUser is null
            null
        }

        val listView = findViewById<ListView>(R.id.DonarPrescriptionListView)
        prescriptionAdapter = DonarPrescriptionAdapter(this)
        listView.adapter = prescriptionAdapter

        // Fetch prescriptions with "not accepted" status
        db.collection("prescriptions")
            .whereEqualTo("status", "DonerRequest")
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
            updatePrescriptionwithID(prescription)
        }
    }

    private fun updatePrescriptionwithID(prescription: Prescription) {
        //val prescriptionRef = db.collection("prescriptions").document(prescription.prescriptionId)
        val intent = Intent(this, PatientMainPage::class.java)
        intent.putExtra("PRESCRIPTION_ID", prescription.prescriptionId)
        startActivity(intent)
        finish()
    }
}