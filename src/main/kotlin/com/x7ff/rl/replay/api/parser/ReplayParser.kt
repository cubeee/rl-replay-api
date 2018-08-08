package com.x7ff.rl.replay.api.parser

import com.x7ff.rl.replay.api.model.response.ParseResponse
import java.io.InputStream

interface ReplayParser {

    fun parseReplay(content: InputStream): ParseResponse

}