package com.x7ff.rl.replay.api.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.x7ff.rl.replay.api.model.replay.rattletrap.Properties
import java.io.IOException

class ArrayPropertyAdapter(
    private val propertiesAdapter: JsonAdapter<List<Properties>>
): JsonAdapter<List<Properties>>() {

    @FromJson
    override fun fromJson(reader: JsonReader): List<Properties> {
        reader.beginObject()
        val name = reader.nextName()
        if (name == "array") {
            val value = propertiesAdapter.fromJson(reader)!!
            reader.endObject()
            return value
        }
        throw IOException("Invalid property kind")
    }

    override fun toJson(writer: JsonWriter?, value: List<Properties>?) = throw RuntimeException("Unsupported action")

}