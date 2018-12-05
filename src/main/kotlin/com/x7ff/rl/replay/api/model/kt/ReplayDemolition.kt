package com.x7ff.rl.replay.api.model.kt

import com.x7ff.rl.replay.api.model.replay.Demolition
import com.x7ff.rl.replay.api.model.replay.DemolitionPlayer

data class ReplayDemolition(
    private val attackerActorId: Int,
    private val victimActorId: Int
) {
    fun toDemolition(players: List<ReplayPlayer>): Demolition? {
        val attacker = players.findByActorId(attackerActorId) ?: return null
        val victim = players.findByActorId(victimActorId) ?: return null
        return Demolition(attacker, victim)
    }

    private fun List<ReplayPlayer>.findByActorId(actorId: Int): DemolitionPlayer? {
        return filter { player -> player.id == actorId.toLong() }
            .map { player -> DemolitionPlayer(player.id, player.name) }
            .firstOrNull()
    }

}