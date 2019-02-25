package com.x7ff.rl.replay.api.transformer

import com.x7ff.parser.replay.PropertyList
import com.x7ff.parser.replay.attribute.CameraSettingsAttribute
import com.x7ff.parser.replay.attribute.DemolishAttribute
import com.x7ff.parser.replay.attribute.UniqueIdAttribute
import com.x7ff.parser.replay.property
import com.x7ff.parser.replay.propertyOrNull
import com.x7ff.parser.replay.stream.ActiveActor
import com.x7ff.parser.replay.stream.DestroyedReplication
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
    val id: Int,
    val values: MutableMap<String, MutableSet<Any>>
)

class MainReplayTransformer : ReplayTransformer<com.x7ff.parser.replay.Replay> {

    private fun MutableSet<Any>.firstOrNull() = asIterable().firstOrNull()

    override fun transform(fileName: String, parsedReplay: com.x7ff.parser.replay.Replay): Replay {
        val properties = parsedReplay.header.properties
        val playerStats = properties.property("PlayerStats") as List<PropertyList>

        val demolitions = mutableSetOf<ReplayDemolition>()
        val currentActorIds = mutableSetOf<Int>()
        val currentActors = mutableMapOf<Int, ActorInfo>()
        val carPlayerIds = mutableMapOf<Int, Int>()
        val playerCarIds = mutableMapOf<Int, Int>()

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

        parsedReplay.frames.forEach { frame ->
            frame.replications.forEach { replication ->
                val actorId = replication.actor.first.toInt()
                val value = replication.value
                when(value) {
                    is DestroyedReplication -> {
                        currentActorIds.remove(actorId)
                        currentActors.remove(actorId)
                        if (carPlayerIds.containsKey(actorId)) {
                            val playerActorId = carPlayerIds[actorId]
                            carPlayerIds.remove(playerActorId)
                            playerCarIds.remove(playerActorId)
                        }
                    }
                    is SpawnedReplication -> {
                        currentActorIds.add(actorId)
                        currentActors[actorId] = ActorInfo(
                            typeName = value.objectName,
                            className = value.className,
                            name = value.name,
                            id = actorId,
                            values = mutableMapOf()
                        )
                    }
                    is UpdatedReplications -> {
                        val actualUpdate = value.updates.map { it.propertyName to mutableSetOf(it.data) }.toMap()
                        currentActorIds.add(actorId)
                        currentActors[actorId]?.values?.putAll(actualUpdate)
                    }
                }
            }

            for ((actorId, actorData) in currentActors) {
                val values = actorData.values

                val playerName = values["Engine.PlayerReplicationInfo:PlayerName"]?.firstOrNull()
                if (actorData.typeName == "TAGame.Default__PRI_TA" && actorData.className == "TAGame.PRI_TA"
                    && playerName is String
                ) {
                    val uniqueId = values["Engine.PlayerReplicationInfo:UniqueId"]?.firstOrNull()
                    val steeringSensitivity = values["TAGame.PRI_TA:SteeringSensitivity"]?.firstOrNull()

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

                val playerActor = actorData.values["Engine.Pawn:PlayerReplicationInfo"]?.firstOrNull()
                if (actorData.typeName == "Archetypes.Car.Car_Default" && playerActor is ActiveActor) {
                    val playerActorId = playerActor.actorId
                    playerCarIds[playerActorId] = actorId
                    carPlayerIds[actorId] = playerActorId

                    val actorDemolitions = values["TAGame.Car_TA:ReplicatedDemolish"]
                    actorDemolitions?.filter { demo -> demo is DemolishAttribute }?.onEach { demo ->
                        val demolition = demo as DemolishAttribute
                        val attackerCarId = demolition.attackerActorId
                        val victimCarId = demolition.victimActorId

                        if (attackerCarId != -1 && victimCarId != -1) {
                            val attackerPlayerId = carPlayerIds[attackerCarId] ?: -1
                            val victimPlayerId = carPlayerIds[victimCarId] ?: -1

                            demolitions.add(
                                ReplayDemolition(
                                    attackerActorId = attackerPlayerId,
                                    victimActorId = victimPlayerId,
                                    attackerVelocity = demolition.attackerVelocity,
                                    victimVelocity = demolition.victimVelocity
                                )
                            )
                        }
                    }
                    actorData.values.remove("TAGame.Car_TA:ReplicatedDemolish")
                }
            }

            for ((_, actorData) in currentActors) {
                val values = actorData.values

                val cameraSettingsActor = values["TAGame.CameraSettingsActor_TA:PRI"]?.firstOrNull()
                if (actorData.typeName == "TAGame.Default__CameraSettingsActor_TA"
                    && cameraSettingsActor is ActiveActor) {
                    val profileSettings = values["TAGame.CameraSettingsActor_TA:ProfileSettings"]?.firstOrNull()
                    if (profileSettings is CameraSettingsAttribute) {
                        val player = players.firstOrNull { p -> p.id == cameraSettingsActor.actorId }
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
