package com.example.gains.home.network


import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import com.example.gains.home.model.WorkoutRoutine
import android.util.Log
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.serializer

object WorkoutApi {

    suspend fun getWorkout(userId: Int): WorkoutRoutine {
        try{
            Log.d("Making requet","$userId")
            //val response = httpClient.get("http://52.24.121.169:8000/api/$userId/workouts/current_week/") {
            //    contentType(ContentType.Application.Json)
            //}
            val response = httpClient.get("http://52.24.121.169:8000/api/$userId/workouts/all/") {
                contentType(ContentType.Application.Json)
            }
            val responseBody = response.body<String>()
            Log.d("Workout","$response")

            return Json { ignoreUnknownKeys = true }.decodeFromString(WorkoutRoutine.serializer(), responseBody)

        }
        catch (e: Exception) {
            println("Error logging in: ${e.message}")
            throw e
        }
    }
}


