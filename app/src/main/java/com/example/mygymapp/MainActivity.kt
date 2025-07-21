package com.example.mygymapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.example.mygymapp.navigation.BottomNavBar
import com.example.mygymapp.navigation.AppNavHost
import com.example.mygymapp.ui.theme.MyGymAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MyGymAppTheme {
                Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
                    AppNavHost(navController = navController, modifier = Modifier.padding(padding))
                }
            }
        }
    }
}

