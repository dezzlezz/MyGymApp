package com.example.mygymapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygymapp.data.ParagraphRepository
import com.example.mygymapp.model.Paragraph
import com.example.mygymapp.model.PlannedParagraph
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ParagraphViewModel(
    private val repo: ParagraphRepository = ParagraphRepository()
) : ViewModel() {
    private val _paragraphs = MutableStateFlow<List<Paragraph>>(emptyList())
    val paragraphs: StateFlow<List<Paragraph>> = _paragraphs.asStateFlow()

    private val _templates = MutableStateFlow<List<Paragraph>>(emptyList())
    val templates: StateFlow<List<Paragraph>> = _templates.asStateFlow()

    private val _planned = MutableStateFlow<List<PlannedParagraph>>(emptyList())
    val planned: StateFlow<List<PlannedParagraph>> = _planned.asStateFlow()

    init {
        viewModelScope.launch {
            repo.paragraphs.collect { _paragraphs.value = it }
        }
    }

    fun addParagraph(paragraph: Paragraph) = repo.add(paragraph)
    fun editParagraph(paragraph: Paragraph) = repo.edit(paragraph)
    fun deleteParagraph(paragraph: Paragraph) = repo.delete(paragraph)

    fun saveTemplate(paragraph: Paragraph) {
        _templates.update { it + paragraph.copy() }
    }

    fun planParagraph(paragraph: Paragraph, startDate: LocalDate) {
        _planned.update { it + PlannedParagraph(paragraph, startDate) }
    }
}
