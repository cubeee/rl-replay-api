package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.fasterxml.jackson.annotation.JsonProperty

data class ReplicationActor (
    @JsonProperty("spawned") val spawn: ActorSpawn?,
    @JsonProperty("updated") val updates: List<ActorUpdate>?
)