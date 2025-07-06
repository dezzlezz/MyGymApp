package com.example.mygymapp.ui.components

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mygymapp.data.Plan
import com.example.mygymapp.data.PlanType
import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.data.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPlanSheet(
    initialPlan: Plan?,
    onSave: (Plan, List<PlanExerciseCrossRef>) -> Unit,
    onCancel: () -> Unit,
    allExercises: List<Exercise>
) {
    var name by remember { mutableStateOf(initialPlan?.name.orEmpty()) }
    var description by remember { mutableStateOf(initialPlan?.description.orEmpty()) }
    var isFav by remember { mutableStateOf(initialPlan?.isFavorite ?: false) }
    var type by remember { mutableStateOf(initialPlan?.type ?: PlanType.DAILY) }
    var iconUri by remember { mutableStateOf(initialPlan?.iconUri) }
    val selectedRefs = remember { mutableStateListOf<PlanExerciseCrossRef>() }

    // Use TabRow for Daily/Weekly selection
    var typeTab by remember { mutableStateOf(if (type == PlanType.DAILY) 0 else 1) }
    TabRow(selectedTabIndex = typeTab) {
        Tab(
            selected = typeTab == 0,
            onClick = {
                typeTab = 0
                type = PlanType.DAILY
            },
            text = { Text("Daily") }
        )
        Tab(
            selected = typeTab == 1,
            onClick = {
                typeTab = 1
                type = PlanType.WEEKLY
            },
            text = { Text("Weekly") }
        )
    }

    Column(Modifier
        .fillMaxHeight(0.9f)
        .padding(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Favorite")
            Spacer(Modifier.width(8.dp))
            Switch(checked = isFav, onCheckedChange = { isFav = it })
        }

        Spacer(Modifier.height(16.dp))
        Text("Select Exercises")
        LazyColumn {
            items(allExercises) { ex ->
                val isSelected = selectedRefs.any { it.exerciseId == ex.id }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isSelected) selectedRefs.removeAll { it.exerciseId == ex.id }
                            else selectedRefs += PlanExerciseCrossRef(
                                planId = 0L,
                                exerciseId = ex.id,
                                sets = 3,
                                reps = 10,
                                orderIndex = selectedRefs.size
                            )
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isSelected, onCheckedChange = null)
                    Spacer(Modifier.width(8.dp))
                    Text(ex.name)
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) { Text("Cancel") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                val plan = Plan(
                    planId = initialPlan?.planId ?: 0L,
                    name = name,
                    description = description,
                    isFavorite = isFav,
                    iconUri = iconUri,
                    type = type
                )
                onSave(plan, selectedRefs)
            }) {
                Text("Save")
            }
        }
    }
}