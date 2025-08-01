package com.example.mygymapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.mygymapp.ui.theme.MyGymAppTheme
import com.example.mygymapp.ui.pages.PageScaffold

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MyGymAppTheme {
                PageScaffold()
            }
        }
    }
}

