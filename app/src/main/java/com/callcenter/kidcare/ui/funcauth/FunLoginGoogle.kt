package com.callcenter.kidcare.ui.funcauth

import android.app.AlertDialog
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.callcenter.kidcare.R
import com.callcenter.kidcare.ui.KidCareApp
import com.callcenter.kidcare.ui.uionly.GoogleLoginScreen
import com.callcenter.kidcare.ui.uionly.UiLoginViaEmail
import com.callcenter.kidcare.ui.theme.KidCareTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class FunLoginGoogle : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.R)
    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? = task.result
                account?.let {
                    firebaseAuthWithGoogle(it.idToken!!)
                }
            } else {
                showSignInFailureDialog(task.exception?.message)
            }
        }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            setContent { KidCareApp() }
        } else {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)

            setContent {
                KidCareTheme {
                    GoogleLoginScreen(
                        onClick = { checkInternetAndSignIn() },
                        onEmailLoginClick = { navigateToEmailLogin() }
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkInternetAndSignIn() {
        if (isInternetAvailable()) {
            signIn()
        } else {
            showNoInternetDialog()
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun showNoInternetDialog() {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    setContent {
                        KidCareTheme {
                            GoogleLoginScreen(
                                onClick = { checkInternetAndSignIn() },
                                onEmailLoginClick = { navigateToEmailLogin() })
                        }
                    }
                }
                .setCancelable(false)
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    setContent { KidCareApp() }
                } else {
                    showSignInFailureDialog(task.exception?.message)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun showSignInFailureDialog(errorMessage: String?) {
        AlertDialog.Builder(this)
            .setTitle("Sign-In Failed")
            .setMessage(errorMessage ?: "Unknown error occurred.")
            .setPositiveButton("Retry") { dialog, _ ->
                dialog.dismiss()
                checkInternetAndSignIn()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun navigateToEmailLogin() {
        setContent {
            KidCareTheme {
                UiLoginViaEmail()
            }
        }
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
@Composable
fun PreviewGoogleLoginScreen() {
    KidCareTheme {
        GoogleLoginScreen(onClick = {}, onEmailLoginClick = {})
    }
}
