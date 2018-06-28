package com.x7ff.rl.replay.api.model.replay.parsed

import com.squareup.moshi.Json

data class ParsedReplayUniqueId(
    @Json(name = "SteamID64") val steamId: Long,
    @Json(name = "SteamProfileUrl") val steamProfileUrl: String,
    @Json(name = "Type") val type: Int,
    @Json(name = "Id") val id: String,
    @Json(name = "PlayerNumber") val playerNumber: Int
)