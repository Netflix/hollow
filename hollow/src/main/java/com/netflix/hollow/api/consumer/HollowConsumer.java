/*
 *
 *  Copyright 2017 Netflix, Inc.
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
package com.netflix.hollow.api.consumer;

import com.netflix.hollow.api.client.FailedTransitionTracker;
import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.client.HollowClientUpdater;
import com.netflix.hollow.api.client.StaleHollowReferenceDetector;
import com.netflix.hollow.api.codegen.HollowAPIClassJavaGenerator;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.metrics.HollowConsumerMetrics;
import com.netflix.hollow.api.metrics.HollowMetricsCollector;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.util.DefaultHashCodeFinder;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.tools.history.HollowHistory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 *
 * A HollowConsumer is the top-level class used by consumers of Hollow data to initialize and keep up-to-date a local in-memory
 * copy of a hollow dataset.  The interactions between the "blob" transition store and announcement listener are defined by
 * this class, and the implementations of the data retrieval, announcement mechanism are abstracted in the interfaces which
 * are provided to this class.
 *
 * To obtain a HollowConsumer, you should use a builder pattern, for example:
 *
 * <pre>
 * {@code
 *
 * HollowConsumer consumer = HollowConsumer.withBlobRetriever(retriever)
 *                                         .withAnnouncementWatcher(watcher)
 *                                         .withGeneratedAPIClass(MovieAPI.class)
 *                                         .build();
 * }
 * </pre>
 *
 * The following components are injectable, but only an implementation of the HollowConsumer.BlobRetriever is
 * required to be injected, all other components are optional. :
 *
 * <dl>
 *      <dt>{@link HollowConsumer.BlobRetriever}</dt>
 *      <dd>Implementations of this class define how to retrieve blob data from the blob store.</dd>
 *
 *      <dt>{@link HollowConsumer.AnnouncementWatcher}</dt>
 *      <dd>Implementations of this class define the announcement mechanism, which is used to track the version of the
 *          currently announced state.  It's also expected that implementations will trigger a refresh each time current
 *          data version is updated.</dd>
 *
 *      <dt>a List of {@link HollowConsumer.RefreshListener}s</dt>
 *      <dd>RefreshListener implementations will define what to do when various events happen before, during, and after updating
 *          local in-memory copies of hollow data sets.</dd>
 *
 *      <dt>the Class representing a generated Hollow API</dt>
 *      <dd>Defines how to create a {@link HollowAPI} for the dataset, useful when wrapping a dataset with an api which has
 *          been generated (via the {@link HollowAPIClassJavaGenerator})</dd>
 *
 *      <dt>{@link HollowFilterConfig}</dt>
 *      <dd>Defines what types and fields to load (or not load) into memory from hollow datasets.  Generally useful to reduce
 *          heap footprint on consumers which do not require visibility of an entire dataset.</dd>
 *
 *      <dt>{@link HollowConsumer.DoubleSnapshotConfig}</dt>
 *      <dd>Defines whether this consumer may attempt a double snapshot, and how many deltas will be attempted during a single refresh.
 *          A double snapshot will allow your consumer to update in case of a broken delta chain, but will also result in a doubling of
 *          the heap footprint while the double snapshot is occurring.</dd>
 *
 *      <dt>{@link HollowConsumer.ObjectLongevityConfig}</dt>
 *      <dd>Object longevity is used to guarantee that Hollow objects which are backed by removed records will remain usable and
 *          consistent until old references are discarded.  This behavior is turned off by default.  Implementations of this config
 *          can be used to enable and configure this behavior.</dd>
 *
 *      <dt>{@link HollowConsumer.ObjectLongevityDetector}</dt>
 *      <dd>Implementations of this config will be notified when usage of expired Hollow object references is attempted.</dd>
 *
 *      <dt>An Executor</dt>
 *      <dd>The Executor which will be used to perform updates when {@link #triggerAsyncRefresh()} is called.  This will
 *          default to a new fixed thread pool with a single refresh thread.</dd>
 *
 *
 *
 * </dl>
 */
public class HollowConsumer {

    protected final AnnouncementWatcher announcementWatcher;
    protected final HollowClientUpdater updater;
    protected final ReadWriteLock refreshLock;
    protected final HollowConsumerMetrics metrics;

    private final Executor refreshExecutor;

    protected HollowConsumer(BlobRetriever blobRetriever,
                             AnnouncementWatcher announcementWatcher,
                             List<RefreshListener> updateListeners,
                             HollowAPIFactory apiFactory,
                             HollowFilterConfig dataFilter,
                             ObjectLongevityConfig objectLongevityConfig,
                             ObjectLongevityDetector objectLongevityDetector,
                             DoubleSnapshotConfig doubleSnapshotConfig,
                             HollowObjectHashCodeFinder hashCodeFinder,
                             Executor refreshExecutor) {
        this(blobRetriever, announcementWatcher, updateListeners, apiFactory, dataFilter, objectLongevityConfig, objectLongevityDetector, doubleSnapshotConfig, hashCodeFinder, refreshExecutor, null);
    }

    protected HollowConsumer(BlobRetriever blobRetriever,
                             AnnouncementWatcher announcementWatcher,
                             List<RefreshListener> updateListeners,
                             HollowAPIFactory apiFactory,
                             HollowFilterConfig dataFilter,
                             ObjectLongevityConfig objectLongevityConfig,
                             ObjectLongevityDetector objectLongevityDetector,
                             DoubleSnapshotConfig doubleSnapshotConfig,
                             HollowObjectHashCodeFinder hashCodeFinder,
                             Executor refreshExecutor,
                             HollowMetricsCollector<HollowConsumerMetrics> metricsCollector) {

        this.metrics = new HollowConsumerMetrics();
        this.updater = new HollowClientUpdater(blobRetriever,
                                               updateListeners,
                                               apiFactory,
                                               doubleSnapshotConfig,
                                               hashCodeFinder,
                                               objectLongevityConfig,
                                               objectLongevityDetector,
                                               metrics,
                                               metricsCollector);
        updater.setFilter(dataFilter);
        this.announcementWatcher = announcementWatcher;
        this.refreshExecutor = refreshExecutor;
        this.refreshLock = new ReentrantReadWriteLock();
        if(announcementWatcher != null)
            announcementWatcher.subscribeToUpdates(this);
    }

    /**
     * Triggers a refresh to the latest version specified by the {@link HollowConsumer.AnnouncementWatcher}.
     * If already on the latest version, this operation is a no-op.
     *
     * If a {@link HollowConsumer.AnnouncementWatcher} is not present, this call trigger a refresh to the
     * latest version available in the blob store.
     *
     * This is a blocking call.
     */
    public void triggerRefresh() {
        refreshLock.writeLock().lock();
        try {
            updater.updateTo(announcementWatcher == null ? Long.MAX_VALUE : announcementWatcher.getLatestVersion());
        } catch(Throwable th) {
            throw new RuntimeException(th);
        } finally {
            refreshLock.writeLock().unlock();
        }
    }

    /**
     * Immediately triggers a refresh in a different thread to the latest version
     * specified by the {@link HollowConsumer.AnnouncementWatcher}. If already on
     * the latest version, this operation is a no-op.
     *
     * If a {@link HollowConsumer.AnnouncementWatcher} is not present, this call trigger a refresh to the
     * latest version available in the blob store.
     *
     * This is an asynchronous call.
     */
    public void triggerAsyncRefresh() {
        triggerAsyncRefreshWithDelay(0);
    }

    /**
     * Triggers async refresh after the specified number of milliseconds has passed.
     *
     * Any subsequent calls for async refresh will not begin until after the specified delay
     * has completed.
     *
     */
    public void triggerAsyncRefreshWithDelay(int delayMillis) {
        final long targetBeginTime = System.currentTimeMillis() + delayMillis;

        refreshExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    long delay = targetBeginTime - System.currentTimeMillis();
                    if(delay > 0)
                        Thread.sleep(delay);
                    triggerRefresh();
                } catch(Throwable th) {
                    th.printStackTrace();
                }
            }
        });
    }

    /**
     * If a {@link HollowConsumer.AnnouncementWatcher} is not specified, then this method will update
     * to the specified version.
     *
     * Otherwise, an UnsupportedOperationException will be thrown.
     *
     * This is a blocking call.
     *
     * @param version
     */
    public void triggerRefreshTo(long version) {
        if(announcementWatcher != null)
            throw new UnsupportedOperationException("Cannot trigger refresh to specified version when a HollowConsumer.AnnouncementWatcher is present");

        try {
            updater.updateTo(version);
        } catch(Throwable th) {
            throw new RuntimeException(th);
        }
    }

    /**
     * @return the {@link HollowReadStateEngine} which is holding the underlying hollow dataset.
     */
    public HollowReadStateEngine getStateEngine() {
        return updater.getStateEngine();
    }

    /**
     * @return the current version of the dataset.  This is the unique identifier of the data's state.
     */
    public long getCurrentVersionId() {
        return updater.getCurrentVersionId();
    }

    /**
     * @return the api which wraps the underlying dataset.
     */
    public HollowAPI getAPI() {
        return updater.getAPI();
    }

    /**
     * Will force a double snapshot refresh on the next update.
     */
    public void forceDoubleSnapshotNextUpdate() {
        updater.forceDoubleSnapshotNextUpdate();
    }

    /**
     * Clear any failed transitions from the {@link FailedTransitionTracker}, so that they may be reattempted when an update is triggered.
     */
    public void clearFailedTransitions() {
        updater.clearFailedTransitions();
    }

    /**
     * Returns a {@link ReadWriteLock#readLock()}, the corresponding writeLock() of which is used to synchronize refreshes.
     *
     * This is useful if performing long-running operations which require a consistent view of the entire dataset in a single data state, to guarantee that updates do not happen while the operation runs.
     */
    public Lock getRefreshLock() {
        return refreshLock.readLock();
    }

    /**
     * Add a {@link RefreshListener} to this consumer.
     */
    public void addRefreshListener(RefreshListener listener) {
        updater.addRefreshListener(listener);
    }

    /**
     * Remove a {@link RefreshListener} from this consumer.
     */
    public void removeRefreshListener(RefreshListener listener) {
        updater.removeRefreshListener(listener);
    }

    /**
     * Returns the metrics for this consumer
     */
    public HollowConsumerMetrics getMetrics() {
        return metrics;
    }

    /**
     * An interface which defines the necessary interactions of Hollow with a blob data store.
     *
     * Implementations will define how to retrieve blob data from a data store.
     *
     */
    public interface BlobRetriever {

        /**
         * Returns the snapshot for the state with the greatest version identifier which is equal to or less than the desired version
         */
        public HollowConsumer.Blob retrieveSnapshotBlob(long desiredVersion);

        /**
         * Returns a delta transition which can be applied to the specified version identifier
         */
        public HollowConsumer.Blob retrieveDeltaBlob(long currentVersion);

        /**
         * Returns a reverse delta transition which can be applied to the specified version identifier
         */
        public HollowConsumer.Blob retrieveReverseDeltaBlob(long currentVersion);

    }

    /**
     * A Blob, which is either a snapshot or a delta, defines three things:
     *
     * <dl>
     *      <dt>The "from" version</dt>
     *      <dd>The unique identifier of the state to which a delta transition should be applied.  If
     *          this is a snapshot, then this value is Long.MIN_VALUE</dd>
     *
     *      <dt>The "to" version</dt>
     *      <dd>The unique identifier of the state at which a dataset will arrive after this blob is applied.</dd>
     *
     *      <dt>The actual blob data</dt>
     *      <dd>Implementations will define how to retrieve the actual blob data for this specific blob from a data store as an InputStream.</dd>
     * </dl>
     *
     */
    public static abstract class Blob {

        private final long fromVersion;
        private final long toVersion;

        /**
         * Instantiate a snapshot to a specified data state version.
         */
        public Blob(long toVersion) {
            this(Long.MIN_VALUE, toVersion);
        }

        /**
         * Instantiate a delta from one data state version to another.
         */
        public Blob(long fromVersion, long toVersion) {
            this.fromVersion = fromVersion;
            this.toVersion = toVersion;
        }

        /**
         * Implementations will define how to retrieve the actual blob data for this specific transition from a data store.
         *
         * It is expected that the returned InputStream will not be interrupted.  For this reason, it is a good idea to
         * retrieve the entire blob (e.g. to disk) from a remote datastore prior to returning this stream.
         *
         * @return
         * @throws IOException
         */
        public abstract InputStream getInputStream() throws IOException;

        public boolean isSnapshot() {
            return fromVersion == Long.MIN_VALUE;
        }

        public boolean isReverseDelta() {
            return toVersion < fromVersion;
        }

        public boolean isDelta() {
            return !isSnapshot() && !isReverseDelta();
        }

        public long getFromVersion() {
            return fromVersion;
        }

        public long getToVersion() {
            return toVersion;
        }
    }

    /**
     * Implementations of this class are responsible for two things:
     *
     * 1) Tracking the latest announced data state version.
     * 2) Keeping the client up to date by calling triggerAsyncRefresh() on self when the latest version changes.
     *
     * If an AnnouncementWatcher is provided to a HollowConsumer, then calling HollowConsumer#triggerRefreshTo() is unsupported.
     */
    public static interface AnnouncementWatcher {

        public static final long NO_ANNOUNCEMENT_AVAILABLE = Long.MIN_VALUE;

        /**
         * Return the latest announced version.
         * @return
         */
        public long getLatestVersion();

        /**
         * Implementations of this method should subscribe a HollowConsumer to updates to announced versions.
         *
         * When announcements are received via a push mechanism, or polling reveals a new version, a call should be placed to one
         * of the flavors of {@link HollowConsumer#triggerRefresh()} on the provided HollowConsumer.
         */
        public abstract void subscribeToUpdates(HollowConsumer consumer);
    }

    public static interface DoubleSnapshotConfig {

        public boolean allowDoubleSnapshot();

        public int maxDeltasBeforeDoubleSnapshot();

        public static DoubleSnapshotConfig DEFAULT_CONFIG = new DoubleSnapshotConfig() {
            @Override public int maxDeltasBeforeDoubleSnapshot() { return 32; }
            @Override public boolean allowDoubleSnapshot() { return true; }
        };
    }


    public interface ObjectLongevityConfig {

        /**
         * Whether or not long-lived object support is enabled.
         *
         * Because Hollow reuses pooled memory, if references to Hollow records are held too long, the underlying data may
         * be overwritten.  When long-lived object support is enabled, Hollow records referenced via a {@link HollowAPI} will,
         * after an update, be backed by a reserved copy of the data at the time the reference was created.  This guarantees
         * that even if a reference is held for a long time, it will continue to return the same data when interrogated.
         *
         * These reserved copies are backed by the {@link HollowHistory} data structure.
         */
        public boolean enableLongLivedObjectSupport();

        public boolean enableExpiredUsageStackTraces();

        /**
         * If long-lived object support is enabled, this returns the number of milliseconds before the {@link StaleHollowReferenceDetector}
         * will begin flagging usage of stale objects.
         *
         * @return
         */
        public long gracePeriodMillis();

        /**
         * If long-lived object support is enabled, this defines the number of milliseconds, after the grace period, during which
         * data is still available in stale references, but usage will be flagged by the {@link StaleHollowReferenceDetector}.
         *
         * After the grace period + usage detection period have expired, the data from stale references will become inaccessible if
         * dropDataAutomatically() is enabled.
         *
         * @return
         */
        public long usageDetectionPeriodMillis();

        /**
         * Whether or not to drop data behind stale references after the grace period + usage detection period has elapsed, assuming
         * that no usage was detected during the usage detection period.
         *
         * @return
         */
        public boolean dropDataAutomatically();

        /**
         * Drop data even if flagged during the usage detection period.
         * @return
         */
        public boolean forceDropData();

        public static final ObjectLongevityConfig DEFAULT_CONFIG = new ObjectLongevityConfig() {
            @Override public boolean enableLongLivedObjectSupport() { return false; }
            @Override public boolean dropDataAutomatically() { return false; }
            @Override public boolean forceDropData() { return false; }
            @Override public boolean enableExpiredUsageStackTraces() { return false; }
            @Override public long usageDetectionPeriodMillis() { return 60 * 60 * 1000; }
            @Override public long gracePeriodMillis() { return 60 * 60 * 1000; }
        };
    }

    /**
     * Listens for stale Hollow object usage
     */
    public interface ObjectLongevityDetector {

        /**
         * Stale reference detection hint.  This will be called every ~30 seconds.
         *
         * If a nonzero value is reported, then stale references to Hollow objects may be cached somewhere in your codebase.
         *
         * This signal can be noisy, and a nonzero value indicates that some reference to stale data exists somewhere.
         */
        public void staleReferenceExistenceDetected(int count);

        /**
         * Stale reference USAGE detection.  This will be called every ~30 seconds.
         *
         * If a nonzero value is reported, then stale references to Hollow objects are being accessed from somewhere in your codebase.
         *
         * This signal is noiseless, and a nonzero value indicates that some reference to stale data is USED somewhere.
         */
        public void staleReferenceUsageDetected(int count);

        public static ObjectLongevityDetector DEFAULT_DETECTOR = new ObjectLongevityDetector() {
            @Override public void staleReferenceUsageDetected(int count) { }
            @Override public void staleReferenceExistenceDetected(int count) { }
        };

    }

    /**
     * Implementations of this class will define what to do when various events happen before, during, and after updating
     * local in-memory copies of hollow data sets.
     */
    public interface RefreshListener {

        /**
         * Indicates that a refresh has begun.  Generally useful for logging.
         *
         * A refresh is the process of a consumer getting from a current version to a desired version.
         *
         * A refresh will consist of one of the following:
         * <ul>
         * <li>one or more deltas</li>
         * <li>a snapshot load, plus zero or more deltas</li>
         * </ul>
         * @param currentVersion the current state version
         * @param requestedVersion the version to which the refresh is progressing
         */
        public void refreshStarted(long currentVersion, long requestedVersion);

        /**
         * This method is called when either data was initialized for the first time, <i>or</i> an update occurred across a
         * discontinuous delta chain (double snapshot).
         *
         * If this method is called, it means that the current refresh consists of a snapshot load, plus zero or more deltas.
         *
         * Implementations may initialize (or re-initialize) any indexing which is critical to keep in-sync with the data.
         *
         * This method will be called a maximum of once per refresh, after the data has reached the final state of the refresh.
         *
         * @param api the {@link HollowAPI} instance
         * @param stateEngine the {@link HollowReadStateEngine}
         * @param version the current state version
         * @throws Exception thrown if an error occurs in processing
         */
        public void snapshotUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception;

        /**
         * This method is called whenever a live state engine's data is updated with a delta.  This method is <i>not</i>
         * called during first time initialization or when an update across a discontinuous delta chain (double snapshot)
         * occurs.
         *
         * Implementations should incrementally update any indexing which is critical to keep in-sync with the data.
         *
         * If this method is called, it means that the current refresh consists of one or more deltas, and does not include
         * a snapshot load.
         *
         * This method may be called multiple times per refresh, once for each time a delta is applied.
         *
         * @param api the {@link HollowAPI} instance
         * @param stateEngine the {@link HollowReadStateEngine}
         * @param version the current state version
         * @throws Exception thrown if an error occurs in processing
         */
        public void deltaUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception;

        /**
         * Called to indicate a blob was loaded (either a snapshot or delta).  Generally useful for logging or tracing of applied updates.
         *
         * @param transition The transition which was applied.
         */
        public void blobLoaded(HollowConsumer.Blob transition);

        /**
         * Indicates that a refresh completed successfully.
         *
         * @param beforeVersion - The version when the refresh started
         * @param afterVersion - The version when the refresh completed
         * @param requestedVersion - The specific version which was requested
         */
        public void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion);


        /**
         * Indicates that a refresh failed with an Exception.
         *
         * @param beforeVersion - The version when the refresh started
         * @param afterVersion - The version when the refresh completed
         * @param requestedVersion - The specific version which was requested
         * @param failureCause - The Exception which caused the failure.
         */
        public void refreshFailed(long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause);

    }

    public interface TransitionAwareRefreshListener extends RefreshListener {

        /**
         * This method is called <i>whenever</i> a snapshot is processed.  In the case of first time initialization or an update
         * across a discontinuous delta chain (double snapshot), this method will be called once (as the first transition).
         *
         * Implementations may initialize (or re-initialize) any indexing which is critical to keep in-sync with the data.
         *
         * @param api the {@link HollowAPI} instance
         * @param stateEngine the {@link HollowReadStateEngine}
         * @param version the current state version
         * @throws Exception thrown if an error occurs in processing
         */
        public void snapshotApplied(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception;

        /**
         * This method is called <i>whenever</i> a delta is processed.  In the case of first time initialization or an update
         * across a discontinuous delta chain (double snapshot), this method may be called one or more times before arriving
         * at the final state (after which {@link #snapshotUpdateOccurred(HollowAPI, HollowReadStateEngine, long)} is called.
         *
         * Implementations may incrementally update any indexing which is critical to keep in-sync with the data.
         *
         * @param api the {@link HollowAPI} instance
         * @param stateEngine the {@link HollowReadStateEngine}
         * @param version the current state version
         * @throws Exception thrown if an error occurs in processing
         */
        public void deltaApplied(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception;

    }

    public static class AbstractRefreshListener implements TransitionAwareRefreshListener {
        @Override
        public void refreshStarted(long currentVersion, long requestedVersion) {
            // no-op
        }

        @Override
        public void snapshotUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
            // no-op
        }

        @Override
        public void deltaUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
            // no-op
        }

        @Override
        public void blobLoaded(Blob transition) {
            // no-op
        }

        @Override
        public void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion) {
            // no-op
        }

        @Override
        public void refreshFailed(long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause) {
            // no-op
        }

        @Override
        public void snapshotApplied(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
            // no-op
        }

        @Override
        public void deltaApplied(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
            // no-op
        }
    }

    public static HollowConsumer.Builder withBlobRetriever(HollowConsumer.BlobRetriever blobRetriever) {
        HollowConsumer.Builder builder = new Builder();
        return builder.withBlobRetriever(blobRetriever);
    }

    public static HollowConsumer.Builder withLocalBlobStore(File localBlobStoreDir) {
        HollowConsumer.Builder builder = new Builder();
        return builder.withLocalBlobStore(localBlobStoreDir);
    }

    public static class Builder {
        private HollowConsumer.BlobRetriever blobRetriever = null;
        private HollowConsumer.AnnouncementWatcher announcementWatcher = null;
        private HollowFilterConfig filterConfig = null;
        private List<HollowConsumer.RefreshListener> refreshListeners = new CopyOnWriteArrayList<HollowConsumer.RefreshListener>();
        private HollowAPIFactory apiFactory = HollowAPIFactory.DEFAULT_FACTORY;
        private HollowObjectHashCodeFinder hashCodeFinder = new DefaultHashCodeFinder();
        private HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig = DoubleSnapshotConfig.DEFAULT_CONFIG;
        private HollowConsumer.ObjectLongevityConfig objectLongevityConfig = ObjectLongevityConfig.DEFAULT_CONFIG;
        private HollowConsumer.ObjectLongevityDetector objectLongevityDetector = ObjectLongevityDetector.DEFAULT_DETECTOR;
        private File localBlobStoreDir = null;
        private Executor refreshExecutor = null;
        private HollowMetricsCollector<HollowConsumerMetrics> metricsCollector;

        public HollowConsumer.Builder withBlobRetriever(HollowConsumer.BlobRetriever blobRetriever) {
            this.blobRetriever = blobRetriever;
            return this;
        }

        public HollowConsumer.Builder withLocalBlobStore(File localBlobStoreDir) {
            this.localBlobStoreDir = localBlobStoreDir;
            return this;
        }

        public HollowConsumer.Builder withAnnouncementWatcher(HollowConsumer.AnnouncementWatcher announcementWatcher) {
            this.announcementWatcher = announcementWatcher;
            return this;
        }

        public HollowConsumer.Builder withRefreshListener(HollowConsumer.RefreshListener refreshListener) {
            refreshListeners.add(refreshListener);
            return this;
        }

        public HollowConsumer.Builder withRefreshListeners(HollowConsumer.RefreshListener... refreshListeners) {
            for(HollowConsumer.RefreshListener refreshListener : refreshListeners)
                this.refreshListeners.add(refreshListener);
            return this;
        }

        public <T extends HollowAPI> HollowConsumer.Builder withGeneratedAPIClass(Class<T> generatedAPIClass) {
            this.apiFactory = new HollowAPIFactory.ForGeneratedAPI<T>(generatedAPIClass);
            return this;
        }

        public HollowConsumer.Builder withFilterConfig(HollowFilterConfig filterConfig) {
            this.filterConfig = filterConfig;
            return this;
        }

        public HollowConsumer.Builder withDoubleSnapshotConfig(HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig) {
            this.doubleSnapshotConfig = doubleSnapshotConfig;
            return this;
        }

        public HollowConsumer.Builder withObjectLongevityConfig(HollowConsumer.ObjectLongevityConfig objectLongevityConfig) {
            this.objectLongevityConfig = objectLongevityConfig;
            return this;
        }

        public HollowConsumer.Builder withObjectLongevityDetector(HollowConsumer.ObjectLongevityDetector objectLongevityDetector) {
            this.objectLongevityDetector = objectLongevityDetector;
            return this;
        }

        public HollowConsumer.Builder withRefreshExecutor(Executor refreshExecutor) {
            this.refreshExecutor = refreshExecutor;
            return this;
        }

        public HollowConsumer.Builder withMetricsCollector(HollowMetricsCollector<HollowConsumerMetrics> metricsCollector) {
            this.metricsCollector = metricsCollector;
            return this;
        }

        @Deprecated
        public HollowConsumer.Builder withHashCodeFinder(HollowObjectHashCodeFinder hashCodeFinder) {
            this.hashCodeFinder = hashCodeFinder;
            return this;
        }

        public HollowConsumer build() {
            if(blobRetriever == null && localBlobStoreDir == null)
                throw new IllegalArgumentException("A HollowBlobRetriever or local blob store directory must be specified when building a HollowClient");

            BlobRetriever blobRetriever = this.blobRetriever;
            if(localBlobStoreDir != null)
                blobRetriever = new HollowFilesystemBlobRetriever(localBlobStoreDir, blobRetriever);

            if(refreshExecutor == null)
                refreshExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setDaemon(true);
                        return t;
                    }
                });


            return new HollowConsumer(blobRetriever,
                                      announcementWatcher,
                                      refreshListeners,
                                      apiFactory,
                                      filterConfig,
                                      objectLongevityConfig,
                                      objectLongevityDetector,
                                      doubleSnapshotConfig,
                                      hashCodeFinder,
                                      refreshExecutor,
                                      metricsCollector);
        }
    }

}
