package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.squareup.moshi.Json

data class ReplicationActor (
    @Json(name = "spawned") val spawn: ActorSpawn?,
    @Json(name = "updated") val updates: List<ActorUpdate>?
)