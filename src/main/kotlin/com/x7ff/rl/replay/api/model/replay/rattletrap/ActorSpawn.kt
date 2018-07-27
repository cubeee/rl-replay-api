package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.squareup.moshi.Json

data class ActorSpawn (
    @Json(name = "class_name") val className: String,
    @Json(name = "flag") val flag: Boolean,
    @Json(name = "name") val name: String,
    @Json(name = "name_index") val nameIndex: Int,
    @Json(name = "object_id") val objectId: Int,
    @Json(name = "object_name") val objectName: String
)