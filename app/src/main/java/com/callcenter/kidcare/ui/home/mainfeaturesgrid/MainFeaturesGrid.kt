package com.callcenter.kidcare.ui.home.mainfeaturesgrid

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.callcenter.kidcare.R
import com.callcenter.kidcare.ui.components.KidCareCard
import com.callcenter.kidcare.ui.theme.MinimalTextDark
import com.callcenter.kidcare.ui.theme.MinimalTextLight

@Composable
fun MainFeaturesGrid(navController: NavController) {
    @Suppress("DEPRECATION") val buttons: List<Pair<Int, ImageVector>> = listOf(
        R.string.predict to Icons.Default.ShowChart,
        R.string.ibu_hamil to Icons.Default.FamilyRestroom,
        R.string.pencegahan_stunting to Icons.Default.HealthAndSafety,
        R.string.penanganan_stunting to Icons.Default.MedicalServices,
        R.string.info_produk to Icons.Default.Info,
        R.string.resep_mpasi to Icons.Default.FoodBank
    )

    val textColor = if (isSystemInDarkTheme()) MinimalTextDark else MinimalTextLight

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(buttons) { (textRes, icon) ->
            KidCareCard(
                modifier = Modifier
                    .width(150.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        when (textRes) {
                            R.string.predict -> navController.navigate("predict")
                            R.string.ibu_hamil -> navController.navigate("ibu_hamil")
                            R.string.pencegahan_stunting -> navController.navigate("pencegahan_stunting")
                            R.string.penanganan_stunting -> navController.navigate("penanganan_stunting")
                            R.string.info_produk -> navController.navigate("info_produk")
                            R.string.resep_mpasi -> navController.navigate("resep_mpasi")
                        }
                    },
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    Icon(
                        icon,
                        contentDescription = stringResource(id = textRes),
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xff00a1c7)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(id = textRes),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = textColor,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}
