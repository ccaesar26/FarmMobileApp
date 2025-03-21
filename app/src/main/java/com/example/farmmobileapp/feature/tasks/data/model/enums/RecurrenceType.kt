package com.example.farmmobileapp.feature.tasks.data.model.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
enum class RecurrenceType {
    None,
    Daily,
    Weekly,
    Monthly,
    Yearly
}

object RecurrenceTypeSerializer : KSerializer<RecurrenceType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("RecurrenceType", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: RecurrenceType) {
        encoder.encodeInt(value.ordinal) // Serialize to integer
    }

    override fun deserialize(decoder: Decoder): RecurrenceType {
        return when (val value = decoder.decodeInt()) {
            0 -> RecurrenceType.None
            1 -> RecurrenceType.Daily
            2 -> RecurrenceType.Weekly
            3 -> RecurrenceType.Monthly
            4 -> RecurrenceType.Yearly
            else -> RecurrenceType.None // Default or handle error
        }
    }
}