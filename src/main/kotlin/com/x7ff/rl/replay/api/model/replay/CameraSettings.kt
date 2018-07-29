package com.x7ff.rl.replay.api.model.replay

import com.fasterxml.jackson.annotation.JsonProperty

data class CameraSettings(
    @JsonProperty("fov") val fieldOfView: Int,
    @JsonProperty("height") val height: Int,
    @JsonProperty("pitch") val pitch: Int,
    @JsonProperty("distance") val distance: Int,
    @JsonProperty("stiffness") val stiffness: Float,
    @JsonProperty("swivel_speed") val swivelSpeed: Float,
    @JsonProperty("transition_speed") val transitionSpeed: Float
)