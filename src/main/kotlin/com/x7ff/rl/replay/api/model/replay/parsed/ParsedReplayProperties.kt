package com.x7ff.rl.replay.api.model.replay.parsed

import com.fasterxml.jackson.annotation.JsonProperty

data class ParsedReplayProperties(
    @JsonProperty("TeamSize") val teamSize: Int,
    @JsonProperty("Team0Score") val team0Score: Int?,
    @JsonProperty("Team1Score") val team1Score: Int?,
    @JsonProperty("Goals") val goals: List<ParsedReplayGoal>,
    @JsonProperty("PlayerStats") val playerStats: List<ParsedReplayPlayerStats>,
    @JsonProperty("ReplayName") val replayName: String?,
    @JsonProperty("RecordFPS") val recordFps: Int,
    @JsonProperty("Date") val date: String,
    @JsonProperty("NumFrames") val frames: Int,
    @JsonProperty("PlayerName") val recorder: String
)