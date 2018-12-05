package com.x7ff.rl.replay.api.transformer

import com.x7ff.parser.replay.PropertyList
import com.x7ff.parser.replay.attribute.CameraSettingsAttribute
import com.x7ff.parser.replay.attribute.DemolishAttribute
import com.x7ff.parser.replay.attribute.UniqueIdAttribute
import com.x7ff.parser.replay.property
import com.x7ff.parser.replay.propertyOrNull
import com.x7ff.parser.replay.stream.ActiveActor
import com.x7ff.parser.replay.stream.SpawnedReplication
import com.x7ff.parser.replay.stream.UpdatedReplications
import com.x7ff.rl.replay.api.model.kt.ReplayDemolition
import com.x7ff.rl.replay.api.model.kt.ReplayPlayer
import com.x7ff.rl.replay.api.model.kt.ReplayTeam.Companion.createBlueTeam
import com.x7ff.rl.replay.api.model.kt.ReplayTeam.Companion.createOrangeTeam
import com.x7ff.rl.replay.api.model.replay.CameraSettings
import com.x7ff.rl.replay.api.model.replay.Replay

data class ActorInfo(
    val typeName: String,
    val className: String,
    val name: String?,
    val id: Long,
    val values: MutableMap<String, Any?>
)

class MainReplayTransformer: ReplayTransformer<com.x7ff.parser.replay.Replay> {

    override fun transform(fileName: String, parsedReplay: com.x7ff.parser.replay.Replay): Replay {
        val properties = parsedReplay.header.properties
        val playerStats = properties.property("PlayerStats") as List<PropertyList>

        val demolitions = mutableSetOf<ReplayDemolition>()
        val playerCarIds = mutableMapOf<Int, Int>()
        val carPlayerIds = mutableMapOf<Int, Int>()

        val players = playerStats
            .map { stats ->
                val player = ReplayPlayer(
                    name = stats.property("Name"),
                    onlineId = stats.property("OnlineID"),
                    score = stats.property("Score"),
                    goals = stats.property("Goals"),
                    assists = stats.property("Assists"),
                    saves = stats.property("Saves"),
                    shots = stats.property("Shots"),
                    team = stats.property("Team")
                )
                player
            }
            .toList()

        for (frame in parsedReplay.frames) {
            val replicationUpdates = frame.replications
                .filter { replication -> replication.value is UpdatedReplications }
                .map { replication -> replication.actor.first to replication.value as UpdatedReplications }

            val actors = frame.replications
                .filter { replication -> replication.value is SpawnedReplication }
                .map { replication ->
                    val spawn = replication.value as SpawnedReplication
                    val actorId = replication.actor.first

                    with(spawn) {
                        actorId to ActorInfo(
                            typeName = objectName,
                            className = className,
                            name = name,
                            id = actorId,
                            values = mutableMapOf()
                        )
                    }
                }
                .toMap()

            replicationUpdates.onEach { update ->
                val actorId = update.first

                update.second.updates.forEach { actors[actorId]?.values?.put(it.propertyName, it.data) }
            }

            for ((actorId, actorData) in actors) {
                val values = actorData.values

                val playerName = values["Engine.PlayerReplicationInfo:PlayerName"]
                val playerActorId = values["Engine.Pawn:PlayerReplicationInfo"]

                if (actorData.typeName == "TAGame.Default__PRI_TA" && actorData.className == "TAGame.PRI_TA"
                    && playerName is String) {
                    val uniqueId = values["Engine.PlayerReplicationInfo:UniqueId"]
                    val steeringSensitivity = values["TAGame.PRI_TA:SteeringSensitivity"]

                    if (uniqueId is UniqueIdAttribute) {
                        val player = players.firstOrNull { p -> p.onlineId == uniqueId.id }

                        player?.apply {
                            this.id = actorId
                            this.steeringSensitivity = when (steeringSensitivity) {
                                is Float -> steeringSensitivity
                                else -> 1.0F
                            }
                        }
                    }
                }

                if (actorData.typeName == "Archetypes.Car.Car_Default" && playerActorId is ActiveActor) {
                    playerCarIds[playerActorId.actorId] = actorId.toInt()
                    carPlayerIds[actorId.toInt()] = playerActorId.actorId

                    val demolition = values["TAGame.Car_TA:ReplicatedDemolish"]
                    if (demolition is DemolishAttribute) {
                        val attackerCarId = demolition.attackerActorId
                        val victimCarId = demolition.victimActorId

                        if (attackerCarId != -1 && victimCarId != -1) {
                            val attackerPlayerId = carPlayerIds[attackerCarId] ?: -1
                            val victimPlayerId = carPlayerIds[victimCarId] ?: -1

                            demolitions.add(
                                ReplayDemolition(
                                    attackerActorId = attackerPlayerId,
                                    victimActorId = victimPlayerId
                                )
                            )
                        }
                    }
                }

            }

            replicationUpdates.onEach { update ->
                val updates = update.second.updates

                val profileSettings = updates
                    .firstOrNull { it.propertyName == "TAGame.CameraSettingsActor_TA:ProfileSettings" }?.data
                if (profileSettings is CameraSettingsAttribute) {
                    val playerId = updates
                        .firstOrNull { it.propertyName == "TAGame.CameraSettingsActor_TA:PRI" }?.data
                    if (playerId is ActiveActor) {
                        val player = players.firstOrNull { p -> p.id == playerId.actorId.toLong() }
                        player?.cameraSettings = CameraSettings(
                            fov = profileSettings.fieldOfView.toInt(),
                            distance = profileSettings.distance.toInt(),
                            height = profileSettings.height.toInt(),
                            angle = profileSettings.pitch.toInt(),
                            stiffness = profileSettings.stiffness,
                            swivelSpeed = profileSettings.swivelSpeed,
                            transitionSpeed = profileSettings.transitionSpeed
                        )
                    }
                }
            }

        }

        val blueTeam = players.createBlueTeam(properties)
        val orangeTeam = players.createOrangeTeam(properties)

        return Replay(
            name = properties.propertyOrNull("ReplayName") ?: fileName,
            id = properties.propertyOrNull("Id") ?: "N/A",
            teams = listOf(blueTeam, orangeTeam),
            demolitions = demolitions.mapNotNull { it.toDemolition(players) }
        )
    }

}