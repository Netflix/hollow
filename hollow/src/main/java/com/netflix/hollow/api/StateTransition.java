package com.netflix.hollow.api;

/**
 * Immutable class representing a single point along the delta chain.
 *
 * @author Tim Taylor {@literal<timt@netflix.com>}
 */
public final class StateTransition {

    private final long fromVersion;
    private final long toVersion;

    /**
     * Creates a transition representing a brand new delta chain or a break
     * in an existing chain. This transition doesn't have a from nor a to version;
     * calling {@link #advance(long)} on this transition will return new a transition representing
     * the first state produced on this chain, i.e. the first snapshot.
     *
     * @return a state transition with neither a {@code fromVersion} nor a {@code toVersion}
     */
    public StateTransition() {
        this(Long.MIN_VALUE, Long.MIN_VALUE);
    }

    /**
     * Creates a transition capable of being used to restore from a delta chain at
     * the specified version, a.k.a. a snapshot.<p>
     *
     * Consumers can initialize their read state from a snapshot corresponding to
     * this transition; an already initialized consumer can only utilize
     * this by performing a double snapshot.<p>
     *
     * A producer would use this transition to restore from a previous announced state in order
     * to resume producing on that delta chain by calling {@link #advance(long)} when ready to
     * produce the next state.
     *
     * @return a state transition with no {@code fromVersion} and the specified version as the {@code toVersion}
     *
     * @see <a href="http://hollow.how/advanced-topics/#double-snapshots">Double Snapshot</a>

     */
    public StateTransition(long toVersion) {
        this(Long.MIN_VALUE, toVersion);
    }

    /**
     * Creates a transition fully representing a transition within the delta chain, a.k.a. a delta.
     *
     * @return a state transition with the specified fromVersion and toVersion
     */
    public StateTransition(long fromVersion, long toVersion) {
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
    }

    /**
     * Returns a new transition representing the transition from this state's {@code toVersion} to the specified version;
     * equivalent to calling {@code new StateTransition(this.toVersion, nextVersion)}.
     *
     * <pre>
     * <code>
     * [13,45].advance(72) == [45,72]
     * </code>
     * </pre>
     *
     * @param nextVersion the next version to transition to
     *
     * @return a new state transition with its {@fromVersion} and {@toVersion} assigned our {@toVersion} and
     *     the specified {@code nextVersion} respectively
     */
    public StateTransition advance(long nextVersion) {
        return new StateTransition(toVersion, nextVersion);
    }

    public long getFromVersion() {
        return fromVersion;
    }

    public long getToVersion() {
        return toVersion;
    }

    /**
     * Determines whether this transition represents a new or broken delta chain.
     *
     * @return true if this has neither a {@code fromVersion} nor a {@code toVersion}; false otherwise.
     */
    public boolean isDiscontinous() {
        return fromVersion == Long.MIN_VALUE && toVersion == Long.MIN_VALUE;
    }

    /**
     * Determines whether this state represents a delta, e.g. a transition between two state versions.
     *
     * @return true if this has a {@code fromVersion} and {@code toVersion};
     */
    public boolean isDelta() {
        return fromVersion != Long.MIN_VALUE && toVersion != Long.MIN_VALUE;
    }

    public boolean isForwardDelta() {
        return isDelta() && fromVersion < toVersion;
    }

    public boolean isReverseDelta() {
        return isDelta() && fromVersion > toVersion;
    }

    public boolean isSnapshot() {
        return !isDiscontinous() && !isDelta();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(isDiscontinous()) {
            sb.append("new/broken delta chain");
        } else if(isDelta()) {
            if(isReverseDelta()) sb.append("reverse");
            sb.append("delta [");
            sb.append(fromVersion);
            sb.append(" <-> ");
            sb.append(toVersion);
            sb.append("]");
        } else {
            sb.append("snapshot [");
            sb.append(toVersion);
            sb.append("]");
        }
        return sb.toString();
    }

}
