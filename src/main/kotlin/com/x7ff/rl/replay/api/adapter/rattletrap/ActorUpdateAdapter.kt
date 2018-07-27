package com.x7ff.rl.replay.api.adapter.rattletrap

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.x7ff.rl.replay.api.adapter.*
import com.x7ff.rl.replay.api.model.replay.rattletrap.ActorUpdate
import com.x7ff.rl.replay.api.model.replay.rattletrap.ProfileSettings
import com.x7ff.rl.replay.api.model.replay.rattletrap.ReplicationActorId
import java.io.IOException

class ActorUpdateAdapter {

    private val demolitionAdapter by lazy { DemolitionAdapter() }
    private val strPropertyAdapter by lazy { StrPropertyAdapter() }
    private val intPropertyAdapter by lazy { IntPropertyAdapter() }
    private val floatPropertyAdapter by lazy { FloatPropertyAdapter() }
    private val flaggedIntPropertyAdapter by lazy { FlaggedIntPropertyAdapter() }
    private val uniqueIdPropertyAdapter by lazy { UniqueIdPropertyAdapter() }

    @FromJson
    fun fromJson(
        reader: JsonReader,
        delegateReplicationActorIdAdapter: JsonAdapter<ReplicationActorId>,
        delegateProfileSettingsAdapter: JsonAdapter<ProfileSettings>
    ): ActorUpdate? {
        val update = ActorUpdate()
        reader.beginObject()

        while (reader.hasNext()) {
            val key = reader.nextName()

            when(key) {
                "id" -> update.id = delegateReplicationActorIdAdapter.fromJson(reader)
                "name" -> update.name = reader.nextString()
                "value" -> update.value = readValue(reader, update.name, delegateProfileSettingsAdapter)
                else -> throw IOException("Unknown actor updates field $key")
            }
        }
        reader.endObject()
        return update
    }

    private fun readValue(
        reader: JsonReader,
        name: String?,
        delegateProfileSettingsAdapter: JsonAdapter<ProfileSettings>
    ): Any? {
        return when(name) {
            "TAGame.Car_TA:ReplicatedDemolish" -> demolitionAdapter.fromJson(reader)
            "Engine.PlayerReplicationInfo:PlayerName" -> strPropertyAdapter.fromJson(reader)
            "Engine.PlayerReplicationInfo:Score" -> intPropertyAdapter.fromJson(reader)
            "Engine.PlayerReplicationInfo:Ping" -> intPropertyAdapter.fromJson(reader)
            "Engine.PlayerReplicationInfo:Team" -> flaggedIntPropertyAdapter.fromJson(reader)
            "Engine.PlayerReplicationInfo:UniqueId" -> uniqueIdPropertyAdapter.fromJson(reader)
            "Engine.PlayerReplicationInfo:PlayerID" -> intPropertyAdapter.fromJson(reader)
            "TAGame.PRI_TA:SteeringSensitivity" -> floatPropertyAdapter.fromJson(reader)
            "TAGame.PRI_TA:TotalXP" -> intPropertyAdapter.fromJson(reader)
            "TAGame.PRI_TA:MatchShots" -> intPropertyAdapter.fromJson(reader)
            "TAGame.PRI_TA:MatchGoals" -> intPropertyAdapter.fromJson(reader)
            "TAGame.PRI_TA:MatchScore" -> intPropertyAdapter.fromJson(reader)
            "Engine.Pawn:PlayerReplicationInfo" -> flaggedIntPropertyAdapter.fromJson(reader)
            "TAGame.CameraSettingsActor_TA:ProfileSettings" -> delegateProfileSettingsAdapter.fromJson(reader)
            "TAGame.CameraSettingsActor_TA:PRI" -> flaggedIntPropertyAdapter.fromJson(reader)
            null -> throw IllegalStateException("Trying to read value before name")
            else -> {
                reader.skipValue()
                null
            }
        }
    }

}