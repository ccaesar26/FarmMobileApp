package com.example.farmmobileapp.util.extensions

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String.formatIsoInstantToLocal(
    outputFormat: String = "dd MMM yyyy, HH:mm",
    outputLocale: Locale = Locale.getDefault()
): String? {
    return try {
        val instant = Instant.parse(this)
        val zoneId = ZoneId.systemDefault()
        val zonedDateTime = instant.atZone(zoneId)

        val formatter = DateTimeFormatter.ofPattern(outputFormat, outputLocale)
        formatter.format(zonedDateTime)
    } catch (e: Exception) {
        System.err.println("Error formatting ISO instant '$this': ${e.message}")
        null
    }
}
