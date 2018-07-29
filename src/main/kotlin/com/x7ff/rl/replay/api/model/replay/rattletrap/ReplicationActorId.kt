package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.fasterxml.jackson.annotation.JsonProperty

data class ReplicationActorId (
    @JsonProperty("limit") val limit: Int,
    @JsonProperty("value") val id: Int
)