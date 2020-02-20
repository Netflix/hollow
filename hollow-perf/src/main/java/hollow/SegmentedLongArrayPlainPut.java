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
package hollow;

import com.netflix.hollow.core.memory.HollowUnsafeHandle;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import java.io.DataOutputStream;
import java.io.IOException;
import sun.misc.Unsafe;

// This is a copy of the class in hollow that replaces release stores with plain stores.

/**
 * A segmented long array can grow without allocating successively larger blocks and copying memory.<p>
 * <p>
 * Segment length is always a power of two so that the location of a given index can be found with mask and shift operations.<p>
 * <p>
 * Conceptually this can be thought of as a single long array of undefined length.  The currently allocated buffer will always be
 * a multiple of the size of the segments.  The buffer will grow automatically when a byte is written to an index greater than the
 * currently allocated buffer.
 *
 * @author dkoszewnik
 */
@SuppressWarnings("restriction")
public class SegmentedLongArrayPlainPut {

    private static final Unsafe unsafe = HollowUnsafeHandle.getUnsafe();

    protected final long[][] segments;
    protected final int log2OfSegmentSize;
    protected final int bitmask;

    public SegmentedLongArrayPlainPut(ArraySegmentRecycler memoryRecycler, long numLongs) {
        this.log2OfSegmentSize = memoryRecycler.getLog2OfLongSegmentSize();
        int numSegments = (int) ((numLongs - 1) >>> log2OfSegmentSize) + 1;
        long[][] segments = new long[numSegments][];
        this.bitmask = (1 << log2OfSegmentSize) - 1;

        for (int i = 0; i < segments.length; i++) {
            segments[i] = memoryRecycler.getLongArray();
        }

        /// The following assignment is purposefully placed *after* the population of all segments.
        /// The final assignment after the initialization of the array guarantees that no thread
        /// will see any of the array elements before assignment.
        /// We can't risk the segment values being visible as null to any thread, because
        /// FixedLengthElementArray uses Unsafe to access these values, which would cause the
        /// JVM to crash with a segmentation fault.
        this.segments = segments;
    }

    /**
     * Set the byte at the given index to the specified value
     */
    public void set(long index, long value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the value of the byte at the specified index.
     */
    public long get(long index) {
        int segmentIndex = (int) (index >>> log2OfSegmentSize);
        return segments[segmentIndex][(int) (index & bitmask)];
    }

    public void fill(long value) {
        throw new UnsupportedOperationException();
    }

    public void writeTo(DataOutputStream dos, long numLongs) throws IOException {
        VarInt.writeVLong(dos, numLongs);

        for (long i = 0; i < numLongs; i++) {
            dos.writeLong(get(i));
        }
    }

    public void destroy(ArraySegmentRecycler memoryRecycler) {
        for (int i = 0; i < segments.length; i++) {
            if (segments[i] != null) {
                memoryRecycler.recycleLongArray(segments[i]);
            }
        }
    }
}
