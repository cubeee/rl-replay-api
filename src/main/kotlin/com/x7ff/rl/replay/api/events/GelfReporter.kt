/**
 * Copyright © 2016 Graylog, Inc. (hello@graylog.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.x7ff.rl.replay.api.events

import com.codahale.metrics.*
import com.codahale.metrics.Timer
import org.graylog.metrics.GelfReporter
import org.graylog2.gelfclient.GelfMessageBuilder
import org.graylog2.gelfclient.GelfMessageLevel
import org.graylog2.gelfclient.transport.GelfTransport
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * <p>A kotlin implementation of Graylog's <a href="https://github.com/graylog-labs/metrics-gelf">GelfReporter</a>.</p>
 * <p>Changes made to the class:</p>
 * <ul>
 *     <li>Builder removed and constructor made public to allow use of custom instances of {@link GelfTransport}</li>
 * </ul>
 */
class GelfReporter(
    registry: MetricRegistry,
    rateUnit: TimeUnit = TimeUnit.SECONDS,
    durationUnit: TimeUnit = TimeUnit.MILLISECONDS,
    filter: MetricFilter = MetricFilter.ALL,
    private val gelfTransport: GelfTransport,
    private val clock: Clock = Clock.defaultClock(),
    private val prefix: String? = null,
    private val messageLevel: GelfMessageLevel = GelfMessageLevel.INFO,
    private val source: String? = null,
    private val additionalFields: Map<String, Any>? = mutableMapOf()
) : ScheduledReporter(registry, "gelf-reporter", filter, rateUnit, durationUnit) {

    private val logger = LoggerFactory.getLogger(GelfReporter::class.java)

    override fun report(
        gauges: SortedMap<String, Gauge<Any>>,
        counters: SortedMap<String, Counter>,
        histograms: SortedMap<String, Histogram>,
        meters: SortedMap<String, Meter>,
        timers: SortedMap<String, Timer>
    ) {
        if (gauges.isEmpty() && counters.isEmpty() && histograms.isEmpty() && meters.isEmpty() && timers.isEmpty()) {
            logger.debug("All metrics are empty, nothing to report.")
            return
        }

        val timestamp = clock.time

        for ((key, value) in gauges) {
            if (value.value != null) {
                val name = prefix(key)
                sendGauge(timestamp, name, value)
            }
        }

        for ((key, value) in counters) {
            val name = prefix(key)
            sendCounter(timestamp, name, value)
        }

        for ((key, value) in histograms) {
            val name = prefix(key)
            sendHistogram(timestamp, name, value)
        }

        for ((key, value) in meters) {
            val name = prefix(key)
            sendMeter(timestamp, name, value)
        }

        for ((key, value) in timers) {
            val name = prefix(key)
            sendTimer(timestamp, name, value)
        }
    }

    override fun stop() {
        super.stop()
        gelfTransport.stop()
    }

    private fun sendTimer(timestamp: Long, name: String, timer: Timer) {
        val snapshot = timer.snapshot
        val message = GelfMessageBuilder("name=$name type=TIMER", source)
            .timestamp(timestamp)
            .level(messageLevel)
            .additionalFields(additionalFields)
            .additionalField("name", name)
            .additionalField("type", "TIMER")
            .additionalField("count", timer.count)
            .additionalField("min", convertDuration(snapshot.min.toDouble()))
            .additionalField("max", convertDuration(snapshot.max.toDouble()))
            .additionalField("mean", convertDuration(snapshot.mean))
            .additionalField("stddev", convertDuration(snapshot.stdDev))
            .additionalField("median", convertDuration(snapshot.median))
            .additionalField("p75", convertDuration(snapshot.get75thPercentile()))
            .additionalField("p95", convertDuration(snapshot.get95thPercentile()))
            .additionalField("p98", convertDuration(snapshot.get98thPercentile()))
            .additionalField("p99", convertDuration(snapshot.get99thPercentile()))
            .additionalField("p999", convertDuration(snapshot.get999thPercentile()))
            .additionalField("duration_unit", durationUnit)
            .additionalField("mean_rate", convertRate(timer.meanRate))
            .additionalField("m1", convertRate(timer.oneMinuteRate))
            .additionalField("m5", convertRate(timer.fiveMinuteRate))
            .additionalField("m15", convertRate(timer.fifteenMinuteRate))
            .additionalField("rate_unit", rateUnit)

        gelfTransport.trySend(message.build())
    }

    private fun sendMeter(timestamp: Long, name: String, meter: Meter) {
        val message = GelfMessageBuilder("name=$name type=METER", source)
            .timestamp(timestamp)
            .level(messageLevel)
            .additionalFields(additionalFields)
            .additionalField("name", name)
            .additionalField("type", "METER")
            .additionalField("count", meter.count)
            .additionalField("mean_rate", convertRate(meter.meanRate))
            .additionalField("m1", convertRate(meter.oneMinuteRate))
            .additionalField("m5", convertRate(meter.fiveMinuteRate))
            .additionalField("m15", convertRate(meter.fifteenMinuteRate))
            .additionalField("rate_unit", rateUnit)

        gelfTransport.trySend(message.build())
    }

    private fun sendHistogram(timestamp: Long, name: String, histogram: Histogram) {
        val snapshot = histogram.snapshot
        val message = GelfMessageBuilder("name=$name type=HISTOGRAM", source)
            .timestamp(timestamp)
            .level(messageLevel)
            .additionalFields(additionalFields)
            .additionalField("name", name)
            .additionalField("type", "HISTOGRAM")
            .additionalField("count", histogram.count)
            .additionalField("min", snapshot.min)
            .additionalField("max", snapshot.max)
            .additionalField("mean", snapshot.mean)
            .additionalField("stddev", snapshot.stdDev)
            .additionalField("median", snapshot.median)
            .additionalField("p75", snapshot.get75thPercentile())
            .additionalField("p95", snapshot.get95thPercentile())
            .additionalField("p98", snapshot.get98thPercentile())
            .additionalField("p99", snapshot.get99thPercentile())
            .additionalField("p999", snapshot.get999thPercentile())

        gelfTransport.trySend(message.build())
    }

    private fun sendCounter(timestamp: Long, name: String, counter: Counter) {
        val message = GelfMessageBuilder("name=$name type=COUNTER", source)
            .timestamp(timestamp)
            .level(messageLevel)
            .additionalFields(additionalFields)
            .additionalField("name", name)
            .additionalField("type", "COUNTER")
            .additionalField("count", counter.count)

        gelfTransport.trySend(message.build())
    }

    private fun sendGauge(timestamp: Long, name: String, gauge: Gauge<*>) {
        val message = GelfMessageBuilder("name=$name type=GAUGE", source)
            .timestamp(timestamp)
            .level(messageLevel)
            .additionalFields(additionalFields)
            .additionalField("name", name)
            .additionalField("type", "GAUGE")
            .additionalField("value", gauge.value)

        gelfTransport.trySend(message.build())
    }

    private fun prefix(vararg components: String) = MetricRegistry.name(prefix, *components)

}