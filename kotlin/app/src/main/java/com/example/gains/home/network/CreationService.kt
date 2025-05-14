package com.example.gains.home.network

import android.util.Log
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

object CreationService {

    suspend fun createUserAccount(
        email: String,
        dob: String? = null,
        height: Float? = null,
        weight: Float? = null
    ): String {
        return try {
            // Build JSON payload with non-null fields
            val payload = mutableMapOf<String, String>()
            payload["email"] = email
            //dob?.let { payload["dob"] = it }
            //height?.let { payload["height"] = it.toString() }
            //weight?.let { payload["weight"] = it.toString() }
            //print("$payload")
            Log.d("Payload", "$payload")
            val response: HttpResponse = httpClient.post("http://52.24.121.169:8000/api/user/create/") {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
            Log.d("Response", "$response")
            response.bodyAsText()
        } catch (e: Exception) {
            println("Error creating user account: ${e.message}")
            throw e
        }
    }
}