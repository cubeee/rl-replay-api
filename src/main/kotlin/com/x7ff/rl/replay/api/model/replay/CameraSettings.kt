package com.x7ff.rl.replay.api.model.replay

import com.squareup.moshi.Json

data class CameraSettings(
    @Json(name = "FieldOfView") val fieldOfView: Int,
    @Json(name = "Height") val height: Int,
    @Json(name = "Pitch") val pitch: Int,
    @Json(name = "Distance") val distance: Int,
    @Json(name = "Stiffness") val stiffness: Float,
    @Json(name = "SwivelSpeed") val swivelSpeed: Float,
    @Json(name = "TransitionSpeed") val transitionSpeed: Float
)