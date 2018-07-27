package com.x7ff.rl.replay.api.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.io.IOException

class PairPropertyAdapter: JsonAdapter<Pair<String, String>>() {

    @FromJson
    override fun fromJson(reader: JsonReader): Pair<String, String> {
        reader.beginObject()
        val name = reader.nextName()
        if (name == "byte") {
            reader.beginArray()
            val key = reader.nextString()
            val value = reader.nextString()
            reader.endArray()
            reader.endObject()
            return Pair(key, value)
        }
        throw IOException("Invalid property kind")
    }

    override fun toJson(writer: JsonWriter?, value: Pair<String, String>?) = throw RuntimeException("Unsupported action")

}