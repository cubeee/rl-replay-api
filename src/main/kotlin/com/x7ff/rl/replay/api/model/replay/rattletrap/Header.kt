package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.fasterxml.jackson.annotation.JsonProperty

data class Header (
    @JsonProperty("body") val body: HeaderBody
)