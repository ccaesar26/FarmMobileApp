package com.example.farmmobileapp.feature.reports.data.model.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ReportStatusSerializer::class)
enum class ReportStatus {
    Submitted,   // Initial state when created by a worker
    Seen,        // When the manager has viewed the report
    InProgress,  // Manager is taking action
    Resolved,    // The issue reported has been addressed
    Closed       // No further action needed, different from Resolved
}

object ReportStatusSerializer : KSerializer<ReportStatus> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ReportStatus", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: ReportStatus) {
        encoder.encodeInt(value.ordinal)
    }

    override fun deserialize(decoder: Decoder): ReportStatus {
        return when (val index = decoder.decodeInt()) {
            0 -> ReportStatus.Submitted
            1 -> ReportStatus.Seen
            2 -> ReportStatus.InProgress
            3 -> ReportStatus.Resolved
            4 -> ReportStatus.Closed
            else -> {
                System.err.println("Unknown ReportStatus ordinal: $index. Defaulting to Submitted.")
                ReportStatus.Submitted // Or throw an exception if preferred
            }
        }
    }
}
