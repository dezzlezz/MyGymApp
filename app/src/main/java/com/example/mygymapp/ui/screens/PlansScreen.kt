// app/src/main/java/com/example/mygymapp/ui/screens/PlansScreen.kt
package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.ui.components.PlanCard
import com.example.mygymapp.ui.components.SearchFilterBar
import com.example.mygymapp.ui.components.FilterChips
import com.example.mygymapp.ui.viewmodel.PlansViewModel
import com.example.mygymapp.ui.viewmodel.PlansViewModelFactory
import com.example.mygymapp.MyApp
import kotlinx.coroutines.launch

@Composable
fun PlansScreen(
    viewModel: PlansViewModel = viewModel(
        factory = PlansViewModelFactory(
            PlanRepository(MyApp.database.planDao())
        )
    )
) {
    val plans by viewModel.plans.observeAsState(emptyList())
    val type by viewModel.type.observeAsState(viewModel.type.value!!)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        SearchFilterBar(
            query = "",
            onQueryChange = { /* ... */ },
            favoritesOnly = false,
            onFavoritesToggle = { /* ... */ }
        )
        Spacer(Modifier.height(8.dp))
        FilterChips(
            items = listOf("All", "Favs"),
            selected = null,
            onSelected = { /* ... */ }
        )
        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(plans, key = { it.id }) { plan ->
                val dismissState = rememberDismissState { value ->
                    if (value == DismissValue.DismissedToEnd) {
                        scope.launch { viewModel.delete(plan) }
                        scope.launch {
                            snackbarHostState.showSnackbar("Plan deleted", "Undo")
                        }
                        true
                    } else if (value == DismissValue.DismissedToStart) {
                        // hier könntest du z.B. bottom sheet für Edit öffnen
                        true
                    } else false
                }
                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
                    dismissContent = {
                        PlanCard(
                            plan = plan,
                            onClick = { /* Detail öffnen: plan.id ist String */ }
                        )
                    }
                )
            }
        }
    }

    SnackbarHost(hostState = snackbarHostState)
}
