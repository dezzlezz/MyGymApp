package com.example.mygymapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.*
import androidx.compose.runtime.remember
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.example.mygymapp.ui.theme.*
import androidx.navigation.NavController

private enum class HomeSection { WORKOUT, PLAN, PROGRESS, PROFILE }

@Composable
fun HomePager(navController: NavController) {
    val pages = HomeSection.entries
    val pagerState = rememberPagerState()
    Box(Modifier.fillMaxSize()) {
        ForestBackground(pagerState, pages.size)
        HorizontalPager(count = pages.size, state = pagerState, modifier = Modifier.fillMaxSize()) { index ->
            val page = pages[index]
            Box(Modifier.fillMaxSize()) {
                when (page) {
                    HomeSection.WORKOUT -> {
                        WorkoutScreen()
                        LeavesOverlay()
                    }
                    HomeSection.PLAN -> {
                        PlansScreen(navController)
                        FogOverlay()
                    }
                    HomeSection.PROGRESS -> {
                        ProgressScreen()
                        RiverOverlay(pagerState, pages.size)
                    }
                    HomeSection.PROFILE -> {
                        ProfileScreen(navController)
                        SunRaysOverlay()
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { navController.navigate("exercises") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.FitnessCenter, contentDescription = null)
        }
    }
}

@Composable
private fun ForestBackground(state: PagerState, pageCount: Int) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val width = maxWidth
        val widthPx = with(LocalDensity.current) { width.toPx() }
        val pageOffset = state.currentPage + state.currentPageOffset
        val farOffset = -pageOffset * widthPx * 0.3f
        val midOffset = -pageOffset * widthPx * 0.6f
        val nearOffset = -pageOffset * widthPx

        androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
            val totalWidth = size.width * pageCount.toFloat()
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF233329), Color(0xFF0E1C14))
                ),
                size = size
            )

            val farPath = Path().apply {
                moveTo(0f, size.height * 0.55f)
                lineTo(totalWidth, size.height * 0.5f)
                lineTo(totalWidth, size.height)
                lineTo(0f, size.height)
                close()
            }
            withTransform({ translate(left = farOffset) }) {
                drawPath(farPath, color = Color(0xFF1A2A1A))
            }

            val midPath = Path().apply {
                moveTo(0f, size.height * 0.7f)
                cubicTo(totalWidth * 0.25f, size.height * 0.65f, totalWidth * 0.75f, size.height * 0.75f, totalWidth, size.height * 0.7f)
                lineTo(totalWidth, size.height)
                lineTo(0f, size.height)
                close()
            }
            withTransform({ translate(left = midOffset) }) {
                drawPath(midPath, color = PineGreen)
            }

            val nearPath = Path().apply {
                moveTo(0f, size.height * 0.8f)
                cubicTo(totalWidth * 0.3f, size.height * 0.83f, totalWidth * 0.7f, size.height * 0.77f, totalWidth, size.height * 0.8f)
                lineTo(totalWidth, size.height)
                lineTo(0f, size.height)
                close()
            }
            withTransform({ translate(left = nearOffset) }) {
                drawPath(nearPath, color = MossGreen)
            }
        }
    }
}

@Composable
private fun RiverOverlay(state: PagerState, pageCount: Int) {
    androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val totalW = w * pageCount
        // Compute horizontal translation from current page and offset
        val offset = -(state.currentPage + state.currentPageOffset) * w
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(offset, h * 0.7f)
            var x = 0f
            while (x <= totalW) {
                val y = h * (0.7f + 0.05f * sin((x / totalW) * pageCount * PI).toFloat())
                lineTo(offset + x, y)
                x += w / 20f
            }
        }
        val alphaTransition = rememberInfiniteTransition(label = "river")
        val alpha by alphaTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                tween(durationMillis = 1500, easing = LinearEasing),
                RepeatMode.Reverse
            ),
            label = "alpha"
        )
        drawPath(path, color = SeaFoam.copy(alpha = alpha), style = Stroke(width = h * 0.05f))
    }
}

@Composable
private fun FogOverlay() {
    androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
        val transition = rememberInfiniteTransition(label = "fog")
        val offset by transition.animateFloat(
            initialValue = 0f,
            targetValue = size.width,
            animationSpec = infiniteRepeatable(
                tween(durationMillis = 20000, easing = LinearEasing),
                RepeatMode.Restart
            ),
            label = "offset"
        )

        val gradient = Brush.horizontalGradient(
            colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.2f), Color.Transparent)
        )

        withTransform({ translate(left = -size.width + offset) }) {
            drawRect(gradient, size = size)
        }
        withTransform({ translate(left = offset) }) {
            drawRect(gradient, size = size)
        }
    }
}

@Composable
private fun SunRaysOverlay() {
    androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
        val transition = rememberInfiniteTransition(label = "sun")
        val radius by transition.animateFloat(
            initialValue = size.height * 0.3f,
            targetValue = size.height * 0.35f,
            animationSpec = infiniteRepeatable(
                tween(durationMillis = 3000, easing = LinearEasing),
                RepeatMode.Reverse
            ),
            label = "radius"
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x66FFFACD), Color.Transparent),
                center = Offset(size.width / 2f, 0f),
                radius = radius
            ),
            size = size
        )
    }
}

@Composable
private fun LeavesOverlay() {
    val leaves = remember { List(10) { Random.nextFloat() to Random.nextFloat() } }
    androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
        val transition = rememberInfiniteTransition(label = "leaves")
        val offset by transition.animateFloat(
            initialValue = 0f,
            targetValue = -50f,
            animationSpec = infiniteRepeatable(
                tween(durationMillis = 8000, easing = LinearEasing),
                RepeatMode.Restart
            ),
            label = "offset"
        )
        leaves.forEachIndexed { idx, pair ->
            val x = size.width * pair.first
            val baseY = size.height * 0.3f + pair.second * 60f
            drawCircle(
                color = Color(0x5580C080),
                center = Offset(x, baseY + offset + idx * 10f),
                radius = 6f
            )
        }
    }
}
