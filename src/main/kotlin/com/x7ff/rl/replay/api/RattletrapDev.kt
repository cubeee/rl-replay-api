package com.x7ff.rl.replay.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.x7ff.rl.replay.api.model.parse.SuccessfulRattletrapParseResponse
import com.x7ff.rl.replay.api.parser.LocalFileRattletrapParser
import com.x7ff.rl.replay.api.transformer.RattletrapReplayTransformer

private val objectMapper by lazy {
    ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .registerKotlinModule()
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

        println()
        println(objectMapper.writeValueAsString(transformed))
    } else {
        println("transform failed")
    }
}