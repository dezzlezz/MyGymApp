package com.example.mygymapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mygymapp.R

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: Int) {
    object Home : BottomNavItem("main", Icons.Outlined.Home, R.string.home)
    object Exercises : BottomNavItem("exercises", Icons.Outlined.FitnessCenter, R.string.exercises)
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(BottomNavItem.Home, BottomNavItem.Exercises)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) { launchSingleTop = true } },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(stringResource(id = item.label)) }
            )
        }
    }
}
