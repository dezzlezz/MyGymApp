package com.example.mygymapp.data

import com.example.mygymapp.model.Line
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class LineRepository {
    private val _lines = MutableStateFlow<List<Line>>(emptyList())
    val lines: StateFlow<List<Line>> = _lines

    fun add(line: Line) {
        _lines.update { it + line }
    }

    fun update(line: Line) {
        _lines.update { list ->
            list.map { if (it.id == line.id) line else it }
        }
    }

    fun archive(lineId: Long) {
        _lines.update { list ->
            list.map { if (it.id == lineId) it.copy(isArchived = true) else it }
        }
    }

    fun unarchive(lineId: Long) {
        _lines.update { list ->
            list.map { if (it.id == lineId) it.copy(isArchived = false) else it }
        }
    }
}
