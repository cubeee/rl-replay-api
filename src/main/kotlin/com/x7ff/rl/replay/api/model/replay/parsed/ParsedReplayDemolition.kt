package com.x7ff.rl.replay.api.model.replay.parsed

import com.squareup.moshi.Json
import com.x7ff.rl.replay.api.model.replay.Demolition
import com.x7ff.rl.replay.api.model.replay.DemolitionPlayer

data class ParsedReplayDemolition(
    @Json(name = "AttackerActorId") private val attackerActorId: Int,
    @Json(name = "VictimActorId") private val victimActorId: Int
) {
    fun toDemolition(parsedPlayers: List<ParsedPlayer>): Demolition? {
        val attacker = findPlayerByActorId(parsedPlayers, attackerActorId)
            ?.let { DemolitionPlayer(it.id, it.name) }
                ?: return null
        val victim = findPlayerByActorId(parsedPlayers, victimActorId)
            ?.let { DemolitionPlayer(it.id, it.name) }
                ?: return null
        return Demolition(
            attacker = attacker,
            victim = victim
        )
    }

    private fun findPlayerByActorId(players: List<ParsedPlayer>, actorId: Int): ParsedPlayer? {
        return players.firstOrNull { player -> player.id == actorId }
    }

}