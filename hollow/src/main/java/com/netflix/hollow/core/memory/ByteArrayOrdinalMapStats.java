/*
 *  Copyright 2016-2021 Netflix, Inc.
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
package com.netflix.hollow.core.memory;

/**
 * Statistics for a {@link ByteArrayOrdinalMap}.
 */
public class ByteArrayOrdinalMapStats {
    private final int maxOrdinal;
    private final long byteDataLength;
    private final float loadFactor;

    public ByteArrayOrdinalMapStats(int maxOrdinal, long byteDataLength, float loadFactor) {
        this.maxOrdinal = maxOrdinal;
        this.byteDataLength = byteDataLength;
        this.loadFactor = loadFactor;
    }

    /**
     * @return the maximum ordinal value in the map
     */
    public int getMaxOrdinal() {
        return maxOrdinal;
    }

    /**
     * @return the length of the byte data array
     */
    public long getByteDataLength() {
        return byteDataLength;
    }

    /**
     * @return the current load factor of the map
     */
    public float getLoadFactor() {
        return loadFactor;
    }

    @Override
    public String toString() {
        return String.format("{\"maxOrdinal\":%d,\"byteDataLength\":%d,\"loadFactor\":%f}",
            maxOrdinal, byteDataLength, loadFactor);
    }

}

