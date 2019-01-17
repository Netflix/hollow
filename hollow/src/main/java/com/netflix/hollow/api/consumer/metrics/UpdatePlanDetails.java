package com.netflix.hollow.api.consumer.metrics;

import com.netflix.hollow.api.consumer.HollowConsumer.Blob.BlobType;
import java.util.List;

/**
 * A class that contains details of the consumer refresh update plan that may be useful to report as metrics or logs.
 * These details are computed in {@code AbstractRefreshMetricsListener} during execution of the update plan.
 */
public class UpdatePlanDetails {

    long beforeVersion;
    long desiredVersion;
    List<BlobType> transitionSequence;
    int numSuccessfulTransitions;
}
