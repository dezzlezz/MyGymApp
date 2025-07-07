package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.MyApp
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.ui.components.FilterChips
import com.example.mygymapp.ui.components.PlanCard
import com.example.mygymapp.ui.components.SearchFilterBar
import com.example.mygymapp.ui.viewmodel.PlansViewModel
import com.example.mygymapp.ui.viewmodel.PlansViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlansScreen(
    viewModel: PlansViewModel = viewModel(
        factory = PlansViewModelFactory(
            PlanRepository(MyApp.database.planDao())
        )
    ),
    onAddPlan: () -> Unit                       // Callback für Add-Button
) {
    // Compose-State
    val plans by viewModel.plans.observeAsState(emptyList())
    val selectedType by viewModel.type.observeAsState(com.example.mygymapp.model.PlanType.DAILY)
    var query by remember { mutableStateOf("") }
    var favoritesOnly by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPlan) {
                Icon(Icons.Default.Add, contentDescription = "Add Plan")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)     // Achtung: Platz für FAB & Snackbar
                .padding(16.dp)
        ) {
            // Search & Filter
            SearchFilterBar(
                query = query,
                onQueryChange = { query = it },
                favoritesOnly = favoritesOnly,
                onFavoritesToggle = { favoritesOnly = !favoritesOnly }
            )
            Spacer(Modifier.height(8.dp))

            // Type-Chips
            val types = com.example.mygymapp.model.PlanType.values().map { it.name }
            FilterChips(
                items = types,
                selected = selectedType.name,
                onSelected = { viewModel.switchType(com.example.mygymapp.model.PlanType.valueOf(it.orEmpty())) }
            )
            Spacer(Modifier.height(16.dp))

            // Plan-Liste
            LazyColumn {
                items(plans, key = { it.planId }) { plan ->
                    val dismissState = rememberDismissState { value ->
                        when (value) {
                            DismissValue.DismissedToEnd -> {
                                scope.launch {
                                    viewModel.delete(plan)
                                    snackbarHostState.showSnackbar("Plan gelöscht", "Undo")
                                }
                                true
                            }
                            DismissValue.DismissedToStart -> true
                            else -> false
                        }
                    }
                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
                        background = { /* optional */ },
                        dismissContent = {
                            PlanCard(plan = plan, onClick = { /* navigate to detail */ })
                        }
                    )
                }
            }
        }
    }
}
