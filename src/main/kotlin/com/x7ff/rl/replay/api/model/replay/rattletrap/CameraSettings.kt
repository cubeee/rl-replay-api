package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.squareup.moshi.Json

data class CameraSettings (
    @Json(name = "angle") val angle: Int,
    @Json(name = "distance") val distance: Int,
    @Json(name = "fov") val fov: Int,
    @Json(name = "height") val height: Int,
    @Json(name = "stiffness") val stiffness: Float,
    @Json(name = "swivel_speed") val swivelSpeed: Float,
    @Json(name = "transition_speed") val transitionSpeed: Float
)