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
package com.netflix.hollow.core.memory;

/**
 * This interface is used to hide the underlying implementation of a range of bytes.
 *  
 * This is useful because Hollow often uses pooled arrays to back range of bytes.  
 *
 * @see SegmentedByteArray
 * @see EncodedByteBuffer
 *
 * @author dkoszewnik
 *
 */
public interface ByteData {

    default long readLongBits(long position) {
        long longBits = (long) (get(position++) & 0xFF) << 56;
        longBits |= (long) (get(position++) & 0xFF) << 48;
        longBits |= (long) (get(position++) & 0xFF) << 40;
        longBits |= (long) (get(position++) & 0xFF) << 32;
        longBits |= (long) (get(position++) & 0xFF) << 24;
        longBits |= (get(position++) & 0xFF) << 16;
        longBits |= (get(position++) & 0xFF) << 8;
        longBits |= (get(position) & 0xFF);
        return longBits;
    }

    default int readIntBits(long position) {
        int intBits = (get(position++) & 0xFF) << 24;
        intBits |= (get(position++) & 0xFF) << 16;
        intBits |= (get(position++) & 0xFF) << 8;
        intBits |= (get(position) & 0xFF);
        return intBits;
    }

    default long length() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the value of the byte at the specified position.
     * @param index the position (in byte units)
     * @return the byte value
     */
    byte get(long index);

}
