package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.example.mygymapp.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.mygymapp.model.PlanType
import com.example.mygymapp.ui.screens.WeeklyPlansTab


@Composable
fun PlansScreen(navController: NavController) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
    val selected = if (tabIndex == 0) PlanType.DAILY else PlanType.WEEKLY

    Column(Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TabRow(selectedTabIndex = tabIndex, modifier = Modifier.weight(1f)) {
                Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }) {
                    Text(stringResource(id = R.string.daily))
                }
                Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }) {
                    Text(stringResource(id = R.string.weekly))
                }
            }
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
