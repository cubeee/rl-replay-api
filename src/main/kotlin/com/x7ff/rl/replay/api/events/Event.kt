package com.x7ff.rl.replay.api.events

import org.graylog2.gelfclient.GelfMessage
import org.graylog2.gelfclient.GelfMessageBuilder
import org.graylog2.gelfclient.GelfMessageVersion

abstract class Event(private val eventName: String) {

    abstract fun populateMessage(builder: GelfMessageBuilder)

    fun createGelfMessage(source: String?): GelfMessage {
        val builder = GelfMessageBuilder("", source, GelfMessageVersion.V1_1)
            .additionalField("name", eventName)
            .additionalField("source", source)
        populateMessage(builder)
        return builder.build()
    }

}