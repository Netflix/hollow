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
import java.util.HashSet;

/**
 * Tracks the blobs which failed to be successfully applied by a HollowClient.  Blobs logged in this
 * tracker will not be attempted again.
 */
public class FailedTransitionTracker {

    private final HashSet<Long> failedSnapshotTransitions;
    private final HashSet<DeltaTransition> failedDeltaTransitions;

    public FailedTransitionTracker() {
        this.failedSnapshotTransitions = new HashSet<Long>();
        this.failedDeltaTransitions = new HashSet<DeltaTransition>();
    }

    public void markAllTransitionsAsFailed(HollowUpdatePlan plan) {
        for(HollowConsumer.Blob transition : plan)
            markFailedTransition(transition);
    }

    public void markFailedTransition(HollowConsumer.Blob transition) {
        if(transition.isSnapshot()) {
            failedSnapshotTransitions.add(transition.getToVersion());
        } else {
            failedDeltaTransitions.add(delta(transition));
        }
    }

    public boolean anyTransitionWasFailed(HollowUpdatePlan plan) {
        for(HollowConsumer.Blob transition : plan) {
            if(transitionWasFailed(transition))
                return true;
        }
        return false;
    }

    /**
     * @return the number of failed snapshot transitions.
     */
    public int getNumFailedSnapshotTransitions() {
        return this.failedSnapshotTransitions.size();
    }

    /**
     * @return the number of failed delta transitions.
     */
    public int getNumFailedDeltaTransitions() {
        return this.failedDeltaTransitions.size();
    }

    /**
     * Clear all failing transitions.
     */
    public void clear() {
        failedSnapshotTransitions.clear();
        failedDeltaTransitions.clear();
    }

    private boolean transitionWasFailed(HollowConsumer.Blob transition) {
        if(transition.isSnapshot())
            return failedSnapshotTransitions.contains(transition.getToVersion());

        return failedDeltaTransitions.contains(delta(transition));
    }

    private DeltaTransition delta(HollowConsumer.Blob transition) {
        return new DeltaTransition(transition.getFromVersion(), transition.getToVersion());
    }

    private class DeltaTransition {
        private final long fromState;
        private final long toState;

        DeltaTransition(long fromState, long toState) {
            this.fromState = fromState;
            this.toState = toState;
        }

        @Override
        public int hashCode() {
            return (int) fromState
                    ^ (int) (fromState >> 32)
                    ^ (int) toState
                    ^ (int) (toState >> 32);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof DeltaTransition) {
                DeltaTransition other = (DeltaTransition) obj;
                return other.fromState == fromState && other.toState == toState;
            }
            return false;
        }

    }

}
