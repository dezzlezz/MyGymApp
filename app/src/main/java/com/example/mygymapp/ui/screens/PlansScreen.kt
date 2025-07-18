package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.example.mygymapp.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mygymapp.model.PlanType
import com.example.mygymapp.ui.screens.WeeklyPlansTab


@Composable
fun PlansScreen(navController: NavController) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
    val selected = if (tabIndex == 0) PlanType.DAILY else PlanType.WEEKLY

    Column(Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            FilterChip(
                selected = tabIndex == 0,
                onClick = { tabIndex = 0 },
                label = { Text(stringResource(id = R.string.daily)) }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = tabIndex == 1,
                onClick = { tabIndex = 1 },
                label = { Text(stringResource(id = R.string.weekly)) }
            )
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { navController.navigate("preferences") }) {
                Text(stringResource(id = R.string.preferences_button))
            }
        }
        if (selected == PlanType.DAILY) {
            DailyPlansTab(navController)
        } else {
            WeeklyPlansTab(navController)
        }
    }}
