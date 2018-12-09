package com.x7ff.rl.replay.api.events

import com.codahale.metrics.MetricRegistry
import org.graylog2.gelfclient.GelfConfiguration
import org.graylog2.gelfclient.GelfTransports
import org.graylog2.gelfclient.transport.GelfTransport
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

class EventLogger(
    private val config: EventLoggerConfig
) {
    private val metricRegistry = MetricRegistry()

    private val eventTransport: GelfTransport by lazy {
        val configuration = GelfConfiguration(InetSocketAddress(config.host, config.port))
            .transport(GelfTransports.UDP)
            .queueSize(256)
            .connectTimeout(5000)
            .reconnectDelay(1000)
            .tcpNoDelay(true)
            .sendBufferSize(32768)
        GelfTransports.create(configuration)
    }

    private val eventReporter: GelfReporter by lazy {
        GelfReporter(
            gelfTransport = eventTransport,
            registry = metricRegistry,
            source = config.source
        )
    }

    private val requestTimer = metricRegistry.timer("metric_request_time")
    private val parsingTimer = metricRegistry.timer("metric_parsing_time")

    init {
        eventReporter.start(15, TimeUnit.SECONDS)
    }

    fun logEvent(event: Event): Boolean {
        when (event) {
            is ParsingEvent -> parsingTimer.update(event.duration, TimeUnit.NANOSECONDS)
            is HttpRequestEvent -> requestTimer.update(event.duration, TimeUnit.NANOSECONDS)
        }
        val message = event.createGelfMessage(config.source)
        return eventTransport.trySend(message)
    }

}