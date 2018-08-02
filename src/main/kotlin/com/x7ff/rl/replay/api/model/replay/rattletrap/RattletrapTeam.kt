package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.x7ff.rl.replay.api.model.replay.Team

data class RattletrapTeam(
    val id: Int,
    val name: String,
    val players: MutableSet<RattletrapPlayer> = mutableSetOf()
) {
    companion object {
        private fun blueTeam() = RattletrapTeam(0, "Blue")
        private fun redTeam() = RattletrapTeam(1, "Red")

        fun createTeams() = listOf(
            blueTeam(),
            redTeam()
        )
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