package com.example.gains.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.compose.foundation.clickable

@Composable
fun Login(navController: NavController, modifier: Modifier = Modifier) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
                //.requiredHeight(height = 513.dp)
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
                    text = "Login",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 9.38.em,
                    style = TextStyle(
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = "Enter your username and password to login",
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
                    //.requiredHeight(height = 184.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
                    modifier = Modifier
                        .requiredWidth(width = 327.dp)
                ) {
                    Text(
                        text = "Username or Email",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 10.71.em,
                        style = TextStyle(
                            fontSize = 14.sp
                        ),
                        modifier = Modifier
                            .requiredWidth(width = 129.dp)
                            .requiredHeight(height = 21.dp)
                    )
                    TextField(
                        value = username, // bind the value to the username state
                        onValueChange = {
                            username = it
                        }, // update the username/email state when the text changes
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
                                if (isLoginFormValid(username, password)) {
                                    navController.navigate("HomePage")
                                } else {
                                    // You can show an error state or Toast here
                                }
                            }
                        )
                    )
                    Text(
                        text = "Reset Password",
                        color = Color.White,
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.End,
                        lineHeight = 10.71.em,
                        style = TextStyle(
                            fontSize = 14.sp,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.25f),
                                offset = Offset(0f, 4f),
                                blurRadius = 4f
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeight(height = 21.dp)
                            .shadow(elevation = 4.dp)
                            .clickable {
                                navController.navigate("ResetPassword")
                            }
                    )
                }
                Button(
                    onClick = {
                        if (isLoginFormValid(username, password)) {
                            navController.navigate("HomeScreen")
                        } else {
                            // Show error or Toast
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .requiredWidth(width = 327.dp)
                ) {
                    HorizontalDivider(
                        color = Color(0xffe6e6e6),
                        modifier = Modifier
                            .requiredHeight(height = 1.dp)
                            .weight(weight = 0.5f)
                    )
                    Text(
                        text = "or",
                        color = Color(0xffbdbdbd),
                        textAlign = TextAlign.Center,
                        lineHeight = 10.em,
                        style = TextStyle(
                            fontSize = 14.sp
                        )
                    )
                    HorizontalDivider(
                        color = Color(0xffe6e6e6),
                        modifier = Modifier
                            .requiredHeight(height = 1.dp)
                            .weight(weight = 0.5f)
                    )
                }
                Button(
                    onClick = {
                        navController.navigate("NewAccount1")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Create New Account",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
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
}

fun isLoginFormValid(username: String, password: String): Boolean {
    return username.isNotBlank() &&
            password.isNotBlank()
}