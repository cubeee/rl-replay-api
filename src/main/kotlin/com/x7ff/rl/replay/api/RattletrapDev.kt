package com.x7ff.rl.replay.api

import com.squareup.moshi.Moshi
import com.x7ff.rl.replay.api.model.parse.SuccessfulRattletrapParseResponse
import com.x7ff.rl.replay.api.model.replay.Replay
import com.x7ff.rl.replay.api.parser.LocalFileRattletrapParser
import com.x7ff.rl.replay.api.transformer.RattletrapReplayTransformer

private val moshi by lazy {
    Moshi.Builder()
        .build()
}

fun main(args: Array<String>) {
    val file = args[0]

    val rattletrapParser = LocalFileRattletrapParser()
    val rattletrapTransformer = RattletrapReplayTransformer()

    val start = System.currentTimeMillis()
    val parseResult = rattletrapParser.parseReplay(file, null)
    println("Parsed replay JSON file in ${System.currentTimeMillis() - start}ms")

    if (parseResult is SuccessfulRattletrapParseResponse) {
        val transformed = rattletrapTransformer.transform(parseResult.replay)

        val replayAdapter = moshi
            .adapter(Replay::class.java)
            .indent("  ")
            .lenient()
        println()
        println(replayAdapter.toJson(transformed))
    } else {
        println("transform failed")
    }
}