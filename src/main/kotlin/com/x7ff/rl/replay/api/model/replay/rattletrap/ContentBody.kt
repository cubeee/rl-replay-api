package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.squareup.moshi.Json

data class ContentBody(
    @Json(name = "levels") val levels: List<String>,
    @Json(name = "frames") val frames: List<Frame>
)