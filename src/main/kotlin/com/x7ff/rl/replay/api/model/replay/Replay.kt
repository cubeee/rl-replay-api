package com.x7ff.rl.replay.api.model.replay

import com.fasterxml.jackson.annotation.JsonProperty

data class Replay(
    @JsonProperty("name") val name: String?,
    @JsonProperty("id") val id: String?,
    @JsonProperty("teams") val teams: List<Team>,
    @JsonProperty("demolitions") val demolitions: List<Demolition>
)