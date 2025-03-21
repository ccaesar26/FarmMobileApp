package com.example.farmmobileapp.feature.tasks.data.model.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
enum class TaskPriority {
    Low,
    Medium,
    High,
    Urgent
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