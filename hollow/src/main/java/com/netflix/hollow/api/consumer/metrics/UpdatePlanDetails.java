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
