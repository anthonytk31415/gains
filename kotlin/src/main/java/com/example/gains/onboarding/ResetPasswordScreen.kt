package com.example.gains.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController

@Composable
fun ResetPassword(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Gains",
            color = Color.White,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .requiredWidth(width = 210.dp)
                .requiredHeight(height = 58.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Forgot your Password",
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 9.38.em,
                style = TextStyle(
                    fontSize = 16.sp))
            Text(
                text = "Please enter the email address you’d like your ",
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 10.71.em,
                style = TextStyle(
                    fontSize = 14.sp))
            Text(
                text = "password reset information sent to",
                color = Color.White,
                lineHeight = 10.71.em,
                style = TextStyle(
                    fontSize = 14.sp))
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
            modifier = Modifier
                .requiredWidth(width = 327.dp)
                .requiredHeight(height = 184.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
                modifier = Modifier
                    .requiredWidth(width = 327.dp)
            ) {
                Text(
                    text = "Email Address",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 10.71.em,
                    style = TextStyle(
                        fontSize = 14.sp),
                    modifier = Modifier
                        .requiredWidth(width = 95.dp)
                        .requiredHeight(height = 21.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(height = 40.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(color = Color.White)
                        .border(border = BorderStroke(1.dp, Color(0xffe0e0e0)),
                            shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp,
                            vertical = 8.dp))
            }
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(height = 40.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(color = Color.Black)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Request reset link",
                    color = Color.White,
                    lineHeight = 10.em,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium),
                    modifier = Modifier
                        .wrapContentHeight(align = Alignment.CenterVertically))
            }
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(height = 40.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(color = Color.Black)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Back to Login",
                    color = Color.White,
                    lineHeight = 10.em,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium),
                    modifier = Modifier
                        .wrapContentHeight(align = Alignment.CenterVertically))
            }
            Text(
                textAlign = TextAlign.Center,
                lineHeight = 13.sp,
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(
                        color = Color(0xffb4b4b4),
                        fontSize = 12.sp)) {append("By clicking continue, you agree to our")}
                    withStyle(style = SpanStyle(
                        color = Color(0xff828282),
                        fontSize = 12.sp)) {append(" ")}
                    withStyle(style = SpanStyle(
                        color = Color.White,
                        fontSize = 12.sp)) {append("Terms of Service")}
                    withStyle(style = SpanStyle(
                        color = Color(0xff828282),
                        fontSize = 12.sp)) {append(" ")}
                    withStyle(style = SpanStyle(
                        color = Color(0xffb4b4b4),
                        fontSize = 12.sp)) {append("and")}
                    withStyle(style = SpanStyle(
                        color = Color(0xff828282),
                        fontSize = 12.sp)) {append(" ")}
                    withStyle(style = SpanStyle(
                        color = Color.White,
                        fontSize = 12.sp)) {append("Privacy Policy")}},
                modifier = Modifier
                    .fillMaxWidth())
        }
    }
}
