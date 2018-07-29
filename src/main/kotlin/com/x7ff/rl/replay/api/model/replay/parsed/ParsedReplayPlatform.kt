package com.x7ff.rl.replay.api.model.replay.parsed

import com.fasterxml.jackson.annotation.JsonProperty

data class ParsedReplayPlatform(
    @JsonProperty("Type") val type: String,
    @JsonProperty("Value") val value: String
)