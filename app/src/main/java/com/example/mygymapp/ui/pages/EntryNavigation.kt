package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun EntryNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var entryNumber by remember { mutableStateOf(1) }

    NavHost(
        navController = navController,
        startDestination = "entry",
        modifier = modifier
    ) {
        composable("entry") {
            EntryPage(
                entryNumber = entryNumber,
                onFinished = {
                    entryNumber += 1
                    navController.navigate("done")
                }
            )
        }
        composable("done") {
            ConfirmationPage(onBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun ConfirmationPage(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        Text(
            text = "A new page was written.",
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = FontFamily.Serif,
                color = Color.Black
            )
        )
        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            Text(
                text = "Back",
                color = Color.Black,
                fontFamily = FontFamily.Serif
            )
        }
    }
}

