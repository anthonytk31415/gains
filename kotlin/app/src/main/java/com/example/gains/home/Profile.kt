package com.example.gains.home

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gains.UserSession
import com.example.gains.home.model.UserProfile
import com.example.gains.home.network.UserService
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val userId = UserSession.userId
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // State variables
    var height by remember { mutableStateOf(0f) }
    var weight by remember { mutableStateOf(0f) }
    var heightInput by remember { mutableStateOf("") }
    var weightInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDataLoaded by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }

    // DOB state with dropdowns
    var selectedYear by remember { mutableStateOf("") }
    var selectedMonth by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf("") }

    var yearExpanded by remember { mutableStateOf(false) }
    var monthExpanded by remember { mutableStateOf(false) }
    var dayExpanded by remember { mutableStateOf(false) }

    // Options for dropdowns
    val currentYear = LocalDate.now().year
    val years = (currentYear downTo 1920).map { it.toString() }

    val months = listOf(
        "January" to "01", "February" to "02", "March" to "03", "April" to "04",
        "May" to "05", "June" to "06", "July" to "07", "August" to "08",
        "September" to "09", "October" to "10", "November" to "11", "December" to "12"
    )

    val days = (1..31).map { it.toString().padStart(2, '0') }

    // Calculate DOB string for database
    val dob = if (selectedYear.isNotEmpty() && selectedMonth.isNotEmpty() && selectedDay.isNotEmpty()) {
        val monthNum = months.first { it.first == selectedMonth }.second
        "$selectedYear-$monthNum-$selectedDay"
    } else {
        ""
    }

    // Fetch user data
    LaunchedEffect(Unit) {
        if (!isDataLoaded) {
            try {
                val user = userId?.let { UserService.getUserDetails(it) }
                if (user != null) {
                    // Set height and weight
                    height = user.height ?: 0f
                    weight = user.weight ?: 0f
                    heightInput = if (user.height != null && user.height > 0) user.height.toString() else ""
                    weightInput = if (user.weight != null && user.weight > 0) user.weight.toString() else ""

                    // Parse DOB if available
                    if (!user.dob.isNullOrEmpty()) {
                        val parts = user.dob.split("-")
                        if (parts.size == 3) {
                            selectedYear = parts[0]
                            val monthIndex = parts[1].toIntOrNull()?.minus(1) ?: 0
                            if (monthIndex in 0..11) {
                                selectedMonth = months[monthIndex].first
                            }
                            selectedDay = parts[2]
                        }
                    }
                }
                isDataLoaded = true
            } catch (e: Exception) {
                Log.e("ProfileScreen", "Error loading user data", e)
                isDataLoaded = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Profile info card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // User info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        UserSession.username?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email
                    UserSession.email?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Personal Information card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Personal Information",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Date of Birth dropdown selectors
                    Text(
                        text = "Date of Birth",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Month dropdown
                        ExposedDropdownMenuBox(
                            expanded = monthExpanded,
                            onExpandedChange = { monthExpanded = it },
                            modifier = Modifier.weight(1.2f)
                        ) {
                            OutlinedTextField(
                                value = selectedMonth,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Month") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthExpanded) },
                                modifier = Modifier.menuAnchor(),
                                singleLine = true
                            )

                            ExposedDropdownMenu(
                                expanded = monthExpanded,
                                onDismissRequest = { monthExpanded = false }
                            ) {
                                months.forEach { (month, _) ->
                                    DropdownMenuItem(
                                        text = { Text(month) },
                                        onClick = {
                                            selectedMonth = month
                                            monthExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Day dropdown
                        ExposedDropdownMenuBox(
                            expanded = dayExpanded,
                            onExpandedChange = { dayExpanded = it },
                            modifier = Modifier.weight(0.8f)
                        ) {
                            OutlinedTextField(
                                value = selectedDay,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Day") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                                modifier = Modifier.menuAnchor(),
                                singleLine = true
                            )

                            ExposedDropdownMenu(
                                expanded = dayExpanded,
                                onDismissRequest = { dayExpanded = false }
                            ) {
                                days.forEach { day ->
                                    DropdownMenuItem(
                                        text = { Text(day) },
                                        onClick = {
                                            selectedDay = day
                                            dayExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Year dropdown
                        ExposedDropdownMenuBox(
                            expanded = yearExpanded,
                            onExpandedChange = { yearExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedYear,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Year") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded) },
                                modifier = Modifier.menuAnchor(),
                                singleLine = true
                            )

                            ExposedDropdownMenu(
                                expanded = yearExpanded,
                                onDismissRequest = { yearExpanded = false },
                                modifier = Modifier.heightIn(max = 300.dp)
                            ) {
                                years.forEach { year ->
                                    DropdownMenuItem(
                                        text = { Text(year) },
                                        onClick = {
                                            selectedYear = year
                                            yearExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Height input with clear unit indicator
                    OutlinedTextField(
                        value = heightInput,
                        onValueChange = {
                            heightInput = it
                            height = it.toFloatOrNull() ?: 0f
                        },
                        label = { Text("Height") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Height,
                                contentDescription = "Height"
                            )
                        },
                        trailingIcon = {
                            Text(
                                text = "cm",
                                modifier = Modifier.padding(end = 16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        supportingText = { Text("Your height in centimeters") }
                    )

                    // Weight input with clear unit indicator
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = {
                            weightInput = it
                            weight = it.toFloatOrNull() ?: 0f
                        },
                        label = { Text("Weight") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.MonitorWeight,
                                contentDescription = "Weight"
                            )
                        },
                        trailingIcon = {
                            Text(
                                text = "kg",
                                modifier = Modifier.padding(end = 16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        supportingText = { Text("Your weight in kilograms") }
                    )
                }
            }

            // Update button
            Button(
                onClick = {
                    errorMessage = null
                    isUpdating = true

                    val profile = userId?.let {
                        UserProfile(
                            user_id = userId,
                            dob = dob,
                            height = height,
                            weight = weight
                        )
                    }

                    runBlocking {
                        try {
                            Log.d("ProfileScreen", "Updating profile: $profile")
                            val response = userId?.let {
                                if (profile != null) {
                                    UserService.sendUserData(it, userProfile = profile)
                                }
                            }
                            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            isUpdating = false
                        } catch (e: Exception) {
                            errorMessage = "Failed to update data: ${e.message}"
                            isUpdating = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .height(50.dp),
                enabled = !isUpdating && selectedYear.isNotEmpty() &&
                        selectedMonth.isNotEmpty() && selectedDay.isNotEmpty() &&
                        heightInput.isNotEmpty() && weightInput.isNotEmpty()
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Update"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Profile")
                }
            }

            // Error message
            if (errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}