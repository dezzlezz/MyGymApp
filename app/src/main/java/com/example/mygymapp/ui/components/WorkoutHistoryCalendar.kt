package com.example.mygymapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import com.example.mygymapp.data.WorkoutHistoryEntry

@Composable
fun WorkoutHistoryCalendar(
    currentDate: LocalDate,
    workoutHistory: Map<LocalDate, WorkoutHistoryEntry>,
    onDateClick: (LocalDate) -> Unit
) {
    val startMonth = YearMonth.from(workoutHistory.keys.minOrNull() ?: currentDate)
    val endMonth = YearMonth.from(currentDate)
    val months = generateSequence(startMonth) { it.plusMonths(1) }
        .takeWhile { !it.isAfter(endMonth) }
        .toList()

    Column(modifier = Modifier.fillMaxWidth()) {
        months.forEach { month ->
            MonthView(month, workoutHistory, currentDate, onDateClick)
        }
    }
}

@Composable
private fun MonthView(
    month: YearMonth,
    entries: Map<LocalDate, WorkoutHistoryEntry>,
    today: LocalDate,
    onDayClick: (LocalDate) -> Unit
) {
    val dayLabels = java.time.DayOfWeek.values().map {
        it.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = month.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + month.year,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            dayLabels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        val firstDay = month.atDay(1)
        var offset = firstDay.dayOfWeek.value - 1
        var day = 1
        val daysInMonth = month.lengthOfMonth()
        while (day <= daysInMonth) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 7) {
                    when {
                        offset > 0 -> {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f)) {}
                            offset--
                        }
                        day <= daysInMonth -> {
                            val date = month.atDay(day)
                            val entry = entries[date]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clickable { onDateClick(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (entry != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = day.toString(), color = MaterialTheme.colorScheme.onPrimary)
                                    }
                                } else {
                                    val color = when {
                                        date == today -> MaterialTheme.colorScheme.primary
                                        date.isBefore(today) -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                        else -> MaterialTheme.colorScheme.onBackground
                                    }
                                    Text(text = day.toString(), color = color)
                                }
                            }
                            day++
                        }
                        else -> {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f)) {}
                        }
                    }
                }
            }
        }
    }
}

