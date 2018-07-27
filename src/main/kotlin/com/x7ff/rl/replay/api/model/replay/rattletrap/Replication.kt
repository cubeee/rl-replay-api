package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.squareup.moshi.Json

data class Replication (
    @Json(name = "actor_id") val actorId: ReplicationActorId,
    @Json(name = "value") val actor: ReplicationActor
)