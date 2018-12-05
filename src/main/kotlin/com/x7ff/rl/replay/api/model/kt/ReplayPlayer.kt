package com.x7ff.rl.replay.api.model.kt

import com.x7ff.rl.replay.api.model.replay.CameraSettings
import com.x7ff.rl.replay.api.model.replay.Player

data class ReplayPlayer(
    var id: Long = -1,
    val name: String,
    val onlineId: Long,
    val score: Int,
    val goals: Int,
    val assists: Int,
    val saves: Int,
    val shots: Int,
    val team: Int,
    var cameraSettings: CameraSettings = CameraSettings.Default,
    var steeringSensitivity: Float = 1.0f,
    val actorIds: MutableSet<Int> = mutableSetOf()
) {
    fun toPlayer(): Player {
        return Player(
            id = id,
            name = name,
            onlineId = onlineId.toString(),
            score = score,
            goals = goals,
            assists = assists,
            saves = saves,
            shots = shots,
            cameraSettings = cameraSettings,
            steeringSensitivity = steeringSensitivity
        )
    }
}