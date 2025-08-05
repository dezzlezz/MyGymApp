package com.example.mygymapp.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.mygymapp.R

object AppColors {
    val Paper = Color(0xFFF3E5AB)           // warmes Papier
    val DeepText = Color(0xFF2E2E2E)        // fast schwarz, wie Tinte
    val SubtleText = Color(0xFF4B3B2F)      // ruhiger, bräunlicher Text
    val ButtonGreen = Color(0xFF3B5F47)     // moosgrüner Button
    val ErrorMaroon = Color(0xFF8B0000)     // dunkles Blutrot
    val SectionLine = Color(0xFFAA9870)     // feine Bleistiftlinie
    val SelectedChip = Color(0xFFD5C5AA)    // ruhiger Beige-Ton für Auswahl
}

object AppTypography {
    val GaeguRegular = FontFamily(Font(R.font.gaegu_regular))
    val GaeguBold = FontFamily(Font(R.font.gaegu_bold))
    val GaeguLight = FontFamily(Font(R.font.gaegu_light))

    val Title = TextStyle(
        fontFamily = GaeguBold,
        fontSize = 24.sp,
        color = AppColors.DeepText
    )

    val Body = TextStyle(
        fontFamily = GaeguRegular,
        fontSize = 16.sp,
        color = AppColors.DeepText
    )

    val Hint = TextStyle(
        fontFamily = GaeguLight,
        fontSize = 14.sp,
        color = AppColors.SubtleText
    )

    val Button = TextStyle(
        fontFamily = GaeguBold,
        fontSize = 16.sp,
        color = Color.White
    )
}

object AppShapes {
    val Card = RoundedCornerShape(12.dp)
    val Button = RoundedCornerShape(8.dp)
    val Small = RoundedCornerShape(4.dp)
}

object AppPadding {
    val Screen = 24.dp
    val Section = 16.dp
    val Element = 12.dp
    val Small = 8.dp
}

