package com.x7ff.rl.replay.api.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.io.IOException

class FloatPropertyAdapter: JsonAdapter<Float>() {

    @FromJson
    override fun fromJson(reader: JsonReader): Float {
        reader.beginObject()
        val name = reader.nextName()
        if (name == "float") {
            val value = reader.nextDouble().toFloat()
            reader.endObject()
            return value
        }
        throw IOException("Invalid property kind")
    }

    override fun toJson(writer: JsonWriter?, value: Float?) = throw RuntimeException("Unsupported action")

}