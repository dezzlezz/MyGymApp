package com.example.mygymapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mygymapp.ui.theme.AppColors
import com.example.mygymapp.ui.theme.AppPadding
import com.example.mygymapp.ui.theme.AppTypography
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun PoeticDivider(
    modifier: Modifier = Modifier,
    paddingStart: Dp = AppPadding.Screen,
    paddingEnd: Dp = AppPadding.Screen,
    centerText: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = paddingStart,
                end = paddingEnd,
                top = AppPadding.Element,
                bottom = AppPadding.Element
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            color = AppColors.SectionLine,
            thickness = 1.dp,
            modifier = Modifier.weight(1f)
        )

        if (centerText != null) {
            Spacer(Modifier.width(AppPadding.Small))
            Text(
                centerText,
                fontFamily = AppTypography.GaeguBold,
                fontSize = 18.sp,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(AppPadding.Small))
            Divider(
                color = AppColors.SectionLine,
                thickness = 1.dp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

