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

import com.netflix.hollow.core.memory.encoding.BlobByteBuffer;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import sun.misc.Unsafe;

/**
 * A segmented long array can grow without allocating successively larger blocks and copying memory.<p>
 *
 * Segment length is always a power of two so that the location of a given index can be found with mask and shift operations.<p>
 *
 * Conceptually this can be thought of as a single long array of undefined length.  The currently allocated buffer will always be
 * a multiple of the size of the segments.  The buffer will grow automatically when a byte is written to an index greater than the
 * currently allocated buffer.
 *
 * @author dkoszewnik
 *
 */
@SuppressWarnings("restriction")
public class SegmentedLongArray {   // SNAP: Rename to EncodedLongBuffer

    protected BlobByteBuffer bufferView;
    public final int log2OfSegmentSize; // in longs
    public final int log2OfByteSegments;
    protected final int bitmask;
    protected long maxLongs = -1;
    protected long maxByteIndex = -1;

    public SegmentedLongArray(ArraySegmentRecycler memoryRecycler, long numLongs) {
        this.log2OfSegmentSize = memoryRecycler.getLog2OfLongSegmentSize();
        this.log2OfByteSegments = 8 * this.log2OfSegmentSize;

        int numSegments = (int)((numLongs - 1) >>> log2OfSegmentSize) + 1;

        this.bitmask = (1 << log2OfByteSegments) - 1;
    }

    /**
     * Set the byte at the given index to the specified value
     *
     * @param index the index
     * @param value the byte value
     */
    public void set(long index, long value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the value of the byte at the specified index.
     *
     * @param index the index (in multiples of 8 bytes)
     * @return the byte value
     */
    public long get(long index) {
        if (this.bufferView == null) {
            throw new IllegalStateException("Type is queried before it has been read in");
        }

        long byteIndex = 8 * index;
        if (byteIndex > this.maxByteIndex) {  // it's illegal to read a byte starting past the last byte boundary of data
            throw new IllegalStateException();
        }
        return this.bufferView.getLong(bufferView.position() + byteIndex);
    }

    public void fill(long value) {
        throw new UnsupportedOperationException();
    }

    public void writeTo(DataOutputStream dos, long numLongs) throws IOException {
        VarInt.writeVLong(dos, numLongs);

        for(long i=0;i<numLongs;i++) {
            dos.writeLong(get(i));
        }
    }

    public void destroy(ArraySegmentRecycler memoryRecycler) {
        throw new UnsupportedOperationException();
    }

    protected void readFrom(RandomAccessFile raf, BlobByteBuffer buffer, ArraySegmentRecycler memoryRecycler, long numLongs) throws IOException {
        this.maxLongs = numLongs;
        this.maxByteIndex = this.maxLongs * 64 - 8; // SNAP: should we work this into bufferView capacity?

        if(numLongs == 0)
            return;

        buffer.position(raf.getFilePointer());
        this.bufferView = buffer.duplicate();
        buffer.position(buffer.position() + numLongs*8);
        raf.seek(raf.getFilePointer() + (numLongs  * 8));
    }

    protected void readFrom(DataInputStream dis, ArraySegmentRecycler memoryRecycler, long numLongs) throws IOException {
        throw new UnsupportedOperationException();
    }
}
