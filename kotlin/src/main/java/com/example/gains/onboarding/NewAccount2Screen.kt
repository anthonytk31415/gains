package com.example.gains.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController

@Composable
fun NewAccount2(navController: NavController, modifier: Modifier = Modifier) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    0f to Color(0xffe80707),
                    1f to Color(0xff0010f4),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 0f)
                )
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .offset(y = (-100).dp)
        ) {
            Text(
                text = "Gains",
                color = Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .requiredWidth(width = 210.dp)
                    .requiredHeight(height = 58.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create an account",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 9.38.em,
                    style = TextStyle(
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = "Enter your username and password ",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 10.71.em,
                    style = TextStyle(
                        fontSize = 14.sp
                    )
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                modifier = Modifier
                    .requiredWidth(width = 327.dp)
                //.requiredHeight(height = 249.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
                    modifier = Modifier
                        .requiredWidth(width = 327.dp)
                    //.requiredHeight(height = 193.dp)
                ) {
                    Text(
                        text = "Username",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 10.71.em,
                        style = TextStyle(
                            fontSize = 14.sp
                        ),
                        modifier = Modifier
                            .requiredWidth(width = 68.dp)
                            .requiredHeight(height = 21.dp)
                    )

                    TextField(
                        value = username, // bind the value to the username state
                        onValueChange = {
                            username = it
                        }, // update the email state when the text changes
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black
                        ),
                        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                // No need to do anything, focus moves automatically
                            }
                        )
                    )
                    Text(
                        text = "Password",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 10.71.em,
                        style = TextStyle(
                            fontSize = 14.sp
                        )
                    )
                    TextField(
                        value = password, // bind the value to the password state
                        onValueChange = {
                            password = it
                        }, // update the password state when the text changes
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black
                        ),
                        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                // No need to do anything, focus moves automatically
                            }
                        )
                    )
                    Text(
                        text = "Confirm Password",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 10.71.em,
                        style = TextStyle(
                            fontSize = 14.sp
                        )
                    )
                    TextField(
                        value = confirmPassword, // bind the value to the confirmPassword state
                        onValueChange = {
                            confirmPassword = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black
                        ),
                        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (isNewAccountFormValid(username, password, confirmPassword)) {
                                    navController.navigate("HomePage")
                                } else {
                                    // You can show an error state or Toast here
                                }
                            }
                        )
                    )
                }
                Button(
                    onClick = {
                        if (isNewAccountFormValid(username, password, confirmPassword)) {
                            navController.navigate("HomePage")
                        } else {
                            // Show error or Toast
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Continue",
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            Text(
                textAlign = TextAlign.Center,
                lineHeight = 13.sp,
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xffb4b4b4),
                            fontSize = 12.sp
                        )
                    ) { append("By clicking continue, you agree to our") }
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xff828282),
                            fontSize = 12.sp
                        )
                    ) { append(" ") }
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    ) { append("Terms of Service") }
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xff828282),
                            fontSize = 12.sp
                        )
                    ) { append(" ") }
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xffb4b4b4),
                            fontSize = 12.sp
                        )
                    ) { append("and") }
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xff828282),
                            fontSize = 12.sp
                        )
                    ) { append(" ") }
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    ) { append("Privacy Policy") }
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

fun isNewAccountFormValid(username: String, password: String, confirmPassword: String): Boolean {
    return username.isNotBlank() &&
            password.isNotBlank() &&
            confirmPassword.isNotBlank() &&
            password == confirmPassword
}