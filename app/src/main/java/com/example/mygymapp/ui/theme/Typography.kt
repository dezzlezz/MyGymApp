package com.example.mygymapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
val Lora = FontFamily.Serif

val WorkSans = FontFamily.SansSerif

val Handwriting = FontFamily.Cursive

val handwritingText = TextStyle(
    fontFamily = Handwriting,
    fontSize = 16.sp,
    lineHeight = 24.sp
)

val MyGymTypography = Typography(
    headlineSmall = TextStyle(
        fontFamily = Lora,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 30.sp
    ),
    labelLarge = TextStyle(
        fontFamily = WorkSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 1.1.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = WorkSans,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodySmall = TextStyle(
        fontFamily = WorkSans,
        fontSize = 13.sp,
        color = Color.Gray
    )
)
