package com.example.gains.home.network

import android.util.Log
import com.example.gains.home.model.UserProfile
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.decodeFromJsonElement

object UserService {

    suspend fun sendUserData(userId: Int, userProfile: UserProfile): String {
        return try {
            val response: HttpResponse = httpClient.put("http://52.24.121.169:8000/api/$userId/user/update/") {
                contentType(ContentType.Application.Json)
                setBody(userProfile)
            }
            Log.d(response.bodyAsText(), "Hello")
            response.bodyAsText()

        } catch (e: Exception) {
            println("Caught exception in sendUserData: ${e.message}")
            throw e
        }
    }

    suspend fun getUserDetails(userId: Int): UserProfile {
        val response = httpClient.get("http://52.24.121.169:8000/api/$userId/user/") {
            contentType(ContentType.Application.Json)
        }

        val responseBody = response.body<String>()
        Log.d("1", responseBody)

        val jsonElement = Json.parseToJsonElement(responseBody)
        val userElement = jsonElement.jsonObject["user"]

        return Json { ignoreUnknownKeys = true }
            .decodeFromJsonElement(UserProfile.serializer(), userElement!!)
    }

}