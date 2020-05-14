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

import static com.netflix.hollow.core.util.Threads.daemonThread;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.HollowConstants;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementations of this class are responsible for two things:
 *
 * 1) Tracking the latest announced data state version.
 * 2) Keeping the client up to date by calling triggerAsyncRefresh() on self when the latest version changes.
 * 
 * A default implementation {@link HollowAnnouncementWatcher.DefaultWatcher} is available.  If this implementation
 * is used, calling {@link HollowClient#triggerRefresh()} will always attempt to get to the latest state, unless an
 * explicit state was specified via {@link HollowClient#triggerRefreshTo(long)}.
 * 
 * @deprecated Implement the {@link HollowConsumer.AnnouncementWatcher} for use with the {@link HollowConsumer} instead.
 *
 */
@Deprecated
public abstract class HollowAnnouncementWatcher {
    private static final Logger log = Logger.getLogger(HollowAnnouncementWatcher.class.getName());
    private final ExecutorService refreshExecutor;

    /**
     * Construct a HollowAnnouncementWatcher with a default ExecutorService.
     */
    public HollowAnnouncementWatcher() {
        refreshExecutor = Executors.newFixedThreadPool(1,
                r -> daemonThread(r, getClass(), "watch"));
    }

    /**
     * Construct a HollowAnnouncementWatcher with the specified ExecutorService.
     *
     * @param refreshExecutor the ExecutorService to use for asynchronous state refresh.
     */
    public HollowAnnouncementWatcher(ExecutorService refreshExecutor) {
        this.refreshExecutor = refreshExecutor;
    }

    /**
     * @return the latest announced version.
     */
    public abstract long getLatestVersion();

    /**
     * If some push announcement mechanism is to be provided by this HollowAnnouncementWatcher, subscribe here.
     * Alternatively, if some polling announcement mechanism is to be provided, setup the polling cycle here.
     *
     * When announcements are received, or polling reveals a new version, a call should be placed to triggerRefresh().
     */
    public abstract void subscribeToEvents();

    /**
     * Override this method ONLY if it is legal to explicitly update to a specific version.
     *
     * @param latestVersion the latest version
     */
    public void setLatestVersion(long latestVersion) {
        throw new UnsupportedOperationException("Cannot explicitly set latest version on a " + this.getClass());
    }

    /**
     * Will force a double snapshot refresh on the next update.
     */
    protected void forceDoubleSnapshotNextUpdate() {
        client.forceDoubleSnapshotNextUpdate();
    }

    /**
     * Triggers a refresh in a new thread immediately.
     */
    public void triggerAsyncRefresh() {
        triggerAsyncRefreshWithDelay(0);
    }

    /**
     * Triggers async refresh after some random number of milliseconds have passed,
     * between now and the specified maximum number of milliseconds.
     *
     * Any subsequent calls for async refresh will not begin until after the specified delay
     * has completed.
     *
     * @param maxDelayMillis the maximum delay in milliseconds
     */
    public void triggerAsyncRefreshWithRandomDelay(int maxDelayMillis) {
        Random rand = new Random();
        int delayMillis = maxDelayMillis > 0 ? rand.nextInt(maxDelayMillis) : 0;
        triggerAsyncRefreshWithDelay(delayMillis);
    }

    /**
     * Triggers async refresh after the specified number of milliseconds has passed.
     *
     * Any subsequent calls for async refresh will not begin until after the specified delay
     * has completed.
     *
     * @param delayMillis the delay in milliseconds
     */
    public void triggerAsyncRefreshWithDelay(int delayMillis) {
        final HollowClient client = this.client;
        final long targetBeginTime = System.currentTimeMillis() + delayMillis;

        refreshExecutor.execute(new Runnable() {
            public void run() {
                try {
                    long delay = targetBeginTime - System.currentTimeMillis();
                    if(delay > 0)
                        Thread.sleep(delay);
                    client.triggerRefresh();
                } catch(Throwable th) {
                    log.log(Level.SEVERE, "Async refresh failed", th);
                }
            }
        });
    }


    private HollowClient client;
    
    protected HollowClient getClientToNotify() { return client; } 

    void setClientToNotify(HollowClient client) {
        this.client = client;
        subscribeToEvents();
    }

    public static class DefaultWatcher extends HollowAnnouncementWatcher {

        private long latestVersion = HollowConstants.VERSION_LATEST;

        public DefaultWatcher() {
            super();
        }

        public DefaultWatcher(ExecutorService refreshExecutor) {
            super(refreshExecutor);
        }

        @Override
        public long getLatestVersion() {
            /// by default, just try to fetch the maximum available version
            return latestVersion;
        }

        @Override
        public void subscribeToEvents() {
            // by default, update events not available.
        }

        @Override
        public void setLatestVersion(long latestVersion) {
            this.latestVersion = latestVersion;
        }
    };

}
