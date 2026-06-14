package com.example.generateresep.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val cookingTime: String,
    val difficulty: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val createdAt: Long = System.currentTimeMillis()
)
