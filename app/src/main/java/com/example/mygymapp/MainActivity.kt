package com.example.mygymapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mygymapp.ui.theme.MyGymAppTheme
import com.example.mygymapp.ui.screens.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyGymAppTheme {
                MainScreen()
            }
        }
    }
}

