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
package com.netflix.hollow.core;

/**
 * An interface to gather various sentinel constants used across hollow.
 */
public interface HollowConstants {
    /**
     * A version of VERSION_LATEST signifies "latest version".
     */
    long VERSION_LATEST = Long.MAX_VALUE;

    /**
     * A version of VERSION_NONE signifies "no version".
     */
    long VERSION_NONE = Long.MIN_VALUE;

    /**
     * An ordinal of NULL_ORDINAL signifies "null reference" or "no ordinal"
     */
    int ORDINAL_NONE = -1;

    /**
     * The maximum number of buckets allowed in a Hollow hash table. Empty space is reserved (based on 70% load factor),
     * otherwise performance approaches O(n).
     */
    int HASH_TABLE_MAX_SIZE = (int) ((1L << 30) * 7 / 10);
}
