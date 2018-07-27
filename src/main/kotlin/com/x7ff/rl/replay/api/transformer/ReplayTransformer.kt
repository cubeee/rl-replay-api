package com.x7ff.rl.replay.api.transformer

import com.x7ff.rl.replay.api.model.replay.Replay
import com.x7ff.rl.replay.api.model.replay.parsed.*

typealias ParsedPlayers = MutableMap<String, ParsedPlayer>
typealias CarBodies = MutableMap<Int, MutableList<Int>>

class ReplayTransformer {

    fun transform(parsedReplay: ParsedReplay): Replay {
        val teams = listOf(ParsedTeam.Blue, ParsedTeam.Red)
        val players = mutableMapOf<String, ParsedPlayer>()
        val demolitions = mutableListOf<ParsedReplayDemolition>()

        val replayName = parsedReplay.properties.replayName ?: "N/A"

        // Create players and assign them to teams
        for (stats in parsedReplay.properties.playerStats) {
            val player = ParsedPlayer(
                id = -1,
                name = stats.name,
                onlineId = stats.onlineId,
                score = stats.score,
                goals = stats.goals,
                assists = stats.assists,
                saves = stats.saves,
                shots = stats.shots,
                cameraSettings = ParsedReplayCameraSettings.Default,
                steeringSensitivity = 1.0f,
                actorIds = mutableSetOf()
            )

            players[player.name] = player
            teams[stats.team].players.add(player)
        }

        // Filter updates with no type or class, they are mostly position updates and we don't care about them
        val actorUpdates = parsedReplay.frames
            .flatMap { it.actorUpdates }
            .filter { it.demolition != null || (it.typeName != null && it.className != null) }

        val actorCarBodies = mutableMapOf<Int, MutableList<Int>>()

        for (update in actorUpdates) {
            when {
                // Find player information
                update.typeName == "TAGame.Default__PRI_TA" -> {
                    update.playerName?.let { name ->
                        val player = players[name]
                        player?.id = update.id

                        player?.apply {
                            id = update.id
                            steeringSensitivity = update.steeringSensitivity ?: 1.0f
                        }
                    }
                }
                // Map car body actor ids to player actor ids
                update.typeName == "Archetypes.Car.Car_Default" && update.playerActor != null -> {
                    actorCarBodies.addBody(update.playerActor.actorId, update.id)
                }

                // Save demolition data
                update.demolition != null -> {
                    demolitions.add(update.demolition)
                }
            }
        }

        // Map car body actor ids to players
        actorCarBodies.forEach { actorId, bodyIds ->
            players
                .filter { it.value.id == actorId }
                .onEach { it.value.actorIds.addAll(bodyIds) }
        }

        for (update in actorUpdates) {
            when {
                // Find camera settings for players
                update.cameraSettings != null && update.cameraActor != null -> {
                    val player = players.findByActorId(update.cameraActor.actorId)
                    player?.let {
                        player.cameraSettings = update.cameraSettings
                    }
                }
            }
        }
        return parsedReplay.toReplay(replayName, teams, demolitions)
    }

    companion object {
        fun ParsedPlayers.findByActorId(actorId: Int): ParsedPlayer? {
            return values.find { player -> player.id == actorId }
        }

        fun CarBodies.addBody(actorId: Int, bodyId: Int) {
            val list = getOrPut(actorId) { mutableListOf() }
            list.add(bodyId)
        }

    }

}
