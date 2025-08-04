package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.mygymapp.store.JournalStore
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.mygymapp.R

@Composable
fun EntryNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val entryNumber = JournalStore.currentEntry.value?.id?.toInt() ?: 1

    NavHost(
        navController = navController,
        startDestination = "entry",
        modifier = modifier
    ) {
        composable("entry") {
            EntryPage(
                entryNumber = entryNumber,
                onFinished = {
                    JournalStore.currentEntry.value = null
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
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.background_parchment),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
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
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = "Back",
                    color = Color.White,
                    fontFamily = FontFamily.Serif
                )
            }
        }
    }
}

