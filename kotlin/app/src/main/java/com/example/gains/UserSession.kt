package com.example.gains

import com.google.firebase.auth.FirebaseAuth

object UserSession {
    val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: "No USER ID"
}
