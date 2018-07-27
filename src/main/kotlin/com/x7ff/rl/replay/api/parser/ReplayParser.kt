package com.x7ff.rl.replay.api.parser

import com.x7ff.rl.replay.api.model.parse.ParseResult
import java.io.InputStream

interface ReplayParser {

    fun parseReplay(name: String, content: InputStream?): ParseResult

}