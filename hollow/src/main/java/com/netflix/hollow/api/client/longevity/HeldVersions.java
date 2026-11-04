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
package com.netflix.hollow.api.client.longevity;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.client.StackTraceRecorder;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.longevity.HollowVersionDiff;
import com.netflix.hollow.api.consumer.longevity.HollowVersionHold;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.proxy.HollowProxyDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.util.IntMap;
import com.netflix.hollow.core.util.RemovedOrdinalIterator;
import com.netflix.hollow.tools.history.HollowHistoricalStateCreator;
import com.netflix.hollow.tools.history.HollowHistoricalStateDataAccess;
import com.netflix.hollow.tools.history.IntMapOrdinalRemapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Held mode's bookkeeping for one state engine: its holds, and for each delta applied since the oldest of them, copies
 * of the records it removed, its added and removed ordinals and its version's header tags. Public only for the
 * consumer's data holder and updater; not an API.
 * <p>
 * A position counts the deltas the engine has applied since its snapshot, so it follows apply order, which a reverse
 * delta does not reverse. A hold at a position reads the copies of every later delta, in apply order, and then the
 * engine: the first copy that has an ordinal holds its record as it was at the hold's version. With direct lookup, a
 * hold finds that copy, or the engine, with one lookup; with it off, a hold walks the chain of copies.
 * <p>
 * Holds and deltas change under the {@link com.netflix.hollow.api.client.HollowClientUpdater}'s lock, and each delta is applied under this object's
 * monitor too. A hold's populated ordinals are computed under the monitor, diffs read {@link #published}, and reads
 * take no lock.
 */
public final class HeldVersions {
    private static final Logger LOG = Logger.getLogger(HeldVersions.class.getName());
    private static final int[] NONE = new int[0];

    private final HollowReadStateEngine stateEngine;
    private final HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig;
    private final HollowConsumer.HeldVersionsConfig config;
    private final List<Hold> holds = new ArrayList<>(); // open, and closed ones whose reads are still exact
    private final List<Delta> deltas = new ArrayList<>(); // in apply order, every delta after the earliest hold
    private volatile List<Delta> published = Collections.emptyList(); // a copy of deltas, for diffs
    private long position;
    private long version = HollowConstants.VERSION_NONE;
    private boolean copyNextDelta;
    private boolean copiesFailed;
    private boolean applying; // the engine may have moved past what this records
    private DirectLookup lookup; // null while direct lookup is off

    public HeldVersions(HollowReadStateEngine stateEngine, HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig,
            HollowConsumer.HeldVersionsConfig config) {
        this.stateEngine = stateEngine;
        this.doubleSnapshotConfig = doubleSnapshotConfig;
        this.config = config;
        readDirectLookupGuard();
    }

    /**
     * Reads {@link HollowConsumer.HeldVersionsConfig#enableDirectLookup()}, at every refresh. Turned off, every hold
     * reading through direct lookup goes back to the chain of copies, which stays linked; turned on, holds from the
     * engine's position on read through it.
     */
    public void readDirectLookupGuard() {
        boolean enabled = config.enableDirectLookup();
        if (enabled && lookup == null) {
            lookup = new DirectLookup(stateEngine, position);
        } else if (!enabled && lookup != null) {
            lookup = null;
            for (Hold hold : holds) {
                if (hold.dataAccess.getProxiedDataAccess() instanceof HollowHeldStateDataAccess)
                    hold.dataAccess.setDataAccess(deltaAt(hold.position + 1).copy);
            }
        }
    }

    /** @return whether a hold is open, which makes every refresh other than a hold's a background refresh */
    public boolean hasOpenHolds() {
        for (Hold hold : holds) {
            if (!hold.closed)
                return true;
        }
        return false;
    }

    public void snapshotLoaded(long version) {
        this.version = version;
    }

    /**
     * Applies a delta to the engine. A hold's populated ordinals are read from the engine and the deltas' records, so
     * they wait until the delta is applied and recorded. Listeners the delta calls on this thread, such as a refresh
     * listener's blobLoaded or a type state listener, see the engine move before the delta is recorded, so they can't
     * hold versions or read populated ordinals.
     */
    public synchronized void applyDelta(long toVersion, Transition transition) throws IOException {
        beforeDelta();
        applying = true;
        try {
            transition.apply();
            afterDelta(toVersion);
        } finally {
            applying = false;
        }
    }

    private void requireNoDeltaApplying() {
        if (applying)
            throw new IllegalStateException("Versions can't be held and populated ordinals can't be read while a delta "
                    + "is applied, as in blobLoaded or a type state listener: use deltaUpdateOccurred or later");
    }

    /**
     * A hold closed since the previous delta takes effect now: it no longer needs copies, because the records this
     * delta removes stay readable in the engine until the next delta.
     */
    private void beforeDelta() {
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
     * Records the delta and copies the records it removed if a hold is open, then stops serving copies to holds closed
     * before the previous delta, and drops the deltas no hold needs.
     */
    private void afterDelta(long toVersion) {
        long previous = position++;
        version = toVersion;
        Delta delta = null;
        if (copyNextDelta) {
            try {
                delta = new Delta(position, toVersion, stateEngine);
                deltas.add(delta); // even if copying fails: holds that read the engine still need its records
                delta.copy = new HollowHistoricalStateCreator(null).createBasedOnNewDelta(toVersion, stateEngine);
                if (lookup != null)
                    lookup.index(position, delta.copy, delta.removed);
            } catch (RuntimeException | Error e) {
                copiesFailed = true;
                published = new ArrayList<>(deltas);
                boolean recoverable = doubleSnapshotConfig.allowDoubleSnapshot();
                LOG.log(recoverable ? Level.WARNING : Level.SEVERE, stuck(recoverable), e);
                throw e;
            }
            Delta last = deltas.size() > 1 ? deltas.get(deltas.size() - 2) : null;
            if (last != null && last.position == previous)
                last.copy.setNextState(delta.copy);
        }

        for (Iterator<Hold> it = holds.iterator(); it.hasNext(); ) {
            Hold hold = it.next();
            if (hold.releasedAt >= 0 && hold.releasedAt < position) {
                hold.dataAccess.setDataAccess(releasedView(hold.version));
                it.remove();
            } else if (delta != null && hold.position == previous) {
                hold.dataAccess.setDataAccess(behind(previous));
            }
        }
        long earliest = Long.MAX_VALUE;
        for (Hold hold : holds)
            earliest = Math.min(earliest, hold.position);
        while (!deltas.isEmpty() && deltas.get(0).position <= earliest) {
            Delta pruned = deltas.remove(0);
            if (lookup != null)
                lookup.prune(pruned.position, pruned.removed);
        }
        published = new ArrayList<>(deltas);
    }

    /** What a hold behind the engine reads: direct lookup if it indexed every later delta, else the chain of copies. */
    private HollowDataAccess behind(long at) {
        return lookup != null && at >= lookup.getSince() ? lookup.view(at) : deltaAt(at + 1).copy;
    }

    /**
     * Holds {@code version} if it is the engine's, or the engine applied it since the oldest open hold.
     *
     * @return the hold, or null if holding the version needs a refresh
     */
    public HollowVersionHold hold(long version) {
        requireNoDeltaApplying();
        long at = positionOf(version);
        if (at < 0)
            return null;
        HollowProxyDataAccess dataAccess = new HollowProxyDataAccess();
        dataAccess.setDataAccess(at == position ? stateEngine : behind(at));
        Hold hold = new Hold(this, version, at, headerTagsAt(at), dataAccess);
        holds.add(hold);
        return hold;
    }

    /** A version's populated ordinals: the engine's, with each later delta undone, newest first. */
    private synchronized BitSet populatedOrdinals(long at, String type) {
        requireNoDeltaApplying();
        HollowTypeReadState typeState = stateEngine.getTypeState(type);
        if (typeState == null)
            return null;
        BitSet populated = new BitSet();
        populated.or(typeState.getPopulatedOrdinals());
        long undone = position;
        for (int i = deltas.size() - 1; i >= 0 && undone > at && deltas.get(i).position == undone; i--, undone--) {
            Delta delta = deltas.get(i);
            for (int ordinal : delta.added.getOrDefault(type, NONE))
                populated.clear(ordinal);
            for (int ordinal : delta.removed.getOrDefault(type, NONE))
                populated.set(ordinal);
        }
        if (undone != at)
            throw new IllegalStateException("Delta " + undone + " has no records, so the populated ordinals of "
                    + "versions before it are unknown");
        return populated;
    }

    /**
     * Combines the deltas between two holds, from one to the other. An ordinal is removed if its first change is a
     * removal, which takes the record the first hold's version has there, and added if its last change is an addition.
     */
    private HollowVersionDiff diff(Hold from, Hold to) {
        long low = Math.min(from.position, to.position);
        long high = Math.max(from.position, to.position);
        List<Delta> between = new ArrayList<>();
        for (Delta delta : published) {
            if (delta.position > low && delta.position <= high)
                between.add(delta);
        }
        if (between.size() != high - low)
            throw new IllegalStateException("The deltas between versions " + from.version + " and " + to.version
                    + " are no longer kept: diff holds before closing them");
        boolean forward = from.position <= to.position;
        if (!forward)
            Collections.reverse(between);

        Map<String, BitSet> removed = new HashMap<>();
        Map<String, BitSet> added = new HashMap<>();
        Map<String, BitSet> changed = new HashMap<>();
        for (Delta delta : between) {
            for (Map.Entry<String, int[]> removals : (forward ? delta.removed : delta.added).entrySet()) {
                BitSet changedOfType = changed.computeIfAbsent(removals.getKey(), t -> new BitSet());
                BitSet addedOfType = added.computeIfAbsent(removals.getKey(), t -> new BitSet());
                for (int ordinal : removals.getValue()) {
                    if (!changedOfType.get(ordinal))
                        removed.computeIfAbsent(removals.getKey(), t -> new BitSet()).set(ordinal);
                    changedOfType.set(ordinal);
                    addedOfType.clear(ordinal);
                }
            }
            for (Map.Entry<String, int[]> additions : (forward ? delta.added : delta.removed).entrySet()) {
                BitSet changedOfType = changed.computeIfAbsent(additions.getKey(), t -> new BitSet());
                BitSet addedOfType = added.computeIfAbsent(additions.getKey(), t -> new BitSet());
                for (int ordinal : additions.getValue()) {
                    changedOfType.set(ordinal);
                    addedOfType.set(ordinal);
                }
            }
        }
        added.values().removeIf(BitSet::isEmpty);
        return new Diff(from.version, to.version, (int) (high - low), removed, added);
    }

    /** The header tags of the version at a position, which is the engine's, a kept delta's, or the oldest hold's. */
    private Map<String, String> headerTagsAt(long at) {
        if (at == position)
            return Collections.unmodifiableMap(new HashMap<>(stateEngine.getHeaderTags()));
        for (Delta delta : deltas) {
            if (delta.position == at)
                return delta.headerTags;
        }
        for (Hold hold : holds) {
            if (hold.position == at)
                return hold.headerTags;
        }
        throw new IllegalStateException("No header tags at position " + at);
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
        String reason = "Keeping the delta to version " + version + " for version holds failed, so they would read "
                + "records the next delta replaces: this state engine applies no more deltas";
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

    /** Applies a transition to the state engine. */
    public interface Transition {
        void apply() throws IOException;
    }

    /** A delta's records, read from the engine right after it: its version's header tags and the ordinals it changed. */
    private static final class Delta {
        final long position;
        final long toVersion;
        final Map<String, String> headerTags;
        final Map<String, int[]> removed = new HashMap<>(); // ascending, for each type the delta changed
        final Map<String, int[]> added = new HashMap<>();
        HollowHistoricalStateDataAccess copy; // null only if copying failed: the engine applies no more deltas

        Delta(long position, long toVersion, HollowReadStateEngine stateEngine) {
            this.position = position;
            this.toVersion = toVersion;
            this.headerTags = Collections.unmodifiableMap(new HashMap<>(stateEngine.getHeaderTags()));
            for (HollowTypeReadState typeState : stateEngine.getTypeStates()) {
                PopulatedOrdinalListener listener = typeState.getListener(PopulatedOrdinalListener.class);
                record(removed, typeState, new RemovedOrdinalIterator(listener));
                record(added, typeState, new RemovedOrdinalIterator(listener, true));
            }
        }

        private static void record(Map<String, int[]> changes, HollowTypeReadState typeState, RemovedOrdinalIterator it) {
            IntList ordinals = new IntList();
            for (int ordinal = it.next(); ordinal != HollowConstants.ORDINAL_NONE; ordinal = it.next())
                ordinals.add(ordinal);
            if (ordinals.size() > 0)
                changes.put(typeState.getSchema().getName(), ordinals.arrayCopyOfRange(0, ordinals.size()));
        }
    }

    private static final class Diff implements HollowVersionDiff {
        private final long fromVersion;
        private final long toVersion;
        private final int deltaCount;
        private final Map<String, BitSet> removed;
        private final Map<String, BitSet> added;
        private final Set<String> changedTypes;

        /** Between holds of different state engines. */
        Diff(long fromVersion, long toVersion) {
            this(fromVersion, toVersion, -1, Collections.emptyMap(), Collections.emptyMap());
        }

        Diff(long fromVersion, long toVersion, int deltaCount, Map<String, BitSet> removed, Map<String, BitSet> added) {
            this.fromVersion = fromVersion;
            this.toVersion = toVersion;
            this.deltaCount = deltaCount;
            this.removed = removed;
            this.added = added;
            Set<String> changedTypes = new HashSet<>(removed.keySet());
            changedTypes.addAll(added.keySet());
            this.changedTypes = Collections.unmodifiableSet(changedTypes);
        }

        @Override
        public long getFromVersion() {
            return fromVersion;
        }

        @Override
        public long getToVersion() {
            return toVersion;
        }

        @Override
        public boolean isIncremental() {
            return deltaCount >= 0;
        }

        @Override
        public int getDeltaCount() {
            return deltaCount;
        }

        @Override
        public Set<String> getChangedTypes() {
            requireIncremental();
            return changedTypes;
        }

        @Override
        public BitSet getRemovedOrdinals(String type) {
            requireIncremental();
            return removed.getOrDefault(type, new BitSet());
        }

        @Override
        public BitSet getAddedOrdinals(String type) {
            requireIncremental();
            return added.getOrDefault(type, new BitSet());
        }

        private void requireIncremental() {
            if (deltaCount < 0)
                throw new IllegalStateException("Versions " + fromVersion + " and " + toVersion + " are held on "
                        + "different state engines, whose ordinals are unrelated");
        }
    }

    static final class Hold implements HollowVersionHold {
        private final HeldVersions owner;
        private final long version;
        private final long position;
        private final Map<String, String> headerTags;
        private final HollowProxyDataAccess dataAccess;
        private final ConcurrentHashMap<Class<?>, HollowAPI> apis = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<String, BitSet> populated = new ConcurrentHashMap<>();
        private volatile boolean closed;
        private long releasedAt = -1;

        private Hold(HeldVersions owner, long version, long position, Map<String, String> headerTags,
                HollowProxyDataAccess dataAccess) {
            this.owner = owner;
            this.version = version;
            this.position = position;
            this.headerTags = headerTags;
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
        public Map<String, String> getHeaderTags() {
            return headerTags;
        }

        @Override
        public HollowDataAccess getDataAccess() {
            return dataAccess;
        }

        @Override
        public BitSet getPopulatedOrdinals(String type) {
            requireOpen();
            BitSet ordinals = populated.get(type);
            if (ordinals == null) {
                ordinals = owner.populatedOrdinals(position, type);
                if (ordinals != null) {
                    BitSet raced = populated.putIfAbsent(type, ordinals);
                    if (raced != null)
                        ordinals = raced;
                }
            }
            return ordinals;
        }

        @Override
        public HollowVersionDiff diffFrom(HollowVersionHold older) {
            if (!(older instanceof Hold))
                throw new IllegalArgumentException("Not a version hold of a held-mode consumer: " + older);
            Hold from = (Hold) older;
            requireOpen();
            from.requireOpen();
            return from.owner == owner ? owner.diff(from, this) : new Diff(from.version, version);
        }

        private void requireOpen() {
            if (closed)
                throw new IllegalStateException("The hold of version " + version + " is closed");
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
