package com.example.mygymapp.data

import com.example.mygymapp.model.Paragraph
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ParagraphRepository(private val dao: ParagraphDao) {
    val paragraphs: Flow<List<Paragraph>> =
        dao.getActive().map { list -> list.map { it.toModel() } }

    val archived: Flow<List<Paragraph>> =
        dao.getArchived().map { list -> list.map { it.toModel() } }

    fun add(paragraph: Paragraph) {
        dao.insert(paragraph.toEntity())
    }

    fun edit(paragraph: Paragraph) {
        dao.update(paragraph.toEntity())
    }

    fun archive(id: Long) {
        dao.archive(id)
    }

    fun unarchive(id: Long) {
        dao.unarchive(id)
    }

    fun delete(paragraph: Paragraph) {
        dao.delete(paragraph.toEntity())
    }
}

fun Paragraph.toEntity(): ParagraphEntity =
    ParagraphEntity(id = id, title = title, lineTitles = lineTitles, note = note, isArchived = isArchived)

fun ParagraphEntity.toModel(): Paragraph =
    Paragraph(id = id, title = title, lineTitles = lineTitles, note = note, isArchived = isArchived)
