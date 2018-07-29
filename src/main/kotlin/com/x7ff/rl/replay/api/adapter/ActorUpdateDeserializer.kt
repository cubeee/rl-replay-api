package com.x7ff.rl.replay.api.adapter

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.x7ff.rl.replay.api.model.replay.rattletrap.ActorUpdate
import com.x7ff.rl.replay.api.model.replay.rattletrap.ReplicationActorId

class ActorUpdateDeserializer: JsonDeserializer<ActorUpdate>() {

    private val objectMapper by lazy {
        ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    private val propertyValueDeserializer = PropertyValueDeserializer()

    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext?): ActorUpdate {
        val tree: JsonNode = parser.codec.readTree(parser)

        val id: ReplicationActorId = objectMapper.readValue(tree.get("id").toString(), ReplicationActorId::class.java)
        val name = tree.get("name").asText()
        val value = propertyValueDeserializer.deserializeValue(name, tree.get("value"))

        return ActorUpdate(id, name, value)
    }

}