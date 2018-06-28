package com.x7ff.rl.replay.api.model.replay.parsed

import com.squareup.moshi.Json

data class ParsedReplayFrame(
    @Json(name = "Time") val time: Double,
    @Json(name = "ActorUpdates") val actorUpdates: List<ParsedReplayFrameActorUpdate>
)