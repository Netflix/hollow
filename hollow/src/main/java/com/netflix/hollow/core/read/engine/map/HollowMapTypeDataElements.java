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
package com.netflix.hollow.core.read.engine.map;

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;

/**
 * This class holds the data for a {@link HollowMapTypeReadState}.
 * 
 * During a delta, the HollowMapTypeReadState will create a new HollowMapTypeDataElements and atomically swap
 * with the existing one to make sure a consistent view of the data is always available. 
 */
public class HollowMapTypeDataElements {

    int maxOrdinal;

    FixedLengthElementArray mapPointerAndSizeArray;
    FixedLengthElementArray entryArray;

    GapEncodedVariableLengthIntegerReader encodedRemovals;
    GapEncodedVariableLengthIntegerReader encodedAdditions;

    int bitsPerMapPointer;
    int bitsPerMapSizeValue;
    int bitsPerFixedLengthMapPortion;
    int bitsPerKeyElement;
    int bitsPerValueElement;
    int bitsPerMapEntry;
    int emptyBucketKeyValue;
    long totalNumberOfBuckets;

    final ArraySegmentRecycler memoryRecycler;

    public HollowMapTypeDataElements(ArraySegmentRecycler memoryRecycler) {
        this.memoryRecycler = memoryRecycler;
    }

    void readSnapshot(RandomAccessFile raf, MappedByteBuffer buffer, BufferedWriter debug) throws IOException {
        readFromStream(raf, buffer, debug, false);
    }

    void readDelta(RandomAccessFile raf) throws IOException {
        throw new UnsupportedOperationException();
    }

    private void readFromStream(RandomAccessFile raf, MappedByteBuffer buffer, BufferedWriter debug, boolean isDelta) throws IOException {
        maxOrdinal = VarInt.readVInt(raf);

        if(isDelta) {
            //encodedRemovals = GapEncodedVariableLengthIntegerReader.readEncodedDeltaOrdinals(dis, memoryRecycler);
            //encodedAdditions = GapEncodedVariableLengthIntegerReader.readEncodedDeltaOrdinals(dis, memoryRecycler);
            throw new UnsupportedOperationException();
        }

        bitsPerMapPointer = VarInt.readVInt(raf);
        bitsPerMapSizeValue = VarInt.readVInt(raf);
        bitsPerKeyElement = VarInt.readVInt(raf);
        bitsPerValueElement = VarInt.readVInt(raf);
        bitsPerFixedLengthMapPortion = bitsPerMapPointer + bitsPerMapSizeValue;
        bitsPerMapEntry = bitsPerKeyElement + bitsPerValueElement;
        emptyBucketKeyValue = (1 << bitsPerKeyElement) - 1;
        totalNumberOfBuckets = VarInt.readVLong(raf);

        /// list pointer array
        mapPointerAndSizeArray = FixedLengthElementArray.deserializeFrom(raf, buffer, memoryRecycler);

        /// element array
        entryArray = FixedLengthElementArray.deserializeFrom(raf, buffer, memoryRecycler);

        debug.append("HollowMapTypeDataElements mapPointerAndSizeArray= \n");
        mapPointerAndSizeArray.pp(debug);
        debug.append("HollowMapTypeDataElements entryArray= \n");
        entryArray.pp(debug);
        debug.append("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * end HollowMapTypeDataElements\n");
    }

    static void discardFromStream(DataInputStream dis, int numShards, boolean isDelta) throws IOException {
        if(numShards > 1)
            VarInt.readVInt(dis); /// max ordinal

        for(int i=0; i<numShards; i++) {
            VarInt.readVInt(dis); /// max ordinal

            if(isDelta) {
                /// addition/removal ordinals
                GapEncodedVariableLengthIntegerReader.discardEncodedDeltaOrdinals(dis);
                GapEncodedVariableLengthIntegerReader.discardEncodedDeltaOrdinals(dis);
            }
    
            /// statistics
            VarInt.readVInt(dis);
            VarInt.readVInt(dis);
            VarInt.readVInt(dis);
            VarInt.readVInt(dis);
            VarInt.readVLong(dis);
    
            /// fixed length data
            FixedLengthElementArray.discardFrom(dis);
            FixedLengthElementArray.discardFrom(dis);
        }
    }

    public void applyDelta(HollowMapTypeDataElements fromData, HollowMapTypeDataElements deltaData) {
        new HollowMapDeltaApplicator(fromData, deltaData, this).applyDelta();
    }

    public void destroy() {
        mapPointerAndSizeArray.destroy(memoryRecycler);
        entryArray.destroy(memoryRecycler);
    }

}
