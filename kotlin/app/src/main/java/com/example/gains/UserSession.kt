package com.example.gains

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object UserSession {
    val firebaseUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: "No USER ID"

    var username: String? = null
        set

    var email: String? = null
        set

    var userId: Int? = null
        set

    fun loadUserData(onComplete: (() -> Unit)? = null, onError: ((Exception) -> Unit)? = null) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        username = document.getString("username")
                        email = document.getString("email")
                    }
                    onComplete?.invoke()
                }
                .addOnFailureListener { e ->
                    onError?.invoke(e)
                }
        } else {
            onError?.invoke(Exception("User not logged in"))
        }
    }
}
