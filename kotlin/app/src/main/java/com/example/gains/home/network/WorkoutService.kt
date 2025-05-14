package com.example.gains.home.network

import android.util.Log
import com.example.gains.home.model.WorkoutFormRequest
import com.example.gains.home.model.WorkoutRoutine
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

object WorkoutService {

    suspend fun sendWorkoutData(userId: Int, routine: WorkoutRoutine): String {
        return try {
            Log.d("Got request 1", " $routine")
            val response: HttpResponse = httpClient.post("http://52.24.121.169:8000/api/$userId/schedule/save/") {
                contentType(ContentType.Application.Json)
                setBody(routine)
            }
            Log.d("Response","$response")
            response.bodyAsText()
        } catch (e: Exception) {
            println("Caught exception in sendWorkoutData: ${e.message}")
            throw e
        }
    }

    suspend fun updateWorkoutData(userId: Int, routine: WorkoutRoutine): String {
        return try {
            Log.d("Got request 2", " $routine")
            val response: HttpResponse = httpClient.put("http://52.24.121.169:8000/api/$userId/schedule/update/") {
                contentType(ContentType.Application.Json)
                setBody(routine)
            }
            Log.d("Response","$response")
            response.bodyAsText()
        } catch (e: Exception) {
            println("Caught exception in sendWorkoutData: ${e.message}")
            throw e
        }
    }

    suspend fun submitUserForm(userId: Int, formRequest: WorkoutFormRequest): WorkoutRoutine {
        return try {
            Log.d("FormSubmit", "Request: $formRequest")

            val response: HttpResponse = httpClient.post("http://52.24.121.169:8000/api/$userId/workouts/generate/") {
                contentType(ContentType.Application.Json)
                setBody(formRequest)
            }

            val responseBody = response.bodyAsText()
            Log.d("FormSubmit", "Raw Response: $responseBody")

            // Extract only the `schedule` part of the JSON
            val jsonElement = Json.parseToJsonElement(responseBody).jsonObject
            val scheduleJson = jsonElement["schedule"]

            if (scheduleJson == null) {
                throw Exception("Missing 'schedule' field in response")
            }

            val workoutRoutine = WorkoutRoutine(
                schedule = Json.decodeFromJsonElement(scheduleJson)
            )

            Log.d("FormSubmit", "Parsed WorkoutRoutine: $workoutRoutine")
            return workoutRoutine

        } catch (e: Exception) {
            Log.e("FormSubmit", "Error: ${e.message}", e)
            throw e
        }
    }
}
