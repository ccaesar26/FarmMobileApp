package com.example.farmmobileapp.feature.tasks.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Field(
    val id: String,
    var farmId: String,
    val name: String,
    val boundary: Polygon? = null
)

@Serializable
data class Polygon(
    val type: String? = "Polygon", // Assuming type is always "Polygon"
    val coordinates: List<List<List<Double>>>? = null // Represent coordinates as List of Lists of Doubles
)