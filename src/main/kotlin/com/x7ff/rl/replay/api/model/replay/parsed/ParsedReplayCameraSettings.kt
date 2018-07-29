package com.x7ff.rl.replay.api.model.replay.parsed

import com.fasterxml.jackson.annotation.JsonProperty
import com.x7ff.rl.replay.api.model.replay.CameraSettings

data class ParsedReplayCameraSettings(
    @JsonProperty("FieldOfView") private val fieldOfView: Int,
    @JsonProperty("Height") private val height: Int,
    @JsonProperty("Pitch") private val pitch: Int,
    @JsonProperty("Distance") private val distance: Int,
    @JsonProperty("Stiffness") private val stiffness: Float,
    @JsonProperty("SwivelSpeed") private val swivelSpeed: Float,
    @JsonProperty("TransitionSpeed") private val transitionSpeed: Float
) {
    fun toCameraSettings(): CameraSettings {
        return CameraSettings(
            fieldOfView = fieldOfView,
            height = height,
            pitch = pitch,
            distance = distance,
            stiffness = stiffness,
            swivelSpeed = swivelSpeed,
            transitionSpeed = transitionSpeed
        )
    }

    companion object {
        val Default = ParsedReplayCameraSettings(
            fieldOfView = 0,
            height = 0,
            pitch = 0,
            distance = 0,
            stiffness = 0f,
            swivelSpeed = 0f,
            transitionSpeed = 0f
        )
    }
}