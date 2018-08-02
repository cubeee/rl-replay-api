package com.x7ff.rl.replay.api.model.replay

import com.fasterxml.jackson.annotation.JsonProperty

data class CameraSettings (
    @JsonProperty("field_of_view") val fov: Int,
    @JsonProperty("distance") val distance: Int,
    @JsonProperty("height") val height: Int,
    @JsonProperty("angle") val angle: Int,
    @JsonProperty("stiffness") val stiffness: Float,
    @JsonProperty("swivel_speed") val swivelSpeed: Float,
    @JsonProperty("transition_speed") val transitionSpeed: Float
)