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
import com.netflix.hollow.core.HollowConstants;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The HollowUpdatePlanner defines the logic responsible for interacting with a {@link HollowBlobRetriever} 
 * to create a {@link HollowUpdatePlan}.
 */
public class HollowUpdatePlanner {
    private static final Logger LOG = Logger.getLogger(HollowUpdatePlanner.class.getName());

    private final HollowConsumer.BlobRetriever transitionCreator;
    private final HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig;
    private final HollowConsumer.UpdatePlanBlobVerifier updatePlanBlobVerifier;
    
    @Deprecated
    public HollowUpdatePlanner(HollowBlobRetriever blobRetriever) {
        this(HollowClientConsumerBridge.consumerBlobRetrieverFor(blobRetriever));
    }

    public HollowUpdatePlanner(HollowConsumer.BlobRetriever blobRetriever) {
        this(blobRetriever, HollowConsumer.DoubleSnapshotConfig.DEFAULT_CONFIG);
    }

    public HollowUpdatePlanner(HollowConsumer.BlobRetriever transitionCreator, HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig) {
        this(transitionCreator, doubleSnapshotConfig, HollowConsumer.UpdatePlanBlobVerifier.DEFAULT_INSTANCE);
    }

    /**
     * @param transitionCreator A blob retriever implementation
     * @param doubleSnapshotConfig Double snapshot config
     * @param updatePlanBlobVerifier Update plan config
     */
    public HollowUpdatePlanner(HollowConsumer.BlobRetriever transitionCreator,
                               HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig,
                               HollowConsumer.UpdatePlanBlobVerifier updatePlanBlobVerifier) {
        this.transitionCreator = transitionCreator;
        this.doubleSnapshotConfig = doubleSnapshotConfig;
        this.updatePlanBlobVerifier = updatePlanBlobVerifier;
    }

    /**
     * @deprecated use {@link #planInitializingUpdate(HollowConsumer.VersionInfo)} instead.
     */
    @Deprecated
    public HollowUpdatePlan planInitializingUpdate(long desiredVersion) throws Exception {
        return planInitializingUpdate(new HollowConsumer.VersionInfo(desiredVersion));
    }

    /**
     * @return the sequence of steps necessary to initialize a hollow state engine to a given state.
     * @param desiredVersionInfo - The version to which the hollow state engine should be updated once the resultant steps are applied.
     * @throws Exception if the plan cannot be initialized
     */
    public HollowUpdatePlan planInitializingUpdate(HollowConsumer.VersionInfo desiredVersionInfo) throws Exception {
        return planUpdate(HollowConstants.VERSION_NONE, desiredVersionInfo, true);
    }

    /**
     * @deprecated use {@link #planUpdate(long, HollowConsumer.VersionInfo, boolean)} instead.
     */
    @Deprecated
    public HollowUpdatePlan planUpdate(long currentVersion, long desiredVersion, boolean allowSnapshot) throws Exception {
        return planUpdate(currentVersion, new HollowConsumer.VersionInfo(desiredVersion), allowSnapshot);
    }

    /**
     * Create an update plan from the current version to the desired version.
     * If the desired version was announced, and if announcement watcher impl has been initialized, the update plan config
     * will default to snapshot plans only retrieving a snapshot version that was successfully announced.
     *
     * @param currentVersion - The current version of the hollow state engine, or HollowConstants.VERSION_NONE if not yet initialized
     * @param desiredVersionInfo - The version info to which the hollow state engine should be updated once the resultant steps are applied.
     * @param allowSnapshot  - Allow a snapshot plan to be created if the destination version is not reachable
     * @return the sequence of steps necessary to bring a hollow state engine up to date.
     * @throws Exception if the plan cannot be updated
     */
    public HollowUpdatePlan planUpdate(long currentVersion, HollowConsumer.VersionInfo desiredVersionInfo, boolean allowSnapshot) throws Exception {
        long desiredVersion = desiredVersionInfo.getVersion();

        if(desiredVersion == currentVersion)
            return HollowUpdatePlan.DO_NOTHING;

        if (currentVersion == HollowConstants.VERSION_NONE)
            return snapshotPlan(desiredVersionInfo);

        HollowUpdatePlan deltaPlan = deltaPlan(currentVersion, desiredVersion, doubleSnapshotConfig.maxDeltasBeforeDoubleSnapshot());

        long deltaDestinationVersion = deltaPlan.destinationVersion(currentVersion);

        if(deltaDestinationVersion != desiredVersion && allowSnapshot) {
            LOG.log(Level.INFO, String.format(
                "Delta path cannot reach desired version %d (reaches %d), attempting snapshot fallback",
                desiredVersion, deltaDestinationVersion));

            HollowUpdatePlan snapshotPlan = snapshotPlan(desiredVersionInfo);
            long snapshotDestinationVersion = snapshotPlan.destinationVersion(currentVersion);

            if(snapshotDestinationVersion == desiredVersion
                    || ((deltaDestinationVersion > desiredVersion) && (snapshotDestinationVersion < desiredVersion))
                    || ((snapshotDestinationVersion < desiredVersion) && (snapshotDestinationVersion > deltaDestinationVersion))) {

                LOG.log(Level.INFO, String.format(
                    "Using snapshot transition fallback to version %d (snapshot transition - checksum validation not required)",
                    snapshotDestinationVersion));
                return snapshotPlan;
            }
        }

        return deltaPlan;
    }

    /**
     * Returns an update plan that if executed will update the client to a version that is either equal to or as close to but
     * less than the desired version as possible. This plan normally contains one snapshot transition and zero or more delta
     * transitions but if no previous versions were found then an empty plan, {@code HollowUpdatePlan.DO_NOTHING}, is returned.
     * @param desiredVersionInfo Version info for the desired version to which the client wishes to update to, or update to as close to as possible but lesser than this version
     * @return An update plan containing 1 snapshot transition and 0+ delta transitions if requested versions were found,
     *         or an empty plan, {@code HollowUpdatePlan.DO_NOTHING}, if no previous versions were found
     */
    private HollowUpdatePlan snapshotPlan(HollowConsumer.VersionInfo desiredVersionInfo) {
        HollowUpdatePlan plan = new HollowUpdatePlan();
        long desiredVersion = desiredVersionInfo.getVersion();
        long nearestPreviousSnapshotVersion = includeNearestSnapshot(plan, desiredVersionInfo);

        // The includeNearestSnapshot function returns a  snapshot version that is less than or equal to the desired version
        if(nearestPreviousSnapshotVersion > desiredVersion)
            return HollowUpdatePlan.DO_NOTHING;

        // If the nearest snapshot version is {@code HollowConstants.VERSION_LATEST} then no past snapshots were found, so
        // skip the delta planning and the update plan does nothing
        if(nearestPreviousSnapshotVersion == HollowConstants.VERSION_LATEST)
            return HollowUpdatePlan.DO_NOTHING;

        plan.appendPlan(deltaPlan(nearestPreviousSnapshotVersion, desiredVersion, Integer.MAX_VALUE));

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

        while (currentVersion > desiredVersion && transitionCounter < maxDeltas) {
            currentVersion = includeNextReverseDelta(plan, currentVersion);
            if (currentVersion != HollowConstants.VERSION_NONE)
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

        return HollowConstants.VERSION_LATEST;
    }

    private long includeNextReverseDelta(HollowUpdatePlan plan, long currentVersion) {
        HollowConsumer.Blob transition = transitionCreator.retrieveReverseDeltaBlob(currentVersion);
        if(transition != null) {
            plan.add(transition);
            return transition.getToVersion();
        }

        return HollowConstants.VERSION_NONE;
    }

    private long includeNearestSnapshot(HollowUpdatePlan plan, HollowConsumer.VersionInfo desiredVersionInfo) {
        long desiredVersion = desiredVersionInfo.getVersion();
        HollowConsumer.Blob transition = transitionCreator.retrieveSnapshotBlob(desiredVersion);
        if (transition != null) {
            // exact match with desired version
            if (transition.getToVersion() == desiredVersion) {
                plan.add(transition);
                return transition.getToVersion();
            }

            // else if update is to an announced version then add only announced versions to plan
            if (updatePlanBlobVerifier != null
                && updatePlanBlobVerifier.announcementVerificationEnabled()
                && desiredVersionInfo.wasAnnounced() != null
                && desiredVersionInfo.wasAnnounced().isPresent()
                && desiredVersionInfo.wasAnnounced().get()) {

                int lookback = 1;
                int maxLookback = updatePlanBlobVerifier.announcementVerificationMaxLookback();
                HollowConsumer.AnnouncementWatcher announcementWatcher = updatePlanBlobVerifier.announcementWatcher();
                while (lookback <= maxLookback) {
                    HollowConsumer.AnnouncementStatus announcementStatus = announcementWatcher == null
                            ? HollowConsumer.AnnouncementStatus.UNKNOWN : announcementWatcher.getVersionAnnouncementStatus(transition.getToVersion());

                    if (announcementStatus == null
                     || announcementStatus.equals(HollowConsumer.AnnouncementStatus.UNKNOWN)
                     || announcementStatus.equals(HollowConsumer.AnnouncementStatus.ANNOUNCED)) {
                        // backwards compatibility
                        if (announcementStatus == HollowConsumer.AnnouncementStatus.UNKNOWN) {
                            if (announcementWatcher == null) {
                                LOG.warning("HollowUpdatePlanner was not initialized with an announcement watcher so it does not support getVersionAnnouncementStatus. " +
                                        "Consumer will continue with the update but runs the risk of consuming a snapshot version that was not announced");
                            } else {
                                LOG.warning(String.format("Announcement watcher impl bound (%s) to HollowUpdatePlanner does not support getVersionAnnouncementStatus. " +
                                        "Consumer will continue with the update but runs the risk of consuming a snapshot version that was not announced", announcementWatcher.getClass().getName()));
                            }
                        } else if (announcementStatus == null) {
                            LOG.warning(String.format("Expecting a valid announcement stats for version(%s), but Announcement watcher impl (%s) " +
                                    "returned null", transition.getToVersion(), announcementWatcher.getClass().getName()));
                        }
                        plan.add(transition);
                        return transition.getToVersion();
                    }
                    lookback ++;
                    desiredVersion = transition.getToVersion() - 1; // try the next highest snapshot version less than the previous one
                    transition = transitionCreator.retrieveSnapshotBlob(desiredVersion);
                    if (transition == null) {
                        break;
                    }
                }
                LOG.warning("No past snapshot found within lookback period that corresponded to an announced version, maxLookback configured to " + maxLookback);
            } else {
                // if desired version is either not an announced version, or unknown whether it is an announced version (e.g. backwards compatibility)
                plan.add(transition);
                return transition.getToVersion();
            }
        }
        return HollowConstants.VERSION_LATEST;
    }

    /**
     * Plans an update with an additional repair transition if needed.
     *
     * This method creates a regular update plan (delta or snapshot based) and then
     * appends a REPAIR transition at the end. The repair transition uses the snapshot
     * at the target version to surgically repair any integrity violations.
     *
     * @param currentVersion version to transition from
     * @param desiredVersion version to transition to
     * @return update plan with delta/snapshot + repair transitions
     */
    public HollowUpdatePlan planUpdateWithRepair(long currentVersion, long desiredVersion) {
        // First get regular update plan
        HollowUpdatePlan basePlan;
        try {
            basePlan = planUpdate(currentVersion, desiredVersion, true);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to create base plan for repair", e);
            return null;
        }

        if (basePlan == null || basePlan == HollowUpdatePlan.DO_NOTHING) {
            return basePlan;
        }

        // Add repair transition at the end using snapshot at desired version
        HollowConsumer.Blob snapshotBlob = transitionCreator.retrieveSnapshotBlob(desiredVersion);
        if (snapshotBlob == null) {
            LOG.log(Level.WARNING,
                "Cannot create repair plan: snapshot not available for version " + desiredVersion);
            return basePlan;
        }

        // Create repair blob wrapper
        HollowConsumer.Blob repairBlob = new RepairBlob(desiredVersion, snapshotBlob);

        // Build new plan with repair
        return basePlan.withAdditionalTransition(repairBlob);
    }

    /**
     * Plans an update with a repair transition for version jumping.
     *
     * When delta chains are broken or unavailable, this method creates a REPAIR transition
     * that uses a snapshot to jump directly from currentVersion to desiredVersion.
     *
     * This differs from snapshot transitions in that:
     * - SNAPSHOT: fromVersion=-1 or VERSION_NONE, toVersion=N (initialization)
     * - REPAIR: fromVersion=M, toVersion=N (version jump using snapshot, M != N)
     *
     * @param currentVersion version to transition from
     * @param desiredVersion version to transition to
     * @return update plan with REPAIR transition for version jumping, or null if snapshot unavailable
     */
    public HollowUpdatePlan planUpdateWithRepairJump(long currentVersion, long desiredVersion) {
        // Special case: if already at desired version, nothing to do
        if (currentVersion == desiredVersion) {
            return HollowUpdatePlan.DO_NOTHING;
        }

        // Try to get snapshot at desired version for repair
        HollowConsumer.Blob snapshotBlob = transitionCreator.retrieveSnapshotBlob(desiredVersion);
        if (snapshotBlob == null) {
            LOG.log(Level.WARNING,
                "Cannot create repair plan: snapshot not available for version " + desiredVersion);

            // Fall back to regular update plan (might use deltas or snapshot)
            try {
                return planUpdate(currentVersion, desiredVersion, true);
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Failed to create fallback plan", e);
                return null;
            }
        }

        // Create repair blob with version jump
        HollowConsumer.Blob repairBlob = new RepairBlob(currentVersion, desiredVersion, snapshotBlob);

        // Create plan with single repair transition
        HollowUpdatePlan plan = new HollowUpdatePlan();
        plan.add(repairBlob);

        LOG.log(Level.INFO, String.format(
            "Created repair transition plan for version jump: %d -> %d",
            currentVersion, desiredVersion));

        return plan;
    }

    /**
     * Wrapper blob that represents a repair transition.
     * A repair transition uses snapshot data to jump between versions.
     *
     * Two modes:
     * 1. Same-version repair: fromVersion == toVersion (fix corruption at same version)
     * 2. Version jumping: fromVersion != toVersion (jump using snapshot when delta unavailable)
     */
    private static class RepairBlob extends HollowConsumer.Blob {
        private final HollowConsumer.Blob snapshotBlob;

        // Same-version repair constructor (existing behavior)
        RepairBlob(long version, HollowConsumer.Blob snapshotBlob) {
            super(version, version, BlobType.REPAIR);
            this.snapshotBlob = snapshotBlob;
        }

        // Version jumping constructor (new behavior for planUpdateWithRepairJump)
        RepairBlob(long fromVersion, long toVersion, HollowConsumer.Blob snapshotBlob) {
            super(fromVersion, toVersion, BlobType.REPAIR);
            this.snapshotBlob = snapshotBlob;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return snapshotBlob.getInputStream();
        }
    }
}
