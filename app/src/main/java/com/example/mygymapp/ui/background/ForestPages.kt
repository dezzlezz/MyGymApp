package com.example.mygymapp.ui.background

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.random.Random

/**
 * Draws one page of a stylized forest.
 */
fun DrawScope.drawForestPage(
    page: Int,
    pageWidth: Float,
    height: Float,
    dense: Boolean,
    withFireflies: Boolean
) {
    val startX = pageWidth * page

    // Draw rolling ground as a simple wave
    val ground = Path().apply {
        moveTo(startX, height * 0.9f)
        quadraticBezierTo(
            startX + pageWidth * 0.25f,
            height * 0.85f,
            startX + pageWidth * 0.5f,
            height * 0.9f
        )
        quadraticBezierTo(
            startX + pageWidth * 0.75f,
            height * 0.95f,
            startX + pageWidth,
            height * 0.9f
        )
        lineTo(startX + pageWidth, height)
        lineTo(startX, height)
        close()
    }
    drawPath(ground, if (dense) Color(0xFF4B6E4D) else Color(0xFF88A97A))

    // Draw simple tree rectangles
    val treeColor = Color(0xFF2E4A32)
    val treeCount = if (dense) 7 else 4
    val treeWidth = pageWidth / (treeCount * 3f)
    repeat(treeCount) { index ->
        val frac = (index + 1) / (treeCount + 1f)
        val x = startX + pageWidth * frac - treeWidth / 2f
        drawRect(
            color = treeColor,
            topLeft = Offset(x, height * 0.9f - height * 0.15f),
            size = Size(treeWidth, height * 0.15f)
        )
    }

    if (withFireflies) {
        repeat(10) {
            val x = startX + Random.nextFloat() * pageWidth
            val y = height * (0.3f + Random.nextFloat() * 0.3f)
            drawCircle(
                color = Color.Yellow.copy(alpha = 0.8f),
                center = Offset(x, y),
                radius = pageWidth * 0.01f
            )
        }
    }
}


/**
 * Draws a horizontally flowing river segment for the given page.
 */
fun DrawScope.drawRiverPage(
    page: Int,
    pageWidth: Float,
    height: Float
) {
    val startX = pageWidth * page
    val startY = height * 0.7f

    val path = Path().apply {
        moveTo(startX, startY)
        // gentle wave across the page
        val midX = startX + pageWidth / 2f
        quadraticBezierTo(
            startX + pageWidth * 0.25f,
            startY - height * 0.05f,
            midX,
            startY
        )
        quadraticBezierTo(
            startX + pageWidth * 0.75f,
            startY + height * 0.05f,
            startX + pageWidth,
            startY
        )
        lineTo(startX + pageWidth, startY + height * 0.1f)
        quadraticBezierTo(
            startX + pageWidth * 0.75f,
            startY + height * 0.15f,
            midX,
            startY + height * 0.1f
        )
        quadraticBezierTo(
            startX + pageWidth * 0.25f,
            startY + height * 0.05f,
            startX,
            startY + height * 0.1f
        )
        close()
    }
    drawPath(
        path = path,
        color = Color(0xFF96D1FF).copy(alpha = 0.9f)
    )
}
