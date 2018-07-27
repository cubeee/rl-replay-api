package com.x7ff.rl.replay.api.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.io.IOException

class LongPropertyAdapter: JsonAdapter<Long>() {

    @FromJson
    override fun fromJson(reader: JsonReader): Long {
        reader.beginObject()
        val name = reader.nextName()
        if (name == "q_word") {
            val value = reader.nextLong()
            reader.endObject()
            return value
        }
        throw IOException("Invalid property kind")
    }

    override fun toJson(writer: JsonWriter?, value: Long?) = throw RuntimeException("Unsupported action")

}