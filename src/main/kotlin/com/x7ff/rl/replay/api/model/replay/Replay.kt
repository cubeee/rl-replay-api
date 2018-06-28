package com.x7ff.rl.replay.api.model.replay

data class Replay(
    val name: String?,
    val teams: List<Team>,
    val demolitions: List<Demolition>
)