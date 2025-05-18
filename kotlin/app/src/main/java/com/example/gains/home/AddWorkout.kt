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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import com.example.gains.home.model.EditableExercise
import com.example.gains.home.model.EditableWorkoutDay
import com.example.gains.home.model.ExerciseDetail
import com.example.gains.home.model.WorkoutDay
import com.example.gains.home.model.WorkoutRoutine
import com.example.gains.home.model.WorkoutViewModel
import com.example.gains.home.model.ExerciseMappings
import com.example.gains.home.network.WorkoutService
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratedWorkoutScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val workoutRoutine by workoutViewModel.generatedWorkout.collectAsState(initial = null)
    var isSaving by remember { mutableStateOf(false) }
    var showSaveSuccessMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showBackConfirmationDialog by remember { mutableStateOf(false) }

    Log.d("GeneratedWorkout", "Routine: $workoutRoutine")

    var editableWorkout by remember { mutableStateOf<List<EditableWorkoutDay>>(emptyList()) }
    val userId = UserSession.userId

    LaunchedEffect(workoutRoutine) {
        workoutRoutine?.let { routine ->
            editableWorkout = routine.schedule.mapIndexed { idx, day ->
                val raw = day.exercise_sets ?: day.exercises ?: emptyList()
                EditableWorkoutDay(
                    day = "Day ${idx + 1}",
                    exercises = raw.map {
                        EditableExercise(
                            exercise_set_id = it.exercise_set_id,
                            exerciseId = it.exercise_id,
                            sets = it.sets,
                            reps = it.reps,
                            weight = it.weight,
                            is_done = it.is_done
                        )
                    }.toMutableList()
                )
            }
        }
    }

    if (showBackConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showBackConfirmationDialog = false },
            title = { Text("Leave Workout Plan?") },
            text = { Text("Your changes will be lost. Leave anyway?") },
            confirmButton = {
                TextButton(onClick = {
                    showBackConfirmationDialog = false
                    navController.popBackStack()
                }) { Text("Yes") }
            },
            dismissButton = {
                FilledTonalButton(onClick = { showBackConfirmationDialog = false }) { Text("No") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Workout Plan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { showBackConfirmationDialog = true }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (workoutRoutine == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.FitnessCenter,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        "Your Personalized Workout",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "This plan has been customized based on your fitness goals, experience level, and preferences. You can modify it below as needed.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Text(
                                        "Tap the ",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Icon(
                                        Icons.Default.Save,
                                        contentDescription = "Save icon",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        " when you're ready to start your fitness journey!",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                    items(editableWorkout) { day ->
                        WorkoutDayCard(
                            day = day,
                            editableWorkout = editableWorkout.toMutableList(),
                            onWorkoutUpdated = { editableWorkout = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            if (isSaving) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                )
            } else {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            isSaving = true
                            errorMessage = null
                            try {
                                val request = WorkoutRoutine(
                                    schedule = editableWorkout.map { d ->
                                        WorkoutDay(
                                            execution_date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE),
                                            created_at = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE),
                                            exercise_sets = d.exercises.map { ex ->
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
                                WorkoutService.sendWorkoutData(userId ?: "", request)
                                showSaveSuccessMessage = true
                                kotlinx.coroutines.delay(2000)
                                showSaveSuccessMessage = false
                                navController.navigate("home") { popUpTo("home") { inclusive = true } }
                            } catch (e: Exception) {
                                errorMessage = e.message
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                }
            }

            errorMessage?.let {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(it, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }

            AnimatedVisibility(
                visible = showSaveSuccessMessage,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
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
fun WorkoutDayCard(
    day: EditableWorkoutDay,
    editableWorkout: MutableList<EditableWorkoutDay>,
    onWorkoutUpdated: (MutableList<EditableWorkoutDay>) -> Unit,
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    ) {
                        Text(
                            day.day.substringAfter("Day "),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            day.day,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            "${day.exercises.size} exercises",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
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

                    day.exercises.forEachIndexed { idx, ex ->
                        WorkoutExerciseItem(
                            exerciseName = ExerciseMappings.getExerciseName(ex.exerciseId),
                            sets = ex.sets,
                            reps = ex.reps,
                            onRemoveClick = {
                                val updated = editableWorkout.toMutableList().apply {
                                    this[editableWorkout.indexOf(day)].exercises.removeAt(idx)
                                }
                                onWorkoutUpdated(updated)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
                            Text("Add New Exercise", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
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
                                    modifier = Modifier.fillMaxWidth()
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

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = sets, onValueChange = { sets = it }, label = { Text("Sets") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = reps, onValueChange = { reps = it }, label = { Text("Reps") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight") }, modifier = Modifier.weight(1f))
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { addExerciseExpanded = false }, modifier = Modifier.weight(1f)) { Text("Cancel") }
                                Button(onClick = { /* Add logic */ }, modifier = Modifier.weight(1f)) { Text("Add") }
                            }
                        }
                    }

                    if (!addExerciseExpanded) {
                        Button(onClick = { addExerciseExpanded = true }, modifier = Modifier.fillMaxWidth()) {
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

@Composable
fun WorkoutExerciseItem(
    exerciseName: String,
    sets: Int,
    reps: Int,
    onRemoveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.FitnessCenter,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(exerciseName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("$sets sets", style = MaterialTheme.typography.bodyMedium)
                Text("$reps reps", style = MaterialTheme.typography.bodyMedium)
            }
        }
        IconButton(onClick = onRemoveClick) { Icon(Icons.Default.Clear, contentDescription = "Remove") }
    }
}
