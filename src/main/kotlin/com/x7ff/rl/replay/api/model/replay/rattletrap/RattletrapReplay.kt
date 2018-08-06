package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.fasterxml.jackson.annotation.JsonProperty
import com.x7ff.rl.replay.api.model.replay.Replay

/**
 * Hierarchy:
 * - header: Header
 *   - header: HeaderBody
 *     - engineVersion: Int
 *     - label: String
 *     - licenseeVersion: Int
 *     - patchVersion: Int
 *     - properties: Properties
 * - content: Content
 *   - body: ContentBody
 *     - levels: List<String>
 *     - frames: List<Frame>
 *       - delta: Float
 *       - replications: List<Replication>
 *         - actorId: ReplicationActorId
 *           - limit: Int
 *           - value: Long
 *         - value: ReplicationActor
 *           - spawned: ActorSpawn
 *             - className: String
 *             - flag: Boolean
 *             - name: String
 *             - nameIndex: Int
 *             - objectId: Int
 *             - objectName: String
 *           - updated: List<ActorUpdate>
 *             - id: ReplicationActorId
 *             - name: String
 */
data class RattletrapReplay (
    @JsonProperty("header") val header: Header,
    @JsonProperty("content") val content: Content
) {
    fun toReplay(
        name: String,
        id: String,
        rattletrapTeams: List<RattletrapTeam>,
        rattletrapDemolitions: MutableList<RattletrapDemolition>
    ): Replay {
        val teams = rattletrapTeams.map { it.toTeam() }
        val players = rattletrapTeams.flatMap { it.players }
        val demolitions = rattletrapDemolitions.mapNotNull { it.toDemolition(players) }

        return Replay(
            name = name,
            id = id,
            teams = teams,
            demolitions = demolitions
        )
    }

}