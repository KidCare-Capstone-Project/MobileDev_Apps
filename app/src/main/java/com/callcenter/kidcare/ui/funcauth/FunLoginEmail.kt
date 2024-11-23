package com.callcenter.kidcare.ui.funcauth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.random.Random

class FunLoginEmail : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private fun generateRandomCode(): String {
        return Random.nextInt(100000, 999999).toString()
    }

    private fun sendEmail(receiverEmail: String, verificationCode: String) {
        viewModelScope.launch {
            try {
                val senderEmail = "kidcarex@gmail.com"
                val senderPassword = "mmgvsuyxhqsoxxtc" // **Important:** Store credentials securely!
                val host = "smtp.gmail.com"

                val properties: Properties = System.getProperties().apply {
                    put("mail.transport.protocol", "smtp")
                    put("mail.smtp.host", host)
                    put("mail.smtp.port", "465")
                    put("mail.smtp.socketFactory.fallback", "false")
                    put("mail.smtp.quitwait", "false")
                    put("mail.smtp.socketFactory.port", "465")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                    put("mail.smtp.ssl.enable", "true")
                    put("mail.smtp.auth", "true")
                }

                val session: Session = Session.getInstance(properties, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(senderEmail, senderPassword)
                    }
                })

                val mimeMessage = MimeMessage(session).apply {
                    addRecipient(Message.RecipientType.TO, InternetAddress(receiverEmail))
                    subject = "Your Verification Code"
                    setText("Your verification code is: $verificationCode")
                }

                withContext(Dispatchers.IO) {
                    Transport.send(mimeMessage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    dialogMessage = "Failed to send email: ${e.message}",
                    dialogVisible = true
                )
            }
        }
    }

    private fun saveVerificationCode(code: String, email: String) {
        val timestamp = System.currentTimeMillis()
        val codeData = hashMapOf(
            "verificationCode" to code,
            "email" to email,
            "timestamp" to timestamp
        )

        firestore.collection("verificationCodes")
            .document(email)
            .set(codeData)
            .addOnSuccessListener {
                sendEmail(email, code)
                _uiState.value = _uiState.value.copy(
                    dialogMessage = "Verification code has been sent to your email.",
                    dialogVisible = true
                )
            }
            .addOnFailureListener { e ->
                _uiState.value = _uiState.value.copy(
                    dialogMessage = "Error saving verification code: ${e.message}",
                    dialogVisible = true
                )
            }
    }

    fun validateVerificationCode(inputCode: String, email: String, password: String) {
        _uiState.value = _uiState.value.copy(loadingVerification = true)

        firestore.collection("verificationCodes")
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val savedCode = document.getString("verificationCode")
                    val savedTimestamp = document.getLong("timestamp") ?: 0
                    val currentTime = System.currentTimeMillis()
                    val expirationTime = 5 * 60 * 1000 // 5 minutes

                    if (inputCode == savedCode && (currentTime - savedTimestamp) < expirationTime) {
                        firestore.collection("verificationCodes").document(email).delete()
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    user?.let {
                                        assignUserRole(it)
                                    }
                                    _uiState.value = _uiState.value.copy(
                                        navigateToHome = true,
                                        loadingVerification = false
                                    )
                                } else {
                                    _uiState.value = _uiState.value.copy(
                                        verificationError = "Login failed: ${task.exception?.message}",
                                        loadingVerification = false
                                    )
                                }
                            }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            verificationError = "Invalid or expired verification code.",
                            loadingVerification = false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        verificationError = "Verification code not found.",
                        loadingVerification = false
                    )
                }
            }
            .addOnFailureListener {
                _uiState.value = _uiState.value.copy(
                    verificationError = "Failed to validate verification code.",
                    loadingVerification = false
                )
            }
    }

    // Assign User Role and Store Data in Firestore
    private fun assignUserRole(user: FirebaseUser) {
        val adminEmails = listOf(
            "akunstoragex@gmail.com",
            "cerberus404x@gmail.com",
            "kidcarex@gmail.com",
            "kennyjosiahresa@gmail.com"
        )

        val userEmail = user.email
        val userProfilePicUrl = user.photoUrl?.toString()
        val username = user.displayName ?: userEmail?.substringBefore("@") ?: "Unknown"
        val userUuid = user.uid

        val role = if (userEmail in adminEmails) "admin" else "user"

        val userData = mapOf(
            "uuid" to userUuid,
            "email" to userEmail,
            "role" to role,
            "username" to username,
            "profilePic" to userProfilePicUrl,
            "timestamp" to FieldValue.serverTimestamp()
        )

        firestore.collection("users")
            .document(user.uid)
            .set(userData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("FunLoginEmail", "$role role assigned to $userEmail")
            }
            .addOnFailureListener { e ->
                Log.e("FunLoginEmail", "Error assigning $role role", e)
            }
    }

    // Handle Login Button Click
    fun onLoginClick(email: String, password: String) {
        if (password.length >= 8) {
            _uiState.value = _uiState.value.copy(loading = true)
            val verificationCode = generateRandomCode()
            saveVerificationCode(verificationCode, email)
            _uiState.value = _uiState.value.copy(
                loading = false,
                verificationDialogVisible = true
            )
        } else {
            _uiState.value = _uiState.value.copy(
                dialogMessage = "Please enter a valid password.",
                dialogVisible = true,
                loading = false
            )
        }
    }

    // Handle General Dialog Dismiss
    fun dismissDialog() {
        _uiState.value = _uiState.value.copy(dialogVisible = false)
    }

    // Handle Verification Dialog Dismiss
    fun dismissVerificationDialog() {
        _uiState.value = _uiState.value.copy(verificationDialogVisible = false)
    }

    // UI State Data Class
    data class LoginUiState(
        val email: String = "",
        val password: String = "",
        val verificationCode: String = "",
        val showPassword: Boolean = false,
        val passwordError: String = "",
        val loading: Boolean = false,
        val verificationDialogVisible: Boolean = false,
        val verificationError: String = "",
        val loadingVerification: Boolean = false,
        val dialogVisible: Boolean = false,
        val dialogMessage: String = "",
        val navigateToHome: Boolean = false
    )
}
