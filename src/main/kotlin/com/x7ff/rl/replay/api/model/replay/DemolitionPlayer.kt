package com.x7ff.rl.replay.api.model.replay

import com.fasterxml.jackson.annotation.JsonProperty

data class DemolitionPlayer(
    @JsonProperty("id") val id: Long,
    @JsonProperty("name") val name: String
)