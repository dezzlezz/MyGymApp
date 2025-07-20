package com.example.mygymapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mygymapp.data.WorkoutHistoryEntry
import java.time.ZoneId
import com.example.mygymapp.viewmodel.ProfileViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import com.example.mygymapp.model.MuscleGroupStat
import com.example.mygymapp.ui.components.MuscleGroupStatsChart
@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel = viewModel()) {
    val name by viewModel.userName.collectAsState()
    val notify by viewModel.notifications.collectAsState()
    val history by viewModel.history.collectAsState()

    var dialogInfo by remember { mutableStateOf<Pair<String, String>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Outlined.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { viewModel.setUserName(it) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Workouts abgeschlossen: ${viewModel.totalWorkouts}",
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Diese Woche: ${viewModel.workoutsThisWeek}",
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Streak: ${viewModel.workoutStreak}",
            color = MaterialTheme.colorScheme.onBackground
        )
        if (viewModel.totalWorkouts >= 7) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(" 7-Tage-Serie!", color = MaterialTheme.colorScheme.onBackground)
            }
        }
        val stats by produceState(initialValue = emptyList<MuscleGroupStat>(), history) {
            value = viewModel.getMuscleGroupStats()
        }
        Spacer(Modifier.height(16.dp))
        MuscleGroupStatsChart(stats)
        Spacer(Modifier.height(24.dp))
        Text(
            "Workout Historie",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(8.dp))
        WorkoutCalendar(entries = history) { date ->
            history[date]?.let { entry ->
                viewModel.getEntryInfo(entry) { plan, day ->
                    dialogInfo = plan to day
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                "Benachrichtigungen",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground
            )
            Switch(checked = notify, onCheckedChange = { viewModel.setNotifications(it) })
        }
    Spacer(Modifier.height(16.dp))
    Button(
            onClick = { viewModel.logout() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Outlined.Logout, contentDescription = null)
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
    onDayClick: (LocalDate) -> Unit
) {
    val start = entries.keys.minOrNull() ?: LocalDate.now(ZoneId.systemDefault())
    val startMonth = YearMonth.from(start)
    val todayMonth = YearMonth.from(LocalDate.now(ZoneId.systemDefault()))
    val months = generateSequence(startMonth) { it.plusMonths(1) }
        .takeWhile { !it.isAfter(todayMonth) }
        .toList()

    Column(modifier = Modifier.fillMaxWidth()) {
        months.forEach { month ->
            MonthView(month = month, entries = entries, onDayClick = onDayClick)
        }
    }
}

@Composable
private fun MonthView(
    month: YearMonth,
    entries: Map<LocalDate, WorkoutHistoryEntry>,
    onDayClick: (LocalDate) -> Unit
) {
    val daysOfWeek = listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")
    val today = LocalDate.now(ZoneId.systemDefault())
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            month.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + month.year,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
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
                                .clickable { onDayClick(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (entry != null) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(day.toString(), color = MaterialTheme.colorScheme.onPrimary)
                                }
                            } else {
                                val textColor = when {
                                    date == today -> MaterialTheme.colorScheme.primary
                                    date.isBefore(today) -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    else -> MaterialTheme.colorScheme.onBackground
                                }
                                Text(day.toString(), color = textColor)
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
