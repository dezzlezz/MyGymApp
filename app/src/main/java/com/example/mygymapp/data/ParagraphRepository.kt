package com.example.mygymapp.data

import com.example.mygymapp.model.Paragraph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ParagraphRepository {
    private val _paragraphs = MutableStateFlow<List<Paragraph>>(emptyList())
    val paragraphs = _paragraphs

    fun add(paragraph: Paragraph) {
        _paragraphs.update { it + paragraph }
    }

    fun edit(paragraph: Paragraph) {
        _paragraphs.update { list ->
            list.map { if (it.id == paragraph.id) paragraph else it }
        }
    }

    fun delete(paragraph: Paragraph) {
        _paragraphs.update { it.filterNot { p -> p.id == paragraph.id } }
    }
}
