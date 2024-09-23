package com.example.mad_medicine_managment

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class PatientMainPage : AppCompatActivity() {

    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()

    private val currentUser = FirebaseAuth.getInstance().currentUser

    private lateinit var chooseFileButton: Button
    private lateinit var submitButton: Button
    private lateinit var commentsEditText: EditText
    private lateinit var fileNameTextView: TextView
    private lateinit var imageView: ImageView

    private var fileUrl: String? = null
    private var prescriptionIdToUpdate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_main_page)

        chooseFileButton = findViewById(R.id.choose_file_button)
        submitButton = findViewById(R.id.submit_button)
        commentsEditText = findViewById(R.id.comments_edit_text)
        fileNameTextView = findViewById(R.id.fileNameTextView)
        imageView = findViewById(R.id.imageView)

        val prescriptionId = intent.getStringExtra("PRESCRIPTION_ID")
        if (prescriptionId != null) {
            prescriptionIdToUpdate = prescriptionId
            submitButton.text = "Update Prescription"
            firebaseFirestore.collection("prescriptions")
                .document(prescriptionId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val prescription = documentSnapshot.toObject(Prescription::class.java)
                    if (prescription != null) {
                        commentsEditText.setText(prescription.comments)
                        //fileNameTextView.text = prescription.fileName
                        chooseFileButton.text = "Update"
                        Glide.with(this)
                            .load(prescription.fileUrl)
                            .into(imageView)
                    }
                }
        }

        chooseFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, REQUEST_CODE_FILE_CHOOSER)
        }

        submitButton.setOnClickListener {
            val comments = commentsEditText.text.toString().trim()

            if (fileUrl != null && comments.isNotEmpty() && currentUser != null) {

                if (prescriptionIdToUpdate != null) {
                    // Update existing prescription
                    firebaseFirestore.collection("prescriptions")
                        .document(prescriptionIdToUpdate!!)
                        .update(mapOf(
                            "fileUrl" to fileUrl!!,
                            "comments" to comments,
                            "timestamp" to System.currentTimeMillis(),
                        ))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Prescription updated successfully", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, PatientPrescriptionList::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update prescription", Toast.LENGTH_LONG).show()
                        }
                } else {
                    // Add new prescription
                    val prescription = Prescription(
                        prescriptionId = "", // initialize prescriptionId to empty string
                        userRef = firebaseFirestore.collection("users").document(currentUser.uid),
                        fileUrl = fileUrl!!,
                        comments = comments,
                        status =  "not accepted",
                        timestamp = System.currentTimeMillis(),
                        amount =  "0.00"
                    )

                    firebaseFirestore.collection("prescriptions")
                        .add(prescription)
                        .addOnSuccessListener { documentReference ->
                            // Get the ID of the document reference
                            val prescriptionId = documentReference.id

                            // Update the prescription object with the ID
                            prescription.prescriptionId = prescriptionId

                            // Update the Firestore document with the prescription object
                            documentReference.set(prescription)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Prescription added successfully", Toast.LENGTH_LONG).show()
                                    val intent = Intent(this, PatientPrescriptionList::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to add prescription", Toast.LENGTH_LONG).show()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to add prescription", Toast.LENGTH_LONG).show()
                        }
                }
            } else {
                Toast.makeText(this, "Please choose a file and add comments", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FILE_CHOOSER && resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            if (fileUri != null) {
                val fileName = fileUri.lastPathSegment
                val fileRef = firebaseStorage.reference.child("prescriptions").child(fileName!!)
                val uploadTask = fileRef.putFile(fileUri)
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        throw task.exception!!
                    }
                    fileRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        fileUrl = task.result.toString()
                        Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_LONG).show()
                        fileNameTextView.text = fileName
                        Glide.with(this)
                            .load(fileUrl)
                            .into(imageView)
                    } else {
                        Toast.makeText(this, "Failed to upload file", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_FILE_CHOOSER = 123
    }
}