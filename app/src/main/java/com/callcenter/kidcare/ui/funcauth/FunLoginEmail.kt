package com.callcenter.kidcare.ui.funcauth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    // UI State
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    // Generate Random Verification Code
    private fun generateRandomCode(): String {
        return Random.nextInt(100000, 999999).toString()
    }

    // Send Email Function
    private fun sendEmail(receiverEmail: String, verificationCode: String) {
        viewModelScope.launch {
            try {
                val senderEmail = "kidcarex@gmail.com"
                val senderPassword = "mmgvsuyxhqsoxxtc" // **Penting:** Simpan kredensial dengan aman!
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
                    dialogMessage = "Gagal mengirim email: ${e.message}",
                    dialogVisible = true
                )
            }
        }
    }

    // Save Verification Code to Firestore
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
                    dialogMessage = "Kode verifikasi telah dikirim ke email Anda.",
                    dialogVisible = true
                )
            }
            .addOnFailureListener { e ->
                _uiState.value = _uiState.value.copy(
                    dialogMessage = "Error menyimpan kode verifikasi: ${e.message}",
                    dialogVisible = true
                )
            }
    }

    // Validate Verification Code
    fun validateVerificationCode(inputCode: String, email: String, password: String) {
        _uiState.value = _uiState.value.copy(loadingVerification = true) // Mulai loading verifikasi

        firestore.collection("verificationCodes")
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val savedCode = document.getString("verificationCode")
                    val savedTimestamp = document.getLong("timestamp") ?: 0
                    val currentTime = System.currentTimeMillis()
                    val expirationTime = 5 * 60 * 1000 // 5 menit

                    if (inputCode == savedCode && (currentTime - savedTimestamp) < expirationTime) {
                        firestore.collection("verificationCodes").document(email).delete()
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    _uiState.value = _uiState.value.copy(
                                        navigateToHome = true,
                                        loadingVerification = false
                                    )
                                } else {
                                    _uiState.value = _uiState.value.copy(
                                        verificationError = "Login gagal: ${task.exception?.message}",
                                        loadingVerification = false
                                    )
                                }
                            }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            verificationError = "Kode verifikasi tidak valid atau sudah kadaluarsa.",
                            loadingVerification = false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        verificationError = "Kode verifikasi tidak ditemukan.",
                        loadingVerification = false
                    )
                }
            }
            .addOnFailureListener {
                _uiState.value = _uiState.value.copy(
                    verificationError = "Gagal memvalidasi kode verifikasi.",
                    loadingVerification = false
                )
            }
    }

    // Handle Login Button Click
    fun onLoginClick(email: String, password: String) {
        if (password.length >= 8) {
            _uiState.value = _uiState.value.copy(loading = true) // Mulai loading
            val verificationCode = generateRandomCode()
            saveVerificationCode(verificationCode, email)
            _uiState.value = _uiState.value.copy(
                loading = false, // Hentikan loading setelah mengirim kode
                verificationDialogVisible = true
            )
        } else {
            _uiState.value = _uiState.value.copy(
                dialogMessage = "Silakan masukkan password yang valid.",
                dialogVisible = true,
                loading = false // Hentikan loading jika ada error
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
