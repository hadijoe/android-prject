package com.hadijoe.projectandroid.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hadijoe.projectandroid.ui.setlists.SetlistDetailScreen
import com.hadijoe.projectandroid.ui.setlists.SetlistsScreen
import com.hadijoe.projectandroid.ui.songs.SongsScreen

// The route names live in one place, so a typo cannot send us to a wrong screen.
object Routes {
    const val SONGS = "songs"
    const val SETLISTS = "setlists"
    const val SETLIST_ID = "setlistId"
    // {setlistId} is a placeholder: the real id is put in when we navigate.
    const val SETLIST_DETAIL = "setlist/{$SETLIST_ID}"
    fun setlistDetail(setlistId: Long) = "setlist/$setlistId"
}

@Composable
fun AppNavigation() {
    // The NavController remembers the back stack (which screens are open).
    val navController = rememberNavController()
    // Observe the current screen, so the tabs can highlight the right one.
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            // The detail screen has its own Back button, so the tabs are hidden there.
            if (currentRoute != Routes.SETLIST_DETAIL) {
                MainTabs(
                    currentRoute = currentRoute,
                    onTabClick = { route ->
                        navController.navigate(route) {
                            // Go back to the start screen first, so tabs do not pile up on the back stack.
                            popUpTo(Routes.SONGS)
                            // Do not open a second copy of the screen we are already on.
                            launchSingleTop = true
                        }
                    }
                )
            }
            NavHost(
                navController = navController,
                startDestination = Routes.SONGS,
                modifier = Modifier.weight(1f)
            ) {
                composable(Routes.SONGS) { SongsScreen() }
                composable(Routes.SETLISTS) {
                    SetlistsScreen(
                        // The screen does not know the NavController. It only reports the click.
                        onSetlistClick = { setlistId -> navController.navigate(Routes.setlistDetail(setlistId)) }
                    )
                }
                composable(
                    route = Routes.SETLIST_DETAIL,
                    // The id arrives as text inside the route. This tells Navigation to read it as a Long.
                    arguments = listOf(navArgument(Routes.SETLIST_ID) { type = NavType.LongType })
                ) { entry ->
                    val setlistId = entry.arguments?.getLong(Routes.SETLIST_ID) ?: return@composable
                    SetlistDetailScreen(setlistId = setlistId, onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

@Composable
private fun MainTabs(currentRoute: String?, onTabClick: (String) -> Unit) {
    // 0 = Songs tab, 1 = Setlists tab.
    val selectedIndex = if (currentRoute == Routes.SETLISTS) 1 else 0
    PrimaryTabRow(selectedTabIndex = selectedIndex) {
        Tab(selected = selectedIndex == 0, onClick = { onTabClick(Routes.SONGS) }, text = { Text("Songs") })
        Tab(selected = selectedIndex == 1, onClick = { onTabClick(Routes.SETLISTS) }, text = { Text("Setlists") })
    }
}
