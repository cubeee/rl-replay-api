package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.fasterxml.jackson.annotation.JsonProperty

data class Demolition (
    @JsonProperty("attacker_actor_id") val attackerActorId: Long,
    @JsonProperty("victim_actor_id") val victimActorId: Long
)