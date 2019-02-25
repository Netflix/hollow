package com.netflix.hollow.api.producer.metrics;

/**
 * Allows implementations to plug in custom reporting of producer metrics, while not enforcing that any or all metrics
 * are reported. For example, an implementation might only be insterested in cycle metrics but not announcement metrics, etc.
 */
public interface ProducerMetricsReporting {

    default void cycleMetricsReporting(CycleMetrics cycleMetrics) {
        // no-op
    }

    default void announcementMetricsReporting(AnnouncementMetrics announcementMetrics) {
        // no-op
    }
}
