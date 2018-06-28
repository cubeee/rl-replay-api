package com.x7ff.rl.replay.api.model.replay.parsed

import com.squareup.moshi.Json

data class ParsedReplayProperties(
    @Json(name = "TeamSize") val teamSize: Int,
    @Json(name = "Team0Score") val team0Score: Int?,
    @Json(name = "Team1Score") val team1Score: Int?,
    @Json(name = "Goals") val goals: List<ParsedReplayGoal>,
    @Json(name = "PlayerStats") val playerStats: List<ParsedReplayPlayerStats>,
    @Json(name = "ReplayName") val replayName: String?,
    @Json(name = "RecordFPS") val recordFps: Int,
    @Json(name = "Date") val date: String,
    @Json(name = "NumFrames") val frames: Int,
    @Json(name = "PlayerName") val recorder: String
)