package com.example.generateresep.data

import com.example.generateresep.data.api.RecipeApiService
import com.example.generateresep.model.Recipe
import com.example.generateresep.model.RecipeResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepository @Inject constructor(
    private val apiService: RecipeApiService,
    private val recipeDao: RecipeDao
) {
    val allSavedRecipes: Flow<List<RecipeEntity>> = recipeDao.getAllRecipes()

    suspend fun generateRecipeFromText(query: String): Response<RecipeResponse> {
        return apiService.generateFromText(query)
    }

    suspend fun detectAndGenerateRecipe(body: MultipartBody.Part): Response<RecipeResponse> {
        return apiService.detectAndGenerate(body)
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
        recipeDao.insertRecipe(entity)
    }

    suspend fun getRecipeById(id: Int): RecipeEntity? = recipeDao.getRecipeById(id)

    suspend fun deleteRecipe(id: Int) {
        val entity = recipeDao.getRecipeById(id)
        if (entity != null) {
            recipeDao.deleteRecipe(entity)
        }
    }
}