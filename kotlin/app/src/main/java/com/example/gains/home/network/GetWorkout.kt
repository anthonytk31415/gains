package com.example.gains.home.network


//import io.ktor.client.call.*
//import io.ktor.client.request.*
//import io.ktor.http.*
//import kotlinx.serialization.json.Json
//import com.example.gains.home.model.WorkoutRoutine
//
//object WorkoutApi {
//
//    suspend fun getWorkout(userId: String): WorkoutRoutine {
//        val response = httpClient.get("http://10.0.2.2:8000/api/$userId/workouts/current_week/") {
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

    suspend fun getWorkout(userId: Int): WorkoutRoutine {
        val mockJson = """
             {
  
                 "creation_date": "2025-04-28",
                   "schedule":[
                      {
             	    
             	    "workout_id" : 2,
                         "exercise_sets":[
                            {
                               "exercise_id":1,
                               "sets":3,
                               "reps":8,
                               "weight":52.5
             		  
                            },
                            {
                               "exercise_id":2,
                               "sets":3,
                               "reps":8,
                               "weight":42.5
                            },
                            {
                               "exercise_id":3,
                               "sets":3,
                               "reps":8,
                               "weight":42.5
                            }
                         ]
                      },
                      {
             	 "workout_id" : 2,
             	 
              
                         "exercises":[
                            {
                               "exercise_id":1,
                               "sets":1,
                               "reps":5,
                               "weight":65.0
                            },
                            {
                               "exercise_id":2,
                               "sets":3,
                               "reps":10,
                               "weight":85.0
                            },
                            {
                               "exercise_id":3,
                               "sets":3,
                               "reps":8,
                               "weight":50.0
                            }
                         ]
                      }
                   ]
                
             }
        """.trimIndent()

        return Json { ignoreUnknownKeys = true }
            .decodeFromString(mockJson)
    }
}