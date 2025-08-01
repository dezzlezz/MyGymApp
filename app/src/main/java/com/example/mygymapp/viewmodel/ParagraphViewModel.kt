package com.example.mygymapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygymapp.data.ParagraphRepository
import com.example.mygymapp.model.Paragraph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ParagraphViewModel(
    private val repo: ParagraphRepository = ParagraphRepository()
) : ViewModel() {
    private val _paragraphs = MutableStateFlow<List<Paragraph>>(emptyList())
    val paragraphs: StateFlow<List<Paragraph>> = _paragraphs.asStateFlow()

    init {
        viewModelScope.launch {
            repo.paragraphs.collect { _paragraphs.value = it }
        }
    }

    fun addParagraph(paragraph: Paragraph) = repo.add(paragraph)
    fun editParagraph(paragraph: Paragraph) = repo.edit(paragraph)
    fun deleteParagraph(paragraph: Paragraph) = repo.delete(paragraph)
}
