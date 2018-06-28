package com.x7ff.rl.replay.api.parser

import com.x7ff.rl.replay.api.model.parse.FailedParseResponse
import com.x7ff.rl.replay.api.model.parse.ParseResult
import java.io.InputStream

class DockerExternalReplayParser : ReplayParser {

    override fun parseReplay(name: String, content: InputStream): ParseResult {
        return FailedParseResponse("oops $name")
    }

}