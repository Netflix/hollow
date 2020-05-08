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
package com.netflix.hollow.api.consumer;

import static com.netflix.hollow.core.util.Threads.daemonThread;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

import com.netflix.hollow.api.client.FailedTransitionTracker;
import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.client.HollowClientUpdater;
import com.netflix.hollow.api.client.StaleHollowReferenceDetector;
import com.netflix.hollow.api.codegen.HollowAPIClassJavaGenerator;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.metrics.HollowConsumerMetrics;
import com.netflix.hollow.api.metrics.HollowMetricsCollector;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.util.DefaultHashCodeFinder;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.tools.history.HollowHistory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * A HollowConsumer is the top-level class used by consumers of Hollow data to initialize and keep up-to-date a local in-memory
 * copy of a hollow dataset.  The interactions between the "blob" transition store and announcement listener are defined by
 * this class, and the implementations of the data retrieval, announcement mechanism are abstracted in the interfaces which
 * are provided to this class.
 * <p>
 * To obtain a HollowConsumer, you should use a builder pattern, for example:
 * <pre>
 * {@code
 *
 * HollowConsumer consumer = HollowConsumer.withBlobRetriever(retriever)
 *                                         .withAnnouncementWatcher(watcher)
 *                                         .withGeneratedAPIClass(MovieAPI.class)
 *                                         .build();
 * }
 * </pre>
 * <p>
 * The following components are injectable, but only an implementation of the HollowConsumer.BlobRetriever is
 * required to be injected, all other components are optional. :
 * <dl>
 * <dt>{@link HollowConsumer.BlobRetriever}</dt>
 * <dd>Implementations of this class define how to retrieve blob data from the blob store.</dd>
 *
 * <dt>{@link HollowConsumer.AnnouncementWatcher}</dt>
 * <dd>Implementations of this class define the announcement mechanism, which is used to track the version of the
 * currently announced state.  It's also expected that implementations will trigger a refresh each time current
 * data version is updated.</dd>
 *
 * <dt>a List of {@link HollowConsumer.RefreshListener}s</dt>
 * <dd>RefreshListener implementations will define what to do when various events happen before, during, and after updating
 * local in-memory copies of hollow data sets.</dd>
 *
 * <dt>the Class representing a generated Hollow API</dt>
 * <dd>Defines how to create a {@link HollowAPI} for the dataset, useful when wrapping a dataset with an api which has
 * been generated (via the {@link HollowAPIClassJavaGenerator})</dd>
 *
 * <dt>{@link HollowFilterConfig}</dt>
 * <dd>Defines what types and fields to load (or not load) into memory from hollow datasets.  Generally useful to reduce
 * heap footprint on consumers which do not require visibility of an entire dataset.</dd>
 *
 * <dt>{@link HollowConsumer.DoubleSnapshotConfig}</dt>
 * <dd>Defines whether this consumer may attempt a double snapshot, and how many deltas will be attempted during a single refresh.
 * A double snapshot will allow your consumer to update in case of a broken delta chain, but will also result in a doubling of
 * the heap footprint while the double snapshot is occurring.</dd>
 *
 * <dt>{@link HollowConsumer.ObjectLongevityConfig}</dt>
 * <dd>Object longevity is used to guarantee that Hollow objects which are backed by removed records will remain usable and
 * consistent until old references are discarded.  This behavior is turned off by default.  Implementations of this config
 * can be used to enable and configure this behavior.</dd>
 *
 * <dt>{@link HollowConsumer.ObjectLongevityDetector}</dt>
 * <dd>Implementations of this config will be notified when usage of expired Hollow object references is attempted.</dd>
 *
 * <dt>An Executor</dt>
 * <dd>The Executor which will be used to perform updates when {@link #triggerAsyncRefresh()} is called.  This will
 * default to a new fixed thread pool with a single refresh thread.</dd>
 *
 * </dl>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class HollowConsumer {
    private static final Logger LOG = Logger.getLogger(HollowConsumer.class.getName());

    protected final AnnouncementWatcher announcementWatcher;
    protected final HollowClientUpdater updater;
    protected final ReadWriteLock refreshLock;
    protected final HollowConsumerMetrics metrics;

    private final Executor refreshExecutor;

    protected HollowConsumer(BlobRetriever blobRetriever,
                             AnnouncementWatcher announcementWatcher,
                             List<RefreshListener> refreshListeners,
                             HollowAPIFactory apiFactory,
                             HollowFilterConfig dataFilter,
                             ObjectLongevityConfig objectLongevityConfig,
                             ObjectLongevityDetector objectLongevityDetector,
                             DoubleSnapshotConfig doubleSnapshotConfig,
                             HollowObjectHashCodeFinder hashCodeFinder,
                             Executor refreshExecutor) {
        this(blobRetriever, announcementWatcher, refreshListeners, apiFactory, dataFilter,
                objectLongevityConfig, objectLongevityDetector, doubleSnapshotConfig,
                hashCodeFinder, refreshExecutor, null);
    }

    protected HollowConsumer(BlobRetriever blobRetriever,
                             AnnouncementWatcher announcementWatcher,
                             List<RefreshListener> refreshListeners,
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
                refreshListeners,
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
        if (announcementWatcher != null)
            announcementWatcher.subscribeToUpdates(this);
    }

    /**
     * Triggers a refresh to the latest version specified by the {@link HollowConsumer.AnnouncementWatcher}.
     * If already on the latest version, this operation is a no-op.
     * <p>
     * If a {@link HollowConsumer.AnnouncementWatcher} is not present, this call trigger a refresh to the
     * latest version available in the blob store.
     * <p>
     * This is a blocking call.
     */
    public void triggerRefresh() {
        refreshLock.writeLock().lock();
        try {
            updater.updateTo(announcementWatcher == null ? Long.MAX_VALUE : announcementWatcher.getLatestVersion());
        } catch (Error | RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            refreshLock.writeLock().unlock();
        }
    }

    /**
     * Immediately triggers a refresh in a different thread to the latest version
     * specified by the {@link HollowConsumer.AnnouncementWatcher}. If already on
     * the latest version, this operation is a no-op.
     * <p>
     * If a {@link HollowConsumer.AnnouncementWatcher} is not present, this call trigger a refresh to the
     * latest version available in the blob store.
     * <p>
     * This is an asynchronous call.
     */
    public void triggerAsyncRefresh() {
        triggerAsyncRefreshWithDelay(0);
    }

    /**
     * Triggers async refresh after the specified number of milliseconds has passed.
     * <p>
     * Any subsequent calls for async refresh will not begin until after the specified delay
     * has completed.
     *
     * @param delayMillis the delay, in millseconds, before triggering the refresh
     */
    public void triggerAsyncRefreshWithDelay(int delayMillis) {
        final long targetBeginTime = System.currentTimeMillis() + delayMillis;

        refreshExecutor.execute(() -> {
            try {
                long delay = targetBeginTime - System.currentTimeMillis();
                if (delay > 0)
                    Thread.sleep(delay);
            } catch (InterruptedException e) {
                // Interrupting, such as shutting down the executor pool,
                // cancels the trigger
                LOG.log(Level.INFO, "Async refresh interrupted before trigger, refresh cancelled", e);
                return;
            }

            try {
                triggerRefresh();
            } catch (Error | RuntimeException e) {
                // Ensure exceptions are propagated to the executor
                LOG.log(Level.SEVERE, "Async refresh failed", e);
                throw e;
            }
        });
    }

    /**
     * If a {@link HollowConsumer.AnnouncementWatcher} is not specified, then this method will attempt to update
     * to the specified version, and if the specified version does not exist then to a different version as specified
     * by functionality in the {@code BlobRetriever}.
     * <p>
     * Otherwise, an UnsupportedOperationException will be thrown.
     * <p>
     * This is a blocking call.
     *
     * @param version the version to refresh to
     */
    public void triggerRefreshTo(long version) {
        if (announcementWatcher != null)
            throw new UnsupportedOperationException("Cannot trigger refresh to specified version when a HollowConsumer.AnnouncementWatcher is present");

        try {
            updater.updateTo(version);
        } catch (Error | RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
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
     * Returns a {@code CompletableFuture} that completes after the initial data load succeeds.
     * <p>
     * Callers can use methods like {@link CompletableFuture#join()} or {@link CompletableFuture#get(long, TimeUnit)}
     * to block until the initial load is complete.
     * <p>
     * A failure during the initial load <em>will not</em> cause the future to complete exceptionally; this allows
     * for a subsequent data version to eventually succeed.
     * <p>
     * In a consumer without published or announced versions – or one that always fails the initial load – the future
     * will remain incomplete indefinitely.
     *
     * @return a future which, when completed, has a value set to the data version that was initially loaded
     */
    public CompletableFuture<Long> getInitialLoad() {
        return updater.getInitialLoad();
    }

    /**
     * @return the api which wraps the underlying dataset.
     */
    public HollowAPI getAPI() {
        return updater.getAPI();
    }

    /**
     * Equivalent to calling {@link #getAPI()} and casting to the specified API.
     *
     * @param apiClass the class of the API
     * @param <T> the type of the API
     * @return the API which wraps the underlying dataset
     */
    public <T extends HollowAPI> T getAPI(Class<T> apiClass) {
        return apiClass.cast(updater.getAPI());
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
     * @return the number of failed snapshot transitions stored in the {@link FailedTransitionTracker}.
     */
    public int getNumFailedSnapshotTransitions() {
        return updater.getNumFailedSnapshotTransitions();
    }

    /**
     * @return the number of failed delta transitions stored in the {@link FailedTransitionTracker}.
     */
    public int getNumFailedDeltaTransitions() {
        return updater.getNumFailedDeltaTransitions();
    }

    /**
     * @return a {@link ReadWriteLock#readLock()}, the corresponding writeLock() of which is used to synchronize refreshes.
     * <p>
     * This is useful if performing long-running operations which require a consistent view of the entire dataset in a
     * single data state, to guarantee that updates do not happen while the operation runs.
     */
    public Lock getRefreshLock() {
        return refreshLock.readLock();
    }

    /**
     * Adds a {@link RefreshListener} to this consumer.
     * <p>
     * If the listener was previously added to this consumer, as determined by reference equality or {@code Object}
     * equality, then this method does nothing.
     * <p>
     * If a listener is added, concurrently, during the occurrence of a refresh then the listener will not receive
     * events until the next refresh.  The listener may also be removed concurrently.
     * <p>
     * If the listener instance implements {@link RefreshRegistrationListener} then before the listener is added
     * the {@link RefreshRegistrationListener#onBeforeAddition} method is be invoked.  If that method throws an
     * exception then that exception will be thrown by this method and the listener will not be added.
     *
     * @param listener the refresh listener to add
     */
    public void addRefreshListener(RefreshListener listener) {
        updater.addRefreshListener(listener, this);
    }

    /**
     * Removes a {@link RefreshListener} from this consumer.
     * <p>
     * If the listener was not previously added to this consumer, as determined by reference equality or {@code Object}
     * equality, then this method does nothing.
     * <p>
     * If a listener is removed, concurrently, during  the occurrence of a refresh then the listener will receive all
     * events for that refresh but not receive events for subsequent any refreshes.
     * <p>
     * If the listener instance implements {@link RefreshRegistrationListener} then after the listener is removed
     * the {@link RefreshRegistrationListener#onAfterRemoval} method is be invoked.  If that method throws an
     * exception then that exception will be thrown by this method.
     *
     * @param listener the refresh listener to remove
     */
    public void removeRefreshListener(RefreshListener listener) {
        updater.removeRefreshListener(listener, this);
    }

    /**
     * @return the metrics for this consumer
     */
    public HollowConsumerMetrics getMetrics() {
        return metrics;
    }

    /**
     * An interface which defines the necessary interactions of Hollow with a blob data store.
     * <p>
     * Implementations will define how to retrieve blob data from a data store.
     */
    public interface BlobRetriever {

        /**
         * Returns the snapshot for the state with the greatest version identifier which is equal to or less than the desired version
         * @param desiredVersion the desired version
         * @return the blob of the snapshot
         */
        HollowConsumer.Blob retrieveSnapshotBlob(long desiredVersion);

        /**
         * Returns a delta transition which can be applied to the specified version identifier
         * @param currentVersion the current version
         * @return the blob of the delta
         */
        HollowConsumer.Blob retrieveDeltaBlob(long currentVersion);

        /**
         * Returns a reverse delta transition which can be applied to the specified version identifier
         * @param currentVersion the current version
         * @return the blob of the reverse delta
         */
        HollowConsumer.Blob retrieveReverseDeltaBlob(long currentVersion);
    }

    /**
     * A Blob, which is either a snapshot or a delta, defines three things:
     * <dl>
     * <dt>The "from" version</dt>
     * <dd>The unique identifier of the state to which a delta transition should be applied.  If
     * this is a snapshot, then this value is HollowConstants.VERSION_NONE.</dd>
     *
     * <dt>The "to" version</dt>
     * <dd>The unique identifier of the state at which a dataset will arrive after this blob is applied.</dd>
     *
     * <dt>The actual blob data</dt>
     * <dd>Implementations will define how to retrieve the actual blob data for this specific blob from a data store as an InputStream.</dd>
     * </dl>
     */
    public static abstract class Blob {

        private final long fromVersion;
        private final long toVersion;
        private final BlobType blobType;

        /**
         * Instantiate a snapshot to a specified data state version.
         *
         * @param toVersion the version
         */
        public Blob(long toVersion) {
            this(HollowConstants.VERSION_NONE, toVersion);
        }

        /**
         * Instantiate a delta from one data state version to another.
         *
         * @param fromVersion the version to start the delta from
         * @param toVersion the version to end the delta from
         */
        public Blob(long fromVersion, long toVersion) {
            this.fromVersion = fromVersion;
            this.toVersion = toVersion;

            if (this.isSnapshot())
                this.blobType = BlobType.SNAPSHOT;
            else if (this.isReverseDelta())
                this.blobType = BlobType.REVERSE_DELTA;
            else
                this.blobType = BlobType.DELTA;
        }

        /**
         * Implementations will define how to retrieve the actual blob data for this specific transition from a data store.
         * <p>
         * It is expected that the returned InputStream will not be interrupted.  For this reason, it is a good idea to
         * retrieve the entire blob (e.g. to disk) from a remote datastore prior to returning this stream.
         *
         * @return the input stream to the blob
         * @throws IOException if the input stream to the blob cannot be obtained
         */
        public abstract InputStream getInputStream() throws IOException;

        public File getFile() throws IOException {
            throw new NotImplementedException();
        }

        /**
         * Blobs can be of types {@code SNAPSHOT}, {@code DELTA} or {@code REVERSE_DELTA}.
         */
        public enum BlobType {
            SNAPSHOT("snapshot"),
            DELTA("delta"),
            REVERSE_DELTA("reversedelta");

            private final String type;
            BlobType(String type) {
                this.type = type;
            }

            public String getType() {
                return this.type;
            }
        }

        public boolean isSnapshot() {
            return fromVersion == HollowConstants.VERSION_NONE;
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

        public BlobType getBlobType() {
            return blobType;
        }
    }

    /**
     * Implementations of this class are responsible for two things:
     * <p>
     * 1) Tracking the latest announced data state version.
     * 2) Keeping the client up to date by calling triggerAsyncRefresh() on self when the latest version changes.
     * <p>
     * If an AnnouncementWatcher is provided to a HollowConsumer, then calling HollowConsumer#triggerRefreshTo() is unsupported.
     */
    public interface AnnouncementWatcher {

        long NO_ANNOUNCEMENT_AVAILABLE = HollowConstants.VERSION_NONE;

        /**
         * @return the latest announced version.
         */
        long getLatestVersion();

        /**
         * Implementations of this method should subscribe a HollowConsumer to updates to announced versions.
         * <p>
         * When announcements are received via a push mechanism, or polling reveals a new version, a call should be placed to one
         * of the flavors of {@link HollowConsumer#triggerRefresh()} on the provided HollowConsumer.
         *
         * @param consumer the hollow consumer
         */
        void subscribeToUpdates(HollowConsumer consumer);
    }

    public interface DoubleSnapshotConfig {

        boolean allowDoubleSnapshot();

        int maxDeltasBeforeDoubleSnapshot();

        DoubleSnapshotConfig DEFAULT_CONFIG = new DoubleSnapshotConfig() {
            @Override
            public int maxDeltasBeforeDoubleSnapshot() {
                return 32;
            }

            @Override
            public boolean allowDoubleSnapshot() {
                return true;
            }
        };
    }


    public interface ObjectLongevityConfig {

        /**
         * @return whether or not long-lived object support is enabled.
         * <p>
         * Because Hollow reuses pooled memory, if references to Hollow records are held too long, the underlying data may
         * be overwritten.  When long-lived object support is enabled, Hollow records referenced via a {@link HollowAPI} will,
         * after an update, be backed by a reserved copy of the data at the time the reference was created.  This guarantees
         * that even if a reference is held for a long time, it will continue to return the same data when interrogated.
         * <p>
         * These reserved copies are backed by the {@link HollowHistory} data structure.
         */
        boolean enableLongLivedObjectSupport();

        boolean enableExpiredUsageStackTraces();

        /**
         * @return if long-lived object support is enabled, the number of milliseconds before the {@link StaleHollowReferenceDetector}
         * will begin flagging usage of stale objects.
         */
        long gracePeriodMillis();

        /**
         * @return if long-lived object support is enabled, the number of milliseconds, after the grace period, during which
         * data is still available in stale references, but usage will be flagged by the {@link StaleHollowReferenceDetector}.
         * <p>
         * After the grace period + usage detection period have expired, the data from stale references will become inaccessible if
         * dropDataAutomatically() is enabled.
         */
        long usageDetectionPeriodMillis();

        /**
         * @return whether or not to drop data behind stale references after the grace period + usage detection period has elapsed, assuming
         * that no usage was detected during the usage detection period.
         */
        boolean dropDataAutomatically();

        /**
         * @return whether data is dropped even if flagged during the usage detection period.
         */
        boolean forceDropData();

        ObjectLongevityConfig DEFAULT_CONFIG = new ObjectLongevityConfig() {
            @Override
            public boolean enableLongLivedObjectSupport() {
                return false;
            }

            @Override
            public boolean dropDataAutomatically() {
                return false;
            }

            @Override
            public boolean forceDropData() {
                return false;
            }

            @Override
            public boolean enableExpiredUsageStackTraces() {
                return false;
            }

            @Override
            public long usageDetectionPeriodMillis() {
                return 60 * 60 * 1000;
            }

            @Override
            public long gracePeriodMillis() {
                return 60 * 60 * 1000;
            }
        };
    }

    /**
     * Listens for stale Hollow object usage
     */
    public interface ObjectLongevityDetector {

        /**
         * Stale reference detection hint.  This will be called every ~30 seconds.
         * <p>
         * If a nonzero value is reported, then stale references to Hollow objects may be cached somewhere in your codebase.
         * <p>
         * This signal can be noisy, and a nonzero value indicates that some reference to stale data exists somewhere.
         *
         * @param count the count of stale references
         */
        void staleReferenceExistenceDetected(int count);

        /**
         * Stale reference USAGE detection.  This will be called every ~30 seconds.
         * <p>
         * If a nonzero value is reported, then stale references to Hollow objects are being accessed from somewhere in your codebase.
         * <p>
         * This signal is noiseless, and a nonzero value indicates that some reference to stale data is USED somewhere.
         *
         * @param count the count of stale references
         */
        void staleReferenceUsageDetected(int count);

        ObjectLongevityDetector DEFAULT_DETECTOR = new ObjectLongevityDetector() {
            @Override
            public void staleReferenceUsageDetected(int count) {
            }

            @Override
            public void staleReferenceExistenceDetected(int count) {
            }
        };
    }

    /**
     * Implementations of this class will define what to do when various events happen before, during, and after updating
     * local in-memory copies of hollow data sets.
     */
    public interface RefreshListener {

        /**
         * Indicates that a refresh has begun.  Generally useful for logging.
         * <p>
         * A refresh is the process of a consumer getting from a current version to a desired version.
         * <p>
         * A refresh will consist of one of the following:
         * <ul>
         * <li>one or more deltas</li>
         * <li>a snapshot load, plus zero or more deltas</li>
         * </ul>
         *
         * @param currentVersion   the current state version
         * @param requestedVersion the version to which the refresh is progressing
         */
        void refreshStarted(long currentVersion, long requestedVersion);

        /**
         * This method is called when either data was initialized for the first time, <i>or</i> an update occurred across a
         * discontinuous delta chain (double snapshot).
         * <p>
         * If this method is called, it means that the current refresh consists of a snapshot load, plus zero or more deltas.
         * <p>
         * Implementations may initialize (or re-initialize) any indexing which is critical to keep in-sync with the data.
         * <p>
         * This method will be called a maximum of once per refresh, after the data has reached the final state of the refresh.
         *
         * @param api         the {@link HollowAPI} instance
         * @param stateEngine the {@link HollowReadStateEngine}
         * @param version     the current state version
         * @throws Exception thrown if an error occurs in processing
         */
        void snapshotUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception;

        /**
         * This method is called whenever a live state engine's data is updated with a delta.  This method is <i>not</i>
         * called during first time initialization or when an update across a discontinuous delta chain (double snapshot)
         * occurs.
         * <p>
         * Implementations should incrementally update any indexing which is critical to keep in-sync with the data.
         * <p>
         * If this method is called, it means that the current refresh consists of one or more deltas, and does not include
         * a snapshot load.
         * <p>
         * This method may be called multiple times per refresh, once for each time a delta is applied.
         *
         * @param api         the {@link HollowAPI} instance
         * @param stateEngine the {@link HollowReadStateEngine}
         * @param version     the current state version
         * @throws Exception thrown if an error occurs in processing
         */
        void deltaUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception;

        /**
         * Called to indicate a blob was loaded (either a snapshot or delta).  Generally useful for logging or tracing of applied updates.
         *
         * @param transition The transition which was applied.
         */
        void blobLoaded(HollowConsumer.Blob transition);

        /**
         * Indicates that a refresh completed successfully.
         *
         * @param beforeVersion    - The version when the refresh started
         * @param afterVersion     - The version when the refresh completed
         * @param requestedVersion - The specific version which was requested
         */
        void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion);


        /**
         * Indicates that a refresh failed with an Exception.
         *
         * @param beforeVersion    - The version when the refresh started
         * @param afterVersion     - The version when the refresh completed
         * @param requestedVersion - The specific version which was requested
         * @param failureCause     - The Exception which caused the failure.
         */
        void refreshFailed(long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause);

    }

    public interface TransitionAwareRefreshListener extends RefreshListener {

        /**
         * This method is called <i>whenever</i> a snapshot is processed.  In the case of first time initialization or an update
         * across a discontinuous delta chain (double snapshot), this method will be called once (as the first transition).
         * <p>
         * Implementations may initialize (or re-initialize) any indexing which is critical to keep in-sync with the data.
         *
         * @param api         the {@link HollowAPI} instance
         * @param stateEngine the {@link HollowReadStateEngine}
         * @param version     the current state version
         * @throws Exception thrown if an error occurs in processing
         */
        void snapshotApplied(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception;

        /**
         * This method is called <i>whenever</i> a delta is processed.  In the case of first time initialization or an update
         * across a discontinuous delta chain (double snapshot), this method may be called one or more times before arriving
         * at the final state (after which {@link #snapshotUpdateOccurred(HollowAPI, HollowReadStateEngine, long)} is called.
         * <p>
         * Implementations may incrementally update any indexing which is critical to keep in-sync with the data.
         *
         * @param api         the {@link HollowAPI} instance
         * @param stateEngine the {@link HollowReadStateEngine}
         * @param version     the current state version
         * @throws Exception thrown if an error occurs in processing
         */
        void deltaApplied(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception;

        /**
         * Called after refresh started and update plan has been initialized, but before the update plan starts executing.
         * It is called only once per update plan (and thus only once per consumer refresh). Exposes details of the
         * update plan.
         * @implSpec The default implementation provided does nothing.
         *
         * @param beforeVersion The version when refresh started
         * @param desiredVersion The version that the consumer refresh tries update to, even though it might not be attainable eg. HollowConstants.VERSION_LATEST
         * @param isSnapshotPlan Indicates whether the refresh involves a snapshot transition
         * @param transitionSequence List of transitions comprising the refresh
         */
        default void transitionsPlanned(long beforeVersion, long desiredVersion, boolean isSnapshotPlan, List<HollowConsumer.Blob.BlobType> transitionSequence) {}
    }

    /**
     * A listener of refresh listener addition and removal.
     * <p>
     * A {@link RefreshListener} implementation may  implement this interface to get notified before
     * the listener is added (via a call to {@link #addRefreshListener(RefreshListener)} and after a listener
     * is removed (via a call to {@link #removeRefreshListener(RefreshListener)}.
     * <p>
     * An implementation should not add or remove itself in response to addition or removal.  Such actions may result
     * in a {@link StackOverflowError} or unspecified behaviour.
     */
    public interface RefreshRegistrationListener {
        /**
         * Called before the refresh listener is added.
         * @param c the consumer the associated reference listener is being added to
         */
        void onBeforeAddition(HollowConsumer c);

        /**
         * Called after the refresh listener is removed.
         * @param c the consumer the associated reference listener is being removed from
         */
        void onAfterRemoval(HollowConsumer c);
    }


    public static class AbstractRefreshListener implements TransitionAwareRefreshListener {
        @Override
        public void refreshStarted(long currentVersion, long requestedVersion) {
            // no-op
        }

        @Override
        public void transitionsPlanned(long beforeVersion, long desiredVersion, boolean isSnapshotPlan, List<HollowConsumer.Blob.BlobType> transitionSequence) {
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

    public static HollowConsumer.Builder<?> withBlobRetriever(HollowConsumer.BlobRetriever blobRetriever) {
        HollowConsumer.Builder<?> builder = new Builder<>();
        return builder.withBlobRetriever(blobRetriever);
    }

    public static HollowConsumer.Builder<?> withLocalBlobStore(File localBlobStoreDir) {
        HollowConsumer.Builder<?> builder = new Builder<>();
        return builder.withLocalBlobStore(localBlobStoreDir);
    }

    @SuppressWarnings("unchecked")
    public static class Builder<B extends HollowConsumer.Builder<B>> {

        protected HollowConsumer.BlobRetriever blobRetriever = null;
        protected HollowConsumer.AnnouncementWatcher announcementWatcher = null;
        protected HollowFilterConfig filterConfig = null;
        protected List<HollowConsumer.RefreshListener> refreshListeners = new ArrayList<>();
        protected HollowAPIFactory apiFactory = HollowAPIFactory.DEFAULT_FACTORY;
        protected HollowObjectHashCodeFinder hashCodeFinder = new DefaultHashCodeFinder();
        protected HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig = DoubleSnapshotConfig.DEFAULT_CONFIG;
        protected HollowConsumer.ObjectLongevityConfig objectLongevityConfig = ObjectLongevityConfig.DEFAULT_CONFIG;
        protected HollowConsumer.ObjectLongevityDetector objectLongevityDetector = ObjectLongevityDetector.DEFAULT_DETECTOR;
        protected File localBlobStoreDir = null;
        protected boolean noFallBackForExistingSnapshot;
        protected Executor refreshExecutor = null;
        protected HollowMetricsCollector<HollowConsumerMetrics> metricsCollector;

        public B withBlobRetriever(HollowConsumer.BlobRetriever blobRetriever) {
            this.blobRetriever = blobRetriever;
            return (B)this;
        }

        public B withLocalBlobStore(File localBlobStoreDir) {
            this.localBlobStoreDir = localBlobStoreDir;
            return (B)this;
        }

        public B withLocalBlobStore(File localBlobStoreDir, boolean noFallBackForExistingSnapshot) {
            this.localBlobStoreDir = localBlobStoreDir;
            this.noFallBackForExistingSnapshot = noFallBackForExistingSnapshot;
            return (B)this;
        }

        public B withAnnouncementWatcher(HollowConsumer.AnnouncementWatcher announcementWatcher) {
            this.announcementWatcher = announcementWatcher;
            return (B)this;
        }

        public B withRefreshListener(HollowConsumer.RefreshListener refreshListener) {
            refreshListeners.add(refreshListener);
            return (B)this;
        }

        public B withRefreshListeners(HollowConsumer.RefreshListener... refreshListeners) {
            Collections.addAll(this.refreshListeners, refreshListeners);
            return (B)this;
        }

        /**
         * Provide the code generated API class that extends {@link HollowAPI}.
         *
         * The instance returned from {@link HollowConsumer#getAPI()} will be of the provided type and can be cast
         * to access generated methods.
         *
         * @param generatedAPIClass the code generated API class
         * @return this builder
         * @throws IllegalArgumentException if provided API class is {@code HollowAPI} instead of a subclass
         */
        public B withGeneratedAPIClass(Class<? extends HollowAPI> generatedAPIClass) {
            if (HollowAPI.class.equals(generatedAPIClass))
                throw new IllegalArgumentException("must provide a code generated API class");
            this.apiFactory = new HollowAPIFactory.ForGeneratedAPI<>(generatedAPIClass);
            return (B)this;
        }

        public B withFilterConfig(HollowFilterConfig filterConfig) {
            this.filterConfig = filterConfig;
            return (B)this;
        }

        public B withDoubleSnapshotConfig(HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig) {
            this.doubleSnapshotConfig = doubleSnapshotConfig;
            return (B)this;
        }

        public B withObjectLongevityConfig(HollowConsumer.ObjectLongevityConfig objectLongevityConfig) {
            this.objectLongevityConfig = objectLongevityConfig;
            return (B)this;
        }

        public B withObjectLongevityDetector(HollowConsumer.ObjectLongevityDetector objectLongevityDetector) {
            this.objectLongevityDetector = objectLongevityDetector;
            return (B)this;
        }

        public B withRefreshExecutor(Executor refreshExecutor) {
            this.refreshExecutor = refreshExecutor;
            return (B)this;
        }

        public B withMetricsCollector(HollowMetricsCollector<HollowConsumerMetrics> metricsCollector) {
            this.metricsCollector = metricsCollector;
            return (B)this;
        }

        @Deprecated
        public B withHashCodeFinder(HollowObjectHashCodeFinder hashCodeFinder) {
            this.hashCodeFinder = hashCodeFinder;
            return (B)this;
        }

        protected void checkArguments() {
            if (blobRetriever == null && localBlobStoreDir == null) {
                throw new IllegalArgumentException(
                        "A HollowBlobRetriever or local blob store directory must be specified when building a HollowClient");
            }

            BlobRetriever blobRetriever = this.blobRetriever;
            if (localBlobStoreDir != null) {
                this.blobRetriever = new HollowFilesystemBlobRetriever(
                        localBlobStoreDir.toPath(), blobRetriever, noFallBackForExistingSnapshot);
            }

            if (refreshExecutor == null) {
                refreshExecutor = newSingleThreadExecutor(r -> daemonThread(r, getClass(), "refresh"));
            }
        }

        public HollowConsumer build() {
            checkArguments();
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
