@file:OptIn(
    ExperimentalSharedTransitionApi::class
)

package com.callcenter.kidcare.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.callcenter.kidcare.ui.components.KidCareScaffold
import com.callcenter.kidcare.ui.components.KidCareSnackbar
import com.callcenter.kidcare.ui.components.rememberKidCareScaffoldState
import com.callcenter.kidcare.ui.home.DeliveryOptionsPanel
import com.callcenter.kidcare.ui.home.DestinationBar
import com.callcenter.kidcare.ui.home.HomeSections
import com.callcenter.kidcare.ui.home.KidCareBottomBar
import com.callcenter.kidcare.ui.home.addHomeGraph
import com.callcenter.kidcare.ui.home.composableWithCompositionLocal
import com.callcenter.kidcare.ui.navigation.MainDestinations
import com.callcenter.kidcare.ui.navigation.rememberKidCareNavController
import com.callcenter.kidcare.ui.options.AIInteraction
import com.callcenter.kidcare.ui.options.YouTubeHealth
import com.callcenter.kidcare.ui.theme.KidCareTheme
import com.callcenter.kidcare.ui.uionly.VerifyEmailScreen
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.R)
@Preview
@Composable
fun KidCareApp() {
    KidCareTheme {
        val kidCareNavController = rememberKidCareNavController()
        SharedTransitionLayout {
            CompositionLocalProvider(
                LocalSharedTransitionScope provides this
            ) {
                NavHost(
                    navController = kidCareNavController.navController,
                    startDestination = MainDestinations.HOME_ROUTE
                ) {
                    composableWithCompositionLocal(
                        route = MainDestinations.HOME_ROUTE
                    ) { backStackEntry ->
                        MainContainer()
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun MainContainer(
    modifier: Modifier = Modifier
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var isEmailVerified by remember { mutableStateOf(user?.isEmailVerified == true) }
    var showVerifyEmailScreen by remember { mutableStateOf(!isEmailVerified && user != null) }

    LaunchedEffect(user) {
        auth.addAuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            isEmailVerified = currentUser?.isEmailVerified == true
            showVerifyEmailScreen = !isEmailVerified && currentUser != null
        }
    }

    if (showVerifyEmailScreen) {
        VerifyEmailScreen(
            onVerificationCompleted = {
                showVerifyEmailScreen = false
            }
        )
    } else {
        val kidCareScaffoldState = rememberKidCareScaffoldState()
        val navController = rememberKidCareNavController().navController
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        Log.d("NavigationRoute", "Current route: $currentRoute")

        val showBottomBar = HomeSections.entries.any { it.route == currentRoute }

        var showOptionPanel by remember { mutableStateOf(false) }

        KidCareScaffold(
            bottomBar = {
                if (showBottomBar) {
                    KidCareBottomBar(
                        tabs = HomeSections.entries.toTypedArray(),
                        currentRoute = currentRoute ?: HomeSections.FEED.route,
                        navigateToRoute = navController::navigate,
                        modifier = Modifier.navigationBarsPadding()
                    )
                }
            },
            modifier = modifier,
            snackbarHost = {
                SnackbarHost(
                    hostState = it,
                    modifier = Modifier.systemBarsPadding(),
                    snackbar = { snackbarData -> KidCareSnackbar(snackbarData) }
                )
            },
            snackBarHostState = kidCareScaffoldState.snackBarHostState,
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (currentRoute != MainDestinations.YOUTUBE_HEALTH_ROUTE &&
                        currentRoute != MainDestinations.AI_INTERACTION_ROUTE
                    ) {
                        DestinationBar(
                            modifier = Modifier.fillMaxWidth(),
                            navigateTo = { route ->
                                navController.navigate(route)
                            },
                            onOpenOptions = { showOptionPanel = true }
                        )
                    }

                    NavHost(
                        navController = navController,
                        startDestination = HomeSections.FEED.route,
                        modifier = Modifier.weight(1f)
                    ) {
                        addHomeGraph(
                            modifier = Modifier
                                .padding(padding)
                                .consumeWindowInsets(padding)
                        )
                        composable(MainDestinations.YOUTUBE_HEALTH_ROUTE) { backStackEntry ->
                            YouTubeHealth(onClose = { navController.navigateUp() })
                        }
                        composable(MainDestinations.AI_INTERACTION_ROUTE) { backStackEntry ->
                            AIInteraction(onClose = { navController.navigateUp() })
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedVisibility(
                        visible = showOptionPanel,
                        enter = slideInVertically(
                            initialOffsetY = { fullHeight -> fullHeight },
                            animationSpec = tween(durationMillis = 300)
                        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                        exit = slideOutVertically(
                            targetOffsetY = { fullHeight -> fullHeight },
                            animationSpec = tween(durationMillis = 300)
                        ) + fadeOut(animationSpec = tween(durationMillis = 300))
                    ) {
                        DeliveryOptionsPanel(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .wrapContentHeight(),
                            onDismiss = { showOptionPanel = false },
                            onOptionSelected = { option ->
                                when (option) {
                                    1 -> {
                                        navController.navigate(MainDestinations.AI_INTERACTION_ROUTE)
                                        showOptionPanel = false
                                    }
                                    2 -> {
                                        navController.navigate(MainDestinations.YOUTUBE_HEALTH_ROUTE)
                                        showOptionPanel = false
                                    }
                                    3 -> {
                                        // Tindakan untuk Option 3
                                        showOptionPanel = false
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
