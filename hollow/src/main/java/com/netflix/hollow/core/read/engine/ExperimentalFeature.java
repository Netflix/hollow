package com.netflix.hollow.core.read.engine;

/**
 * Explicit opt-ins to experimental consumer implementations. Each feature has a fixed scope;
 * opting in does not enable unrelated experiments added by a later release.
 * <p>
 * Absence of an opt-in uses the release default. A feature may become the default in a future
 * release, at which point its identifier will be retained as a deprecated, ineffective opt-in.
 */
public enum ExperimentalFeature {
    /**
     * Alternative shard reads and storage implementations. Initially skips trailing read
     * validation for immutable shards; dependent read-path changes extend this experiment
     * with contiguous fixed-length storage, bulk byte-field reads, and scalar fast paths.
     * Does not change recycler selection or permit validation elision for recycling arrays.
     */
    SHARD_READ_FAST_PATHS,

    /** Direct decoding and equality checks against immutable on-heap byte segments. */
    DIRECT_SEGMENT_STRING_READS
}
