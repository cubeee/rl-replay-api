package com.x7ff.rl.replay.api.events

import org.graylog2.gelfclient.GelfMessageBuilder
import org.graylog2.gelfclient.GelfMessageLevel

data class ParsingEvent(
    val replay: String,
    val replaySize: Int,
    val uploadName: String,
    val duration: Long,
    val success: Boolean
) : Event("parsing_event") {

    override fun populateMessage(builder: GelfMessageBuilder) {
        builder.message(replay)
        builder.additionalField("success", success)
        builder.additionalField("replay_size", replaySize)
        builder.additionalField("upload_name", uploadName)
        builder.additionalField("parsing_time", duration / 1_000_000)
        if (!success) {
            builder.level(GelfMessageLevel.ALERT)
        }
    }

}