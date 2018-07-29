package com.x7ff.rl.replay.api.model.replay.parsed

import com.fasterxml.jackson.annotation.JsonProperty

data class ParsedReplayUniqueId(
    @JsonProperty("SteamID64") val steamId: Long,
    @JsonProperty("SteamProfileUrl") val steamProfileUrl: String,
    @JsonProperty("Type") val type: Int,
    @JsonProperty("Id") val id: String,
    @JsonProperty("PlayerNumber") val playerNumber: Int
)