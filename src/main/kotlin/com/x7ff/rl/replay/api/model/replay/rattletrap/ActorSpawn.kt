package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.fasterxml.jackson.annotation.JsonProperty

data class ActorSpawn (
    @JsonProperty("class_name") val className: String,
    @JsonProperty("flag") val flag: Boolean,
    @JsonProperty("name") val name: String,
    @JsonProperty("name_index") val nameIndex: Int,
    @JsonProperty("object_id") val objectId: Int,
    @JsonProperty("object_name") val objectName: String
)