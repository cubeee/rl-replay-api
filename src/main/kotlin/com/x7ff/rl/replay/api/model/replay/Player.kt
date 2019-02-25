package com.x7ff.rl.replay.api.model.replay

import com.fasterxml.jackson.annotation.JsonProperty

data class Player(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String,
    @JsonProperty("online_id") val onlineId: String,
    @JsonProperty("score") val score: Int,
    @JsonProperty("goals") val goals: Int,
    @JsonProperty("assists") val assists: Int,
    @JsonProperty("saves") val saves: Int,
    @JsonProperty("shots") val shots: Int,
    @JsonProperty("camera_settings") val cameraSettings: CameraSettings,
    @JsonProperty("steering_sensitivity") val steeringSensitivity: Float
)