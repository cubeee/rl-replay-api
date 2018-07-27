package com.x7ff.rl.replay.api.parser

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.x7ff.rl.replay.api.adapter.PropertyValuesAdapter
import com.x7ff.rl.replay.api.adapter.rattletrap.ActorUpdateAdapter
import com.x7ff.rl.replay.api.model.parse.FailedParseResponse
import com.x7ff.rl.replay.api.model.parse.ParseResult
import com.x7ff.rl.replay.api.model.parse.SuccessfulRattletrapParseResponse
import com.x7ff.rl.replay.api.model.replay.rattletrap.RattletrapReplay
import java.io.File
import java.io.InputStream

class LocalFileRattletrapParser : ReplayParser {

    private val moshi by lazy {
        Moshi.Builder()
            .add(PropertyValuesAdapter())
            .add(ActorUpdateAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    override fun parseReplay(name: String, content: InputStream?): ParseResult {
        val fileName = File(name).nameWithoutExtension + ".json"
        val resource = javaClass.classLoader.getResource("replays/$fileName")

        resource?.let {
            val fileContent = resource.readText()

            val replayAdapter = moshi
                .adapter(RattletrapReplay::class.java)
                .lenient()
            val replay = replayAdapter.fromJson(fileContent)

            replay?.let {
                return SuccessfulRattletrapParseResponse(replay)
            }
            return FailedParseResponse("Failed to parse replay JSON")
        }
        return FailedParseResponse("Local parsed JSON file for replay not found")
    }

}