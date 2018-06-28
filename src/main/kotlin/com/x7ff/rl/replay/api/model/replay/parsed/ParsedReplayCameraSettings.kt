package com.x7ff.rl.replay.api.model.replay.parsed

import com.squareup.moshi.Json
import com.x7ff.rl.replay.api.model.replay.CameraSettings

data class ParsedReplayCameraSettings(
    @Json(name = "FieldOfView") private val fieldOfView: Int,
    @Json(name = "Height") private val height: Int,
    @Json(name = "Pitch") private val pitch: Int,
    @Json(name = "Distance") private val distance: Int,
    @Json(name = "Stiffness") private val stiffness: Float,
    @Json(name = "SwivelSpeed") private val swivelSpeed: Float,
    @Json(name = "TransitionSpeed") private val transitionSpeed: Float
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