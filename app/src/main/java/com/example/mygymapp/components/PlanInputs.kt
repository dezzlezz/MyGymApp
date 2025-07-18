package com.example.mygymapp.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mygymapp.R
import com.example.mygymapp.model.Equipment

@Composable
fun PlanInputField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes labelRes: Int,
    modifier: Modifier = Modifier,
    vararg formatArgs: Any
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(id = labelRes, *formatArgs)) },
        modifier = modifier
    )
}

@Composable
fun DurationSlider(duration: Int, onDurationChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    Text(stringResource(id = R.string.duration_label, duration))
    Slider(
        value = duration.toFloat(),
        onValueChange = { onDurationChange(it.toInt()) },
        valueRange = 10f..60f,
        modifier = modifier
    )
}
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun EquipmentChipsRow(selected: List<String>, onToggle: (String) -> Unit, modifier: Modifier = Modifier) {
    FlowRow(modifier = modifier) {
        Equipment.options.forEach { eq ->
            FilterChip(
                selected = eq in selected,
                onClick = { onToggle(eq) },
                label = { Text(eq) }
            )
            Spacer(Modifier.width(4.dp))
        }
    }
}
