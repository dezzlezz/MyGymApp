package com.example.mygymapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

// Use only Gaegu fonts defined in DesignSystem
val handwritingText = TextStyle(
    fontFamily = AppTypography.GaeguRegular,
    fontSize = 16.sp,
    lineHeight = 24.sp
)

val MyGymTypography = Typography(
    headlineSmall = AppTypography.Title,
    titleMedium = AppTypography.Title.copy(fontSize = 20.sp),
    bodyMedium = AppTypography.Body,
    bodySmall = AppTypography.Hint,
    labelLarge = AppTypography.Button
)
