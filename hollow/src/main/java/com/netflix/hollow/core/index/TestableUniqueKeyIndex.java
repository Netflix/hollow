package com.netflix.hollow.core.index;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex.DuplicateKeyInfo;

import java.util.Collection;

/**
 * This package is for internal use. Do not depend on it.
 *
 * This interface allows us to re-use tests for two very similar classes. If we
 * merge {@link HollowPrimaryKeyIndex} and {@link HollowUniqueKeyIndex}, then this
 * interface won't be necessary.
 */
@SuppressWarnings({"DeprecatedIsStillUsed", "override"})
@Deprecated
interface TestableUniqueKeyIndex {
    void listenForDeltaUpdates();

    int getMatchingOrdinal(Object key);
    int getMatchingOrdinal(Object key1, Object key2);
    int getMatchingOrdinal(Object key1, Object key2, Object key3);

    Object[] getRecordKey(int ordinal);

    boolean containsDuplicates();

    Collection<Object[]> getDuplicateKeys();

    Collection<DuplicateKeyInfo> getDuplicateKeys(int maxDuplicateKeys);
}
