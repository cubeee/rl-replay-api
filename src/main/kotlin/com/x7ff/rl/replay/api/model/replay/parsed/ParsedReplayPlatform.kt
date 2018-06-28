package com.x7ff.rl.replay.api.model.replay.parsed

import com.squareup.moshi.Json

data class ParsedReplayPlatform(
    @Json(name = "Type") val type: String,
    @Json(name = "Value") val value: String
)