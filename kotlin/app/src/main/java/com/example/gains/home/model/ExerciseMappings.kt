package com.example.gains.home.model

object ExerciseMappings {

    val idToName = mapOf(
        1 to "Bench Press",
        2 to "Dips",
        3 to "Back Squat",
        4 to "Front Squat",
        5 to "Barbell Row",
        6 to "Bent-over Dumbell Row",
        7 to "Box Jumps",
        8 to "Bulgarian Split Squat",
        9 to "Chest Fly",
        10 to "Clean and Press",
        11 to "Deadlift",
        12 to "Incline Bench Press",
        13 to "Decline Bench Press",
        14 to "Kettlebell Swings",
        15 to "Lunges",
        16 to "Mountain Climbers",
        17 to "Overhead Press",
        18 to "Plank",
        19 to "Power Clean",
        20 to "Pull-ups",
        21 to "Push-ups",
        22 to "Sit-ups",
        23 to "Romanian Deadlift",
        24 to "Russian Twists",
        25 to "Hip Thrust",
        26 to "Wall Balls",
        27 to "Lateral Raises",
        28 to "Egyptian Lateral Raise",
        29 to "Jogging",
        30 to "Running",
        31 to "Sprinting",
        32 to "Goblet Squats",
        33 to "Leg Press",
        34 to "Calf Raises",
        35 to "Leg Extension",
        36 to "Leg Curls",
        37 to "Good mornings",
        38 to "Single-Leg Deadlift",
        39 to "Triceps Extension",
        40 to "Skull Crushers",
        41 to "Tricep Pushdown",
        42 to "Diamond Push-ups",
        43 to "Dumbell Bench Press",
        44 to "Close-grip Bench Press",
        45 to "Close-grip Dumbell Bench Press",
        46 to "Bird Dog",
        47 to "Heel Tap",
        48 to "Side Plank",
        49 to "Side Plank with Dips",
        50 to "Flutter Kicks",
        51 to "Dead Bugs",
        52 to "V-ups"
    )


    val nameToId = idToName.entries.associate { (k, v) -> v to k }

    fun getExerciseName(id: Int): String = idToName[id] ?: "Unknown Exercise"
    fun getExerciseId(name: String): Int = nameToId[name] ?: -1
}