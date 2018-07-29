package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.x7ff.rl.replay.api.adapter.PropertyValue
import com.x7ff.rl.replay.api.adapter.PropertyValuesDeserializer

data class Properties(
    @JsonProperty("keys") val keys: List<String>,
    @JsonProperty("last_key") val lastKey: String,
    @JsonProperty("value") @JsonDeserialize(using = PropertyValuesDeserializer::class) val values: Map<String, PropertyValue>
)