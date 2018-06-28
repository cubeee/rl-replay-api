package com.x7ff.rl.replay.api

import com.x7ff.rl.replay.api.model.ErrorResponse
import com.x7ff.rl.replay.api.model.parse.SuccessfulParseResponse
import com.x7ff.rl.replay.api.parser.LocalFileReplayParser
import com.x7ff.rl.replay.api.transformer.ReplayTransformer
import io.javalin.Javalin
import javax.servlet.http.HttpServletResponse

class WebServer {
    companion object {
        private const val REPLAY_UPLOAD_PATH = "/upload"
        private const val REQUEST_FILE_NAME = "replay"
        private const val REPLAY_EXTENSION = ".replay"
    }

    fun start(port: Int) {
        val parser = LocalFileReplayParser()
        val transformer = ReplayTransformer()

        val app = Javalin.start(port)
        app.post(REPLAY_UPLOAD_PATH) { ctx ->
            val replayFile = ctx.uploadedFile(REQUEST_FILE_NAME)

            replayFile?.let { file ->
                if (file.extension != REPLAY_EXTENSION) {
                    ctx.json(ErrorResponse("Invalid file extension '${file.extension}', expected $REPLAY_EXTENSION"))
                    return@let
                }

                val parseResult = parser.parseReplay(file.name, file.content)
                var result: Any = parseResult

                if (parseResult is SuccessfulParseResponse) {
                    result = transformer.transform(parseResult.replay)
                }

                ctx.json(result)
            } ?: run {
                ctx
                    .status(HttpServletResponse.SC_BAD_REQUEST)
                    .json(ErrorResponse("No replay file found"))
            }
        }
    }

}

fun main(args: Array<String>) {
    val webServer = WebServer()
    webServer.start(7000) // TODO: env var
}