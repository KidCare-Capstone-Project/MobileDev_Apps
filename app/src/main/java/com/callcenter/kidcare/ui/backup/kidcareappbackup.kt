//package com.callcenter.kidcare.ui.uionly
//
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Email
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import com.callcenter.kidcare.ui.theme.*
//import com.google.firebase.auth.FirebaseAuth
//import kotlinx.coroutines.launch
//
//@Composable
//fun VerifyEmailScreen(onVerificationCompleted: () -> Unit) {
//    val auth = FirebaseAuth.getInstance()
//    val user = auth.currentUser
//    val coroutineScope = rememberCoroutineScope()
//    var isSending by remember { mutableStateOf(false) }
//    var message by remember { mutableStateOf("") }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Ocean0),
//        contentAlignment = Alignment.Center
//    ) {
//        Card(
//            modifier = Modifier
//                .fillMaxWidth(0.9f)
//                .padding(16.dp),
//            shape = RoundedCornerShape(16.dp),
//            colors = CardDefaults.cardColors(containerColor = Ocean1),
//            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(24.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Email,
//                    contentDescription = "Email Icon",
//                    tint = Ocean8,
//                    modifier = Modifier.size(64.dp)
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//                Text(
//                    text = "Verifikasi Email Anda",
//                    style = MaterialTheme.typography.headlineMedium.copy(
//                        fontWeight = FontWeight.Bold
//                    ),
//                    color = Ocean8,
//                    textAlign = TextAlign.Center
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(
//                    text = "Silakan periksa email Anda dan klik tautan verifikasi untuk mengaktifkan akun Anda.",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = TextColor,
//                    modifier = Modifier.padding(horizontal = 8.dp),
//                    textAlign = TextAlign.Center
//                )
//                Spacer(modifier = Modifier.height(24.dp))
//                AnimatedVisibility(
//                    visible = message.isNotEmpty(),
//                    enter = fadeIn(),
//                    exit = fadeOut()
//                ) {
//                    Text(
//                        text = message,
//                        style = MaterialTheme.typography.bodySmall,
//                        color = when {
//                            message.startsWith("Berhasil") -> FunctionalGreen
//                            message.startsWith("Gagal") -> FunctionalRed
//                            else -> TextColor
//                        },
//                        modifier = Modifier.padding(vertical = 8.dp)
//                    )
//                }
//                Spacer(modifier = Modifier.height(8.dp))
//                Button(
//                    onClick = {
//                        if (user != null) {
//                            coroutineScope.launch {
//                                isSending = true
//                                user.sendEmailVerification().addOnCompleteListener { task ->
//                                    isSending = false
//                                    message = if (task.isSuccessful) {
//                                        "Berhasil mengirim ulang email verifikasi."
//                                    } else {
//                                        "Gagal mengirim ulang email: ${task.exception?.message}"
//                                    }
//                                }
//                            }
//                        }
//                    },
//                    enabled = !isSending,
//                    colors = ButtonDefaults.buttonColors(containerColor = Ocean8),
//                    shape = RoundedCornerShape(8.dp),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(48.dp)
//                ) {
//                    if (isSending) {
//                        CircularProgressIndicator(
//                            modifier = Modifier
//                                .size(24.dp)
//                                .padding(end = 8.dp),
//                            color = WhiteColor,
//                            strokeWidth = 2.dp
//                        )
//                    }
//                    Text(
//                        "Kirim Ulang Email Verifikasi",
//                        color = WhiteColor,
//                        style = MaterialTheme.typography.labelLarge
//                    )
//                }
//                Spacer(modifier = Modifier.height(16.dp))
//                OutlinedButton(
//                    onClick = {
//                        coroutineScope.launch {
//                            user?.reload()?.addOnCompleteListener { task ->
//                                if (task.isSuccessful) {
//                                    if (user.isEmailVerified) {
//                                        onVerificationCompleted()
//                                    } else {
//                                        message = "Email masih belum diverifikasi."
//                                    }
//                                } else {
//                                    message = "Gagal memuat status verifikasi: ${task.exception?.message}"
//                                }
//                            }
//                        }
//                    },
//                    colors = ButtonDefaults.outlinedButtonColors(
//                        containerColor = Ocean3,
//                        contentColor = TextColor
//                    ),
//                    shape = RoundedCornerShape(8.dp),
//                    border = BorderStroke(1.dp, Ocean8),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(48.dp)
//                ) {
//                    Text(
//                        "Periksa Status Verifikasi",
//                        style = MaterialTheme.typography.labelLarge
//                    )
//                }
//            }
//        }
//
//        // Menempatkan tombol "Keluar" di bagian bawah layar
//        Column(
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(16.dp)
//                .fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            OutlinedButton(
//                onClick = { auth.signOut() },
//                colors = ButtonDefaults.outlinedButtonColors(
//                    containerColor = Ocean1,
//                    contentColor = Ocean8
//                ),
//                shape = RoundedCornerShape(8.dp),
//                border = BorderStroke(1.dp, Ocean8),
//                modifier = Modifier
//                    .fillMaxWidth(0.5f)
//                    .height(48.dp)
//            ) {
//                Text("Keluar", style = MaterialTheme.typography.labelLarge)
//            }
//        }
//    }
//}
