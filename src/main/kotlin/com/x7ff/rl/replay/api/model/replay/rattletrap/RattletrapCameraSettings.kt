package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.fasterxml.jackson.annotation.JsonProperty
import com.x7ff.rl.replay.api.model.replay.CameraSettings

data class RattletrapCameraSettings (
    @JsonProperty("fov") val fov: Int,
    @JsonProperty("distance") val distance: Int,
    @JsonProperty("height") val height: Int,
    @JsonProperty("angle") val angle: Int,
    @JsonProperty("stiffness") val stiffness: Float,
    @JsonProperty("swivel_speed") val swivelSpeed: Float,
    @JsonProperty("transition_speed") val transitionSpeed: Float
) {
    fun toCameraSettings(): CameraSettings {
        return CameraSettings(
            fov = fov,
            distance = distance,
            height = height,
            angle = angle,
            stiffness = stiffness,
            swivelSpeed = swivelSpeed,
            transitionSpeed = transitionSpeed
        )
    }

    companion object {
        val Default = RattletrapCameraSettings(
            fov = 0,
            distance = 0,
            height = 0,
            angle = 0,
            stiffness = 0f,
            swivelSpeed = 0f,
            transitionSpeed = 0f
        )
    }
}