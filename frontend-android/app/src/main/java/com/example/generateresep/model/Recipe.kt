package com.example.generateresep.model

data class Recipe(
    val id: Int,
    val title: String,
    val description: String,
    val imageResId: Int,
    val cookingTime: String,
    val difficulty: String,
    val ingredients: List<String>,
    val steps: List<String>
)

data class RecipeResponse(
    val status: String,
    val message: String?,
    val detectedingredients: List<String>?,
    val data: RecipeData?
)

data class RecipeData(
    val title: String,
    val description: String,
    val cookingTime: String,
    val difficulty: String,
    val ingredients: List<String>,
    val steps: List<String>
)

data class IngredientDetectionResponse(
    val status: String,
    val message: String?,
    val detectedingredients: List<String>?
)

// --- Models for Django Cloud Sync ---

data class CrudResponse(
    val status: String,
    val message: String
)

data class RecipeSearchResponse(
    val data: List<RecipeSimpleItem>
)

data class RecipeSimpleItem(
    val id: Int,
    val title: String,
    val ingredients: List<String>
)

data class NoteListResponse(
    val data: List<NoteRemoteItem>
)

data class NoteRemoteItem(
    val id: Int,
    val title: String,
    val ingredients: List<String>
)

data class TambahCatatanRequest(
    val username: String,
    val title: String,
    val ingredients: List<String>
)

data class EditCatatanRequest(
    val title: String,
    val ingredients: List<String>
)
