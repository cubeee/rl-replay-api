package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.fasterxml.jackson.annotation.JsonProperty

data class Replication (
    @JsonProperty("actor_id") val actorId: ReplicationActorId,
    @JsonProperty("value") val actor: ReplicationActor
)