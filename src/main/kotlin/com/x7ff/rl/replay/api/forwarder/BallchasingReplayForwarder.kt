package com.x7ff.rl.replay.api.forwarder

import com.github.kittinunf.fuel.core.BlobDataPart
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.httpUpload
import java.io.ByteArrayInputStream

class BallchasingReplayForwarder: ReplayForwarder() {
    companion object {
        private val API_KEY = System.getenv("BALLCHASING_API_KEY") ?: throw IllegalStateException("BALLCHASING_API_KEY environment variable missing")
    }

    override fun forwardReplay(fileName: String, bytes: ByteArray) {
        println("[Ballchasing] Forwarding '${fileName}'...")
        val (_, resp) = "https://ballchasing.com/api/v2/upload?visibility=public".httpUpload()
            .add { BlobDataPart(ByteArrayInputStream(bytes), name = "file", filename = fileName, contentType = "application/octet-stream") }
            .header(Headers.AUTHORIZATION, API_KEY)
            .response()
        println("[Ballchasing] Status code for '${fileName}': ${resp.statusCode}")
    }

}
