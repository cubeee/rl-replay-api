package com.x7ff.rl.replay.api.model.replay.parsed

import com.fasterxml.jackson.annotation.JsonProperty

data class ParsedReplayFrame(
    @JsonProperty("Time") val time: Double,
    @JsonProperty("ActorUpdates") val actorUpdates: List<ParsedReplayFrameActorUpdate>
)