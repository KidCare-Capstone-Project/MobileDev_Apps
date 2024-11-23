package com.callcenter.kidcare.ui.home.childprofile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.callcenter.kidcare.ui.theme.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChildDetailScreen(childId: String, navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val isLight = !KidCareTheme.colors.isDark

    val tabBackgroundColor = KidCareTheme.colors.uiBackground
    val tabContentColor = if (isLight) MinimalTextLight else MinimalTextDark

    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            backgroundColor = tabBackgroundColor,
            contentColor = tabContentColor,
            modifier = Modifier.shadow(4.dp).fillMaxWidth()
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                modifier = Modifier.background(
                    color = if (selectedTabIndex == 0) tabBackgroundColor else Color.Transparent
                ),
                text = {
                    Text(
                        "Data Anak",
                        style = MaterialTheme.typography.h6.copy(
                            color = if (selectedTabIndex == 0) if (isLight) MinimalTextLight else MinimalTextDark else Ocean9
                        )
                    )
                }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                modifier = Modifier.background(
                    color = if (selectedTabIndex == 1) tabBackgroundColor else Color.Transparent
                ),
                text = {
                    Text(
                        "Diari Harian",
                        style = MaterialTheme.typography.h6.copy(
                            color = if (selectedTabIndex == 1) if (isLight) MinimalTextLight else MinimalTextDark else Ocean9
                        )
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (selectedTabIndex) {
            0 -> ChildDataTabContent(childId = childId, navController = navController)
            1 -> DailyDiaryTabContent()
        }
    }
}

@Composable
fun DailyDiaryTabContent() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Diari Harian", style = MaterialTheme.typography.h5, color = MaterialTheme.colors.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Belum ada catatan diari harian.", style = MaterialTheme.typography.body1, color = MaterialTheme.colors.onSurface)
    }
}
