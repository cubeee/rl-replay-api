package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.squareup.moshi.Json

data class HeaderBody (
    @Json(name = "engine_version") val engineVersion: Int,
    @Json(name = "label") val label: String,
    @Json(name = "licensee_version") val licenseeVersion: Int,
    @Json(name = "patch_version") val patchVersion: Int,
    @Json(name = "properties") val properties: Properties
)