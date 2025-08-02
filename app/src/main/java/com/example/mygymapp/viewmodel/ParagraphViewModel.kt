package com.example.mygymapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.ParagraphRepository
import com.example.mygymapp.model.Paragraph
import com.example.mygymapp.model.PlannedParagraph
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted

class ParagraphViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: ParagraphRepository =
        ParagraphRepository(AppDatabase.getDatabase(application).paragraphDao())

    val paragraphs: StateFlow<List<Paragraph>> =
        repo.paragraphs.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _templates = MutableStateFlow<List<Paragraph>>(emptyList())
    val templates: StateFlow<List<Paragraph>> = _templates.asStateFlow()

    private val _planned = MutableStateFlow<List<PlannedParagraph>>(emptyList())
    val planned: StateFlow<List<PlannedParagraph>> = _planned.asStateFlow()

    fun addParagraph(paragraph: Paragraph) =
        viewModelScope.launch(Dispatchers.IO) { repo.add(paragraph) }

    fun editParagraph(paragraph: Paragraph) =
        viewModelScope.launch(Dispatchers.IO) { repo.edit(paragraph) }

    fun deleteParagraph(paragraph: Paragraph) =
        viewModelScope.launch(Dispatchers.IO) { repo.delete(paragraph) }

    fun saveTemplate(paragraph: Paragraph) {
        _templates.update { it + paragraph.copy() }
    }

    fun planParagraph(paragraph: Paragraph, startDate: LocalDate) {
        _planned.update { it + PlannedParagraph(paragraph, startDate) }
    }
}
