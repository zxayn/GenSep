package com.example.generateresep.data

import com.example.generateresep.data.api.RecipeApiService
import com.example.generateresep.model.IngredientDetectionResponse
import com.example.generateresep.model.Recipe
import com.example.generateresep.model.RecipeResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepository @Inject constructor(
    private val apiService: RecipeApiService,
    private val recipeDao: RecipeDao
) {
    // Single Source of Truth from Room
    fun getAllSavedRecipes(username: String): Flow<List<RecipeEntity>> {
        return recipeDao.getAllRecipes().onStart {
            // Background fetch when flow starts
            syncRiwayat(username)
        }
    }

    private suspend fun syncRiwayat(username: String) {
        try {
            val response = apiService.getRiwayat(username)
            // Note: Since RecipeSimpleItem doesn't have all details like steps/description,
            // a real app would probably fetch full details. 
            // For now, we map what we have from the sync response.
            val entities = response.data.map {
                RecipeEntity(
                    id = it.id,
                    title = it.title,
                    ingredients = it.ingredients,
                    description = "", // Backend should ideally provide this
                    cookingTime = "",
                    difficulty = "",
                    steps = emptyList()
                )
            }
            entities.forEach { recipeDao.insertRecipe(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun generateRecipeFromText(query: String, username: String): Response<RecipeResponse> {
        return apiService.generateFromText(query, username)
    }

    suspend fun detectOnly(image: MultipartBody.Part, username: String): Response<IngredientDetectionResponse> {
        val usernameBody = username.toRequestBody(MultipartBody.FORM)
        return apiService.detectOnly(image, usernameBody)
    }

    suspend fun detectAndGenerateRecipe(image: MultipartBody.Part, username: String): Response<RecipeResponse> {
        val usernameBody = username.toRequestBody(MultipartBody.FORM)
        return apiService.detectAndGenerate(image, usernameBody)
    }

    suspend fun saveRecipe(recipe: Recipe) {
        val entity = RecipeEntity(
            title = recipe.title,
            description = recipe.description,
            cookingTime = recipe.cookingTime,
            difficulty = recipe.difficulty,
            ingredients = recipe.ingredients,
            steps = recipe.steps
        )
        // Local first
        recipeDao.insertRecipe(entity)
        // Note: Task doesn't specify a POST api/riwayat/simpan endpoint, 
        // but normally we'd sync it here.
    }

    suspend fun deleteRecipe(id: Int) {
        val entity = recipeDao.getRecipeById(id)
        if (entity != null) {
            // Local first
            recipeDao.deleteRecipe(entity)
            // Sync with Cloud
            try {
                apiService.deleteRiwayat(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getRecipeById(id: Int): RecipeEntity? = recipeDao.getRecipeById(id)
}
