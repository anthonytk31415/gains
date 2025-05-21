package com.example.gains.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gains.UserSession
import com.example.gains.home.model.*
import com.example.gains.home.network.WorkoutService
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Manual Workout Entry Screen
 * Allows users to create their own custom workouts by adding days and exercises
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualWorkoutEntry(
    navController: NavController,
    modifier: Modifier = Modifier,
    workoutViewModel: WorkoutViewModel = viewModel()
) {
    // State management
    val coroutineScope = rememberCoroutineScope()
    // Initialize with one default workout day
    var workoutDays by remember {
        mutableStateOf(
            listOf(
                EditableWorkoutDay(
                    day = "Workout 1",
                    exercises = mutableListOf()
                )
            )
        )
    }
    var showSaveConfirmationDialog by remember { mutableStateOf(false) }
    var showSaveSuccessMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    fun addWorkoutDay() {
        workoutDays = workoutDays + EditableWorkoutDay(
            day = "Workout ${workoutDays.size + 1}",
            exercises = mutableListOf()
        )
    }
    fun removeWorkoutDay(index: Int) {
        // Prevent removing the last workout day
        if (workoutDays.size > 1) {
            workoutDays = workoutDays.filterIndexed { i, _ -> i != index }
            // Update the day numbers for all workout days
            workoutDays = workoutDays.mapIndexed { i, day ->
                day.copy(day = "Workout ${i + 1}")
            }
        } else {
            errorMessage = "At least one workout day is required"
        }
    }
    fun saveWorkout() {
        if (workoutDays.isEmpty()) {
            errorMessage = "Please add at least one workout day"
            return
        }
        if (workoutDays.any { it.exercises.isEmpty() }) {
            errorMessage = "Each workout day must have at least one exercise"
            return
        }
        showSaveConfirmationDialog = true
    }

    if (showSaveConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showSaveConfirmationDialog = false },
            title = { Text("Save Workout Plan?") },
            text = { Text("Are you sure you want to save this workout plan?") },
            confirmButton = {
                Button(onClick = {
                    showSaveConfirmationDialog = false
                    coroutineScope.launch {
                        isSaving = true
                        errorMessage = null
                        try {
                            val userId = UserSession.userId
                            if (userId == null) {
                                errorMessage = "User not logged in"
                                isSaving = false
                                return@launch
                            }
                            val now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
                            val request = WorkoutRoutine(
                                schedule = workoutDays.map { day ->
                                    WorkoutDay(
                                        execution_date = null,
                                        created_at = now,
                                        exercise_sets = day.exercises.map { ex ->
                                            ExerciseDetail(
                                                exercise_id = ex.exerciseId,
                                                exercise_set_id = ex.exercise_set_id,
                                                sets = ex.sets,
                                                reps = ex.reps,
                                                weight = ex.weight,
                                                is_done = ex.is_done
                                            )
                                        }
                                    )
                                }
                            )
                            WorkoutService.sendWorkoutData(userId, request)
                            showSaveSuccessMessage = true
                            kotlinx.coroutines.delay(2000)
                            showSaveSuccessMessage = false
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        } catch (e: Exception) {
                            errorMessage = "Failed to save workout: ${e.message}"
                        } finally {
                            isSaving = false
                        }
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                FilledTonalButton(onClick = { showSaveConfirmationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Manual Workout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { saveWorkout() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save Workout")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    top = 4.dp,
                    end = 12.dp,
                    bottom = 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // intro card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.FitnessCenter,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Build Your Workout",
                                    style = MaterialTheme.typography.titleMedium
                                        .copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Create your custom workout plan. Tap ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                        .copy(alpha = 0.8f)
                                )
                                Icon(
                                    Icons.Default.Save,
                                    contentDescription = "Save icon",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    " to finalize.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                        .copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                // error message display
                errorMessage?.let {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    it,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                // Workout day cards
                items(workoutDays.withIndex().toList()) { (index, day) ->
                    ManualWorkoutDayCard(
                        day = day.copy(day = "Workout ${index + 1}"),
                        onRemoveDay = { removeWorkoutDay(index) },
                        onUpdateDay = { updated ->
                            workoutDays = workoutDays.toMutableList().apply {
                                set(index, updated)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Add workout button
                item {
                    Button(
                        onClick = { addWorkoutDay() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Add Workout")
                    }
                }
            }

            // loading overlay
            if (isSaving) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // success toast
            AnimatedVisibility(
                visible = showSaveSuccessMessage,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Workout plan saved successfully!",
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualWorkoutDayCard(
    day: EditableWorkoutDay,
    onRemoveDay: () -> Unit,
    onUpdateDay: (EditableWorkoutDay) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(true) }
    var addExerciseExpanded by remember { mutableStateOf(false) }
    var selectedExerciseName by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val exerciseNames = ExerciseMappings.nameToId.keys.toList()

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), // Reduced elevation
        shape = RoundedCornerShape(12.dp) // Smaller radius
    ) {
        Column(modifier = Modifier.padding(12.dp)) { // Reduced padding
            // Card header with day name and expand/collapse button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Day number circle - smaller size
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    ) {
                        Text(
                            day.day.substringAfter("Workout "),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            day.day,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            "${day.exercises.size} exercises",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                Row {
                    // Delete day button
                    IconButton(onClick = onRemoveDay) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Remove Day",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }

                    // Expand/collapse button
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    // Exercise list
                    day.exercises.forEachIndexed { idx, ex ->
                        WorkoutExerciseItem(
                            exerciseName = ExerciseMappings.getExerciseName(ex.exerciseId),
                            sets = ex.sets,
                            reps = ex.reps,
                            onRemoveClick = {
                                val updatedExercises = day.exercises.toMutableList()
                                updatedExercises.removeAt(idx)
                                onUpdateDay(day.copy(exercises = updatedExercises))
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Add exercise form - reduced padding
                    AnimatedVisibility(
                        visible = addExerciseExpanded,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Text("Add New Exercise", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Exercise dropdown
                            ExposedDropdownMenuBox(
                                expanded = dropdownExpanded,
                                onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedExerciseName,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Exercise") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = dropdownExpanded,
                                    onDismissRequest = { dropdownExpanded = false }
                                ) {
                                    exerciseNames.forEach { name ->
                                        DropdownMenuItem(
                                            text = { Text(name) },
                                            onClick = { selectedExerciseName = name; dropdownExpanded = false }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Sets, reps, weight inputs
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = sets,
                                    onValueChange = { sets = it },
                                    label = { Text("Sets") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = reps,
                                    onValueChange = { reps = it },
                                    label = { Text("Reps") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = weight,
                                    onValueChange = { weight = it },
                                    label = { Text("Weight") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Add/Cancel buttons
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { addExerciseExpanded = false },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancel")
                                }
                                Button(
                                    onClick = {
                                        val exerciseId = ExerciseMappings.getExerciseId(selectedExerciseName)
                                        if (exerciseId != -1 && sets.isNotBlank() && reps.isNotBlank()) {
                                            // Create new exercise
                                            val newExercise = EditableExercise(
                                                exerciseId = exerciseId,
                                                exercise_set_id = null,
                                                sets = sets.toIntOrNull() ?: 3,
                                                reps = reps.toIntOrNull() ?: 10,
                                                weight = weight.toFloatOrNull() ?: 0f,
                                                is_done = false
                                            )

                                            // Add to exercises list
                                            val updatedExercises = day.exercises.toMutableList()
                                            updatedExercises.add(newExercise)
                                            onUpdateDay(day.copy(exercises = updatedExercises))

                                            // Reset form
                                            selectedExerciseName = ""
                                            sets = ""
                                            reps = ""
                                            weight = ""
                                            addExerciseExpanded = false
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Add")
                                }
                            }
                        }
                    }

                    // Add Exercise button
                    if (!addExerciseExpanded) {
                        Button(
                            onClick = { addExerciseExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Exercise")
                        }
                    }
                }
            }
        }
    }
}
