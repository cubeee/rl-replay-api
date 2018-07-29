package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.fasterxml.jackson.annotation.JsonProperty

data class CameraSettings (
    @JsonProperty("angle") val angle: Int,
    @JsonProperty("distance") val distance: Int,
    @JsonProperty("fov") val fov: Int,
    @JsonProperty("height") val height: Int,
    @JsonProperty("stiffness") val stiffness: Float,
    @JsonProperty("swivel_speed") val swivelSpeed: Float,
    @JsonProperty("transition_speed") val transitionSpeed: Float
)