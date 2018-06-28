package com.x7ff.rl.replay.api.model.replay.parsed

import com.x7ff.rl.replay.api.model.replay.Team

sealed class ParsedTeam(
    val id: Int,
    val name: String,
    val players: MutableList<ParsedPlayer> = mutableListOf()
) {
    object Blue: ParsedTeam(0, "Blue")
    object Red: ParsedTeam(1, "Red")

    fun toTeam(): Team {
        val players = players.map {
            it.toPlayer()
        }
        return Team(
            id = id,
            name = name,
            players = players
        )
    }

}