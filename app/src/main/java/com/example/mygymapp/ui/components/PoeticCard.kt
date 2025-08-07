package com.example.mygymapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mygymapp.R
import com.example.mygymapp.ui.theme.AppPadding
import com.example.mygymapp.ui.theme.AppShapes
import com.example.mygymapp.ui.theme.AppColors
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

enum class PoeticCardStyle {
    NORMAL, PR_MARKED, ESELOHR
}

/**
 * A poetic card with leaf-textured background and optional top-right cut-out.
 */
@Composable
fun PoeticCard(
    modifier: Modifier = Modifier,
    style: PoeticCardStyle = PoeticCardStyle.NORMAL,
    cutSize: Dp = 80.dp,
    elevation: Dp = 2.dp,
    withTexture: Boolean = true,
    tintOverlayAlpha: Float = 0.16f, // Heller Schleier über Textur
    content: @Composable ColumnScope.() -> Unit
) {
    val shape: Shape = if (style == PoeticCardStyle.ESELOHR) {
        cutCornerShape(cutSize)
    } else {
        AppShapes.Card
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppPadding.Small, vertical = 4.dp)
            .shadow(elevation, shape)
            .clip(shape)
    ) {
        if (withTexture) {
            Image(
                painter = painterResource(R.drawable.leaf_texture),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White.copy(alpha = tintOverlayAlpha)) // Soft Overlay
            )
        }

        if (style == PoeticCardStyle.PR_MARKED) {
            Image(
                painter = painterResource(R.drawable.inkblot),
                contentDescription = "PR Mark",
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 8.dp, y = 8.dp)
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


/**
 * Creates a card shape with a cut-out top-right corner.
 */
fun cutCornerShape(cutSize: Dp): Shape {
    val cutPx = cutSize.value
    return GenericShape { size, _ ->
        moveTo(0f, 0f)
        lineTo(size.width - cutPx, 0f)
        lineTo(size.width, cutPx)
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }
}
