package com.x7ff.rl.replay.api.parser

import com.x7ff.rl.replay.api.model.response.ParseResponse

interface ReplayParser<R> {

    fun parseReplay(bytes: ByteArray): ParseResponse<R>

}