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
import com.example.mygymapp.viewmodel.ProfileViewModel
import com.example.mygymapp.model.MuscleGroupStat
import com.example.mygymapp.ui.components.MuscleGroupStatsChart
@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel = viewModel()) {
    val name by viewModel.userName.collectAsState()
    val notify by viewModel.notifications.collectAsState()
    val history by viewModel.history.collectAsState()
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

}
