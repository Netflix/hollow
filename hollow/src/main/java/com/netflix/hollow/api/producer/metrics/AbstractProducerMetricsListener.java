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
package com.netflix.hollow.api.producer.metrics;

import com.netflix.hollow.api.producer.AbstractHollowProducerListener;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import java.time.Duration;
import java.util.OptionalLong;

/**
 * A class for computing Hollow Producer metrics, that requires extending subclasses to implement custom metrics reporting.
 * <p>
 * This class computes Hollow Producer metrics by listening to the stages of a producer's lifecycle. This class implements the
 * {@code ProducerMetricsReporting} interface which enforces concrete subclasses to implement custom metrics reporting behavior.
 */
public abstract class AbstractProducerMetricsListener extends AbstractHollowProducerListener implements
        ProducerMetricsReporting {

    private AnnouncementMetrics.Builder announcementMetricsBuilder;

    // visible for testing
    private long consecutiveFailures;
    OptionalLong lastCycleSuccessTimeNanoOptional;
    OptionalLong lastAnnouncementSuccessTimeNanoOptional;


    public AbstractProducerMetricsListener() {
        consecutiveFailures = 0l;
        lastCycleSuccessTimeNanoOptional = OptionalLong.empty();
        lastAnnouncementSuccessTimeNanoOptional = OptionalLong.empty();
    }

    @Override
    public void onAnnouncementStart(long version) {
        announcementMetricsBuilder = new AnnouncementMetrics.Builder();
    }

    /**
     * Reports metrics for when cycle is skipped due to reasons such as the producer not being the leader in a multiple-producer setting.
     * In a multiple producer setting, leader election typically favors long-lived leaders to avoid producer runs from frequently requiring
     * to reload the full state before publishing data. When a cycle is skipped because the producer wasn't primary, the current value of
     * no. of consecutive failures and most recent cycle success time are retained, and no cycle status (success or fail) is reported.
     * @param reason Reason why the run was skipped
     */
    @Override
    public void onCycleSkip(CycleSkipReason reason) {
        CycleMetrics.Builder cycleMetricsBuilder = new CycleMetrics.Builder();

        cycleMetricsBuilder.setConsecutiveFailures(consecutiveFailures);
        lastCycleSuccessTimeNanoOptional.ifPresent(cycleMetricsBuilder::setLastCycleSuccessTimeNano);
        // isCycleSuccess and cycleDurationMillis are not set for skipped cycles

        cycleMetricsReporting(cycleMetricsBuilder.build());
    }

    /**
     * Reports announcement-related metrics.
     * @param status Indicates whether the announcement succeeded of failed
     * @param readState Hollow data state that is being published, used in this method for computing data size
     * @param version Version of data that was announced
     * @param elapsed Announcement start to end duration
     */
    @Override
    public void onAnnouncementComplete(com.netflix.hollow.api.producer.Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
        boolean isAnnouncementSuccess = false;
        long dataSizeBytes = 0l;

        if (status.getType() == com.netflix.hollow.api.producer.Status.StatusType.SUCCESS) {
            isAnnouncementSuccess = true;
            lastAnnouncementSuccessTimeNanoOptional = OptionalLong.of(System.nanoTime());
        }

        HollowReadStateEngine stateEngine = readState.getStateEngine();
        dataSizeBytes = stateEngine.calcApproxDataSize();

        announcementMetricsBuilder
                .setDataSizeBytes(dataSizeBytes)
                .setIsAnnouncementSuccess(isAnnouncementSuccess)
                .setAnnouncementDurationMillis(elapsed.toMillis());
        lastAnnouncementSuccessTimeNanoOptional.ifPresent(announcementMetricsBuilder::setLastAnnouncementSuccessTimeNano);

        announcementMetricsReporting(announcementMetricsBuilder.build());
    }

    /**
     * On cycle completion this method reports cycle metrics.
     * @param status Whether the cycle succeeded or failed
     * @param readState Hollow data state published by cycle, not used here because data size is known from when announcement completed
     * @param version Version of data that was published in this cycle
     * @param elapsed Cycle start to end duration
     */
    @Override
    public void onCycleComplete(com.netflix.hollow.api.producer.Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
        boolean isCycleSuccess;
        long cycleEndTimeNano = System.nanoTime();

        if (status.getType() == com.netflix.hollow.api.producer.Status.StatusType.SUCCESS) {
            isCycleSuccess = true;
            consecutiveFailures = 0l;
            lastCycleSuccessTimeNanoOptional = OptionalLong.of(cycleEndTimeNano);
        } else {
            isCycleSuccess = false;
            consecutiveFailures ++;
        }

        CycleMetrics.Builder cycleMetricsBuilder = new CycleMetrics.Builder()
                .setConsecutiveFailures(consecutiveFailures)
                .setCycleDurationMillis(elapsed.toMillis())
                .setIsCycleSuccess(isCycleSuccess);
        lastCycleSuccessTimeNanoOptional.ifPresent(cycleMetricsBuilder::setLastCycleSuccessTimeNano);

        cycleMetricsReporting(cycleMetricsBuilder.build());
    }
}
