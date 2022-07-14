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
package com.netflix.hollow.api.client;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

/**
 * Implementations of this class will define what to do when various events happen before, during, and after updating
 * local in-memory copies of hollow data sets.
 * 
 * A default implementation, which does nothing, is available: {@link HollowUpdateListener#DEFAULT_LISTENER}.
 * 
 * @deprecated Implement the {@link HollowConsumer.RefreshListener} and/or {@link HollowConsumer.ObjectLongevityDetector} for 
 * use with the {@link HollowConsumer} instead.
 * 
 */
@Deprecated
public interface HollowUpdateListener extends HollowConsumer.ObjectLongevityDetector {

    /**
     * Called whenever a state engine is initialized (either because the first snapshot was applied, or because a
     * broken delta chain was found and a double snapshot occurred).
     *
     * This method should be used to initialize any indexing which is critical to keep in-sync with the data.
     *
     * @param api the API
     * @param stateEngine the state engine
     * @param version the version
     * @throws Exception if there was an error processing this event
     */
    void dataInitialized(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception;


    /**
     * Called whenever a state engine's data is updated.
     *
     * This method should be used to update any indexing which is critical to keep in-sync with the data.
     *
     * @param api the API
     * @param stateEngine the state engine
     * @param version the version
     * @throws Exception if there was an error processing this event
     */
    void dataUpdated(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception;

    /**
     * Indicates that a refresh has begun.  Generally useful for logging.
     *
     * @param currentVersion the current version
     * @param requestedVersion the requested version
     */
    void refreshStarted(long currentVersion, long requestedVersion);

    /**
     * Indicates that a refresh completed successfully.
     *
     * @param beforeVersion - The version when the refresh started
     * @param afterVersion - The version when the refresh completed
     * @param requestedVersion - The specific version which was requested
     */
    void refreshCompleted(long beforeVersion, long afterVersion, long requestedVersion);


    /**
     * Indicates that a refresh failed with an Exception.
     *
     * @param beforeVersion - The version when the refresh started
     * @param afterVersion - The version when the refresh completed
     * @param requestedVersion - The specific version which was requested
     * @param failureCause - The Exception which caused the failure.
     */
    void refreshFailed(long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause);


    /**
     * Called to indicate a transition was applied.  Generally useful for logging or tracing of applied updates.
     * 
     * @param transition The transition which was applied.
     */
    void transitionApplied(HollowBlob transition);

    HollowUpdateListener DEFAULT_LISTENER = new HollowUpdateListener() {
        @Override
        public void dataInitialized(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
        }
        @Override
        public void dataUpdated(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
        }
        @Override
        public void transitionApplied(HollowBlob transition) {
        }
        @Override
        public void refreshStarted(long currentVersion, long requestedVersion) {
        }
        @Override
        public void refreshCompleted(long beforeVersion, long afterVersion, long requestedVersion) {
        }
        @Override
        public void refreshFailed(long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause) {
        }
        @Override
        public void staleReferenceExistenceDetected(int count) {
        }
        @Override
        public void staleReferenceUsageDetected(int count) {
        }
    };

}
