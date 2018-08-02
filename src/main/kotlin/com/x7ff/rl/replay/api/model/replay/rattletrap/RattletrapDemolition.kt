package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.x7ff.rl.replay.api.model.replay.Demolition
import com.x7ff.rl.replay.api.model.replay.DemolitionPlayer

data class RattletrapDemolition(
    private val attackerActorId: Int,
    private val victimActorId: Int
) {
    fun toDemolition(players: List<RattletrapPlayer>): Demolition? {
        val attacker = findPlayerByActorId(players, attackerActorId)
            ?.let { DemolitionPlayer(it.id, it.name) }
                ?: return null
        val victim = findPlayerByActorId(players, victimActorId)
            ?.let { DemolitionPlayer(it.id, it.name) }
                ?: return null
        return Demolition(
            attacker = attacker,
            victim = victim
        )
    }

    private fun findPlayerByActorId(players: List<RattletrapPlayer>, actorId: Int): RattletrapPlayer? {
        return players.firstOrNull { player -> player.id == actorId }
    }

}