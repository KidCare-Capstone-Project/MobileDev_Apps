package com.callcenter.kidcare.ui.uionly

import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.callcenter.kidcare.R
import com.callcenter.kidcare.ui.ForgotPassword
import com.callcenter.kidcare.ui.KidCareApp
import com.callcenter.kidcare.ui.UiRegister
import com.callcenter.kidcare.ui.funcauth.FunLoginEmail
import com.callcenter.kidcare.ui.funcauth.FunLoginGoogle
import com.callcenter.kidcare.ui.theme.*

@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun UiLoginViaEmail(funLoginEmail: FunLoginEmail = viewModel()) {
    val uiState by funLoginEmail.uiState.collectAsState()
    val context = LocalContext.current

    DisposableEffect(context) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Kembali ke halaman login Google
                if (context is ComponentActivity) {
                    val intent = Intent(context, FunLoginGoogle::class.java)
                    context.startActivity(intent)
                    context.finish()
                }
            }
        }
        (context as? ComponentActivity)?.onBackPressedDispatcher?.addCallback(callback)
        onDispose {
            callback.remove()
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var showPassword by remember { mutableStateOf(false) }

    var digit1 by remember { mutableStateOf("") }
    var digit2 by remember { mutableStateOf("") }
    var digit3 by remember { mutableStateOf("") }
    var digit4 by remember { mutableStateOf("") }
    var digit5 by remember { mutableStateOf("") }
    var digit6 by remember { mutableStateOf("") }

    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }
    val focusRequester3 = remember { FocusRequester() }
    val focusRequester4 = remember { FocusRequester() }
    val focusRequester5 = remember { FocusRequester() }
    val focusRequester6 = remember { FocusRequester() }

    LaunchedEffect(uiState.verificationDialogVisible) {
        if (uiState.verificationDialogVisible) {
            focusRequester1.requestFocus()
        }
    }

    var titleVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        titleVisible = true
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
                            val intent = Intent(context, FunLoginGoogle::class.java)
                            context.startActivity(intent)
                            context.finish()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to Google Login",
                        tint = Ocean7,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
                exit = fadeOut()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.assets_login),
                    contentDescription = "Login Image",
                    modifier = Modifier
                        .fillMaxWidth(0.35f)
                        .aspectRatio(1f)
                        .padding(bottom = 8.dp)
                )
            }

            AnimatedVisibility(
                visible = titleVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
                exit = fadeOut()
            ) {
                Text(
                    text = "Masuk dengan Email",
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
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Neutral8,
                            unfocusedTextColor = Neutral8,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Ocean8,
                            unfocusedIndicatorColor = Ocean6,
                            cursorColor = Ocean8,
                            errorIndicatorColor = FunctionalRed,
                            selectionColors = TextSelectionColors(
                                handleColor = Ocean8,
                                backgroundColor = Ocean8.copy(alpha = 0.4f)
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                        },
                        label = { Text("Password", color = Ocean7) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password Icon",
                                tint = Ocean8
                            )
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                val icon =
                                    if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                Icon(imageVector = icon, contentDescription = null, tint = Ocean8)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Neutral8,
                            unfocusedTextColor = Neutral8,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Ocean8,
                            unfocusedIndicatorColor = Ocean6,
                            cursorColor = Ocean8,
                            errorIndicatorColor = FunctionalRed,
                            selectionColors = TextSelectionColors(
                                handleColor = Ocean8,
                                backgroundColor = Ocean8.copy(alpha = 0.4f)
                            )
                        )
                    )

                    AnimatedVisibility(
                        visible = password.isNotEmpty() && password.length < 8,
                        enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                        exit = fadeOut(animationSpec = tween(durationMillis = 300))
                    ) {
                        Text(
                            text = "Password harus minimal 8 karakter",
                            color = FunctionalRed,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = {
                            if (context is ComponentActivity) {
                                context.setContent { ForgotPassword() }
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 4.dp)
                    ) {
                        Text(
                            text = "Lupa Password?",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily.SansSerif
                            ),
                            color = Ocean7,
                            textDecoration = TextDecoration.Underline
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            funLoginEmail.onLoginClick(email, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Ocean7,
                            contentColor = Neutral0
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.loading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 8.dp),
                                color = Neutral0
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Login Icon",
                                tint = Neutral0,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Masuk", fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Belum punya akun? Silakan ",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.SansSerif
                            ),
                            color = Neutral8
                        )
                        Text(
                            text = "Daftar",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.SansSerif,
                                textDecoration = TextDecoration.Underline,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Ocean7,
                            modifier = Modifier.clickable {
                                if (context is ComponentActivity) {
                                    context.setContent { UiRegister() }
                                }
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (uiState.verificationDialogVisible) {
            AlertDialog(
                onDismissRequest = { funLoginEmail.dismissVerificationDialog() },
                title = {
                    Text(
                        "Kode Verifikasi",
                        color = Ocean8,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                text = {
                    Column {
                        Text(
                            "Sebuah kode verifikasi telah dikirim ke email Anda.",
                            color = Ocean7,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Row dengan 6 DigitTextField yang telah diperbaiki
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DigitTextField(
                                digit = digit1,
                                onValueChange = { digit1 = it },
                                focusRequester = focusRequester1,
                                onNext = { focusRequester2.requestFocus() },
                                onBackspace = {}
                            )
                            DigitTextField(
                                digit = digit2,
                                onValueChange = { digit2 = it },
                                focusRequester = focusRequester2,
                                onNext = { focusRequester3.requestFocus() },
                                onBackspace = { focusRequester1.requestFocus() }
                            )
                            DigitTextField(
                                digit = digit3,
                                onValueChange = { digit3 = it },
                                focusRequester = focusRequester3,
                                onNext = { focusRequester4.requestFocus() },
                                onBackspace = { focusRequester2.requestFocus() }
                            )
                            DigitTextField(
                                digit = digit4,
                                onValueChange = { digit4 = it },
                                focusRequester = focusRequester4,
                                onNext = { focusRequester5.requestFocus() },
                                onBackspace = { focusRequester3.requestFocus() }
                            )
                            DigitTextField(
                                digit = digit5,
                                onValueChange = { digit5 = it },
                                focusRequester = focusRequester5,
                                onNext = { focusRequester6.requestFocus() },
                                onBackspace = { focusRequester4.requestFocus() }
                            )
                            DigitTextField(
                                digit = digit6,
                                onValueChange = { digit6 = it },
                                focusRequester = focusRequester6,
                                onNext = { /* Tidak ada field berikutnya */ },
                                onBackspace = { focusRequester5.requestFocus() }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (uiState.verificationError.isNotEmpty()) {
                            Text(
                                uiState.verificationError,
                                color = FunctionalRed,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            funLoginEmail.validateVerificationCode(
                                "${digit1}${digit2}${digit3}${digit4}${digit5}${digit6}",
                                email,
                                password
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Ocean7,
                            contentColor = Neutral0
                        )
                    ) {
                        if (uiState.loadingVerification) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Neutral0)
                        } else {
                            Text("Verifikasi & Masuk", color = Neutral0)
                        }
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { funLoginEmail.dismissVerificationDialog() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Ocean6,
                            containerColor = Color.Transparent
                        ),
                        border = BorderStroke(1.dp, Ocean6)
                    ) {
                        Text("Batal", color = Ocean7)
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = Lavender0,
                tonalElevation = 16.dp,
                modifier = Modifier.padding(16.dp)
            )
        }

        if (uiState.dialogVisible) {
            AlertDialog(
                onDismissRequest = { funLoginEmail.dismissDialog() },
                title = null,
                text = {
                    Text(
                        text = uiState.dialogMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ocean7,
                        modifier = Modifier.padding(16.dp)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { funLoginEmail.dismissDialog() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Ocean7,
                            contentColor = Neutral0
                        ),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("OK", color = Neutral0)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.padding(16.dp)
            )
        }

        if (uiState.navigateToHome) {
            LaunchedEffect(Unit) {
                if (context is ComponentActivity) {
                    context.setContent { KidCareApp() }
                }
            }
        }
    }
}

@Composable
fun DigitTextField(
    digit: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onNext: () -> Unit,
    onBackspace: () -> Unit
) {
    OutlinedTextField(
        value = digit,
        onValueChange = {
            if (it.length <= 1 && it.all { char -> char.isDigit() }) {
                onValueChange(it)
                if (it.isNotEmpty()) {
                    onNext()
                }
            }
        },
        singleLine = true,
        maxLines = 1,
        visualTransformation = VisualTransformation.None,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier
            .width(48.dp)
            .background(Lavender1, RoundedCornerShape(10.dp))
            .padding(1.dp)
            .focusRequester(focusRequester)
            .onKeyEvent { keyEvent ->
                if (keyEvent.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DEL &&
                    keyEvent.type == androidx.compose.ui.input.key.KeyEventType.KeyDown) {
                    if (digit.isEmpty()) {
                        onBackspace()
                    }
                    false
                } else {
                    false
                }
            },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Ocean8,
            focusedContainerColor = Lavender0,
            unfocusedContainerColor = Lavender0,
            unfocusedIndicatorColor = Ocean6,
            errorIndicatorColor = FunctionalRed,
            errorContainerColor = Lavender0,
            focusedTextColor = Neutral8,
            unfocusedTextColor = Neutral8,
            cursorColor = Ocean8
        ),
        textStyle = LocalTextStyle.current.copy(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.sp
        )
    )
}
