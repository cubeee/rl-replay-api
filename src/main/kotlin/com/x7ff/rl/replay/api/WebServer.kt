package com.x7ff.rl.replay.api

import com.x7ff.rl.replay.api.model.response.ErrorResponse
import com.x7ff.rl.replay.api.model.response.ParseResponse
import com.x7ff.rl.replay.api.model.response.SuccessfulParseResponse
import com.x7ff.rl.replay.api.parser.RattletrapParser
import com.x7ff.rl.replay.api.parser.ReplayParser
import com.x7ff.rl.replay.api.transformer.RattletrapReplayTransformer
import com.x7ff.rl.replay.api.transformer.ReplayTransformer
import io.javalin.Javalin
import io.javalin.embeddedserver.Location
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.CRC32
import javax.servlet.http.HttpServletResponse

data class ParserContext(
    val rattletrapExecutable: String,
    val parserBufferSize: Int,
    val parserTimeoutSeconds: Long,
    val saveIncompatibleReplays: Boolean,
    val incompatibleReplaysPath: String
) {
    companion object {
        const val DEFAULT_PARSER_BUFFER_SIZE = 100_000
        const val DEFAULT_PARSER_TIMEOUT = 20
        const val DEFAULT_SAVE_INCOMPATIBLE_REPLAYS = false
        const val DEFAULT_INCOMPATIBLE_REPLAYS_DIR = "incompatible_replays"
    }
}

class WebServer {
    companion object {
        const val DEFAULT_ENV = "dev"
        const val DEFAULT_PORT = 7000

        private const val REPLAY_UPLOAD_PATH = "/upload"
        private const val REQUEST_FILE_NAME = "replay"
        private const val REPLAY_EXTENSION = ".replay"
    }

    private lateinit var parser: ReplayParser
    private lateinit var transformer: ReplayTransformer

    fun start(env: String, port: Int, parserContext: ParserContext) {
        println("Starting in '$env' environment...")

        parser = RattletrapParser(parserContext)
        transformer = RattletrapReplayTransformer()

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

                uploadedFile.content.use { input ->
                    val parseResponse = handleReplay(parserContext, input)

                    if (parseResponse is SuccessfulParseResponse) {
                        val transformed = transformer.transform(parseResponse.replay)
                        ctx.json(transformed)
                    } else {
                        ctx
                            .status(HttpServletResponse.SC_BAD_REQUEST)
                            .json(parseResponse)
                    }
                }
            } ?: run {
                ctx
                    .status(HttpServletResponse.SC_BAD_REQUEST)
                    .json(ErrorResponse("No replay file name given"))
            }
        }

        println("Listening on port $port...")
        app.start()
    }

    private fun handleReplay(parserContext: ParserContext, replayInput: InputStream): ParseResponse {
        val bytes = replayInput.readBytes()

        val parseResponse = parser.parseReplay(ByteArrayInputStream(bytes))
        if (parseResponse !is SuccessfulParseResponse && parserContext.saveIncompatibleReplays) {
            saveReplay(parserContext.incompatibleReplaysPath, bytes)
        }
        return parseResponse
    }

    private fun saveReplay(directory: String, bytes: ByteArray) {
        val id = with(CRC32()) {
            update(bytes)
            "%x".format(value)
        }

        val dir = File(directory)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val file = File(dir, "$id.replay")
        if (!file.exists()) {
            FileOutputStream(file).use { out ->
                out.write(bytes)
            }
        }
    }

}

fun main(args: Array<String>) {
    val env = getEnvVar("ENV", WebServer.DEFAULT_ENV)
    val port = getEnvVar("PORT", WebServer.DEFAULT_PORT.toString()).toInt()

    val parserContext = ParserContext(
        rattletrapExecutable =
            getRequiredEnvVar("RATTLETRAP_EXECUTABLE"),
        parserBufferSize =
            getEnvVar("PARSER_BUFFER_SIZE", ParserContext.DEFAULT_PARSER_BUFFER_SIZE.toString()).toInt(),
        parserTimeoutSeconds =
            getEnvVar("PARSER_TIMEOUT", ParserContext.DEFAULT_PARSER_TIMEOUT.toString()).toLong(),
        saveIncompatibleReplays =
            getEnvVar("SAVE_INCOMPATIBLE_REPLAYS", ParserContext.DEFAULT_SAVE_INCOMPATIBLE_REPLAYS.toString()).toBoolean(),
        incompatibleReplaysPath =
            getEnvVar("INCOMPATIBLE_REPLAYS_DIR", ParserContext.DEFAULT_INCOMPATIBLE_REPLAYS_DIR)
    )

    val webServer = WebServer()
    webServer.start(env, port, parserContext)
}

private fun getRequiredEnvVar(key: String): String {
    return System.getenv(key) ?: throw NullPointerException("Environment variable '$key' not set")
}

private fun getEnvVar(key: String, default: String): String {
    return System.getenv(key) ?: default
}