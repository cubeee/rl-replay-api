package com.x7ff.rl.replay.api.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.ToJson
import com.x7ff.rl.replay.api.model.replay.rattletrap.Properties
import java.io.IOException

typealias PropertyValue = Any

class PropertyValuesAdapter {

    private val intPropertyAdapter by lazy { IntPropertyAdapter() }
    private val floatPropertyAdapter by lazy { FloatPropertyAdapter() }
    private val longPropertyAdapter by lazy { LongPropertyAdapter() }
    private val booleanPropertyAdapter by lazy { BooleanPropertyAdapter() }
    private val strPropertyAdapter by lazy { StrPropertyAdapter() }
    private val pairPropertyAdapter by lazy { PairPropertyAdapter() }

    @FromJson
    fun fromJson(reader: JsonReader, propertiesDelegate: JsonAdapter<List<Properties>>): PropertyValue {
        reader.beginObject()

        var kind: String? = null
        var value = Any()

        while (reader.hasNext()) {
            val key = reader.nextName()
            when (key) {
                "kind" -> kind = reader.nextString()
                "size" -> reader.nextInt()
                "value" -> {
                    value = when(kind) {
                        "IntProperty" -> intPropertyAdapter.fromJson(reader)
                        "FloatProperty" -> floatPropertyAdapter.fromJson(reader)
                        "QWordProperty" -> longPropertyAdapter.fromJson(reader)
                        "StrProperty", "NameProperty" -> strPropertyAdapter.fromJson(reader)
                        "ByteProperty" -> pairPropertyAdapter.fromJson(reader)
                        "BoolProperty" -> booleanPropertyAdapter.fromJson(reader)
                        "ArrayProperty" -> ArrayPropertyAdapter(propertiesDelegate).fromJson(reader)
                        else -> throw IOException("Unhandled kind: $kind")
                    }
                }
            }
        }
        reader.endObject()

        return value
    }

    @ToJson
    fun toJson(propertyValues: Any): String = throw RuntimeException("Unsupported action")

}