package com.example.generateresep.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.generateresep.data.AppDatabase
import com.example.generateresep.data.NoteEntity
import com.example.generateresep.data.NoteRepository
import com.example.generateresep.model.Note
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CatatanViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    val notes = mutableStateListOf<Note>()

    init {
        val noteDao = AppDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
        
        viewModelScope.launch {
            repository.allNotes.collectLatest { entities ->
                notes.clear()
                notes.addAll(entities.map { it.toNote() })
            }
        }
    }

    val isAnyEditing: Boolean
        get() = notes.any { it.isEditing }

    fun addNote() {
        val newNote = Note(
            id = 0, // 0 for auto-generate in Room
            title = "",
            ingredients = listOf(""),
            isExpanded = true,
            isEditing = true
        )
        notes.add(0, newNote)
    }

    fun saveNote() {
        val editingNote = notes.find { it.isEditing }
        if (editingNote != null) {
            viewModelScope.launch {
                if (editingNote.id == 0) {
                    repository.insert(editingNote.toEntity())
                } else {
                    repository.update(editingNote.toEntity())
                }
            }
            // Logic to clear editing state is handled by Flow collection usually, 
            // but for immediate UI feedback:
            val index = notes.indexOf(editingNote)
            if (index != -1) {
                notes[index] = editingNote.copy(isEditing = false)
            }
        }
    }

    fun toggleExpand(note: Note) {
        val index = notes.indexOf(note)
        if (index != -1) {
            notes[index] = note.copy(isExpanded = !note.isExpanded)
        }
    }

    fun deleteNote(note: Note) {
        if (note.id != 0) {
            viewModelScope.launch {
                repository.delete(note.toEntity())
            }
        } else {
            notes.remove(note)
        }
    }

    fun startEditing(note: Note) {
        val index = notes.indexOf(note)
        if (index != -1) {
            notes[index] = note.copy(isEditing = true, isExpanded = true)
        }
    }

    fun updateTitle(note: Note, newTitle: String) {
        val index = notes.indexOf(note)
        if (index != -1) {
            notes[index] = note.copy(title = newTitle)
        }
    }

    fun updateIngredients(note: Note, newIngredients: List<String>) {
        val index = notes.indexOf(note)
        if (index != -1) {
            notes[index] = note.copy(ingredients = newIngredients)
        }
    }

    // Helper extensions
    private fun NoteEntity.toNote() = Note(
        id = id,
        title = title,
        ingredients = ingredients
    )

    private fun Note.toEntity() = NoteEntity(
        id = id,
        title = title,
        ingredients = ingredients
    )
}
