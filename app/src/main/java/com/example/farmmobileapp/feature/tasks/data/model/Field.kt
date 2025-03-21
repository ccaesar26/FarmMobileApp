package com.example.farmmobileapp.feature.tasks.data.model

data class Field(
    val id: String,
    var farmId: String,
    val name: String,
    val boundary: String? = null
)
