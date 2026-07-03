package com.example.generateresep.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.generateresep.R
import com.example.generateresep.data.RecipeRepository
import com.example.generateresep.data.TokenManager
import com.example.generateresep.model.Recipe
import com.example.generateresep.model.RecipeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRepository,
    private val tokenManager: TokenManager // <--- MENGAMBIL DARI MEMORI PERMANEN
) : ViewModel() {

    // Membaca username secara real-time dari memori untuk database lokal (Riwayat/Saved)
    @OptIn(ExperimentalCoroutinesApi::class)
    val savedRecipes = tokenManager.username.flatMapLatest { username ->
        if (username.isNullOrBlank()) {
            flowOf(emptyList())
        } else {
            repository.getAllSavedRecipes(username)
        }
    }

    var searchQuery by mutableStateOf("")
    var isDockExpanded by mutableStateOf(false)

    var isGenerating by mutableStateOf(false)
    var generatedRecipe by mutableStateOf<Recipe?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    // State untuk resep yang sedang dibuka dari database
    var savedRecipeDetail by mutableStateOf<Recipe?>(null)

    // Detection State
    var detectedIngredients by mutableStateOf<List<String>>(emptyList())
    var isDetectionActive by mutableStateOf(false)
    var isDetecting by mutableStateOf(false)
    var searchResults by mutableStateOf<List<String>>(emptyList())

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        searchResults = if (newQuery.isBlank()) emptyList() else listOf("Resep $newQuery", "Cara membuat $newQuery")
    }

    fun toggleDock() { isDockExpanded = !isDockExpanded }

    fun onCameraClick() {
        isDockExpanded = false
        isDetectionActive = true
    }

    // FUNGSI 1: Minta Django meracik resep dari Teks (Search Bar)
    fun generateRecipeFromText() {
        if (searchQuery.isBlank()) return

        viewModelScope.launch {
            isGenerating = true
            errorMessage = null
            try {
                // <--- MENGAMBIL USERNAME TERBARU SEBELUM NEMBAK API
                val currentUser = tokenManager.username.firstOrNull() ?: ""
                val response = repository.generateRecipeFromText(searchQuery, currentUser)

                if (response.isSuccessful && response.body()?.status == "success") {
                    val data = response.body()?.data
                    if (data != null) mapToRecipe(data)
                } else {
                    errorMessage = response.body()?.message ?: "Gagal mendapatkan resep."
                }
            } catch (e: Exception) {
                errorMessage = "Error Koneksi ke Server Django: ${e.localizedMessage}"
            } finally {
                isGenerating = false
            }
        }
    }

    // FUNGSI 2: Kirim Foto ke Django -> YOLO deteksi saja!
    fun detectIngredients(bitmap: Bitmap) {
        viewModelScope.launch {
            isDetecting = true
            errorMessage = null
            try {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                val byteArray = stream.toByteArray()
                val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", "ingredient.jpg", requestBody)

                val currentUser = tokenManager.username.firstOrNull() ?: ""
                val response = repository.detectOnly(body, currentUser)

                if (response.isSuccessful) {
                    val bodyResult = response.body()
                    if (bodyResult?.status == "success") {
                        detectedIngredients = bodyResult.detectedingredients ?: emptyList()
                    } else {
                        errorMessage = bodyResult?.message ?: "Gagal mendeteksi bahan."
                    }
                } else {
                    // Berikan info detail jika 404 agar user tahu endpoint belum ada di Django
                    errorMessage = if (response.code() == 404) {
                        "Error 404: Endpoint 'api/detect-only/' tidak ditemukan di server Django Anda."
                    } else {
                        "Server Error ${response.code()}: ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Gagal terhubung ke Django: ${e.localizedMessage}"
            } finally {
                isDetecting = false
            }
        }
    }

    // Mengubah data dari Django ke format tampilan UI Android
    private fun mapToRecipe(data: RecipeData) {
        generatedRecipe = Recipe(
            id = 0,
            title = data.title,
            description = data.description,
            imageResId = R.drawable.makanan, // Gambar default
            cookingTime = data.cookingTime,
            difficulty = data.difficulty,
            ingredients = data.ingredients,
            steps = data.steps
        )
    }

    fun onIngredientsDetected(ingredients: List<String>) {
        detectedIngredients = emptyList() // Clear dialog karena sekarang langsung ke resep
    }

    fun saveCurrentRecipe() {
        generatedRecipe?.let {
            viewModelScope.launch { repository.saveRecipe(it) }
        }
    }

    fun loadRecipeById(id: Int) {
        if (id == 0) return

        viewModelScope.launch {
            val entity = repository.getRecipeById(id)
            if (entity != null) {
                savedRecipeDetail = Recipe(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    imageResId = R.drawable.makanan,
                    cookingTime = entity.cookingTime,
                    difficulty = entity.difficulty,
                    ingredients = entity.ingredients,
                    steps = entity.steps
                )
            }
        }
    }

    fun deleteRecipe(id: Int) {
        viewModelScope.launch {
            repository.deleteRecipe(id)
            if (savedRecipeDetail?.id == id) {
                savedRecipeDetail = null
            }
        }
    }

    fun searchByIngredients() {}
    fun generateByIngredients() {
        if (detectedIngredients.isEmpty()) return
        val query = detectedIngredients.joinToString(", ")
        
        viewModelScope.launch {
            isGenerating = true
            errorMessage = null
            try {
                val currentUser = tokenManager.username.firstOrNull() ?: ""
                val response = repository.generateRecipeFromText(query, currentUser)

                if (response.isSuccessful) {
                    val bodyResult = response.body()
                    if (bodyResult?.status == "success") {
                        val data = bodyResult.data
                        if (data != null) mapToRecipe(data)
                    } else {
                        errorMessage = bodyResult?.message ?: "Gagal mendapatkan resep."
                    }
                } else {
                    errorMessage = "Server Error ${response.code()}: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error Koneksi ke Server Django: ${e.localizedMessage}"
            } finally {
                isGenerating = false
            }
        }
    }

    fun resetGeneratedRecipe() {
        generatedRecipe = null
        errorMessage = null
    }
}
