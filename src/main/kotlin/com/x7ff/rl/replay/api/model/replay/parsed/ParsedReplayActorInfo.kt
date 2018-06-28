package com.x7ff.rl.replay.api.model.replay.parsed

import com.squareup.moshi.Json

data class ParsedReplayActorInfo(
    @Json(name = "ActorId") val actorId: Int
)