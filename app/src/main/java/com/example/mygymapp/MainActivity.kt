package com.example.mygymapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.Scaffold
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import com.example.mygymapp.navigation.RadialItem
import com.example.mygymapp.navigation.RadialMenu
import com.example.mygymapp.navigation.AppNavHost
import com.example.mygymapp.ui.theme.MyGymAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MyGymAppTheme {
                val items = listOf(
                    RadialItem(Icons.Outlined.PlayArrow, stringResource(R.string.nav_start), "workout"),
                    RadialItem(Icons.Outlined.ShowChart, stringResource(R.string.nav_history), "progress"),
                    RadialItem(Icons.Outlined.FitnessCenter, stringResource(R.string.nav_library), "exercises"),
                    RadialItem(Icons.Outlined.Person, stringResource(R.string.nav_profile), "profile")
                )

                Scaffold { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        AppNavHost(navController = navController)
                        RadialMenu(navController, items)
                    }
                }
            }
        }
    }
}

