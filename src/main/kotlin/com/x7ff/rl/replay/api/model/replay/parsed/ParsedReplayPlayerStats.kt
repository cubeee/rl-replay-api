package com.x7ff.rl.replay.api.model.replay.parsed

import com.squareup.moshi.Json

data class ParsedReplayPlayerStats(
    @Json(name = "Name") val name: String,
    @Json(name = "Platform") val platform: ParsedReplayPlatform,
    @Json(name = "OnlineID") val onlineId: String,
    @Json(name = "Team") val team: Int,
    @Json(name = "Score") val score: Int,
    @Json(name = "Goals") val goals: Int,
    @Json(name = "Assists") val assists: Int,
    @Json(name = "Saves") val saves: Int,
    @Json(name = "Shots") val shots: Int
)