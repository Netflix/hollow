/*
 *  Copyright 2026 Netflix, Inc.
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
import com.netflix.hollow.api.consumer.longevity.HollowVersionHold;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.proxy.HollowProxyDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.util.IntMap;
import com.netflix.hollow.tools.history.HollowHistoricalStateCreator;
import com.netflix.hollow.tools.history.HollowHistoricalStateDataAccess;
import com.netflix.hollow.tools.history.IntMapOrdinalRemapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Held mode's bookkeeping for one state engine: its holds, and copies of the records removed by each delta applied
 * since the oldest of them.
 * <p>
 * A position counts the deltas the engine has applied since its snapshot, so it follows apply order, which a reverse
 * delta does not reverse. A hold at a position reads the copies of every later delta, in apply order, and then the
 * engine: the first copy that has an ordinal holds its record as it was at the hold's version.
 * <p>
 * Everything but {@link Hold#close()} and reads runs under the {@link HollowClientUpdater}'s lock.
 */
final class HeldVersions {
    private static final Logger LOG = Logger.getLogger(HeldVersions.class.getName());

    private final HollowReadStateEngine stateEngine;
    private final HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig;
    private final List<Hold> holds = new ArrayList<>(); // open, and closed ones whose reads are still exact
    private final List<Delta> deltas = new ArrayList<>(); // in apply order, every delta after the earliest hold
    private long position;
    private long version = HollowConstants.VERSION_NONE;
    private boolean copyNextDelta;
    private boolean copiesFailed;

    HeldVersions(HollowReadStateEngine stateEngine, HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig) {
        this.stateEngine = stateEngine;
        this.doubleSnapshotConfig = doubleSnapshotConfig;
    }

    void snapshotLoaded(long version) {
        this.version = version;
    }

    /**
     * Called before the engine applies a delta. A hold closed since the previous delta takes effect now: it no longer
     * needs copies, because the records this delta removes stay readable in the engine until the next delta.
     */
    void beforeDelta() {
        if (copiesFailed)
            throw new IllegalStateException(stuck(doubleSnapshotConfig.allowDoubleSnapshot()));
        long next = position + 1;
        copyNextDelta = false;
        for (Hold hold : holds) {
            if (hold.closed && hold.releasedAt < 0)
                hold.releasedAt = next;
            copyNextDelta |= hold.releasedAt < 0;
        }
    }

    /**
     * Called after the engine applied a delta, before the next one. Copies the records it removed if a hold is open,
     * then stops serving copies to holds closed before the previous delta, and drops copies no hold reads.
     */
    void afterDelta(long toVersion) {
        long previous = position++;
        version = toVersion;
        HollowHistoricalStateDataAccess copy = null;
        if (copyNextDelta) {
            try {
                copy = new HollowHistoricalStateCreator(null).createBasedOnNewDelta(toVersion, stateEngine);
            } catch (RuntimeException | Error e) {
                copiesFailed = true;
                boolean recoverable = doubleSnapshotConfig.allowDoubleSnapshot();
                LOG.log(recoverable ? Level.WARNING : Level.SEVERE, stuck(recoverable), e);
                throw e;
            }
        }

        if (copy != null) {
            Delta last = deltas.isEmpty() ? null : deltas.get(deltas.size() - 1);
            if (last != null && last.position == previous)
                last.copy.setNextState(copy);
            deltas.add(new Delta(position, toVersion, copy));
        }
        for (Iterator<Hold> it = holds.iterator(); it.hasNext(); ) {
            Hold hold = it.next();
            if (hold.releasedAt >= 0 && hold.releasedAt < position) {
                hold.dataAccess.setDataAccess(releasedView(hold.version));
                it.remove();
            } else if (copy != null && hold.position == previous) {
                hold.dataAccess.setDataAccess(copy);
            }
        }
        long earliest = Long.MAX_VALUE;
        for (Hold hold : holds)
            earliest = Math.min(earliest, hold.position);
        while (!deltas.isEmpty() && deltas.get(0).position <= earliest)
            deltas.remove(0);
    }

    /**
     * Holds {@code version} if it is the engine's, or the engine applied it since the oldest open hold.
     *
     * @return the hold, or null if holding the version needs a refresh
     */
    Hold hold(long version) {
        long at = positionOf(version);
        if (at < 0)
            return null;
        HollowProxyDataAccess dataAccess = new HollowProxyDataAccess();
        dataAccess.setDataAccess(at == position ? stateEngine : deltaAt(at + 1).copy);
        Hold hold = new Hold(version, at, dataAccess);
        holds.add(hold);
        return hold;
    }

    private long positionOf(long version) {
        if (version == this.version)
            return position;
        if (copiesFailed)
            return -1;
        Hold oldest = null;
        for (Hold hold : holds) {
            if (!hold.closed && (oldest == null || hold.position < oldest.position))
                oldest = hold;
        }
        if (oldest == null)
            return -1;
        for (int i = deltas.size() - 1; i >= 0 && deltas.get(i).position > oldest.position; i--) {
            if (deltas.get(i).toVersion == version)
                return deltas.get(i).position;
        }
        return oldest.version == version ? oldest.position : -1;
    }

    /** Explains why this engine refuses deltas, and whether a double snapshot recovers the consumer. */
    private String stuck(boolean recoverable) {
        String reason = "Copying the records removed by the delta to version " + version + " failed, so version holds "
                + "would read records the next delta replaces: this state engine applies no more deltas";
        return recoverable
                ? reason + ". The consumer recovers at its next double snapshot."
                : reason + ", and double snapshots are disallowed by the consumer's DoubleSnapshotConfig, so it stays at "
                        + "version " + version + " until they are allowed or the consumer is rebuilt.";
    }

    private Delta deltaAt(long position) {
        for (Delta delta : deltas) {
            if (delta.position == position)
                return delta;
        }
        throw new IllegalStateException("No copies of delta " + position);
    }

    /** An empty copy: every read misses it, is reported, and falls through to the engine. */
    private HollowHistoricalStateDataAccess releasedView(long version) {
        IntMapOrdinalRemapper nothingCopied = new IntMapOrdinalRemapper();
        for (HollowTypeReadState typeState : stateEngine.getTypeStates())
            nothingCopied.addOrdinalRemapping(typeState.getSchema().getName(), new IntMap(0));
        HollowHistoricalStateDataAccess view = new HollowHistoricalStateDataAccess(null, version, stateEngine,
                stateEngine.getTypeStates(), nothingCopied, Collections.emptyMap());
        view.setNextState(stateEngine);
        view.setStackTraceRecorder(new ReadsAfterRelease(version));
        return view;
    }

    private static final class Delta {
        final long position;
        final long toVersion;
        final HollowHistoricalStateDataAccess copy;

        Delta(long position, long toVersion, HollowHistoricalStateDataAccess copy) {
            this.position = position;
            this.toVersion = toVersion;
            this.copy = copy;
        }
    }

    static final class Hold implements HollowVersionHold {
        private final long version;
        private final long position;
        private final HollowProxyDataAccess dataAccess;
        private final ConcurrentHashMap<Class<?>, HollowAPI> apis = new ConcurrentHashMap<>();
        private volatile boolean closed;
        private long releasedAt = -1;

        private Hold(long version, long position, HollowProxyDataAccess dataAccess) {
            this.version = version;
            this.position = position;
            this.dataAccess = dataAccess;
        }

        @Override
        public long getVersion() {
            return version;
        }

        @Override
        public <T extends HollowAPI> T getAPI(Class<T> apiClass) {
            // never with cached types: their caches follow the engine's type states
            return apiClass.cast(apis.computeIfAbsent(apiClass,
                    c -> new HollowAPIFactory.ForGeneratedAPI<>(apiClass).createAPI(dataAccess)));
        }

        @Override
        public HollowDataAccess getDataAccess() {
            return dataAccess;
        }

        @Override
        public void close() {
            closed = true;
        }
    }

    /** Logs the first read through a released hold, with its stack trace. */
    private static final class ReadsAfterRelease extends StackTraceRecorder {
        private final long version;
        private final AtomicBoolean logged = new AtomicBoolean();

        ReadsAfterRelease(long version) {
            super(0);
            this.version = version;
        }

        @Override
        public void recordStackTrace(int omitFirstNFrames) {
            if (logged.compareAndSet(false, true)) {
                LOG.log(Level.SEVERE, "Read through the hold of version " + version + " after its release: it reads " +
                        "the engine's current version instead", new IllegalStateException("read after release"));
            }
        }
    }
}
