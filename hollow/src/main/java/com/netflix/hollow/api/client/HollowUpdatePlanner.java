/*
 *
 *  Copyright 2016 Netflix, Inc.
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

/**
 * The HollowUpdatePlanner defines the logic responsible for interacting with a {@link HollowBlobRetriever} 
 * to create a {@link HollowUpdatePlan}.
 */
public class HollowUpdatePlanner {

    private final HollowConsumer.BlobRetriever transitionCreator;
    private final HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig;
    
    @Deprecated
    public HollowUpdatePlanner(HollowBlobRetriever blobRetriever) {
        this(HollowClientConsumerBridge.consumerBlobRetrieverFor(blobRetriever));
    }
    
    public HollowUpdatePlanner(HollowConsumer.BlobRetriever blobRetriever) {
        this(blobRetriever, new HollowConsumer.DoubleSnapshotConfig() {
            @Override
            public int maxDeltasBeforeDoubleSnapshot() {
                return 32;
            }
            
            @Override
            public boolean allowDoubleSnapshot() {
                return true;
            }
        });
    }

    public HollowUpdatePlanner(HollowConsumer.BlobRetriever transitionCreator, HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig) {
        this.transitionCreator = transitionCreator;
        this.doubleSnapshotConfig = doubleSnapshotConfig;
    }

    /**
     * Returns the sequence of steps necessary to initialize a hollow state engine to a given state.
     *
     * @param desiredVersion - The version to which the hollow state engine should be updated once the resultant steps are applied.
     */
    public HollowUpdatePlan planInitializingUpdate(long desiredVersion) throws Exception {
        return planUpdate(Long.MIN_VALUE, desiredVersion, true);
    }

    /**
     * Returns the sequence of steps necessary to bring a hollow state engine up to date.
     *
     * @param currentVersion - The current version of the hollow state engine, or Long.MIN_VALUE if not yet initialized
     * @param desiredVersion - The version to which the hollow state engine should be updated once the resultant steps are applied.
     * @param allowSnapshot  - Allow a snapshot plan to be created if the destination version is not reachable
     */
    public HollowUpdatePlan planUpdate(long currentVersion, long desiredVersion, boolean allowSnapshot) throws Exception {
        if(desiredVersion == currentVersion)
            return HollowUpdatePlan.DO_NOTHING;

        if(currentVersion == Long.MIN_VALUE)
            return snapshotPlan(desiredVersion);

        HollowUpdatePlan deltaPlan = deltaPlan(currentVersion, desiredVersion, doubleSnapshotConfig.maxDeltasBeforeDoubleSnapshot());

        long deltaDestinationVersion = deltaPlan.destinationVersion(currentVersion);

        if(deltaDestinationVersion != desiredVersion && allowSnapshot) {
            HollowUpdatePlan snapshotPlan = snapshotPlan(desiredVersion);
            long snapshotDestinationVersion = snapshotPlan.destinationVersion(currentVersion);

            if(snapshotDestinationVersion == desiredVersion
                    || ((deltaDestinationVersion > desiredVersion) && (snapshotDestinationVersion < desiredVersion))
                    || ((snapshotDestinationVersion < desiredVersion) && (snapshotDestinationVersion > deltaDestinationVersion)))

                return snapshotPlan;
        }

        return deltaPlan;
    }

    private HollowUpdatePlan snapshotPlan(long desiredVersion) {
        HollowUpdatePlan plan = new HollowUpdatePlan();
        long currentVersion = includeNearestSnapshot(plan, desiredVersion);

        if(currentVersion > desiredVersion)
            return HollowUpdatePlan.DO_NOTHING;

        plan.appendPlan(deltaPlan(currentVersion, desiredVersion, Integer.MAX_VALUE));

        return plan;
    }

    private HollowUpdatePlan deltaPlan(long currentVersion, long desiredVersion, int maxDeltas) {
        HollowUpdatePlan plan = new HollowUpdatePlan();
        if(currentVersion < desiredVersion) {
            applyForwardDeltasToPlan(currentVersion, desiredVersion, plan, maxDeltas);
        } else if(currentVersion > desiredVersion) {
            applyReverseDeltasToPlan(currentVersion, desiredVersion, plan, maxDeltas);
        }

        return plan;
    }

    private long applyForwardDeltasToPlan(long currentVersion, long desiredVersion, HollowUpdatePlan plan, int maxDeltas) {
        int transitionCounter = 0;

        while(currentVersion < desiredVersion && transitionCounter < maxDeltas) {
            currentVersion = includeNextDelta(plan, currentVersion, desiredVersion);
            transitionCounter++;
        }
        return currentVersion;
    };

    private long applyReverseDeltasToPlan(long currentVersion, long desiredVersion, HollowUpdatePlan plan, int maxDeltas) {
        long achievedVersion = currentVersion;
        int transitionCounter = 0;

        while(currentVersion > desiredVersion && transitionCounter < maxDeltas) {
            currentVersion = includeNextReverseDelta(plan, currentVersion);
            if(currentVersion > Long.MIN_VALUE)
                achievedVersion = currentVersion;
            transitionCounter++;
        }

        return achievedVersion;
    }

    /**
     * Includes the next delta only if it will not take us *after* the desired version
     */
    private long includeNextDelta(HollowUpdatePlan plan, long currentVersion, long desiredVersion) {
        HollowConsumer.Blob transition = transitionCreator.retrieveDeltaBlob(currentVersion);

        if(transition != null) {
            if(transition.getToVersion() <= desiredVersion) {
                plan.add(transition);
            }

            return transition.getToVersion();
        }

        return Long.MAX_VALUE;
    }

    private long includeNextReverseDelta(HollowUpdatePlan plan, long currentVersion) {
        HollowConsumer.Blob transition = transitionCreator.retrieveReverseDeltaBlob(currentVersion);
        if(transition != null) {
            plan.add(transition);
            return transition.getToVersion();
        }

        return Long.MIN_VALUE;
    }

    private long includeNearestSnapshot(HollowUpdatePlan plan, long desiredVersion) {
        HollowConsumer.Blob transition = transitionCreator.retrieveSnapshotBlob(desiredVersion);
        if(transition != null) {
            plan.add(transition);
            return transition.getToVersion();
        }

        return Long.MAX_VALUE;
    }

}
