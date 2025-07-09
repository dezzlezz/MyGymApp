package com.example.mygymapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.mygymapp.ui.theme.MyGymAppTheme
import com.example.mygymapp.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // default to dark theme for this preview activity
            MyGymAppTheme(darkTheme = true) {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    AppNavGraph()
}