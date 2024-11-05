package com.callcenter.kidcare.ui

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.callcenter.kidcare.R
import com.callcenter.kidcare.ui.theme.*
import com.callcenter.kidcare.ui.uionly.UiLoginViaEmail
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun ForgotPassword() {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }

    val firebaseAuth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        titleVisible = true
    }

    // Handle back press to navigate to UiLoginViaEmail
    DisposableEffect(context) {
        val callback = object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (context is ComponentActivity) {
                    context.setContent { UiLoginViaEmail() }
                }
            }
        }
        (context as? ComponentActivity)?.onBackPressedDispatcher?.addCallback(callback)
        onDispose {
            callback.remove()
        }
    }

    // Function to send password reset email
    fun sendPasswordResetEmail() {
        if (email.isNotEmpty()) {
            loading = true
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    loading = false
                    if (task.isSuccessful) {
                        message = "Password reset email sent."
                        error = ""
                    } else {
                        error = "Failed to send reset email."
                        message = ""
                    }
                }
        } else {
            error = "Please enter your email."
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Lavender0, Ocean6)
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back Button Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (context is ComponentActivity) {
                            context.setContent { UiLoginViaEmail() }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to Login",
                        tint = Ocean7,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Illustration/Image with Animation
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
                exit = fadeOut()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.assets_bg_forgot_password),
                    contentDescription = "Forgot Password Illustration",
                    modifier = Modifier
                        .size(250.dp)
                        .padding(bottom = 8.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // Title with Fade-In Effect
            AnimatedVisibility(
                visible = titleVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
                exit = fadeOut()
            ) {
                Text(
                    text = "Lupa Password",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif
                    ),
                    color = costum01,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Card for Input Fields
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Neutral0)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Email TextField with Icon
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = Ocean7) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email Icon",
                                tint = Ocean8
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Neutral8,
                            unfocusedTextColor = Neutral8,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Ocean8,
                            unfocusedIndicatorColor = Ocean6,
                            cursorColor = Ocean8,
                            errorIndicatorColor = FunctionalRed
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reset Password Button with Icon
                    Button(
                        onClick = { sendPasswordResetEmail() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Ocean7,
                            contentColor = Neutral0
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 8.dp),
                                color = Neutral0
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Reset Password Icon",
                                tint = Neutral0,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reset Password", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Error Message with Animation
                    AnimatedVisibility(
                        visible = error.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = error,
                            color = FunctionalRed,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Success Message with Animation
                    AnimatedVisibility(
                        visible = message.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = message,
                            color = Ocean7,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Optional: You can add more overlay components here if needed
    }
}
