package com.x7ff.rl.replay.api.model.replay.parsed

import com.x7ff.rl.replay.api.model.replay.Team

data class ParsedTeam(
    val id: Int,
    val name: String,
    val players: MutableSet<ParsedPlayer> = mutableSetOf()
) {
    companion object {
        private fun blueTeam() = ParsedTeam(0, "Blue")
        private fun redTeam() = ParsedTeam(1, "Red")

        fun createTeams() = listOf(blueTeam(), redTeam())
    }

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