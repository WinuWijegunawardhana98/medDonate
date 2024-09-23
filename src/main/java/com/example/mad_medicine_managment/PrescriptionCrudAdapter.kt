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

class PrescriptionCrudAdapter(context: Context) :
    ArrayAdapter<Prescription>(context, 0, mutableListOf()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.prescrption_crud_item, parent, false)
        }

        val prescription = getItem(position)

        val prescriptionImageView = convertView!!.findViewById<ImageView>(R.id.prescription_crud_image)
        Glide.with(context).load(prescription?.fileUrl).into(prescriptionImageView)

        val prescriptionCommentsTextView = convertView.findViewById<TextView>(R.id.prescription_crud_comments)
        prescriptionCommentsTextView.text = prescription?.comments

        val prescription_pending = convertView.findViewById<TextView>(R.id.prescription_Status_pending)

        val prescription_accept = convertView.findViewById<TextView>(R.id.prescription_Status_accept)

        val prescription_request = convertView.findViewById<TextView>(R.id.prescription_Status_requesr)
        val prescription_request_donar_accept = convertView.findViewById<TextView>(R.id.prescription_Status_donarAccept)


        val prescriptionRefID = convertView.findViewById<TextView>(R.id.prescription_crud_ID)
        prescriptionRefID.text = prescription?.prescriptionId

        val prescriptionCrudAmount = convertView.findViewById<TextView>(R.id.prescription_crud_amount)
        prescriptionCrudAmount.text = "Amount : ${prescription?.amount}"

        val deleteButton = convertView.findViewById<Button>(R.id.prescription_crud_delete_button)

        deleteButton.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val prescription = getItem(position)
            val prescriptionId = prescription?.prescriptionId ?: ""

            db.collection("prescriptions").document(prescriptionId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Prescription deleted successfully", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error deleting prescription: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        // Set up the update button
        val updateButton = convertView.findViewById<Button>(R.id.prescription_crud_update_button)
        updateButton.setOnClickListener {
            val prescription = getItem(position)
            val prescriptionId = prescription?.prescriptionId ?: ""
            Log.d("Prescription", "Prescription ID: $prescriptionId")

            // Start the PatientMainPage activity with the prescription ID
            val intent = Intent(context, PatientMainPage::class.java)
            intent.putExtra("PRESCRIPTION_ID", prescription?.prescriptionId)
            context.startActivity(intent)
        }

        // Set up the "Request for Donor" button
        val requestButton = convertView.findViewById<Button>(R.id.prescription_crud_request_button)
        if (prescription?.status == "Accepted") {
            requestButton.visibility = View.VISIBLE
        } else {
            requestButton.visibility = View.GONE
        }

        // Set up the "Status"
        if (prescription?.status == "not accepted") {
            prescription_pending.visibility = View.VISIBLE
        } else if(prescription?.status == "Accepted"){
            prescription_accept.visibility = View.VISIBLE
        }else if(prescription?.status == "DonerRequest"){
            prescription_request.visibility = View.VISIBLE
        }else{
            prescription_request_donar_accept.visibility = View.VISIBLE
        }

        requestButton.setOnClickListener {
            // Handle click on the "Request for Donor" button
            val prescriptionId = prescription?.prescriptionId ?: ""
            val prescriptionRef = FirebaseFirestore.getInstance().collection("prescriptions").document(prescriptionId)
            prescriptionRef.update("status", "DonerRequest")
                .addOnSuccessListener {
                    remove(prescription)
                    notifyDataSetChanged()
                    Toast.makeText(context, "Successfully sent the prescription", Toast.LENGTH_LONG).show()

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