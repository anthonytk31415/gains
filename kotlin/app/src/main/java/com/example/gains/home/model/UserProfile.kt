package com.example.gains.home.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val user_id : Int,
    val dob: String,
    val height: Float,
    val weight: Float
)