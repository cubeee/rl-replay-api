package com.x7ff.rl.replay.api.model.replay

data class Player(
    var id: Int,
    val name: String,
    val onlineId: String,
    val score: Int,
    val goals: Int,
    val assists: Int,
    val saves: Int,
    val shots: Int,
    var cameraSettings: CameraSettings,
    var steeringSensitivity: Float
)