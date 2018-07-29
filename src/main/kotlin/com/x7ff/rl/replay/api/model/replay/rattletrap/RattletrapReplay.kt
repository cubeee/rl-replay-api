package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.fasterxml.jackson.annotation.JsonProperty
import com.x7ff.rl.replay.api.model.replay.Replay
import com.x7ff.rl.replay.api.model.replay.parsed.ParsedReplayDemolition
import com.x7ff.rl.replay.api.model.replay.parsed.ParsedTeam

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
        parsedTeams: List<ParsedTeam>,
        parsedDemolitions: MutableList<ParsedReplayDemolition>
    ): Replay {
        val teams = parsedTeams.map { it.toTeam() }
        val parsedPlayers = parsedTeams.flatMap { it.players }
        val demolitions = parsedDemolitions.mapNotNull { it.toDemolition(parsedPlayers) }

        return Replay(
            name = name,
            teams = teams,
            demolitions = demolitions
        )
    }

}