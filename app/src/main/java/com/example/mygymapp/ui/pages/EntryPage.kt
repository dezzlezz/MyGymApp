@file:OptIn(ExperimentalLayoutApi::class)
package com.example.mygymapp.ui.pages
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mygymapp.ui.components.EntryHeader
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.mygymapp.R
import com.example.mygymapp.ui.theme.handwritingText
import java.time.LocalDate
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Box
import com.example.mygymapp.ui.components.MoodChip
import com.example.mygymapp.store.JournalStore


@OptIn(ExperimentalLayoutApi::class)
@Composable
    fun EntryPage(
        entryNumber: Int,
        onFinished: () -> Unit
    ) {
        val today = LocalDate.now()

        var story by remember { mutableStateOf("") }

            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(R.drawable.hintergrundentry),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EntryHeader(
                        entryNumber = entryNumber,
                        date = today
                    )
                    JournalStore.currentMood.value?.let {
                        MoodChip(mood = it)
                    }

                        Text(
                            text = "Today: Push · 3 movements · 34 minutes",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray),
                            textAlign = TextAlign.Center
                        )

                        OutlinedTextField(
                            value = story,
                            onValueChange = { story = it },
                            placeholder = {
                                Text("What was quiet, what loud? What moved through you?")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            textStyle = handwritingText
                        )

                        Button(
                            onClick = {
                                story = ""
                                onFinished()
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Finish this page", color = Color.Black)
                        }
                    }
                }
            }