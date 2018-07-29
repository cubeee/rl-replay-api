package com.x7ff.rl.replay.api.adapter

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode

class PropertyValuesDeserializer: JsonDeserializer<Map<String, PropertyValue?>>() {

    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext?): Map<String, PropertyValue?> {
        val tree: JsonNode = parser.readValueAsTree()
        val valueDeserializer = PropertyValueDeserializer()
        val map = mutableMapOf<String, PropertyValue?>()

        tree.fields().forEach { entry ->
            val name = entry.key
            val valueNode = entry.value

            map[name] = valueDeserializer.deserialize(valueNode)
        }
        return map.toMap()
    }

}