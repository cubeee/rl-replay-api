package com.x7ff.rl.replay.api.model.replay.parsed

import com.squareup.moshi.Json

data class ParsedReplayFrameActorUpdate(
    @Json(name = "Id") val id: Int,
    @Json(name = "NameId") val nameId: Int?,
    @Json(name = "TypeName") val typeName: String?,
    @Json(name = "ClassName") val className: String?,
    @Json(name = "TAGame.Car_TA:ReplicatedDemolish") val demolition: ParsedReplayDemolition?,
    @Json(name = "Engine.PlayerReplicationInfo:PlayerName") val playerName: String?,
    @Json(name = "Engine.PlayerReplicationInfo:PlayerID") val playerId: Int?,
    @Json(name = "Engine.PlayerReplicationInfo:Team") val team: ParsedReplayActorInfo?,
    @Json(name = "Engine.Pawn:PlayerReplicationInfo") val playerActor: ParsedReplayActorInfo?,
    @Json(name = "TAGame.CameraSettingsActor_TA:PRI") val cameraActor: ParsedReplayActorInfo?,
    @Json(name = "TAGame.CarComponent_TA:Vehicle") val vehicleActor: ParsedReplayActorInfo?,
    @Json(name = "TAGame.CameraSettingsActor_TA:ProfileSettings") val cameraSettings: ParsedReplayCameraSettings?,
    @Json(name = "Engine.PlayerReplicationInfo:UniqueId") val uniqueId: ParsedReplayUniqueId?,
    @Json(name = "TAGame.PRI_TA:SteeringSensitivity") val steeringSensitivity: Float?,
    @Json(name = "TAGame.PRI_TA:TotalXP") val totalXp: Int?
)