package com.x7ff.rl.replay.api.model.replay

data class Team(
    val id: Int,
    val name: String,
    val players: List<Player>
)