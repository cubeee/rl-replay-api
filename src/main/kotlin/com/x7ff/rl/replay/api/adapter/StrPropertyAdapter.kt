package com.x7ff.rl.replay.api.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.io.IOException

class StrPropertyAdapter: JsonAdapter<String>() {

    @FromJson
    override fun fromJson(reader: JsonReader): String {
        reader.beginObject()
        val name = reader.nextName()
        if (name == "str" || name == "name" || name == "string") {
            val value = reader.nextString()
            reader.endObject()
            return value
        }
        throw IOException("Invalid property kind '$name'")
    }

    override fun toJson(writer: JsonWriter?, value: String?) = throw RuntimeException("Unsupported action")

}