package com.x7ff.rl.replay.api

import com.x7ff.rl.replay.api.model.response.ErrorResponse
import com.x7ff.rl.replay.api.model.response.SuccessfulParseResponse
import com.x7ff.rl.replay.api.parser.RattletrapParser
import com.x7ff.rl.replay.api.transformer.RattletrapReplayTransformer
import io.javalin.Javalin
import io.javalin.embeddedserver.Location
import javax.servlet.http.HttpServletResponse

data class ParserContext(
    val rattletrapExecutable: String,
    val parserBufferSize: Int,
    val parserTimeoutSeconds: Long
)

class WebServer {
    companion object {
        private const val REPLAY_UPLOAD_PATH = "/upload"
        private const val REQUEST_FILE_NAME = "replay"
        private const val REPLAY_EXTENSION = ".replay"
    }

    fun start(env: String, port: Int, parserContext: ParserContext) {
        println("Starting in '$env' environment...")

        val app = Javalin.create()
        app.port(port)

        when(env) {
            "prod" -> app.enableStaticFiles("/public")
            else -> app.enableStaticFiles("src/main/resources/public/", Location.EXTERNAL)
        }

        app.post(REPLAY_UPLOAD_PATH) { ctx ->
            val uploadedFile = ctx.uploadedFile(REQUEST_FILE_NAME)

            uploadedFile?.let {
                if (uploadedFile.extension != REPLAY_EXTENSION) {
                    ctx
                        .status(HttpServletResponse.SC_BAD_REQUEST)
                        .json(ErrorResponse("Invalid replay file uploaded. Expecting files with '$REPLAY_EXTENSION' extension"))
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

        app.start()
    }

}

fun main(args: Array<String>) {
    val env = getEnvVar("ENV", "dev")
    val port = getEnvVar("PORT", "7000").toInt()

    val parserContext = ParserContext(
        rattletrapExecutable = getRequiredEnvVar("RATTLETRAP_EXECUTABLE"),
        parserBufferSize = getEnvVar("PARSER_BUFFER_SIZE", "100000").toInt(),
        parserTimeoutSeconds = getEnvVar("PARSER_TIMEOUT", "20").toLong()
    )

    val webServer = WebServer()
    println("Listening on port $port...")
    webServer.start(env, port, parserContext)
}

private fun getRequiredEnvVar(key: String): String {
    return System.getenv(key) ?: throw NullPointerException("Environment variable '$key' not set")
}

private fun getEnvVar(key: String, default: String): String {
    return System.getenv(key) ?: default
}