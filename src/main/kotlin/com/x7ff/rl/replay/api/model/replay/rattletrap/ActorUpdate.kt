package com.x7ff.rl.replay.api.model.replay.rattletrap

data class ActorUpdate (
    var id: ReplicationActorId? = null,
    var name: String = "",
    var value: Any? = null
)