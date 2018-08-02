package com.x7ff.rl.replay.api.model.replay

import com.fasterxml.jackson.annotation.JsonProperty

data class Demolition(
    @JsonProperty("attacker") val attacker: DemolitionPlayer,
    @JsonProperty("victim") val victim: DemolitionPlayer
)