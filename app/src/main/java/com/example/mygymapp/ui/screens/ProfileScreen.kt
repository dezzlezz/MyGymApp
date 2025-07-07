package com.example.mygymapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.data.WorkoutHistoryEntry
import com.example.mygymapp.ui.theme.AccentGreen
import com.example.mygymapp.ui.theme.DeepBlack
import com.example.mygymapp.ui.theme.OnDark
import com.example.mygymapp.ui.viewmodel.ProfileViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {
    val name by viewModel.userName.collectAsState()
    val dark by viewModel.darkMode.collectAsState()
    val notify by viewModel.notifications.collectAsState()
    val history by viewModel.history.collectAsState()

    var dialogInfo by remember { mutableStateOf<Pair<String, String>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Person, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(80.dp))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { viewModel.setUserName(it) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentGreen,
                unfocusedBorderColor = AccentGreen,
                focusedTextColor = OnDark,
                unfocusedTextColor = OnDark,
                cursorColor = AccentGreen
            )
        )
        Spacer(Modifier.height(16.dp))
        Text("Workouts abgeschlossen: ${viewModel.totalWorkouts}", color = OnDark)
        if (viewModel.totalWorkouts >= 7) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = AccentGreen)
                Text(" 7-Tage-Serie!", color = OnDark)
            }
        }
        Spacer(Modifier.height(24.dp))
        Text("Workout Historie", style = MaterialTheme.typography.titleMedium, color = OnDark)
        Spacer(Modifier.height(8.dp))
        WorkoutCalendar(entries = history, onDayClick = { date, entry ->
            viewModel.getEntryInfo(entry) { plan, day ->
                dialogInfo = plan to day
            }
        })
        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Dark Mode", modifier = Modifier.weight(1f), color = OnDark)
            Switch(checked = dark, onCheckedChange = { viewModel.setDarkMode(it) })
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Benachrichtigungen", modifier = Modifier.weight(1f), color = OnDark)
            Switch(checked = notify, onCheckedChange = { viewModel.setNotifications(it) })
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = { viewModel.logout() }, colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Logout")
        }
    }

    dialogInfo?.let { (plan, day) ->
        AlertDialog(
            onDismissRequest = { dialogInfo = null },
            confirmButton = {
                TextButton(onClick = { dialogInfo = null }) { Text("OK") }
            },
            title = { Text(plan) },
            text = { Text(day) }
        )
    }
}

@Composable
private fun WorkoutCalendar(
    entries: Map<LocalDate, WorkoutHistoryEntry>,
    onDayClick: (LocalDate, WorkoutHistoryEntry) -> Unit
) {
    val start = entries.keys.minOrNull() ?: LocalDate.now()
    val startMonth = YearMonth.from(start)
    val todayMonth = YearMonth.from(LocalDate.now())
    val months = generateSequence(startMonth) { it.plusMonths(1) }
        .takeWhile { !it.isAfter(todayMonth) }
        .toList()

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        months.forEach { month ->
            item {
                MonthView(month = month, entries = entries, onDayClick = onDayClick)
            }
        }
    }
}

@Composable
private fun MonthView(
    month: YearMonth,
    entries: Map<LocalDate, WorkoutHistoryEntry>,
    onDayClick: (LocalDate, WorkoutHistoryEntry) -> Unit
) {
    val daysOfWeek = listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            month.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + month.year,
            color = OnDark,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = OnDark)
            }
        }
        val firstDay = month.atDay(1)
        var offset = (firstDay.dayOfWeek.value % 7)
        var day = 1
        val daysInMonth = month.lengthOfMonth()
        while (day <= daysInMonth) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 7) {
                    if (offset > 0) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f)) {}
                        offset--
                    } else if (day <= daysInMonth) {
                        val date = month.atDay(day)
                        val entry = entries[date]
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clickable(enabled = entry != null) {
                                    entry?.let { onDayClick(date, it) }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (entry != null) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(AccentGreen, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(day.toString(), color = Color.White)
                                }
                            } else {
                                Text(day.toString(), color = OnDark)
                            }
                        }
                        day++
                    } else {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f)) {}
                    }
                }
            }
        }
    }
}
