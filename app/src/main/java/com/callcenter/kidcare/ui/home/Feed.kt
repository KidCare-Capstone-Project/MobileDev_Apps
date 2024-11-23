@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.callcenter.kidcare.ui.home

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.callcenter.kidcare.ui.components.KidCareSurface
import com.callcenter.kidcare.ui.home.article.ArticleRecommendation
import com.callcenter.kidcare.ui.home.childprofile.ChildProfileSection
import com.callcenter.kidcare.ui.home.imagecarousel.ImageCarousel
import com.callcenter.kidcare.ui.home.mainfeaturesgrid.MainFeaturesGrid
import com.callcenter.kidcare.ui.home.welcome.WelcomeDialog
import com.callcenter.kidcare.ui.theme.KidCareTheme
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.R)

@Composable
fun Feed(
    modifier: Modifier = Modifier,
    navController: NavController // Add navController here
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var showDialog by remember { mutableStateOf(sharedPreferences.getBoolean("show_welcome_dialog", true)) }

    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val userName = firebaseUser?.displayName ?: "User"

    if (showDialog) {
        WelcomeDialog(userName = userName, onDismiss = {
            showDialog = false
            sharedPreferences.edit().putBoolean("show_welcome_dialog", false).apply()
        })
    }

    FeedContent(modifier, navController)
}

@Composable
fun FeedContent(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    KidCareSurface(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                ChildProfileSection(navController)
            }
            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    MainFeaturesGrid(navController)
                }
            }
            item {
                ImageCarousel()
            }
            item {
                ArticleRecommendation(
                    onArticleClick = { uuid ->
                    navController.navigate("articleDetail/$uuid")
                },
                    onSeeMoreClick = {
                        navController.navigate("articleList")
                    })
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun HomePreview() {
    KidCareTheme {
        val navController = rememberNavController()
        Feed(navController = navController)
    }
}