package com.x7ff.rl.replay.api.model.replay.parsed

import com.fasterxml.jackson.annotation.JsonProperty
import com.x7ff.rl.replay.api.model.replay.Replay

data class ParsedReplay(
    @JsonProperty("Properties") val properties: ParsedReplayProperties,
    @JsonProperty("Frames") val frames: List<ParsedReplayFrame>
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