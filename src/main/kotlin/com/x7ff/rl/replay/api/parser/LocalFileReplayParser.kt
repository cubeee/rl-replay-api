package com.x7ff.rl.replay.api.parser

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.x7ff.rl.replay.api.adapter.ActorUpdateDeserializer
import com.x7ff.rl.replay.api.model.parse.FailedParseResponse
import com.x7ff.rl.replay.api.model.parse.ParseResult
import com.x7ff.rl.replay.api.model.parse.SuccessfulParseResponse
import com.x7ff.rl.replay.api.model.replay.parsed.ParsedReplay
import com.x7ff.rl.replay.api.model.replay.rattletrap.ActorUpdate
import java.io.File
import java.io.InputStream

class LocalFileReplayParser : ReplayParser {

    private val objectMapper by lazy {
        val module = SimpleModule()
        module.addDeserializer(ActorUpdate::class.java, ActorUpdateDeserializer())

        ObjectMapper()
            .registerModule(module)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    override fun parseReplay(name: String, content: InputStream?): ParseResult {
        val fileName = File(name).nameWithoutExtension + ".json"
        val resource = javaClass.classLoader.getResource("replays/$fileName")

        resource?.let {
            val fileContent = resource.readText()

            val parsedReplay = objectMapper.readValue(fileContent, ParsedReplay::class.java)
            parsedReplay?.let {
                return SuccessfulParseResponse(parsedReplay)
            }
            return FailedParseResponse("Failed to parse replay JSON")
        }
        return FailedParseResponse("Local parsed JSON file for replay not found")
    }

}