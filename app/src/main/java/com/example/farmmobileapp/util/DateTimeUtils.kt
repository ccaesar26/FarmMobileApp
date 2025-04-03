package com.example.farmmobileapp.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateTimeUtils {

    private val backendDateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME // Assumes backend sends ISO_DATE_TIME format
    private val userFriendlyDateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm") // Example: "Jul 20, 2024 10:30 AM"

    fun formatBackendDateTimeToUserFriendly(dateTimeString: String): String {
        return try {
            val dateTime = LocalDateTime.parse(dateTimeString, backendDateTimeFormatter)
            dateTime.format(userFriendlyDateFormatter)
        } catch (e: DateTimeParseException) {
            // Handle parsing error, log it, or return original string/N/A
            e.printStackTrace() // Log the exception for debugging
            dateTimeString // Return the original string in case of parsing error, or return "N/A"
        }
    }
}