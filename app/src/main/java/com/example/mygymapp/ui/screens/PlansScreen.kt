package com.example.mygymapp.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.ui.viewmodel.PlansViewModel
import com.example.mygymapp.ui.components.*

@Composable
fun PlansScreen(
    viewModel: PlansViewModel = viewModel(),
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Weekly", "Daily")

    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        val plans = if (selectedTab == 0) viewModel.weeklyPlans else viewModel.dailyPlans
        val query by viewModel.searchQuery.observeAsState("")
        val favOnly by viewModel.showFavoritesOnly.observeAsState(false)

        SearchFilterBar(
            query = query,
            onQueryChange = viewModel::setSearch,
            favoritesOnly = favOnly,
            onFavoritesToggle = viewModel::toggleFavorites
        )

        Box(Modifier.weight(1f)) {
            LazyColumn { items(plans.value ?: emptyList(), key = { it.plan.planId }) { pwEx ->
                PlanCard(
                    planWithExercises = pwEx,
                    onClick = { /* open detail sheet */ },
                    onDelete = { viewModel.deletePlan(pwEx.plan) },
                    onEdit = { /* open edit sheet */ }
                )
            }
            }
            FloatingActionButton(
                onClick = { /* open add sheet */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) { Icon(Icons.Default.Add, null) }
        }

        // TODO: BottomSheets for detail and add/edit
    }
}