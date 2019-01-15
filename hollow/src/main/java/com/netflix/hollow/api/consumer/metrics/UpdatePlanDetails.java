package com.netflix.hollow.api.consumer.metrics;

import com.netflix.hollow.api.consumer.HollowConsumer.Blob.BlobType;
import java.util.List;

/**
 * A class that contains details of the update plan that may be useful to report as metrics or logs. These details are computed
 * during execution of the update plan {@code HollowClientUpdater} to {@code TransitionAwareRefreshListener}s.
 */
public class UpdatePlanDetails {

    long beforeVersion;
    long desiredVersion;
    int numTransitions; // how many transitions in this update Plan

    List<BlobType> transitionSequence;
    int numSuccessfulTransitions;
}
