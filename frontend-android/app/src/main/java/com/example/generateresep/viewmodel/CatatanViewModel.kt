package com.example.generateresep.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.generateresep.data.NoteEntity
import com.example.generateresep.data.NoteRepository
import com.example.generateresep.model.Note
import com.example.generateresep.utils.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatatanViewModel @Inject constructor(
    application: Application,
    private val repository: NoteRepository
) : AndroidViewModel(application) {
    
    val notes = mutableStateListOf<Note>()
    var syncErrorMessage: String? = null // For Snackbars
    
    private val currentUsername = UserSession.currentUsername // Ideally fetch from Auth storage

    init {
        viewModelScope.launch {
            repository.getAllNotes(currentUsername).collectLatest { entities ->
                notes.clear()
                notes.addAll(entities.map { it.toNote() })
            }
        }
    }

    val isAnyEditing: Boolean
        get() = notes.any { it.isEditing }

    fun addNote() {
        val newNote = Note(
            id = 0,
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
                try {
                    repository.insert(editingNote.toEntity(), currentUsername)
                } catch (e: Exception) {
                    syncErrorMessage = "Gagal sinkronisasi ke server"
                }
            }
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
        viewModelScope.launch {
            try {
                repository.delete(note.toEntity())
            } catch (e: Exception) {
                syncErrorMessage = "Gagal menghapus dari server"
            }
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
