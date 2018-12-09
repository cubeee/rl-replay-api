package com.x7ff.rl.replay.api.parser

import com.x7ff.parser.replay.Replay
import com.x7ff.rl.replay.api.model.response.FailedParseResponse
import com.x7ff.rl.replay.api.model.response.ParseResponse
import com.x7ff.rl.replay.api.model.response.PartiallySuccessfulParseResponse
import com.x7ff.rl.replay.api.model.response.SuccessfulParseResponse

class ReplayKtParser : ReplayParser<Replay> {

    override fun parseReplay(bytes: ByteArray): ParseResponse<Replay> {
        return try {
            SuccessfulParseResponse(parseReplay(bytes, parseFrames = true))
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                PartiallySuccessfulParseResponse(parseReplay(bytes, parseFrames = false))
            } catch (e: Exception) {
                e.printStackTrace()
                FailedParseResponse("Failed to parse replay file")
            }
        }
    }

    private fun parseReplay(bytes: ByteArray, parseFrames: Boolean = true) = Replay.parse(bytes, parseFrames)

}