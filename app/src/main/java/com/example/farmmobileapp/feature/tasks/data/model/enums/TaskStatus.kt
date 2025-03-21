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
    Completed,
    OnHold
}

object TaskStatusSerializer : KSerializer<TaskStatus> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("TaskStatus", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: TaskStatus) {
        encoder.encodeInt(value.ordinal) // Serialize to integer
    }

    override fun deserialize(decoder: Decoder): TaskStatus {
        return when (val value = decoder.decodeInt()) {
            0 -> TaskStatus.ToDo
            1 -> TaskStatus.InProgress
            2 -> TaskStatus.Completed
            3 -> TaskStatus.OnHold
            else -> TaskStatus.ToDo // Default or handle error
        }
    }
}