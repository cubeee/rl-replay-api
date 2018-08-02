package com.x7ff.rl.replay.api.parser

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.x7ff.rl.replay.api.ParserContext
import com.x7ff.rl.replay.api.adapter.ActorUpdateDeserializer
import com.x7ff.rl.replay.api.model.parse.FailedParseResponse
import com.x7ff.rl.replay.api.model.parse.ParseResult
import com.x7ff.rl.replay.api.model.parse.SuccessfulRattletrapParseResponse
import com.x7ff.rl.replay.api.model.replay.rattletrap.ActorUpdate
import com.x7ff.rl.replay.api.model.replay.rattletrap.RattletrapReplay
import java.io.InputStream

class RattletrapParser(
    private val parserContext: ParserContext
): ReplayParser {

    private val objectMapper by lazy {
        val module = SimpleModule()
        module.addDeserializer(ActorUpdate::class.java, ActorUpdateDeserializer())

        ObjectMapper()
            .registerModule(module)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    override fun parseReplay(content: InputStream?): ParseResult {
        content?.let {
            try {
                val jsonStream = convertToJson(content)
                val replay = objectMapper.readValue(jsonStream, RattletrapReplay::class.java)

                replay?.let {
                    return SuccessfulRattletrapParseResponse(replay)
                }
            } catch (e: Exception) {
                // TODO: store failed replays and stacktraces
                e.printStackTrace()
                return FailedParseResponse("Failed to parse replay file")
            }
        }
        return FailedParseResponse("No replay file sent in request")
    }

    private fun convertToJson(content: InputStream): InputStream {
        val command = "${parserContext.rattletrapExecutable} --compact --mode=decode"
        val parts = command.split("\\s".toRegex())

        val proc = ProcessBuilder(*parts.toTypedArray())
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectInput(ProcessBuilder.Redirect.PIPE)
            .start()

        content.copyTo(proc.outputStream, parserContext.parserBufferSize)
        return proc.inputStream
    }

}
