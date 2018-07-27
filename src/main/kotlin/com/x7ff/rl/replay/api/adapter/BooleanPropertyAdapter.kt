package com.x7ff.rl.replay.api.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.io.IOException

class BooleanPropertyAdapter: JsonAdapter<Boolean>() {

    @FromJson
    override fun fromJson(reader: JsonReader): Boolean {
        reader.beginObject()
        val name = reader.nextName()
        if (name == "bool") {
            val value = reader.nextInt() == 1
            reader.endObject()
            return value
        }
        throw IOException("Invalid property kind")
    }

    override fun toJson(writer: JsonWriter?, value: Boolean?) = throw RuntimeException("Unsupported action")

}