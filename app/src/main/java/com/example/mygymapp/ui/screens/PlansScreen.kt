package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.data.PlanWithExercises
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.ui.components.AddEditPlanSheet
import com.example.mygymapp.ui.components.PlanCard
import com.example.mygymapp.ui.viewmodel.PlansViewModel
import com.example.mygymapp.ui.viewmodel.PlansViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansScreen() {
    val context = LocalContext.current
    val repository = PlanRepository(AppDatabase.getDatabase(context).planDao())
    val viewModel: PlansViewModel = viewModel(
        factory = PlansViewModelFactory(repository)
    )

    val weeklyPlans by viewModel.weeklyPlans.collectAsState()
    val dailyPlans by viewModel.dailyPlans.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showAddSheet by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSheet = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add plan")
            }
        }
    ) { scaffoldPadding ->
        BottomSheetScaffold(
            sheetContent = {
                if (showAddSheet) {
                    AddEditPlanSheet(
                        initialPlan = null,
                        allExercises = (weeklyPlans + dailyPlans).flatMap { it.exercises },
                        onSave = { plan, refs ->
                            viewModel.insertPlan(plan)
                            viewModel.insertCrossRefs(refs)
                            showAddSheet = false
                        },
                        onCancel = { showAddSheet = false }
                    )
                } else {
                    Spacer(modifier = Modifier.height(1.dp))
                }
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding)
                        .padding(paddingValues)
                ) {
                    TabRow(selectedTabIndex = selectedTab) {
                        listOf("Weekly", "Daily").forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title) }
                            )
                        }
                    }

                    val plans = if (selectedTab == 0) weeklyPlans else dailyPlans
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(plans) { pwx ->
                            PlanCard(
                                plan = pwx.plan,
                                exercises = pwx.exercises,
                                onDelete = { viewModel.deletePlan(pwx.plan) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        )
    }
}