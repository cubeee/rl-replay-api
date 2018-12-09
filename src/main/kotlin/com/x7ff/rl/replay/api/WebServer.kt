package com.x7ff.rl.replay.api

import com.x7ff.parser.executeAndMeasureTimeNanos
import com.x7ff.parser.replay.Replay
import com.x7ff.rl.replay.api.events.EventLogger
import com.x7ff.rl.replay.api.events.EventLoggerConfig
import com.x7ff.rl.replay.api.events.HttpRequestEvent
import com.x7ff.rl.replay.api.events.ParsingEvent
import com.x7ff.rl.replay.api.model.response.ErrorResponse
import com.x7ff.rl.replay.api.model.response.PartiallySuccessfulParseResponse
import com.x7ff.rl.replay.api.model.response.SuccessfulParseResponse
import com.x7ff.rl.replay.api.parser.ReplayKtParser
import com.x7ff.rl.replay.api.transformer.MainReplayTransformer
import io.javalin.Context
import io.javalin.Javalin
import io.javalin.UploadedFile
import io.javalin.embeddedserver.Location
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.CRC32
import javax.servlet.http.HttpServletResponse

data class ParserContext(
    val saveIncompatibleReplays: Boolean,
    val incompatibleReplaysPath: String
) {
    companion object {
        const val DEFAULT_SAVE_INCOMPATIBLE_REPLAYS = false
        const val DEFAULT_INCOMPATIBLE_REPLAYS_DIR = "incompatible_replays"
    }
}

class WebServer(
    private val eventLogger: EventLogger?
) {
    companion object {
        private const val REPLAY_UPLOAD_PATH = "/upload"
        private const val REQUEST_FILE_NAME = "replay"
        private const val REPLAY_EXTENSION = ".replay"

        const val DEFAULT_ENV = "dev"
        const val DEFAULT_PORT = 7000

        private val invalidExtensionError =
            ErrorResponse("Invalid replay file uploaded. Expecting files with '$REPLAY_EXTENSION' extension")
    }

    private val parser = ReplayKtParser()
    private val transformer = MainReplayTransformer()

    fun start(env: String, port: Int, parserContext: ParserContext) {
        println("Starting in '$env' environment...")

        val app = Javalin.create()
        app.port(port)

        when (env) {
            "prod" -> app.enableStaticFiles("/public")
            else -> app.enableStaticFiles("src/main/resources/public/", Location.EXTERNAL)
        }

        app.post(REPLAY_UPLOAD_PATH) { ctx ->
            val uploadedFile = ctx.uploadedFile(REQUEST_FILE_NAME)
            when (uploadedFile) {
                is UploadedFile -> handleUpload(ctx, uploadedFile, parserContext)
                else -> ctx
                    .status(HttpServletResponse.SC_BAD_REQUEST)
                    .json(ErrorResponse("No replay file name given"))
            }
        }

        app.before { ctx -> ctx.attribute("req_start", System.nanoTime()) }
        app.after { ctx ->
            val startTime: Long? = ctx.attribute("req_start")
            startTime?.let { start ->
                eventLogger?.logEvent(HttpRequestEvent(
                    duration = System.nanoTime() - start,
                    userAgent = ctx.userAgent(),
                    remoteAddress = ctx.request().remoteAddr,
                    method = ctx.method(),
                    path = ctx.path()
                ))
            }
        }

        println("Listening on port $port...")
        app.start()
    }

    private fun handleUpload(ctx: Context, uploadedFile: UploadedFile, parserContext: ParserContext) {
        when (uploadedFile.extension != REPLAY_EXTENSION) {
            true -> ctx.status(HttpServletResponse.SC_BAD_REQUEST).json(invalidExtensionError)
            else -> uploadedFile.content
                .use { input -> handleReplay(ctx, uploadedFile.name, input, parserContext) }
        }
    }

    private fun handleReplay(ctx: Context, fileName: String, input: InputStream, parserContext: ParserContext) {
        val bytes = input.readBytes()

        val (response, parsingTime) = executeAndMeasureTimeNanos {
            parser.parseReplay(bytes)
        }

        when (response) {
            is SuccessfulParseResponse<Replay>, is PartiallySuccessfulParseResponse<Replay> -> {
                val success = response is SuccessfulParseResponse<Replay>
                eventLogger?.logEvent(ParsingEvent(fileName, bytes.size, parsingTime, success = success))
                ctx.json(transformer.transform(fileName, response.replay!!))
            }
            else -> {
                saveReplay(parserContext.incompatibleReplaysPath, bytes)
                eventLogger?.logEvent(ParsingEvent(fileName, bytes.size, parsingTime, success = false))
                ctx.status(HttpServletResponse.SC_BAD_REQUEST).json(response)
            }
        }
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

    val eventLoggerConfig = with(EventLoggerConfig) {
        val enabled = getEnvVar("EVENT_LOGGING_ENABLED", DEFAULT_ENABLED).toBoolean()
        when (enabled) {
            true -> EventLoggerConfig(
                host = getEnvVar("EVENT_LOGGING_HOST", DEFAULT_HOST),
                port = getEnvVar("EVENT_LOGGING_PORT", DEFAULT_PORT).toInt(),
                source = getEnvVar("EVENT_LOGGING_SOURCE", DEFAULT_SOURCE)
            )
            else -> null
        }
    }

    val parserContext = ParserContext(
        saveIncompatibleReplays = getEnvVar("SAVE_INCOMPATIBLE_REPLAYS",
            ParserContext.DEFAULT_SAVE_INCOMPATIBLE_REPLAYS.toString()).toBoolean(),
        incompatibleReplaysPath = getEnvVar("INCOMPATIBLE_REPLAYS_DIR",
            ParserContext.DEFAULT_INCOMPATIBLE_REPLAYS_DIR)
    )

    val eventLogger = when {
        eventLoggerConfig != null -> EventLogger(eventLoggerConfig)
        else -> null
    }

    WebServer(eventLogger).start(env, port, parserContext)
}

private fun getEnvVar(key: String, default: String): String {
    return System.getenv(key) ?: default
}