package com.x7ff.rl.replay.api.model.replay.parsed

import com.fasterxml.jackson.annotation.JsonProperty

data class ParsedReplayFrameActorUpdate(
    @JsonProperty("Id") val id: Int,
    @JsonProperty("NameId") val nameId: Int?,
    @JsonProperty("TypeName") val typeName: String?,
    @JsonProperty("ClassName") val className: String?,
    @JsonProperty("TAGame.Car_TA:ReplicatedDemolish") val demolition: ParsedReplayDemolition?,
    @JsonProperty("Engine.PlayerReplicationInfo:PlayerName") val playerName: String?,
    @JsonProperty("Engine.PlayerReplicationInfo:PlayerID") val playerId: Int?,
    @JsonProperty("Engine.PlayerReplicationInfo:Team") val team: ParsedReplayActorInfo?,
    @JsonProperty("Engine.Pawn:PlayerReplicationInfo") val playerActor: ParsedReplayActorInfo?,
    @JsonProperty("TAGame.CameraSettingsActor_TA:PRI") val cameraActor: ParsedReplayActorInfo?,
    @JsonProperty("TAGame.CarComponent_TA:Vehicle") val vehicleActor: ParsedReplayActorInfo?,
    @JsonProperty("TAGame.CameraSettingsActor_TA:ProfileSettings") val cameraSettings: ParsedReplayCameraSettings?,
    @JsonProperty("Engine.PlayerReplicationInfo:UniqueId") val uniqueId: ParsedReplayUniqueId?,
    @JsonProperty("TAGame.PRI_TA:SteeringSensitivity") val steeringSensitivity: Float?,
    @JsonProperty("TAGame.PRI_TA:TotalXP") val totalXp: Int?
)