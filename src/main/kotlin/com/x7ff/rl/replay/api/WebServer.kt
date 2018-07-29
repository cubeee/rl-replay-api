package com.x7ff.rl.replay.api

import com.x7ff.rl.replay.api.model.ErrorResponse
import com.x7ff.rl.replay.api.model.parse.SuccessfulParseResponse
import com.x7ff.rl.replay.api.model.parse.SuccessfulRattletrapParseResponse
import com.x7ff.rl.replay.api.parser.LocalFileRattletrapParser
import com.x7ff.rl.replay.api.parser.LocalFileReplayParser
import com.x7ff.rl.replay.api.transformer.RattletrapReplayTransformer
import com.x7ff.rl.replay.api.transformer.ReplayTransformer
import io.javalin.Javalin
import java.io.InputStream
import javax.servlet.http.HttpServletResponse
import kotlin.system.measureTimeMillis

class WebServer {
    companion object {
        private const val REPLAY_UPLOAD_PATH = "/upload"
        private const val REQUEST_FILE_NAME = "replay"
        private const val REPLAY_EXTENSION = ".replay"
    }

    fun start(port: Int) {
        val parser = LocalFileReplayParser()
        val transformer = ReplayTransformer()

        val rattletrapParser = LocalFileRattletrapParser()
        val rattletrapTransformer = RattletrapReplayTransformer()

        val app = Javalin.start(port)
        app.post(REPLAY_UPLOAD_PATH) { ctx ->
            val replayFile = ctx.uploadedFile(REQUEST_FILE_NAME)

            replayFile?.let { file ->
                if (file.extension != REPLAY_EXTENSION) {
                    ctx.json(ErrorResponse("Invalid file extension '${file.extension}', expected $REPLAY_EXTENSION"))
                    return@let
                }

                val parseResult = parser.parseReplay(file.name, file.content)

                if (parseResult is SuccessfulParseResponse) {
                    ctx.json(transformer.transform(parseResult.replay))
                } else {
                    ctx
                        .status(HttpServletResponse.SC_BAD_REQUEST)
                        .json(parseResult)
                }
            } ?: run {
                ctx
                    .status(HttpServletResponse.SC_BAD_REQUEST)
                    .json(ErrorResponse("No replay file found"))
            }
        }

        app.post("/rattletrap") { ctx ->
            val file = ctx.formParam("file")
            val content = object : InputStream() {
                override fun read(): Int {
                    return -1
                }
            }

            file?.let {
                val elapsed = measureTimeMillis {
                    val parseResult = rattletrapParser.parseReplay(file, content)

                    if (parseResult is SuccessfulRattletrapParseResponse) {
                        val transformed = rattletrapTransformer.transform(parseResult.replay)
                        ctx.json(transformed)
                    } else {
                        ctx
                            .status(HttpServletResponse.SC_BAD_REQUEST)
                            .json(parseResult)
                    }
                }
                println("Replay parsed and transformed in ${elapsed}ms")
            } ?: run {
                ctx
                    .status(HttpServletResponse.SC_BAD_REQUEST)
                    .json(ErrorResponse("No replay file name given"))
            }
        }
    }

}

fun main(args: Array<String>) {
    val webServer = WebServer()
    webServer.start(7000) // TODO: env var
}