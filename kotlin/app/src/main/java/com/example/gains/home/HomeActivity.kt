package com.example.gains.home

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Star
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
import com.example.gains.home.model.ExerciseDetail
import com.example.gains.home.model.ExerciseMappings
import com.example.gains.home.model.WorkoutDay
import com.example.gains.home.model.WorkoutRoutine
import com.example.gains.home.network.WorkoutApi
import com.example.gains.home.network.WorkoutService
import kotlinx.coroutines.launch
import com.example.gains.UserSession

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
    var showSaveSuccessMessage by remember { mutableStateOf(false) }
    var showTutorialErrorDialog by remember { mutableStateOf(false) }
    var selectedExerciseName by remember { mutableStateOf("") }

    // Track workout completion percentage
    val completionPercentage = remember {
        derivedStateOf {
            val total = exerciseCheckStates.size
            if (total == 0) 0f else exerciseCheckStates.count { it.value } / total.toFloat()
        }
    }

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

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        "My Workout Plan",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (workoutRoutine != null && editableWorkout.isNotEmpty()) {
                FloatingActionButton(
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

                            // Show success message
                            showSaveSuccessMessage = true
                            // Hide after delay
                            kotlinx.coroutines.delay(2000)
                            showSaveSuccessMessage = false
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save Progress")
                }
            }
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
                                "Loading your workout plan...",
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

                workoutRoutine == null || workoutRoutine?.schedule.isNullOrEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Outlined.Star,
                                contentDescription = "No Workouts",
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No Workouts Created",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Create a workout to get started",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Progress indicator
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Your Progress",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        "${(completionPercentage.value * 100).toInt()}%",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = completionPercentage.value,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            }
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            val todoDays = editableWorkout.withIndex().filter { !isDayDone(it.index) }
                            val doneDays = editableWorkout.withIndex().filter { isDayDone(it.index) }

                            if (todoDays.isNotEmpty()) {
                                item {
                                    SectionHeader(
                                        title = "To Do",
                                        icon = Icons.Default.DirectionsRun
                                    )
                                }
                                items(todoDays) { (index, day) ->
                                    WorkoutDayCard(
                                        dayIndex = index,
                                        day = day,
                                        onCheckChange = ::updateCheckState,
                                        exerciseCheckStates = exerciseCheckStates,
                                        isDone = false,
                                        onExerciseInfoClick = { exerciseId, exerciseName ->
                                            safeNavigateToExerciseDetail(exerciseId, exerciseName)
                                        }
                                    )
                                }
                            }

                            if (doneDays.isNotEmpty()) {
                                item {
                                    SectionHeader(
                                        title = "Done",
                                        icon = Icons.Default.Check,
                                        modifier = Modifier.padding(top = 24.dp)
                                    )
                                }
                                items(doneDays) { (index, day) ->
                                    WorkoutDayCard(
                                        dayIndex = index,
                                        day = day,
                                        onCheckChange = ::updateCheckState,
                                        exerciseCheckStates = exerciseCheckStates,
                                        isDone = true,
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

            // Save success message
            AnimatedVisibility(
                visible = showSaveSuccessMessage,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Progress saved successfully!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDayCard(
    dayIndex: Int,
    day: EditableWorkoutDay,
    onCheckChange: (Int, Int, Boolean) -> Unit,
    exerciseCheckStates: MutableMap<Pair<Int, Int>, Boolean>,
    isDone: Boolean,
    onExerciseInfoClick: (Int, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var addExerciseExpanded by remember { mutableStateOf(false) }
    var selectedExerciseName by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val exerciseNames = ExerciseMappings.nameToId.keys.toList()

    // Calculate day completion percentage
    val dayCompletionPercentage = remember {
        derivedStateOf {
            val exercisesInDay = day.exercises.size
            if (exercisesInDay == 0) 0f else {
                day.exercises.indices.count { exIndex ->
                    exerciseCheckStates[dayIndex to exIndex] == true
                } / exercisesInDay.toFloat()
            }
        }
    }

    // Animation for card alpha
    val cardAlpha by animateFloatAsState(
        targetValue = if (isDone) 0.7f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .alpha(cardAlpha),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isDone -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row with day title and expand/collapse
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Day indicator circle with completion percentage
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    ) {
                        Text(
                            "${dayIndex + 1}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            "Day ${dayIndex + 1}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            "${day.exercises.size} exercises",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                // Day completion indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = dayCompletionPercentage.value,
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
                    Spacer(modifier = Modifier.height(16.dp))

                    // Exercise list
                    day.exercises.forEachIndexed { exIndex, ex ->
                        val checked = exerciseCheckStates[dayIndex to exIndex] ?: false

                        ExerciseItem(
                            exerciseName = ExerciseMappings.getExerciseName(ex.exerciseId),
                            sets = ex.sets,
                            reps = ex.reps,
                            weight = ex.weight,
                            checked = checked,
                            onCheckedChange = { onCheckChange(dayIndex, exIndex, it) },
                            onInfoClick = { onExerciseInfoClick(ex.exerciseId, ExerciseMappings.getExerciseName(ex.exerciseId)) },
                            onRemoveClick = {
                                day.exercises.removeAt(exIndex)
                                exerciseCheckStates.remove(dayIndex to exIndex)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Add exercise section
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
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Text(
                                "Add New Exercise",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

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
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
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

                            // Exercise details in a row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = sets,
                                    onValueChange = { sets = it },
                                    label = { Text("Sets") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                OutlinedTextField(
                                    value = reps,
                                    onValueChange = { reps = it },
                                    label = { Text("Reps") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                OutlinedTextField(
                                    value = weight,
                                    onValueChange = { weight = it },
                                    label = { Text("Weight (kg)") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
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
                                            val newExercise = EditableExercise(
                                                exerciseId = exerciseId,
                                                exercise_set_id = null,
                                                sets = sets.toIntOrNull() ?: 0,
                                                reps = reps.toIntOrNull() ?: 0,
                                                weight = weight.toFloatOrNull() ?: 0f
                                            )
                                            day.exercises.add(newExercise)

                                            // Reset form
                                            addExerciseExpanded = false
                                            selectedExerciseName = ""
                                            sets = ""
                                            reps = ""
                                            weight = ""
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Add")
                                }
                            }
                        }
                    }

                    if (!addExerciseExpanded) {
                        Button(
                            onClick = { addExerciseExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                            Spacer(Modifier.width(8.dp))
                            Text("Add Exercise")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseItem(
    exerciseName: String,
    sets: Int,
    reps: Int,
    weight: Float,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onInfoClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    val alpha = if (checked) 0.6f else 1.0f
    val textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .alpha(alpha)
    ) {
        // Exercise header with checkbox and name
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )

            Text(
                text = exerciseName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = textDecoration
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )

            IconButton(onClick = onRemoveClick) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "Remove Exercise",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        // Exercise details in a row with clearer labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 52.dp, end = 16.dp, top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExerciseDetail(label = "Sets", value = "$sets")
            ExerciseDetail(label = "Reps", value = "$reps")
            ExerciseDetail(label = "Weight", value = "$weight kg")
        }

        // Tutorial button below exercise details
        Button(
            onClick = onInfoClick,
            modifier = Modifier
                .padding(start = 52.dp, end = 16.dp, top = 12.dp)
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