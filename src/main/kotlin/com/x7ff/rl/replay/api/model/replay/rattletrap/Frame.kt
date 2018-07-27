package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.squareup.moshi.Json

data class Frame (
    @Json(name = "delta") val delta: Float,
    @Json(name = "replications") val replications: List<Replication>
)