package com.example.gains.home.model

object ExerciseMappings {
    val idToName = mapOf(
        1 to "Bench Press",
        2 to "Deadlift",
        3 to "Squats",
        4 to "Overhead Press",
        5 to "Pull Ups"
        // Add more as needed
    )

    val nameToId = idToName.entries.associate { (k, v) -> v to k }

    fun getExerciseName(id: Int): String = idToName[id] ?: "Unknown Exercise"
    fun getExerciseId(name: String): Int = nameToId[name] ?: -1
}
