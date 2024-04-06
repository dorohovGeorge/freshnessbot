package org.coliver.enterprise.serialize

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDateTime::class)
class LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toString())
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return Instant.ofEpochMilli(decoder.decodeString().toLong()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
