package com.example.farmmobileapp.feature.tasks.data.model

import com.mapbox.geojson.Point
import kotlinx.serialization.Serializable

@Serializable
data class Field(
    val id: String,
    var farmId: String,
    val name: String,
    val boundary: Polygon
)

@Serializable
data class Polygon(
    val type: String = "Polygon", // Assuming type is always "Polygon"
    val coordinates: List<List<List<Double>>> = emptyList() // Represent coordinates as List of Lists of Doubles
)

fun Polygon.toMapboxPolygon(): List<List<Point>> {
    return coordinates.map { ring ->
        ring.map { coordinate ->
            Point.fromLngLat(coordinate[0], coordinate[1])
        }
    }
}

fun Polygon.computeCenter(): Point? {
    val coordinates = this.coordinates

    val allPoints = coordinates.flatten()
    val sumX = allPoints.sumOf { it[0] }
    val sumY = allPoints.sumOf { it[1] }
    val count = allPoints.size

    return Point.fromLngLat(sumX / count, sumY / count)
}