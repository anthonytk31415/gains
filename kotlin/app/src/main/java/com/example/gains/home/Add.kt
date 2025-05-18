package com.example.gains.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gains.UserSession
import com.example.gains.home.model.WorkoutFormRequest
import com.example.gains.home.model.WorkoutViewModel
import com.example.gains.home.network.WorkoutService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddScreen(navController: NavController, workoutViewModel: WorkoutViewModel) {
    var selection by remember { mutableStateOf<String?>(null) }

    // Custom composables
    @Composable
    fun GenderSelectionButton(
        selected: Boolean,
        icon: ImageVector,
        label: String,
        onClick: () -> Unit,
        iconTint: Color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(120.dp)
                .clickable(onClick = onClick)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        width = if (selected) 3.dp else 1.dp,
                        color = if (selected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }

    @Composable
    fun LocationSelectionButton(
        selected: Boolean,
        icon: ImageVector,
        label: String,
        onClick: () -> Unit
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(120.dp)
                .clickable(onClick = onClick)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (selected)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        width = if (selected) 3.dp else 1.dp,
                        color = if (selected)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (selected)
                        MaterialTheme.colorScheme.onSecondary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }

    @Composable
    fun WorkoutOptionCard(
        title: String,
        description: String,
        icon: ImageVector,
        backgroundColor: Color,
        contentColor: Color,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clickable(onClick = onClick),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon section
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(contentColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Text content
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = contentColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Tap to continue →",
                        style = MaterialTheme.typography.labelMedium,
                        color = contentColor.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }

    @Composable
    fun WorkoutSelectionScreen(
        onSelectManual: () -> Unit,
        onSelectAI: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "How would you like to create your workout?",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // AI Workout Card
            WorkoutOptionCard(
                title = "AI Generated Workout",
                description = "Get a personalized workout plan created by our AI based on your goals, experience, and preferences.",
                icon = Icons.Default.Science,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = onSelectAI
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Manual Workout Card
            WorkoutOptionCard(
                title = "Manual Workout",
                description = "Create your own custom workout plan by selecting exercises, sets, and reps manually.",
                icon = Icons.Default.Assignment,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = onSelectManual
            )
        }
    }

    @Composable
    fun ManualWorkoutEntry(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Manual Workout Entry Screen", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(24.dp))
            // Your manual workout entry implementation here
        }
    }

    @Composable
    fun AIWorkoutForm(
        navController: NavController,
        workoutViewModel: WorkoutViewModel,
        modifier: Modifier = Modifier
    ) {
        // Your existing AIWorkoutForm implementation with the modifier parameter
        val scrollState = rememberScrollState()
        var expandedExperience by remember { mutableStateOf(false) }
        var expandedDay by remember { mutableStateOf(false) }
        val experienceOptions = listOf("Beginner", "Intermediate", "Advanced")
        var experience by remember { mutableStateOf<String?>(null) }
        var workoutLocation by remember { mutableStateOf<String?>(null) }
        var gender by remember { mutableStateOf<String?>(null) }
        var dayOptions = listOf("1","2","3","4","5","6","7")
        var days by remember { mutableStateOf<String?>(null) }
        val goalOptions = listOf("Muscle Gain", "Fat Loss", "Strength", "Toning", "Endurance")
        val goals = remember { mutableStateMapOf<String, Boolean>() }
        val muscleOptions = listOf("Chest", "Back", "Shoulders", "Legs", "Biceps", "Triceps", "Abs")
        val selectedMuscles = remember { mutableStateMapOf<String, Boolean>() }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        val coroutineScope = rememberCoroutineScope()
        val userId = UserSession.userId

        muscleOptions.forEach { option ->
            if (!selectedMuscles.containsKey(option)) {
                selectedMuscles[option] = false
            }
        }
        goalOptions.forEach { option ->
            if (!goals.containsKey(option)) {
                goals[option] = false
            }
        }

        Column(modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Let our AI create the perfect workout plan for you",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Fill out the form below and we'll generate a personalized workout plan tailored to your needs and goals.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            // Form section title
            Text(
                "Personal Details",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Gender selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Gender",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Male option - blue
                        GenderSelectionButton(
                            selected = gender == "Male",
                            icon = Icons.Default.Male,
                            label = "Male",
                            onClick = { gender = "Male" },
                            iconTint = if (gender == "Male")
                                MaterialTheme.colorScheme.onPrimary
                            else
                                Color(0xFF2196F3) // Material Blue
                        )

                        // Female option - pink
                        GenderSelectionButton(
                            selected = gender == "Female",
                            icon = Icons.Default.Female,
                            label = "Female",
                            onClick = { gender = "Female" },
                            iconTint = if (gender == "Female")
                                MaterialTheme.colorScheme.onPrimary
                            else
                                Color(0xFFE91E63) // Material Pink
                        )
                    }
                }
            }

            // Workout Location selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Workout Location",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Gym option
                        LocationSelectionButton(
                            selected = workoutLocation == "Gym",
                            icon = Icons.Default.FitnessCenter,
                            label = "Gym",
                            onClick = { workoutLocation = "Gym" }
                        )

                        // Home option
                        LocationSelectionButton(
                            selected = workoutLocation == "Home",
                            icon = Icons.Default.Home,
                            label = "Home",
                            onClick = { workoutLocation = "Home" }
                        )
                    }
                }
            }

            // Experience and Days section title
            Text(
                "Experience & Schedule",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            // Experience dropdown
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Your Experience Level",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(8.dp))
                            .clickable { expandedExperience = true }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = experience ?: "Select your experience level",
                                color = if (experience == null) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                else MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown"
                            )
                        }

                        DropdownMenu(
                            expanded = expandedExperience,
                            onDismissRequest = { expandedExperience = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            experienceOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        experience = option
                                        expandedExperience = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Days dropdown
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Workout Days Per Week",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(8.dp))
                            .clickable { expandedDay = true }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = days ?: "Select number of days",
                                color = if (days == null) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                else MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown"
                            )
                        }

                        DropdownMenu(
                            expanded = expandedDay,
                            onDismissRequest = { expandedDay = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            dayOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text("$option day${if (option.toInt() > 1) "s" else ""} per week") },
                                    onClick = {
                                        days = option
                                        expandedDay = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Goals section title
            Text(
                "Goals & Target Muscles",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            // Fitness goals
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Your Fitness Goals",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        goalOptions.forEach { goal ->
                            FilterChip(
                                selected = goals[goal] == true,
                                onClick = { goals[goal] = !(goals[goal] ?: false) },
                                label = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(goal)
                                        Icon(
                                            imageVector = if (goals[goal] == true) Icons.Default.Check else Icons.Default.Add,
                                            contentDescription = if (goals[goal] == true) "Selected" else "Select",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = if (goals[goal] == true)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surface,
                                    labelColor = if (goals[goal] == true)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurface,
                                    iconColor = if (goals[goal] == true)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = goals[goal] == true,
                                    borderColor = if (goals[goal] == true)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outline,
                                    borderWidth = 1.dp,
                                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                                    selectedBorderWidth = 1.dp,
                                )
                            )
                        }
                    }
                }
            }

            // Target muscles
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Target Muscles",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        muscleOptions.forEach { muscle ->
                            FilterChip(
                                selected = selectedMuscles[muscle] == true,
                                onClick = { selectedMuscles[muscle] = !(selectedMuscles[muscle] ?: false) },
                                label = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(muscle)
                                        Icon(
                                            imageVector = if (selectedMuscles[muscle] == true) Icons.Default.Check else Icons.Default.Add,
                                            contentDescription = if (selectedMuscles[muscle] == true) "Selected" else "Select",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = if (selectedMuscles[muscle] == true)
                                        MaterialTheme.colorScheme.secondaryContainer
                                    else
                                        MaterialTheme.colorScheme.surface,
                                    labelColor = if (selectedMuscles[muscle] == true)
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurface,
                                    iconColor = if (selectedMuscles[muscle] == true)
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                    else
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedMuscles[muscle] == true,
                                    borderColor = if (selectedMuscles[muscle] == true)
                                        MaterialTheme.colorScheme.secondary
                                    else
                                        MaterialTheme.colorScheme.outline,
                                    borderWidth = 1.dp,
                                    selectedBorderColor = MaterialTheme.colorScheme.secondary,
                                    selectedBorderWidth = 1.dp,
                                )
                            )
                        }
                    }
                }
            }

            // Error message
            if (errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Submit button
            Button(
                onClick = {
                    errorMessage = null

                    // Input validations
                    if (workoutLocation == null || experience == null || gender == null || days == null ||
                        selectedMuscles.values.none { it } || goals.values.none { it }) {
                        errorMessage = "Please fill all the fields"
                        return@Button
                    }

                    // Prepare data
                    val formData = WorkoutFormRequest(
                        gender = gender!!,
                        location = workoutLocation!!,
                        experience = experience!!,
                        workout_days = days!!,
                        muscle_focus = selectedMuscles.filter { it.value }.keys.toList().toString(),
                        goal = goals.filter { it.value }.keys.toList().toString()
                    )

                    coroutineScope.launch {
                        try {
                            Log.d("FormSubmit", "Calling WorkoutService.submitUserForm")
                            val response = userId?.let { WorkoutService.submitUserForm(it, formData) }
                            Log.d("FormSubmit", "response: $response")
                            if (response != null) {
                                workoutViewModel.setWorkout(response)
                            }
                            navController.navigate("generatedWorkout")
                        } catch (e: Exception) {
                            Log.e("FormSubmit", "Failed to send data: ${e.message}", e)
                            errorMessage = "Failed to generate workout: ${e.message}"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Generate Workout Plan",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }

    Scaffold(
        topBar = {
            if (selection != null) {
                TopAppBar(
                    title = {
                        Text(
                            if (selection == "manual") "Create Manual Workout" else "AI Workout Generator"
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { selection = null }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("Create New Workout") }
                )
            }
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = selection == null,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            WorkoutSelectionScreen(
                onSelectManual = { selection = "manual" },
                onSelectAI = { selection = "ai" },
                modifier = Modifier.padding(paddingValues)
            )
        }

        AnimatedVisibility(
            visible = selection == "manual",
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            ManualWorkoutEntry(
                modifier = Modifier.padding(paddingValues)
            )
        }

        AnimatedVisibility(
            visible = selection == "ai",
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            AIWorkoutForm(
                navController = navController,
                workoutViewModel = workoutViewModel,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}