package com.example.farmmobileapp.feature.tasks.data.model.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
enum class TaskStatus {
    ToDo,
    InProgress,
    OnHold,
    Completed,
}

object TaskStatusSerializer : KSerializer<TaskStatus> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("status", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: TaskStatus) {
        encoder.encodeInt(value.ordinal) // Serialize to integer
    }

    override fun deserialize(decoder: Decoder): TaskStatus {
        return when (decoder.decodeInt()) {
            0 -> TaskStatus.ToDo
            1 -> TaskStatus.InProgress
            2 -> TaskStatus.OnHold
            3 -> TaskStatus.Completed
            else -> TaskStatus.ToDo // Default or handle error
        }
    }
}