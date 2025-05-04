package com.example.gains.onboarding

import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthSingleton {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
}