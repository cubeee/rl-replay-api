package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.fasterxml.jackson.annotation.JsonProperty

data class ContentBody(
    @JsonProperty("levels") val levels: List<String>,
    @JsonProperty("frames") val frames: List<Frame>
)