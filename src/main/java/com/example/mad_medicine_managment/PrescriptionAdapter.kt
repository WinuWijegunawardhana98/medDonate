package com.example.mad_medicine_managment

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class PrescriptionAdapter(context: Context) :
    ArrayAdapter<Prescription>(context, 0, mutableListOf()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.prescription_item, parent, false)
        }

        val prescription = getItem(position)

        val prescriptionImageView = convertView!!.findViewById<ImageView>(R.id.prescription_image)
        Glide.with(context).load(prescription?.fileUrl).into(prescriptionImageView)

        val prescriptionCommentsTextView = convertView.findViewById<TextView>(R.id.prescription_comments)
        prescriptionCommentsTextView.text = prescription?.comments

        val prescriptionRefID = convertView.findViewById<TextView>(R.id.prescription_ID)
        prescriptionRefID.text = prescription?.prescriptionId


        val downloadButton = convertView.findViewById<Button>(R.id.prescription_download_button)

        val amount = convertView.findViewById<EditText>(R.id.add_pharmacy_amount)

        downloadButton.setOnClickListener {
            downloadPrescription(prescription?.fileUrl)
        }

        val acceptButton = convertView.findViewById<Button>(R.id.prescription_accept_button)
        acceptButton.setOnClickListener {
            val prescriptionId = prescription?.prescriptionId ?: ""
            val total = amount.text.toString()
            val prescriptionRef = FirebaseFirestore.getInstance().collection("prescriptions").document(prescriptionId)
            val amount = 50 // sample amount, replace with your desired amount
            prescriptionRef.update(mapOf(
                "status" to "Accepted",
                "amount" to total
            ))
                .addOnSuccessListener {
                    remove(prescription)
                    notifyDataSetChanged()
                    Toast.makeText(context, "Successfully accept the prescription", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to accept prescription: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        return convertView
    }

    fun updatePrescriptions(prescriptions: List<Prescription>) {
        clear()
        addAll(prescriptions.toMutableList())
        notifyDataSetChanged()
    }

    private fun downloadPrescription(fileUrl: String?) {
        if (fileUrl == null) {
            Toast.makeText(context, "No file found", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("fileUrl", "fileUrl ID: $fileUrl")

        val fileName = fileUrl.substringAfterLast("/")
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl)
        val localFile = File.createTempFile("prescription", ".jpg")
        Log.d("fileUrl", "fileUrl ID: $storageRef")
        storageRef.getFile(localFile)
            .addOnSuccessListener {
                Toast.makeText(context, "File downloaded successfully", Toast.LENGTH_SHORT).show()
//                val intent = Intent(Intent.ACTION_VIEW)
//                intent.setDataAndType(Uri.fromFile(localFile), "image/*")
//                context.startActivity(intent)
                MediaScannerConnection.scanFile(context, arrayOf(localFile.absolutePath), null, null)


            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to download file: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}