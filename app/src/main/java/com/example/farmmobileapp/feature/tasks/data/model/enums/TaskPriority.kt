package com.example.farmmobileapp.feature.tasks.data.model.enums

import androidx.compose.ui.graphics.Color
import com.example.farmmobileapp.ui.theme.PrimeColors
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

interface Colorable {
    fun getColor(): Color
}

@Serializable
enum class TaskPriority : Colorable {
    Low {
        override fun getColor() = PrimeColors.Green.color500
    },
    Medium {
        override fun getColor() = PrimeColors.Yellow.color500
    },
    High {
        override fun getColor() = PrimeColors.Orange.color500
    },
    Urgent {
        override fun getColor() = PrimeColors.Red.color500
    }
}

object TaskPrioritySerializer : KSerializer<TaskPriority> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("TaskPriority", PrimitiveKind.INT) // Indicate it serializes from/to Int

    override fun serialize(encoder: Encoder, value: TaskPriority) {
        encoder.encodeInt(value.ordinal) // Serialize to integer (ordinal value) - if needed for serialization back to backend
    }

    override fun deserialize(decoder: Decoder): TaskPriority {
        return when (val value = decoder.decodeInt()) {
            0 -> TaskPriority.Low
            1 -> TaskPriority.Medium
            2 -> TaskPriority.High
            3 -> TaskPriority.Urgent
            else -> TaskPriority.Low // Default or handle error as needed, throw exception if invalid int is critical
        }
    }
}