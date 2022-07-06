package com.netflix.hollow.core.index;

import java.util.Collection;

/**
 * This package is for internal use. Do not depend on it.
 *
 * This interface allows us to re-use tests for two very similar classes. If we
 * merge {@link HollowPrimaryKeyIndex} and {@link HollowUniqueKeyIndex}, then this
 * interface won't be necessary.
 */
interface UniqueKeyIndex {
    void listenForDeltaUpdates();

    int getMatchingOrdinal(Object key);
    int getMatchingOrdinal(Object key1, Object key2);
    int getMatchingOrdinal(Object key1, Object key2, Object key3);
    int getMatchingOrdinal(Object ... keys);

    Object[] getRecordKey(int ordinal);

    boolean containsDuplicates();

    Collection<Object[]> getDuplicateKeys();
}
