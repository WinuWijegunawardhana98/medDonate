package com.example.mad_medicine_managment

import com.google.firebase.firestore.DocumentReference

class Prescription {
    var prescriptionId: String = ""
    var userRef: DocumentReference? = null
    var fileUrl: String = ""
    var comments: String = ""
    var status: String = ""
    var timestamp: Long = 0L
    var amount: String = "" // add new field for amount

    constructor()

    constructor(
        prescriptionId: String,
        userRef: DocumentReference,
        fileUrl: String,
        comments: String,
        status: String,
        timestamp: Long,
        amount: String // pass amount to constructor
    ) {
        this.prescriptionId = prescriptionId
        this.userRef = userRef
        this.fileUrl = fileUrl
        this.comments = comments
        this.status = status
        this.timestamp = timestamp
        this.amount = amount // set amount value
    }
}