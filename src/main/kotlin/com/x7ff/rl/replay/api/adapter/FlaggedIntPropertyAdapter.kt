package com.x7ff.rl.replay.api.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.io.IOException

class FlaggedIntPropertyAdapter: JsonAdapter<Int>() {

    @FromJson
    override fun fromJson(reader: JsonReader): Int {
        reader.beginObject()
        val name = reader.nextName()
        if (name == "flagged_int") {
            reader.beginObject()
            var value = -1
            while (reader.hasNext()) {
                val flaggedName = reader.nextName()
                when(flaggedName) {
                    "int" -> value = reader.nextInt()
                    else -> reader.skipValue()
                }
            }
            reader.endObject()
            reader.endObject()
            return value
        }
        throw IOException("Invalid property kind")
    }

    override fun toJson(writer: JsonWriter?, value: Int?) = throw RuntimeException("Unsupported action")

}