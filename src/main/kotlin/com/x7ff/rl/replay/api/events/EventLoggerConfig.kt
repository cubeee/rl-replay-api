package com.x7ff.rl.replay.api.events

data class EventLoggerConfig(
    val host: String,
    val port: Int,
    val source: String,
    val enableTls: Boolean
) {
    companion object {
        const val DEFAULT_ENABLED = "false"
        const val DEFAULT_HOST = "localhost"
        const val DEFAULT_PORT = "12201"
        const val DEFAULT_SOURCE = "rl-replay-api"
        const val DEFAULT_ENABLE_TLS = "false"
    }
}
