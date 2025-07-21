package com.example.mygymapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.mygymapp.navigation.RadialMenu
import com.example.mygymapp.navigation.AppNavHost
import com.example.mygymapp.ui.theme.MyGymAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MyGymAppTheme {
                Scaffold { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        AppNavHost(navController = navController)
                        RadialMenu(navController)
                    }
                }
            }
        }
    }
}

