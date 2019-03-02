/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.api.consumer.metrics;

/**
 * An interface to facilitate reporting of Hollow Consumer refresh metrics.
 * <p>
 * At different stages of Hollow Consumer refresh for eg. refresh start and refresh end, the methods in this interface
 * are called with computed metrics. Hollow consumers can implement custom metrics reporting behavior by implementing
 * these methods.
 */
public interface RefreshMetricsReporting {

    /**
     * Metrics for a refresh (such as duration, status, etc.) are passed to this method at the end of successful and
     * failed refreshes. It allows classes to implement custom metrics reporting behavior.
     *
     * @param refreshMetrics Object containing refresh metrics such as duration, consecutive failures, etc.
     *
     * @see com.netflix.hollow.api.consumer.metrics.ConsumerRefreshMetrics
     */
    void refreshEndMetricsReporting(ConsumerRefreshMetrics refreshMetrics);
}
