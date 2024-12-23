package com.callcenter.kidcare.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

object MainDestinations {
    const val HOME_ROUTE = "home"
    const val YOUTUBE_HEALTH_ROUTE = "youtubehealth"
    const val AI_INTERACTION_ROUTE = "aiinteraction"
    const val ADD_CHILD_PROFILE_ROUTE = "add_child_profile"
}

@Composable
fun rememberKidCareNavController(
    navController: NavHostController = rememberNavController()
): KidCareNavController = remember(navController) {
    KidCareNavController(navController)
}

/**
 * Responsible for holding UI Navigation logic.
 */

@Stable
class KidCareNavController(
    val navController: NavHostController,
) {

    // ----------------------------------------------------------
    // Navigation state source of truth
    // ----------------------------------------------------------

    fun navigateToBottomBarRoute(route: String) {
        if (route != navController.currentDestination?.route) {
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
                // Pop up backstack to the first destination and save state. This makes going back
                // to the start destination when pressing back in any other bottom tab.
                popUpTo(findStartDestination(navController.graph).id) {
                    saveState = true
                }
            }
        }
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

/**
 * Copied from similar function in NavigationUI.kt
 *
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:navigation/navigation-ui/src/main/java/androidx/navigation/ui/NavigationUI.kt
 */

private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}
