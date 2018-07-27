package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.squareup.moshi.Json

data class Header (
    @Json(name = "body") val body: HeaderBody
)