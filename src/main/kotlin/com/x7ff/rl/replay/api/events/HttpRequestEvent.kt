package com.x7ff.rl.replay.api.events

import org.graylog2.gelfclient.GelfMessageBuilder

class HttpRequestEvent(
    val duration: Long,
    private val userAgent: String?,
    private val remoteAddress: String,
    private val method: String,
    private val path: String
) : Event("request_event") {

    override fun populateMessage(builder: GelfMessageBuilder) {
        builder.message(path)
        builder.additionalField("request_duration", duration / 1_000_000)
        builder.additionalField("user_agent", userAgent)
        builder.additionalField("remote_addr", remoteAddress)
        builder.additionalField("method", method)
    }

}