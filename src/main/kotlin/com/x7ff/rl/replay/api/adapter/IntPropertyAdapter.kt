package com.x7ff.rl.replay.api.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.io.IOException

class IntPropertyAdapter: JsonAdapter<Int>() {

    @FromJson
    override fun fromJson(reader: JsonReader): Int {
        reader.beginObject()
        val name = reader.nextName()
        if (name == "int" || name == "byte") {
            val value = reader.nextInt()
            reader.endObject()
            return value
        }
        throw IOException("Invalid property kind")
    }

    override fun toJson(writer: JsonWriter?, value: Int?) = throw RuntimeException("Unsupported action")

}