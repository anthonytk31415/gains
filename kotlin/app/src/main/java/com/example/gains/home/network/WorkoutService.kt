package com.example.gains.home.network

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

object WorkoutService {

    suspend fun sendWorkoutData(workoutRoutine: List<List<Map<String, Any>>>): String {
        return try {
            val response: HttpResponse = httpClient.post("http://10.0.2.2:8000/api/workouts/save/") {
                contentType(ContentType.Application.Json)
                setBody(workoutRoutine)
            }
            response.bodyAsText()
        } catch (e: Exception) {
            println("Caught exception in sendWorkoutData: ${e.message}")
            throw e
        }
    }
}
