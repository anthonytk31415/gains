package com.example.gains.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gains.home.model.EditableExercise
import com.example.gains.home.model.EditableWorkoutDay
import com.example.gains.home.model.ExerciseDetail
import com.example.gains.home.model.ExerciseMappings
import com.example.gains.home.model.WorkoutDay
import com.example.gains.home.model.WorkoutRoutine
import com.example.gains.home.network.WorkoutApi
import com.example.gains.home.network.WorkoutService
import kotlinx.coroutines.launch
import com.example.gains.UserSession
import com.example.gains.home.network.UserService

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var workoutRoutine by remember { mutableStateOf<WorkoutRoutine?>(null) }
    val editableWorkout = remember { mutableStateListOf<EditableWorkoutDay>() }
    val exerciseCheckStates = remember { mutableStateMapOf<Pair<Int, Int>, Boolean>() }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDataLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(UserSession.userId) {
        val userId = UserSession.userId
        Log.d("HomeScreen", "LaunchedEffect started")
        Log.d("HomeScreen", "userId = $userId")
        try {
            Log.d("call initiated","$userId")
            val fetchedWorkout = userId?.let { WorkoutApi.getWorkout(it) }
            workoutRoutine = fetchedWorkout
            editableWorkout.clear()
            if (fetchedWorkout != null) {
                editableWorkout.addAll(
                    fetchedWorkout.schedule.mapIndexed { index, day ->
                        val exercisesRaw = day.exercise_sets ?: day.exercises ?: emptyList()
                        EditableWorkoutDay(
                            day = "Day ${index + 1}",
                            exercises = mutableStateListOf<EditableExercise>().apply {
                                addAll(exercisesRaw.map {
                                    EditableExercise(
                                        exerciseId = it.exercise_id,
                                        exercise_set_id = it.exercise_set_id,
                                        sets = it.sets,
                                        reps = it.reps,
                                        weight = it.weight
                                    )
                                })
                            }
                        )
                    }
                )
                editableWorkout.forEachIndexed { dayIndex, workoutDay ->
                    workoutDay.exercises.forEachIndexed { exIndex, exercise ->
                        val isDone = fetchedWorkout.schedule[dayIndex]
                            .exercise_sets
                            ?.getOrNull(exIndex)
                            ?.is_done == true

                        exerciseCheckStates[dayIndex to exIndex] = isDone
                    }
                }
            }
            isDataLoaded = true
        } catch (e: Exception) {
            Log.e("WorkoutFetch", "Error: ${e.message}")
            errorMessage = "Failed to load workout: ${e.message}"
            isDataLoaded = true // Still set this so UI can show error
        }
    }

    fun isDayDone(dayIndex: Int): Boolean {
        val exercises = editableWorkout[dayIndex].exercises
        return exercises.isNotEmpty() && exercises.indices.all { exIndex ->
            exerciseCheckStates[dayIndex to exIndex] == true
        }
    }

    fun updateCheckState(dayIndex: Int, exIndex: Int, checked: Boolean) {
        exerciseCheckStates[dayIndex to exIndex] = checked
    }
    when {
        !isDataLoaded -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        workoutRoutine == null || workoutRoutine?.schedule.isNullOrEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No Workouts Created")
            }
        }

        else ->{
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                val todoDays = editableWorkout.withIndex().filter { !isDayDone(it.index) }
                val doneDays = editableWorkout.withIndex().filter { isDayDone(it.index) }

                if (todoDays.isNotEmpty()) {
                    item { Text("To Do", style = MaterialTheme.typography.headlineMedium) }
                    items(todoDays) { (index, day) ->
                        WorkoutDayCard(
                            navController = navController,
                            dayIndex = index,
                            day = day,
                            onCheckChange = ::updateCheckState,
                            exerciseCheckStates = exerciseCheckStates
                        )
                    }
                }

                if (editableWorkout.isNotEmpty()) {
                    item {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val today = java.time.LocalDate.now().toString()
                                    val schedule = editableWorkout.mapIndexed { dayIndex, workoutDay ->
                                        WorkoutDay(
                                            workout_id = workoutRoutine?.schedule?.get(dayIndex)?.workout_id
                                                ?: 0,
                                            execution_date = workoutRoutine?.schedule?.get(dayIndex)?.execution_date
                                                ?: today,
                                            created_at = today,
                                            exercise_sets = workoutDay.exercises.mapIndexed { exIndex, exercise ->
                                                ExerciseDetail(
                                                    exercise_id = exercise.exerciseId,
                                                    exercise_set_id = exercise.exercise_set_id,
                                                    sets = exercise.sets,
                                                    reps = exercise.reps,
                                                    weight = exercise.weight,
                                                    is_done = exerciseCheckStates[dayIndex to exIndex]
                                                        ?: false
                                                )
                                            }
                                        )
                                    }

                                    val workoutPayload = WorkoutRoutine(schedule = schedule)
                                    val response = UserSession.userId?.let {
                                        WorkoutService.updateWorkoutData(it, workoutPayload)
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .fillMaxWidth()
                        ) {
                            Text("Save Progress")
                        }
                    }
                }

                if (doneDays.isNotEmpty()) {
                    item {
                        Text(
                            "Done",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    }
                    items(doneDays) { (index, day) ->
                        WorkoutDayCard(
                            navController = navController,
                            dayIndex = index,
                            day = day,
                            onCheckChange = ::updateCheckState,
                            exerciseCheckStates = exerciseCheckStates
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDayCard(
    navController: NavController,
    dayIndex: Int,
    day: EditableWorkoutDay,
    onCheckChange: (Int, Int, Boolean) -> Unit,
    exerciseCheckStates: MutableMap<Pair<Int, Int>, Boolean>
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedExerciseName by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val exerciseNames = ExerciseMappings.nameToId.keys.toList()

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Day ${dayIndex + 1}", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            day.exercises.forEachIndexed { exIndex, ex ->
                val checked = exerciseCheckStates[dayIndex to exIndex] ?: false

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = {
                            onCheckChange(dayIndex, exIndex, it)
                        }
                    )
                    Text(
                        text = "${ExerciseMappings.getExerciseName(ex.exerciseId)} - ${ex.sets} sets, ${ex.reps} reps, ${ex.weight} kg",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                navController.navigate("exerciseDetail/${ex.exerciseId}")
                            }
                    )
                    IconButton(onClick = {
                        day.exercises.removeAt(exIndex)
                        exerciseCheckStates.remove(dayIndex to exIndex)
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Remove Exercise")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { expanded = !expanded }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(Modifier.width(8.dp))
                Text("Add Exercise")
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedExerciseName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Exercise") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                        },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        exerciseNames.forEach { name ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    selectedExerciseName = name
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = sets,
                    onValueChange = { sets = it },
                    label = { Text("Sets") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Reps") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val exerciseId = ExerciseMappings.getExerciseId(selectedExerciseName)
                        if (exerciseId != -1 && sets.isNotBlank() && reps.isNotBlank()) {
                            val newExercise = EditableExercise(
                                exerciseId = exerciseId,
                                exercise_set_id = null,
                                sets = sets.toIntOrNull() ?: 0,
                                reps = reps.toIntOrNull() ?: 0,
                                weight = weight.toFloatOrNull() ?: 0f
                            )
                            day.exercises.add(newExercise)

                            // Reset form
                            expanded = false
                            selectedExerciseName = ""
                            sets = ""
                            reps = ""
                            weight = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Confirm")
                }
            }
        }
    }
}