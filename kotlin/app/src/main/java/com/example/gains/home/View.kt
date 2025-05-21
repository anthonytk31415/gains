package com.example.gains

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gains.home.model.EditableExercise
import com.example.gains.home.model.EditableWorkoutDay
import com.example.gains.home.model.ExerciseMappings
import com.example.gains.home.model.WorkoutRoutine
import com.example.gains.home.network.WorkoutApi
import com.example.gains.UserSession
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViewScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var workoutHistory by remember { mutableStateOf<WorkoutRoutine?>(null) }
    val pastWorkouts = remember { mutableStateListOf<Pair<String, List<EditableWorkoutDay>>>() }
    val exerciseCheckStates = remember { mutableStateMapOf<Triple<Int, Int, Int>, Boolean>() }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDataLoaded by remember { mutableStateOf(false) }
    var showTutorialErrorDialog by remember { mutableStateOf(false) }
    var selectedExerciseName by remember { mutableStateOf("") }

    // Safe navigation function that shows a dialog instead of crashing
    fun safeNavigateToExerciseDetail(exerciseId: Int, exerciseName: String) {
        try {
            selectedExerciseName = exerciseName
            navController.navigate("exerciseDetail/$exerciseId")
        } catch (e: Exception) {
            // If navigation fails, show dialog instead of crashing
            showTutorialErrorDialog = true
        }
    }

    // Tutorial error dialog
    if (showTutorialErrorDialog) {
        AlertDialog(
            onDismissRequest = { showTutorialErrorDialog = false },
            title = { Text("Tutorial Unavailable") },
            text = {
                Text("The tutorial for $selectedExerciseName is temporarily unavailable. We're working on adding this content.")
            },
            confirmButton = {
                Button(onClick = { showTutorialErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    LaunchedEffect(UserSession.userId) {
        val userId = UserSession.userId
        Log.d("WorkoutHistoryScreen", "LaunchedEffect started")
        Log.d("WorkoutHistoryScreen", "userId = $userId")
        try {
            Log.d("call initiated", "$userId")
            // Fixed: Getting a single WorkoutRoutine from getAllWorkout
            val fetchedWorkoutHistory = userId?.let { WorkoutApi.getAllWorkout(it) }
            workoutHistory = fetchedWorkoutHistory
            pastWorkouts.clear()

            // Here we process a single WorkoutRoutine instead of a list
            if (fetchedWorkoutHistory != null) {
                // Group workouts by date
                val workoutsByDate = fetchedWorkoutHistory.schedule.groupBy {
                    try {
                        val dateString = it.execution_date ?: "Unscheduled Workout"
                        val localDate = LocalDate.parse(dateString)
                        localDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                    } catch (e: Exception) {
                        it.execution_date ?: "Unknown Workout"
                    }
                }

                // Process each date group
                workoutsByDate.forEach { (date, workoutDays) ->
                    val editableDays = workoutDays.mapIndexed { index, day ->
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

                    // Store the date with the workout days
                    pastWorkouts.add(date to editableDays)

                    // Set exercise check states
                    val workoutIndex = pastWorkouts.size - 1
                    editableDays.forEachIndexed { dayIndex, workoutDay ->
                        workoutDay.exercises.forEachIndexed { exIndex, _ ->
                            val isDone = workoutDays[dayIndex]
                                .exercise_sets
                                ?.getOrNull(exIndex)
                                ?.is_done == true

                            exerciseCheckStates[Triple(workoutIndex, dayIndex, exIndex)] = isDone
                        }
                    }
                }
            }

            isDataLoaded = true
        } catch (e: Exception) {
            Log.e("WorkoutHistoryFetch", "Error: ${e.message}")
            errorMessage = "Failed to load workout history: ${e.message}"
            isDataLoaded = true // Still set this so UI can show error
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        "Workout History",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                !isDataLoaded -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(50.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Loading your workout history...",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                errorMessage != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Error",
                                modifier = Modifier.size(50.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                errorMessage ?: "An error occurred",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    errorMessage = null
                                    isDataLoaded = false
                                    // Trigger the LaunchedEffect again
                                    UserSession.userId?.let { }
                                }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }

                pastWorkouts.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Outlined.History,
                                contentDescription = "No Workout History",
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No Workout History",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Complete workouts to see them here",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(
                            top = 16.dp,
                            bottom = 24.dp
                        )
                    ) {
                        items(pastWorkouts.withIndex().toList()) { (workoutIndex, workout) ->
                            val (date, days) = workout
                            WorkoutHistoryItem(
                                date = date,
                                workoutIndex = workoutIndex,
                                days = days,
                                exerciseCheckStates = exerciseCheckStates,
                                onExerciseInfoClick = { exerciseId, exerciseName ->
                                    safeNavigateToExerciseDetail(exerciseId, exerciseName)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutHistoryItem(
    date: String,
    workoutIndex: Int,
    days: List<EditableWorkoutDay>,
    exerciseCheckStates: Map<Triple<Int, Int, Int>, Boolean>,
    onExerciseInfoClick: (Int, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Calculate completion percentage
    val completionPercentage = remember {
        derivedStateOf {
            var completed = 0
            var total = 0

            days.forEachIndexed { dayIndex, day ->
                day.exercises.forEachIndexed { exIndex, _ ->
                    total++
                    if (exerciseCheckStates[Triple(workoutIndex, dayIndex, exIndex)] == true) {
                        completed++
                    }
                }
            }

            if (total == 0) 0f else completed.toFloat() / total
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row with date and expand/collapse
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Calendar icon or date circle
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    ) {
                        Text(
                            date.split(" ").firstOrNull() ?: "", // Just show the month abbreviation
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            date,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            "${days.sumOf { it.exercises.size }} exercises in ${days.size} days",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                // Completion indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = completionPercentage.value,
                        modifier = Modifier
                            .width(60.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()

                    days.forEachIndexed { dayIndex, day ->
                        val isDayDone = day.exercises.indices.all { exIndex ->
                            exerciseCheckStates[Triple(workoutIndex, dayIndex, exIndex)] == true
                        }

                        HistoryDayCard(
                            workoutIndex = workoutIndex,
                            dayIndex = dayIndex,
                            day = day,
                            exerciseCheckStates = exerciseCheckStates,
                            isDone = isDayDone,
                            onExerciseInfoClick = onExerciseInfoClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryDayCard(
    workoutIndex: Int,
    dayIndex: Int,
    day: EditableWorkoutDay,
    exerciseCheckStates: Map<Triple<Int, Int, Int>, Boolean>,
    isDone: Boolean,
    onExerciseInfoClick: (Int, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Animation for card alpha
    val cardAlpha by animateFloatAsState(
        targetValue = if (isDone) 0.7f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .alpha(cardAlpha)
    ) {
        // Day header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isDone) Icons.Default.Check else Icons.Default.DirectionsRun,
                    contentDescription = null,
                    tint = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    "Day ${dayIndex + 1}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp)
            ) {
                day.exercises.forEachIndexed { exIndex, ex ->
                    val checked = exerciseCheckStates[Triple(workoutIndex, dayIndex, exIndex)] ?: false

                    HistoryExerciseItem(
                        exerciseName = ExerciseMappings.getExerciseName(ex.exerciseId),
                        sets = ex.sets,
                        reps = ex.reps,
                        weight = ex.weight,
                        checked = checked,
                        onInfoClick = { onExerciseInfoClick(ex.exerciseId, ExerciseMappings.getExerciseName(ex.exerciseId)) }
                    )
                }
            }
        }

        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun HistoryExerciseItem(
    exerciseName: String,
    sets: Int,
    reps: Int,
    weight: Float,
    checked: Boolean,
    onInfoClick: () -> Unit
) {
    val alpha = if (checked) 0.6f else 1.0f
    val textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .alpha(alpha)
    ) {
        // Exercise name with completion status
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (checked) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = exerciseName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = textDecoration
                ),
                modifier = Modifier.weight(1f)
            )
        }

        // Exercise details
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 28.dp, end = 16.dp, top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExerciseDetail(label = "Sets", value = "$sets")
            ExerciseDetail(label = "Reps", value = "$reps")
            ExerciseDetail(label = "Weight", value = "$weight kg")
        }

        // Tutorial button
        Button(
            onClick = onInfoClick,
            modifier = Modifier
                .padding(start = 28.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "View Tutorial",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun ExerciseDetail(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontWeight = FontWeight.Normal
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}