package com.netflix.hollow.api.consumer.metrics;

/**
 * An interface to facilitate reporting of Hollow Consumer refresh metrics.
 * <p>
 * At different stages of Hollow Consumer refresh for eg. refresh start or refresh end, the methods in this interface
 * are called with computed metrics. Hollow consumers can implement custom metrics reporting behavior by implementing
 * these methods.
 */
public interface RefreshMetricsReporting {

    /**
     * Metrics for a refresh (such as duration, status, etc.) are passed to this method at the end of successful and
     * failed refreshes. It allows classes to implement custom metrics reporting behavior.
     *
     * @param refreshMetrics Object containing refresh metrics such as duration, consecutive failures, etc.
     * @see com.netflix.hollow.api.consumer.metrics.ConsumerRefreshMetrics
     */
    void refreshEndMetricsReporting(ConsumerRefreshMetrics refreshMetrics);
}
