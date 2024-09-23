package com.example.mad_medicine_managment

import com.google.firebase.firestore.DocumentReference

data class Rating(
    val documentId: String = "",
    val username: String = "",
    val userRef: DocumentReference? = null,
    val rating: Int = 0,
    val comment: String = ""
) {
    constructor() : this("", "", null, 0, "")

    constructor(documentId: String, userRef: DocumentReference) : this(documentId, "", userRef, 0, "")

    constructor(documentId: String, comment: String, rating: Int, userRef: DocumentReference, username: String) : this(
        documentId, username, userRef, rating, comment
    )
}