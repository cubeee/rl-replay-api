package com.x7ff.rl.replay.api.model.replay.parsed

import com.squareup.moshi.Json

data class ParsedReplayGoal(
    @Json(name = "Time") val time: Double,
    @Json(name = "PlayerName") val player: String,
    @Json(name = "PlayerTeam") val team: Int
)