package com.example.gains.home.network


//import io.ktor.client.call.*
//import io.ktor.client.request.*
//import io.ktor.http.*
//import kotlinx.serialization.json.Json
//import com.example.gains.home.model.WorkoutRoutine
//
//object WorkoutApi {
//
//    suspend fun getWorkout(): WorkoutRoutine {
//        val response = httpClient.get("http://10.0.2.2:8000/test/") {
//            contentType(ContentType.Application.Json)
//        }
//
//        val responseBody = response.body<String>()
//
//        return Json { ignoreUnknownKeys = true }.decodeFromString(WorkoutRoutine.serializer(), responseBody)
//    }
//}

import com.example.gains.home.model.WorkoutRoutine
import kotlinx.serialization.json.Json

object WorkoutApi {

    suspend fun getWorkout(): WorkoutRoutine {
        val mockJson = """
            {
              "days_per_week": 4,
              "focus": "Legs, Biceps",
              "experience_level": "Intermediate",
              "location": "Home",
              "schedule": [
                {
                  "day": "Day 1",
                  "exercises": [
                    {"name": "Leg Raises", "sets": 3, "reps": "8-12"},
                    {"name": "Lunges", "sets": 3, "reps": "10-15 per leg"},
                    {"name": "Glute Bridges", "sets": 3, "reps": "15-20"},
                    {"name": "Calf Raises", "sets": 3, "reps": "15-20"}
                  ]
                },
                {
                  "day": "Day 2",
                  "exercises": [
                    {"name": "Romanian Deadlifts (RDLs)", "sets": 3, "reps": "8-12"},
                    {"name": "Hamstring Curls (using resistance bands or towels)", "sets": 3, "reps": "10-15"},
                    {"name": "Hip Thrusts (using a sturdy elevated surface)", "sets": 3, "reps": "12-15"},
                    {"name": "Single-Leg Calf Raises", "sets": 3, "reps": "10-15 per leg"}
                  ]
                },
                {
                  "day": "Day 3",
                  "exercises": [
                    {"name": "Goblet Squats (using a heavy object)", "sets": 3, "reps": "8-12"},
                    {"name": "Walking Lunges", "sets": 3, "reps": "10-15 per leg"},
                    {"name": "Glute Kickbacks (using resistance bands)", "sets": 3, "reps": "15-20 per leg"},
                    {"name": "Jump Squats", "sets": 3, "reps": "8-12"}
                  ]
                },
                {
                  "day": "Day 4",
                  "exercises": [
                    {"name": "Sumo Squats", "sets": 3, "reps": "10-15", "isDone": true},
                    {"name": "Good Mornings (using a resistance band or broomstick)", "sets": 3, "reps": "10-12","isDone": true},
                    {"name": "Bulgarian Split Squats", "sets": 3, "reps": "10-12 per leg","isDone": true},
                    {"name": "Seated Calf Raises (using a chair or elevated surface)", "sets": 3, "reps": "15-20","isDone": true}
                  ]
                }
              ]
            }
        """.trimIndent()

        return Json { ignoreUnknownKeys = true }
            .decodeFromString(mockJson)
    }
}