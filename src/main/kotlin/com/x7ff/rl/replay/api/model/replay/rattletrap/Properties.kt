package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.squareup.moshi.Json
import com.x7ff.rl.replay.api.adapter.PropertyValue

data class Properties(
    @Json(name = "keys") val keys: List<String>,
    @Json(name = "last_key") val lastKey: String,
    @Json(name = "value") val values: Map<String, PropertyValue>
)