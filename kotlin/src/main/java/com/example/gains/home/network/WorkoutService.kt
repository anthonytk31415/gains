package com.example.gains.home.network

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

object WorkoutService {

    suspend fun sendWorkoutData(): String {
        println("Am in")
        return try {
            val response: HttpResponse = httpClient.get("http://10.0.2.2:8000/test/") {
                contentType(ContentType.Application.Json)
            }
            println("Am out")
            response.bodyAsText()
        } catch (e: Exception) {
            println("Caught exception in sendWorkoutData: ${e.message}")
            throw e
        }
    }
}
