package com.x7ff.rl.replay.api.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.io.IOException

class UniqueIdPropertyAdapter: JsonAdapter<Pair<Long, Long>>() {

    @FromJson
    override fun fromJson(reader: JsonReader): Pair<Long, Long> {
        reader.beginObject()
        val name = reader.nextName()
        if (name == "unique_id") {
            reader.beginObject()
            var localId = -1L
            var remoteId = 0L
            while (reader.hasNext()) {
                val idField = reader.nextName()
                when (idField) {
                    "local_id" -> localId = reader.nextLong()
                    "remote_id" -> {
                        reader.beginObject()
                        reader.nextName() // TODO: check type?
                        remoteId = reader.nextLong()
                        reader.endObject()
                    }
                    else -> reader.skipValue()
                }
            }
            reader.endObject()
            reader.endObject()
            return Pair(localId, remoteId)
        }
        throw IOException("Invalid property kind")
    }

    override fun toJson(writer: JsonWriter?, value: Pair<Long, Long>?) = throw RuntimeException("Unsupported action")

}