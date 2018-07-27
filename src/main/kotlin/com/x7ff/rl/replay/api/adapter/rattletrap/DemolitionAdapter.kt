package com.x7ff.rl.replay.api.adapter.rattletrap

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.x7ff.rl.replay.api.model.replay.rattletrap.Demolition
import java.io.IOException

class DemolitionAdapter: JsonAdapter<Demolition>() {

    @FromJson
    override fun fromJson(reader: JsonReader): Demolition {
        reader.beginObject()
        val name = reader.nextName()
        if (name == "demolish") {
            var attackerActorId = 0
            var victimActorId = 0

            reader.beginObject()
            while (reader.hasNext()) {
                val key = reader.nextName()
                when (key) {
                    "attacker_actor_id" -> attackerActorId = reader.nextInt()
                    "victim_actor_id" -> victimActorId = reader.nextInt()
                    else -> reader.skipValue()
                }
            }
            reader.endObject()
            reader.endObject()
            return Demolition(attackerActorId, victimActorId)
        }
        throw IOException("Invalid object content")
    }

    override fun toJson(writer: JsonWriter?, value: Demolition?) = throw RuntimeException("Unsupported action")

}