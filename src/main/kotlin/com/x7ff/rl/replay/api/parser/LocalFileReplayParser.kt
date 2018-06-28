package com.x7ff.rl.replay.api.parser

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.x7ff.rl.replay.api.model.parse.FailedParseResponse
import com.x7ff.rl.replay.api.model.parse.ParseResult
import com.x7ff.rl.replay.api.model.parse.SuccessfulParseResponse
import com.x7ff.rl.replay.api.model.replay.parsed.ParsedReplay
import java.io.File
import java.io.InputStream

class LocalFileReplayParser : ReplayParser {

    private val moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    override fun parseReplay(name: String, content: InputStream): ParseResult {
        val fileName = File(name).nameWithoutExtension + ".json"
        val resource = javaClass.classLoader.getResource("replays/$fileName")
        val fileContent = resource.readText()

        val parsedReplayAdapter = moshi.adapter(ParsedReplay::class.java)
        val parsedReplay = parsedReplayAdapter.fromJson(fileContent)

        parsedReplay?.let {
            return SuccessfulParseResponse(parsedReplay)
        } ?: run {
            return FailedParseResponse("Failed to parse replay JSON")
        }
    }

}