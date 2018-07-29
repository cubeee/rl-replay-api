package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.fasterxml.jackson.annotation.JsonProperty

data class Content (
    @JsonProperty("body") val body: ContentBody
)