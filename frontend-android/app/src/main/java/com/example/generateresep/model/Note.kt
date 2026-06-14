package com.example.generateresep.model

data class Note(
    val id: Int,
    var title: String,
    var ingredients: List<String>,
    var isExpanded: Boolean = false,
    var isEditing: Boolean = false
)
