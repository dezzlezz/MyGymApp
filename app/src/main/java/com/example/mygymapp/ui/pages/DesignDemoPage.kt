package com.example.mygymapp.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.mygymapp.R
import com.example.mygymapp.ui.components.LinedTextField
import com.example.mygymapp.ui.components.PoeticCard
import com.example.mygymapp.ui.components.PoeticCardStyle
import com.example.mygymapp.ui.components.PoeticDivider
import com.example.mygymapp.ui.components.PoeticTextField
import com.example.mygymapp.ui.theme.AppColors
import com.example.mygymapp.ui.theme.AppPadding
import com.example.mygymapp.ui.theme.AppTypography

/**
 * A showcase page to preview poetic UI components and styling.
 */
@Composable
fun DesignDemoPage() {
    var text by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.background_parchment),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(AppPadding.Screen)
                .verticalScroll(rememberScrollState())
        ) {
            Text("\uD83D\uDCD8 Design Demo", style = AppTypography.Title)

            Spacer(Modifier.height(AppPadding.Section))
            PoeticDivider()

            Spacer(Modifier.height(AppPadding.Section))
            PoeticDivider(centerText = "Poetic Card")

            PoeticCard {
                Text("Normal Card", style = AppTypography.Title)
                Text("Base style", style = AppTypography.Body)
            }

            Spacer(Modifier.height(AppPadding.Element))
            PoeticCard(style = PoeticCardStyle.ESELOHR) {
                Text("Eselsohr", style = AppTypography.Title)
                Text("Dog-ear accent", style = AppTypography.Body)
            }

            Spacer(Modifier.height(AppPadding.Element))
            PoeticCard(style = PoeticCardStyle.PR_MARKED) {
                Text("PR Marked", style = AppTypography.Title)
                Text("Inkblot accent", style = AppTypography.Body)
            }

            Spacer(Modifier.height(AppPadding.Section))
            PoeticDivider(centerText = "Poetic Text Field")

            PoeticTextField(
                value = text,
                onValueChange = { text = it },
                hint = "What moved through you today?",
            )

            Spacer(Modifier.height(AppPadding.Section))
            PoeticDivider(centerText = "Lined Text Field")

            LinedTextField(
                value = notes,
                onValueChange = { notes = it },
                hint = "Write here like in a lined notebook...",
            )

            Spacer(Modifier.height(AppPadding.Section))
            PoeticDivider(centerText = "Buttons & Style")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.ButtonGreen
                    )
                ) {
                    Text("Confirm", style = AppTypography.Button)
                }

                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.ErrorMaroon
                    )
                ) {
                    Text("Cancel", style = AppTypography.Button)
                }
            }

            Spacer(Modifier.height(AppPadding.Section * 2))
        }
    }
}

