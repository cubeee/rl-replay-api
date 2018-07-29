package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.fasterxml.jackson.annotation.JsonProperty

data class Frame (
    @JsonProperty("delta") val delta: Float,
    @JsonProperty("replications") val replications: List<Replication>
)