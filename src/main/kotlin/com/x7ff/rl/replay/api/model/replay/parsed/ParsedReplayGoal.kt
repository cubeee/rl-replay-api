package com.x7ff.rl.replay.api.model.replay.parsed

import com.fasterxml.jackson.annotation.JsonProperty

data class ParsedReplayGoal(
    @JsonProperty("Time") val time: Double,
    @JsonProperty("PlayerName") val player: String,
    @JsonProperty("PlayerTeam") val team: Int
)