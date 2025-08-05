package com.example.mygymapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mygymapp.data.LineRepository
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.Mood
import kotlinx.coroutines.flow.StateFlow

class LineViewModel : ViewModel() {
    private val repo = LineRepository()
    val lines: StateFlow<List<Line>> = repo.lines

    init {
        add(
            Line(
                id = 1L,
                title = "Silent Force",
                category = "Push",
                muscleGroup = "Core",
                mood = Mood.CALM,
                exercises = emptyList(),
                supersets = emptyList(),
                note = "Felt steady and grounded throughout."
            )
        )
        add(
            Line(
                id = 2L,
                title = "Night Owl Session",
                category = "Pull",
                muscleGroup = "Back",
                mood = Mood.FOCUSED,
                exercises = emptyList(),
                supersets = listOf(listOf(1L, 2L)),
                note = "Late session with high focus."
            )
        )
    }

    fun add(line: Line) = repo.add(line)
    fun update(line: Line) = repo.update(line)
    fun archive(lineId: Long) = repo.archive(lineId)
    fun unarchive(lineId: Long) = repo.unarchive(lineId)
}
