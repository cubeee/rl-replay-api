package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.squareup.moshi.Json

data class ReplicationActorId (
    @Json(name = "limit") val limit: Int,
    @Json(name = "value") val id: Int
)