package com.example.mygymapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mygymapp.R
import androidx.compose.ui.Alignment


enum class PoeticCardStyle {
    NORMAL, PR_MARKED, ESELOHR
}

/**
 * A refined poetic card with visual variations and textured background.
 */
@Composable
fun PoeticCard(
    modifier: Modifier = Modifier,
    style: PoeticCardStyle = PoeticCardStyle.NORMAL,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5DC)) // fallback color
        ) {
            // Hintergrundtextur
            Image(
                painter = painterResource(R.drawable.parchment),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize(),
                contentScale = ContentScale.Crop
            )

            // Optionales Eselsohr
            if (style == PoeticCardStyle.ESELOHR) {
                Image(
                    painter = painterResource(R.drawable.corner_fold),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = 4.dp)
                )
            }

            // Optionaler Tintenfleck (PR z. B.)
            if (style == PoeticCardStyle.PR_MARKED) {
                Image(
                    painter = painterResource(R.drawable.inkblot),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .offset(x = (-12).dp, y = (-12).dp)
                        .align(Alignment.BottomStart)
                )
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                content()
            }
        }
    }
}
