package com.x7ff.rl.replay.api.model.replay.parsed

import com.squareup.moshi.Json
import com.x7ff.rl.replay.api.model.replay.Replay

data class ParsedReplay(
    @Json(name = "Properties") val properties: ParsedReplayProperties,
    @Json(name = "Frames") val frames: List<ParsedReplayFrame>
) {
    fun toReplay(
        name: String,
        parsedTeams: List<ParsedTeam>,
        parsedDemolitions: MutableList<ParsedReplayDemolition>
    ): Replay {
        val teams = parsedTeams.map { it.toTeam() }
        val parsedPlayers = parsedTeams.flatMap { it.players }
        val demolitions = parsedDemolitions.mapNotNull { it.toDemolition(parsedPlayers) }

        return Replay(
            name = name,
            teams = teams,
            demolitions = demolitions
        )
    }

}