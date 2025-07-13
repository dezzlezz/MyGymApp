package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mygymapp.R
import com.example.mygymapp.data.Plan
import com.example.mygymapp.model.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestedPlansScreen(
    preferences: UserPreferences,
    allPlans: List<Plan>,
    onPlanSelected: (Plan) -> Unit,
    onBack: () -> Unit
) {
    val suggestions = remember(preferences, allPlans) {
        allPlans.filter { plan ->
            plan.durationMinutes <= preferences.maxDuration &&
                plan.requiredEquipment.all { it in preferences.equipment }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.suggestions_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        if (suggestions.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(id = R.string.no_suggestions))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(suggestions, key = { it.planId }) { plan ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(plan.name, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(stringResource(id = R.string.duration_label, plan.durationMinutes))
                            if (plan.requiredEquipment.isNotEmpty()) {
                                Spacer(Modifier.height(4.dp))
                                Text(plan.requiredEquipment.joinToString(), style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { onPlanSelected(plan) }, modifier = Modifier.align(Alignment.End)) {
                                Text(stringResource(id = R.string.use_plan))
                            }
                        }
                    }
                }
            }
        }
    }
}
