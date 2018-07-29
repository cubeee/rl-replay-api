package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.fasterxml.jackson.annotation.JsonProperty

data class HeaderBody (
    @JsonProperty("engine_version") val engineVersion: Int,
    @JsonProperty("label") val label: String,
    @JsonProperty("licensee_version") val licenseeVersion: Int,
    @JsonProperty("patch_version") val patchVersion: Int,
    @JsonProperty("properties") val properties: Properties
)