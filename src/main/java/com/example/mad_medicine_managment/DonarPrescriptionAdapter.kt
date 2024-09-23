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
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class DonarPrescriptionAdapter(context: Context) :
    ArrayAdapter<Prescription>(context, 0, mutableListOf()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.donar_prescription_item, parent, false)
        }

        val prescription = getItem(position)

        val prescriptionImageView = convertView!!.findViewById<ImageView>(R.id.doanr_prescription_image)
        Glide.with(context).load(prescription?.fileUrl).into(prescriptionImageView)

        val prescriptionCommentsTextView = convertView.findViewById<TextView>(R.id.doanr_prescription_comments)
        prescriptionCommentsTextView.text = prescription?.comments


        val prescriptionRefID = convertView.findViewById<TextView>(R.id.doanr_prescription_ID)
        prescriptionRefID.text = prescription?.prescriptionId

        val donarPrescriptionAmount = convertView.findViewById<TextView>(R.id.doanr_prescription_crud_amount)
        donarPrescriptionAmount.text = "Amount : ${prescription?.amount}"

        val acceptButton = convertView.findViewById<Button>(R.id.doanr_prescription_accept_button)


        // Set up the update button
        val downloadButton = convertView.findViewById<Button>(R.id.doanr_prescription_download_button)
//        val accountDetailButton = convertView.findViewById<Button>(R.id.doanr_prescription_view_button)
//
//        accountDetailButton.setOnClickListener {
//            val prescription = getItem(position)
//            val prescriptionId = prescription?.prescriptionId ?: ""
//            Log.d("Prescription", "Prescription ID: $prescriptionId")
//
//            // Start the PatientMainPage activity with the prescription ID
//            val intent = Intent(context, UserAccountDetails::class.java)
//            intent.putExtra("PRESCRIPTION_ID", prescription?.prescriptionId)
//            context.startActivity(intent)
//        }

        downloadButton.setOnClickListener {
           downloadPrescription(prescription?.fileUrl)
        }

//        // Set up the "Request for Donor" button
//        val requestButton = convertView.findViewById<Button>(R.id.prescription_crud_request_button)
//        if (prescription?.status == "Accepted") {
//            requestButton.visibility = View.VISIBLE
//        } else {
//            requestButton.visibility = View.GONE
//        }

        acceptButton.setOnClickListener {
            // Handle click on the "Request for Donor" button
            val prescriptionId = prescription?.prescriptionId ?: ""
            val prescriptionRef = FirebaseFirestore.getInstance().collection("prescriptions").document(prescriptionId)
            prescriptionRef.update("status", "DonerAccepted")
                .addOnSuccessListener {
                    remove(prescription)
                    notifyDataSetChanged()
                    Toast.makeText(context, "Successfully sent the prescription", Toast.LENGTH_LONG).show()
                    val intent = Intent(context, CardPaymentPage::class.java)
                    intent.putExtra("PRESCRIPTION_ID", prescription?.prescriptionId)
                    context.startActivity(intent)
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