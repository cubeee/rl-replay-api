package com.x7ff.rl.replay.api.model.replay.parsed

import com.fasterxml.jackson.annotation.JsonProperty

data class ParsedReplayActorInfo(
    @JsonProperty("ActorId") val actorId: Int
)