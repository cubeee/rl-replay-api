package com.x7ff.rl.replay.api

import com.x7ff.rl.replay.api.model.response.ErrorResponse
import com.x7ff.rl.replay.api.model.response.SuccessfulParseResponse
import com.x7ff.rl.replay.api.parser.RattletrapParser
import com.x7ff.rl.replay.api.transformer.RattletrapReplayTransformer
import io.javalin.Javalin
import javax.servlet.http.HttpServletResponse

data class ParserContext(val rattletrapExecutable: String, val parserBufferSize: Int)

class WebServer {
    companion object {
        private const val REPLAY_UPLOAD_PATH = "/upload"
        private const val REQUEST_FILE_NAME = "replay"
        private const val REPLAY_EXTENSION = ".replay"
    }

    fun start(port: Int, parserContext: ParserContext) {
        val app = Javalin.start(port)
        app.post(REPLAY_UPLOAD_PATH) { ctx ->
            val uploadedFile = ctx.uploadedFile(REQUEST_FILE_NAME)

            uploadedFile?.let {
                if (uploadedFile.extension != REPLAY_EXTENSION) {
                    ctx
                        .status(HttpServletResponse.SC_BAD_REQUEST)
                        .json(ErrorResponse("Invalid replay file uploaded. Expecting files with '$REPLAY_EXTENSION' extension"))
                        .next()
                    return@post
                }

                val rattletrapParser = RattletrapParser(parserContext)
                val rattletrapTransformer = RattletrapReplayTransformer()

                val parseResponse = rattletrapParser.parseReplay(uploadedFile.content)
                if (parseResponse is SuccessfulParseResponse) {
                    val transformed = rattletrapTransformer.transform(parseResponse.replay)
                    ctx.json(transformed)
                } else {
                    ctx
                        .status(HttpServletResponse.SC_BAD_REQUEST)
                        .json(parseResponse)
                }
            } ?: run {
                ctx
                    .status(HttpServletResponse.SC_BAD_REQUEST)
                    .json(ErrorResponse("No replay file name given"))
            }
        }
    }

}

fun main(args: Array<String>) {
    val port = getEnvVar("PORT", "7000").toInt()

    val parserContext = ParserContext(
        rattletrapExecutable = getRequiredEnvVar("RATTLETRAP_EXECUTABLE"),
        parserBufferSize = getEnvVar("PARSER_BUFFER_SIZE", "100000").toInt()
    )

    val webServer = WebServer()
    println("Listening on port $port...")
    webServer.start(port, parserContext)
}

private fun getRequiredEnvVar(key: String): String {
    return System.getenv(key) ?: throw NullPointerException("Environment variable '$key' not set")
}

private fun getEnvVar(key: String, default: String): String {
    return System.getenv(key) ?: default
}