package com.x7ff.rl.replay.api.model.replay.parsed

import com.fasterxml.jackson.annotation.JsonProperty

data class ParsedReplayPlayerStats(
    @JsonProperty("Name") val name: String,
    @JsonProperty("Platform") val platform: ParsedReplayPlatform,
    @JsonProperty("OnlineID") val onlineId: String,
    @JsonProperty("Team") val team: Int,
    @JsonProperty("Score") val score: Int,
    @JsonProperty("Goals") val goals: Int,
    @JsonProperty("Assists") val assists: Int,
    @JsonProperty("Saves") val saves: Int,
    @JsonProperty("Shots") val shots: Int
)