package com.example.mygymapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.Exercise as LineExercise
import com.example.mygymapp.ui.pages.SupersetState
import kotlinx.coroutines.flow.MutableStateFlow

class LineEditorViewModel(private val initial: Line?) : ViewModel() {
    val title = MutableStateFlow(initial?.title ?: "")
    val note = MutableStateFlow(initial?.note ?: "")
    val selectedExercises = mutableStateListOf<LineExercise>().apply {
        initial?.exercises?.let { addAll(it) }
    }
    val selectedCategories = mutableStateListOf<String>().apply {
        initial?.category?.split(",")?.filter { it.isNotBlank() }?.let { addAll(it) }
    }
    val selectedMuscles = mutableStateListOf<String>().apply {
        initial?.muscleGroup?.split(",")?.filter { it.isNotBlank() }?.let { addAll(it) }
    }
    private val supersetGroups = mutableStateListOf<MutableList<Long>>().apply {
        initial?.supersets?.let { addAll(it.map { grp -> grp.toMutableList() }) }
    }
    val supersetState = SupersetState(supersetGroups)

    val sections: State<List<String>> = derivedStateOf {
        selectedExercises.map { it.section }.filter { it.isNotBlank() }.distinct()
    }

    fun buildLine(): Line = Line(
        id = initial?.id ?: System.currentTimeMillis(),
        title = title.value,
        category = selectedCategories.joinToString(),
        muscleGroup = selectedMuscles.joinToString(),
        mood = initial?.mood,
        exercises = selectedExercises.toList(),
        supersets = supersetState.groups,
        note = note.value,
        isArchived = initial?.isArchived ?: false
    )

    class Factory(private val initial: Line?) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LineEditorViewModel(initial) as T
        }
    }
}

