package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.mygymapp.model.PlanType
import com.example.mygymapp.ui.screens.WeeklyPlansTab


@Composable
fun PlansScreen(navController: NavController) {
    var selected by remember { mutableStateOf(PlanType.DAILY) }

    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = if (selected == PlanType.DAILY) 0 else 1) {
            Tab(
                selected = selected == PlanType.DAILY,
                onClick = { selected = PlanType.DAILY }
            ) { Text("Daily") }
            Tab(
                selected = selected == PlanType.WEEKLY,
                onClick = { selected = PlanType.WEEKLY }
            ) { Text("Weekly") }
        }
        when (selected) {
            PlanType.DAILY -> DailyPlansTab(navController)
            PlanType.WEEKLY -> WeeklyPlansTab(navController)
        }
    }
}