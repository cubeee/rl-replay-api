package com.x7ff.rl.replay.api.transformer

import com.x7ff.rl.replay.api.model.replay.Replay
import com.x7ff.rl.replay.api.model.replay.parsed.ParsedPlayer
import com.x7ff.rl.replay.api.model.replay.parsed.ParsedReplayCameraSettings
import com.x7ff.rl.replay.api.model.replay.parsed.ParsedReplayDemolition
import com.x7ff.rl.replay.api.model.replay.parsed.ParsedTeam
import com.x7ff.rl.replay.api.model.replay.rattletrap.Demolition
import com.x7ff.rl.replay.api.model.replay.rattletrap.ProfileSettings
import com.x7ff.rl.replay.api.model.replay.rattletrap.Properties
import com.x7ff.rl.replay.api.model.replay.rattletrap.RattletrapReplay

data class ActorInfo(
    val typeName: String,
    val className: String,
    val name: String,
    val id: Int,
    val values: MutableMap<String, Any?>
)

class RattletrapReplayTransformer {

    fun transform(parsedReplay: RattletrapReplay): Replay {
        val teams = ParsedTeam.createTeams()
        val players = mutableMapOf<String, ParsedPlayer>()
        val demolitions = mutableSetOf<ParsedReplayDemolition>()

        val actorIds = mutableListOf<Int>()
        val actors = mutableMapOf<Int, ActorInfo>()
        val playerCarIds = mutableMapOf<Int, Int>()
        val carPlayerIds = mutableMapOf<Int, Int>()

        val properties = parsedReplay.header.body.properties
        val playerStats = properties.values["PlayerStats"] as List<Properties>

        playerStats
            .map { statProperty -> statProperty.values }
            .onEach { stats ->
                val player = ParsedPlayer(
                    id = -1,
                    name = stats["Name"] as String,
                    onlineId = (stats["OnlineID"] as Long).toString(),
                    score = stats["Score"] as Int,
                    goals = stats["Goals"] as Int,
                    assists = stats["Assists"] as Int,
                    saves = stats["Saves"] as Int,
                    shots = stats["Shots"] as Int,
                    cameraSettings = ParsedReplayCameraSettings.Default,
                    steeringSensitivity = 1.0f,
                    actorIds = mutableSetOf()
                )
                val team = stats["Team"] as Int

                players[player.name] = player
                teams[team].players.add(player)
            }

        for (frame in parsedReplay.content.body.frames) {
            val replicationUpdates = frame.replications
                .filter { replication -> replication.actor.updates != null }

            frame.replications
                .filter { replication -> replication.actor.spawn != null }
                .onEach { spawn ->
                    val actorId = spawn.actorId.id

                    with(spawn.actor.spawn!!) {
                        actors[actorId] = ActorInfo(
                            typeName = objectName,
                            className = className,
                            name = name,
                            id = actorId,
                            values = mutableMapOf()
                        )
                    }
                }

            replicationUpdates.onEach { update ->
                val actorId = update.actorId.id
                actorIds.add(actorId)

                update.actor.updates?.forEach { actors[actorId]?.values?.put(it.name, it.value) }
            }

            for ((actorId, actorData) in actors) {
                val values = actorData.values

                val playerName = values["Engine.PlayerReplicationInfo:PlayerName"]
                val playerActorId = values["Engine.Pawn:PlayerReplicationInfo"]

                if (actorData.typeName == "TAGame.Default__PRI_TA" && actorData.className == "TAGame.PRI_TA"
                    && playerName is String) {

                    val uniqueId = values["Engine.PlayerReplicationInfo:UniqueId"]
                    val steeringSensitivity = values["TAGame.PRI_TA:SteeringSensitivity"]

                    if (uniqueId is Pair<*, *>) {
                        val player = players.values.firstOrNull { p -> p.onlineId == uniqueId.second!!.toString() }

                        player?.apply {
                            this.id = actorId
                            this.steeringSensitivity = when (steeringSensitivity) {
                                is Float -> steeringSensitivity
                                else -> 1.0F
                            }
                        }
                    }
                }

                if (actorData.typeName == "Archetypes.Car.Car_Default" && playerActorId is Int) {
                    playerCarIds[playerActorId] = actorId
                    carPlayerIds[actorId] = playerActorId

                    val demolition = values["TAGame.Car_TA:ReplicatedDemolish"]
                    if (demolition is Demolition) {
                        val attackerCarId = demolition.attackerActorId
                        val victimCarId = demolition.victimActorId

                        if (attackerCarId != -1 && victimCarId != -1) {
                            val attackerPlayerId = carPlayerIds[attackerCarId] ?: -1
                            val victimPlayerId = carPlayerIds[victimCarId] ?: -1

                            demolitions.add(ParsedReplayDemolition(attackerPlayerId, victimPlayerId))
                        }
                    }
                }
            }

            replicationUpdates.onEach { update ->
                val updates = update.actor.updates

                val profileSettings = updates?.firstOrNull { it.name == "TAGame.CameraSettingsActor_TA:ProfileSettings" }?.value
                if (profileSettings is ProfileSettings) {
                    val playerId = updates.firstOrNull { it.name == "TAGame.CameraSettingsActor_TA:PRI" }?.value
                    if (playerId is Int) {
                        val player = players.values.firstOrNull { p -> p.id == playerId }
                        with(profileSettings.cameraSettings) {
                            player?.cameraSettings = ParsedReplayCameraSettings(
                                fieldOfView = this.fov,
                                height = this.height,
                                pitch = this.angle,
                                distance = this.distance,
                                stiffness = this.stiffness,
                                swivelSpeed = this.swivelSpeed,
                                transitionSpeed = this.transitionSpeed
                            )
                        }
                    }
                }
            }
        }

        return parsedReplay.toReplay(
            name = properties.values["ReplayName"]?.toString() ?: "N/A",
            parsedTeams = teams,
            parsedDemolitions = demolitions.toMutableList()
        )
    }

}
