package com.example.mygymapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.model.Paragraph
import com.example.mygymapp.ui.pages.GaeguBold
import com.example.mygymapp.ui.pages.GaeguRegular
import androidx.compose.ui.res.stringResource
import com.example.mygymapp.R

@Composable
fun ParagraphCard(
    paragraph: Paragraph,
    onEdit: () -> Unit,
    onPlan: () -> Unit,
    onSaveTemplate: () -> Unit,
    modifier: Modifier = Modifier
) {
    PoeticCard(modifier = modifier.padding(vertical = 6.dp)) {
        val textColor = Color(0xFF5D4037)
        val buttonBackground = Color(0xFFFFF8E1)
        Text(
            text = paragraph.title,
            style = MaterialTheme.typography.headlineSmall.copy(fontFamily = GaeguBold, fontSize = 24.sp, color = textColor)
        )
        Spacer(modifier = Modifier.height(8.dp))
        paragraph.lineTitles.forEachIndexed { index, title ->
            Text(
                text = "Day ${index + 1}: $title",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = GaeguRegular, color = textColor),
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TextButton(
                onClick = onEdit,
                colors = ButtonDefaults.textButtonColors(containerColor = buttonBackground, contentColor = textColor)
            ) {
                Text(stringResource(R.string.edit_label), style = TextStyle(fontFamily = GaeguRegular, fontSize = 14.sp))
            }
            TextButton(
                onClick = onPlan,
                colors = ButtonDefaults.textButtonColors(containerColor = buttonBackground, contentColor = textColor)
            ) {
                Text(stringResource(R.string.plan_label), style = TextStyle(fontFamily = GaeguRegular, fontSize = 14.sp))
            }
            TextButton(
                onClick = onSaveTemplate,
                colors = ButtonDefaults.textButtonColors(containerColor = buttonBackground, contentColor = textColor)
            ) {
                Text(stringResource(R.string.save_template_label), style = TextStyle(fontFamily = GaeguRegular, fontSize = 14.sp))
            }
        }
    }
}

