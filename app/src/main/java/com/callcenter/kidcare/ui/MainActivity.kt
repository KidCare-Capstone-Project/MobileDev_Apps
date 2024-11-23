package com.callcenter.kidcare.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.callcenter.kidcare.data.LocaleManager
import com.callcenter.kidcare.ui.funcauth.FunLoginGoogle
import com.callcenter.kidcare.ui.uionly.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        LocaleManager.setLocale(this)
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current

            SplashScreen {
                val intent = Intent(context, FunLoginGoogle::class.java)
                context.startActivity(intent)
                (context as? ComponentActivity)?.finish()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreen {}
    }
}
