package com.example.mygymapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mygymapp.R
import com.example.mygymapp.ui.theme.AppPadding
import com.example.mygymapp.ui.theme.AppShapes
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import com.example.mygymapp.ui.theme.AppColors


enum class PoeticCardStyle {
    NORMAL, PR_MARKED, ESELOHR
}

/**
 * A refined poetic card with visual variations and a leaf-like textured background.
 */
@Composable
fun PoeticCard(
    modifier: Modifier = Modifier,
    style: PoeticCardStyle = PoeticCardStyle.NORMAL,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppPadding.Small, vertical = 4.dp)
            .shadow(2.dp, AppShapes.Card)
            .clip(AppShapes.Card)
    ) {
        // Leaf-textured background
        Image(
            painter = painterResource(R.drawable.leaf_texture),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop // oder FillBounds
        )

        // Optional: dog-ear corner
        if (style == PoeticCardStyle.ESELOHR) {
            Canvas(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = 4.dp)
            ) {
                val path = Path().apply {
                    moveTo(size.width, 0f)
                    lineTo(size.width, size.height)
                    lineTo(0f, 0f)
                    close()
                }

                // Ziehe die Ecke mit transparenter Farbe = „ausgeschnitten“
                drawPath(
                    path = path,
                    color = AppColors.Paper.copy(alpha = 0.0f) // komplett transparent
                )

                // Leichter Schatten an der Kante, um Tiefe zu geben
                drawLine(
                    color = AppColors.DeepText.copy(alpha = 0.1f),
                    start = Offset(size.width, 0f),
                    end = Offset(0f, 0f),
                    strokeWidth = 1.2f
                )
            }

        }

            // Optional: ink blot
            if (style == PoeticCardStyle.PR_MARKED) {
                Image(
                    painter = painterResource(R.drawable.inkblot),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = (-12).dp, y = (-12).dp)
                        .alpha(0.8f)
                )
            }

            Column(
                modifier = Modifier
                    .padding(AppPadding.Element)
            ) {
                content()
            }
        }
    }

