package com.example.mygymapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.model.Paragraph
import com.example.mygymapp.ui.pages.GaeguBold
import com.example.mygymapp.ui.pages.GaeguRegular
import androidx.compose.ui.res.stringResource
import com.example.mygymapp.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.CalendarToday

@Composable
fun ParagraphCard(
    paragraph: Paragraph,
    onEdit: () -> Unit,
    onPlan: () -> Unit,
    onSaveTemplate: () -> Unit,
    modifier: Modifier = Modifier
) {
    PoeticCard(modifier = modifier) {
        val headerColor = Color.Black
        val secondaryColor = Color(0xFF555D50)
        Text(
            text = paragraph.title,
            style = MaterialTheme.typography.headlineSmall.copy(fontFamily = GaeguBold, fontSize = 26.sp, color = headerColor)
        )
        if (paragraph.note.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = paragraph.note,
                style = TextStyle(fontFamily = GaeguRegular, fontSize = 14.sp, fontStyle = FontStyle.Italic, color = secondaryColor)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        paragraph.lineTitles.forEachIndexed { index, title ->
            if (index < days.size) {
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text(days[index], fontFamily = GaeguBold, color = headerColor)
                    Spacer(Modifier.width(4.dp))
                    Text(title, style = TextStyle(fontFamily = GaeguRegular, color = headerColor))
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TextButton(
                onClick = onEdit,
                colors = ButtonDefaults.textButtonColors(containerColor = Color.Transparent, contentColor = headerColor)
            ) {
                Icon(Icons.Filled.Edit, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.edit_label), style = TextStyle(fontFamily = GaeguRegular, fontSize = 14.sp))
            }
            TextButton(
                onClick = onPlan,
                colors = ButtonDefaults.textButtonColors(containerColor = Color.Transparent, contentColor = headerColor)
            ) {
                Icon(Icons.Filled.CalendarToday, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.plan_label), style = TextStyle(fontFamily = GaeguRegular, fontSize = 14.sp))
            }
            TextButton(
                onClick = onSaveTemplate,
                colors = ButtonDefaults.textButtonColors(containerColor = Color.Transparent, contentColor = headerColor)
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.save_template_label), style = TextStyle(fontFamily = GaeguRegular, fontSize = 14.sp))
            }
        }
    }
}

