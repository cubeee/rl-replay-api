package com.x7ff.rl.replay.api.model.replay.parsed

import com.x7ff.rl.replay.api.model.replay.Player

data class ParsedPlayer(
    var id: Int,
    val name: String,
    val onlineId: String,
    val score: Int,
    val goals: Int,
    val assists: Int,
    val saves: Int,
    val shots: Int,
    var cameraSettings: ParsedReplayCameraSettings,
    var steeringSensitivity: Float,
    val actorIds: MutableSet<Int>
) {
    fun toPlayer(): Player {
        return Player(
            id = id,
            name = name,
            onlineId = onlineId,
            score = score,
            goals = goals,
            assists = assists,
            saves = saves,
            shots = shots,
            cameraSettings = cameraSettings.toCameraSettings(),
            steeringSensitivity = steeringSensitivity
        )
    }
}