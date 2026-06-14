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