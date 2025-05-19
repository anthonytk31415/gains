package com.example.gains.onboarding

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.gains.UserSession
import com.example.gains.onboarding.FirebaseAuthSingleton.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

@Composable
fun Login(navController: NavController, modifier: Modifier = Modifier) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 40.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // App Logo/Title with styled "AI" highlight
            Text(
                text = buildAnnotatedString {
                    // Regular style for 'G'
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("G")
                    }
                    // Highlighted style for 'AI'
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append("AI")
                    }
                    // Regular style for 'NS'
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("NS")
                    }
                },
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "AI-Powered Fitness Companion",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Section Header
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Welcome Back",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Enter your username and password to continue",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Username/Email Field
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Username or Email",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                cursorColor = MaterialTheme.colorScheme.primary
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                        )
                    }

                    // Password Field
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Password",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                cursorColor = MaterialTheme.colorScheme.primary
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (isLoginFormValid(username, password) && !isLoading) {
                                    performLogin(username, password, context, navController, coroutineScope) { loading ->
                                        isLoading = loading
                                    }
                                }
                            })
                        )

                        // Reset Password Link
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            Text(
                                text = "Reset Password",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .clickable {
                                        navController.navigate("ResetPassword")
                                    }
                            )
                        }
                    }

                    // Login Button
                    Button(
                        onClick = {
                            if (isLoginFormValid(username, password) && !isLoading) {
                                performLogin(username, password, context, navController, coroutineScope) { loading ->
                                    isLoading = loading
                                }
                            } else if (!isLoginFormValid(username, password)) {
                                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "Log In",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }

            // Divider with "or" text
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp
                )
                Text(
                    text = "OR",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp
                )
            }

            // Create Account Button
            OutlinedButton(
                onClick = {
                    navController.navigate("NewAccount1")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 2.dp,
                )
            ) {
                Text(
                    text = "Create New Account",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

private fun performLogin(
    username: String,
    password: String,
    context: Context,
    navController: NavController,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    updateLoading: (Boolean) -> Unit
) {
    updateLoading(true)

    if (isEmailValid(username)) {
        loginWithEmail(username, password, context, navController, updateLoading)
    } else {
        coroutineScope.launch {
            val email = getEmailFromUsername(username)
            if (email != null) {
                loginWithEmail(email, password, context, navController, updateLoading)
            } else {
                Toast.makeText(context, "Username not found", Toast.LENGTH_SHORT).show()
                updateLoading(false)
            }
        }
    }
}

private fun loginWithEmail(
    email: String,
    password: String,
    context: Context,
    navController: NavController,
    updateLoading: (Boolean) -> Unit
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                UserSession.loadUserData(
                    onComplete = {
                        val isNewAccount = false
                        Log.d("Login", "User ID: ${UserSession.userId}")
                        navController.navigate("HomeNav/$isNewAccount") {
                            popUpTo("Login") { inclusive = true }
                            launchSingleTop = true
                        }
                        updateLoading(false)
                    },
                    onError = { error ->
                        Log.e("Login", "Failed to load user data: ${error.message}")
                        Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show()
                        updateLoading(false)
                    }
                )
            } else {
                val errorMessage = task.exception?.message ?: "Login failed"
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                updateLoading(false)
            }
        }
}

fun isLoginFormValid(username: String, password: String): Boolean {
    return username.isNotBlank() && password.isNotBlank()
}

fun isEmailValid(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()
    return email.matches(emailRegex)
}

suspend fun getEmailFromUsername(username: String): String? {
    // Initialize Firestore
    val firestore = FirebaseFirestore.getInstance()

    // Assuming you have a collection "users" where each document has fields like "username" and "email"
    val usersCollection = firestore.collection("users")

    // Query to find the user by their username
    val query = usersCollection.whereEqualTo("username", username)

    return try {
        // Firestore query is asynchronous, so use suspend function to handle it
        val result = query.get().await()
        Log.d("FirestoreQuery", "Query result size: ${result.size()}")
        // If the query returns a document, get the email
        if (result.isEmpty) {
            Log.d("FirestoreQuery", "No documents found for username: $username")
            null // Username not found
        } else {
            val userDocument = result.documents.first()
            val email = userDocument.getString("email") // Return the user's email
            Log.d("FirestoreQuery", "Retrieved email: $email")
            email
        }
    } catch (e: Exception) {
        // Handle any errors, such as no internet connection or Firestore issues
        Log.e("FirestoreQuery", "Error occurred: ${e.message}")
        null
    }
}