package com.callcenter.kidcare.ui.home.about

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.callcenter.kidcare.ui.theme.ButtonDarkColor
import com.callcenter.kidcare.ui.theme.ButtonLightColor
import com.callcenter.kidcare.ui.theme.TextDarkColor
import com.callcenter.kidcare.ui.theme.TextLightColor

@Composable
fun AdminButton(navController: NavHostController) {
    val isDarkTheme = isSystemInDarkTheme()
    val buttonColor = if (isDarkTheme) ButtonDarkColor else ButtonLightColor
    val textColor = if (isDarkTheme) TextDarkColor else TextDarkColor

    Button(
        onClick = {
            navController.navigate("admin")
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = textColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Admin Panel",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}
