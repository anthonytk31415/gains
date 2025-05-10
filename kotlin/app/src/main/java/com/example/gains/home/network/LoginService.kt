package com.example.gains.home.network

import android.util.Log
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.json.JSONObject

object LoginService {
    suspend fun loginUser(email: String): String {
        try {
            Log.d("LoginService", "Logging in with email: $email")
            val response: HttpResponse = httpClient.post("http://52.24.121.169:8000/api/user/login/") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email))
            }

            val responseBody = response.bodyAsText()

            return responseBody  // Return the response body (or userId)
        } catch (e: Exception) {
            println("Error logging in: ${e.message}")
            throw e
        }
    }
}
