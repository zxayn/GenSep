package com.example.generateresep.data

import com.example.generateresep.data.api.RecipeApiService
import com.example.generateresep.model.EditCatatanRequest
import com.example.generateresep.model.TambahCatatanRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val apiService: RecipeApiService
) {
    // Single Source of Truth from Room
    fun getAllNotes(username: String): Flow<List<NoteEntity>> {
        return noteDao.getAllNotes().onStart {
            // Background fetch when flow starts
            syncNotes(username)
        }
    }

    private suspend fun syncNotes(username: String) {
        try {
            val response = apiService.getCatatan(username)
            val remoteNotes = response.data.map {
                NoteEntity(id = it.id, title = it.title, ingredients = it.ingredients)
            }
            // Overwrite local with remote
            // Usually we'd be more careful with merging, but per task: "simpan/timpa"
            remoteNotes.forEach { noteDao.insertNote(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun insert(note: NoteEntity, username: String) {
        // Room First
        noteDao.insertNote(note)
        // Background Sync
        try {
            apiService.addCatatan(TambahCatatanRequest(username, note.title, note.ingredients))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun update(note: NoteEntity) {
        // Room First
        noteDao.updateNote(note)
        // Background Sync
        try {
            apiService.editCatatan(note.id, EditCatatanRequest(note.title, note.ingredients))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun delete(note: NoteEntity) {
        // Room First
        noteDao.deleteNote(note)
        // Background Sync
        try {
            apiService.deleteCatatan(note.id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
