package com.x7ff.rl.replay.api.model.kt

import com.x7ff.parser.replay.PropertyList
import com.x7ff.parser.replay.intPropertyOrZero
import com.x7ff.rl.replay.api.model.replay.Team

data class ReplayTeam(
    val id: Int,
    val name: String,
    val goals: Int,
    val players: List<ReplayPlayer>
) {
    fun toTeam(): Team {
        val players = players.map { it.toPlayer() }
        return Team(
            id = id,
            name = name,
            goals = goals,
            players = players
        )
    }

    companion object {
        fun List<ReplayPlayer>.createBlueTeam(properties: PropertyList) = createTeam(0, "Blue", properties, this)
        fun List<ReplayPlayer>.createOrangeTeam(properties: PropertyList) = createTeam(1, "Orange", properties, this)

        private fun createTeam(id: Int, name: String, properties: PropertyList, players: List<ReplayPlayer>): Team {
            return ReplayTeam(
                id = id,
                name = name,
                goals = properties.intPropertyOrZero("Team${id}Score"),
                players = players.filter { p -> p.team == id }
            ).toTeam()
        }
    }

}