package com.x7ff.rl.replay.api.adapter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.x7ff.rl.replay.api.model.replay.rattletrap.Demolition
import com.x7ff.rl.replay.api.model.replay.rattletrap.ProfileSettings
import com.x7ff.rl.replay.api.model.replay.rattletrap.Properties
import java.io.IOException

typealias PropertyValue = Any

class PropertyValueDeserializer {

    private val objectMapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private val unmappedTypes = listOf(
        "rigid_body_state",
        "team_paint",
        "reservation",
        "party_leader",
        "loadouts_online",
        "loadouts",
        "pickup", // TODO: map?
        "location",
        "enum",
        "stat_event",
        "extended_explosion"
    )

    fun deserialize(node: JsonNode): PropertyValue? {
        val name = node.get("name")?.asText()
        val valueNode = node.get("value")
        return deserializeValue(name, valueNode)
    }

    fun deserializeValue(name: String?, valueNode: JsonNode): PropertyValue? {
        val entry = valueNode.fields().next()
        val type = entry.key
        val value = entry.value

        if (name == "TAGame.CameraSettingsActor_TA:ProfileSettings") {
            return objectMapper.readValue(valueNode.toString(), object : TypeReference<ProfileSettings>(){})
        }

        return when(type) {
            "int" -> value.asInt()
            "q_word" -> value.asLong()
            "byte" -> value.asInt().toByte()
            "float" -> value.asDouble().toFloat()
            "bool", "boolean" -> value.asBoolean()
            "flagged_int" -> value.get("int").asInt()
            "str", "name", "string" -> value.asText()
            "unique_id" -> {
                val localId = value.get("local_id").asLong()
                val remoteId = value.get("remote_id").get("steam").asLong()
                Pair(localId, remoteId)
            }
            "demolish" -> objectMapper.readValue(value.toString(), object : TypeReference<Demolition>(){})
            "array" -> objectMapper.readValue(value.toString(), object : TypeReference<List<Properties>>(){})
            in unmappedTypes -> null
            else -> throw IOException("Invalid value type: $type")
        }
    }

}