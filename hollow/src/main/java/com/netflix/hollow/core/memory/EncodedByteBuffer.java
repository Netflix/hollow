/*
 *  Copyright 2016-2020 Netflix, Inc.
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
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.IOException;

// SNAP: TODO: @SuppressWarnings("restriction")
public class EncodedByteBuffer implements VariableLengthData {

    private BlobByteBuffer bufferView;
    private long maxIndex;

    public EncodedByteBuffer() {
        this.maxIndex = -1;
    }

    /**
     * Get the value of the byte at the specified index.
     * @param index the index (in multiples of 1 byte)
     * @return the byte value
     */
    @Override
    public byte get(long index) {
        if (index >= this.maxIndex) {
            throw new IllegalStateException();
        }

        byte retVal = this.bufferView.getByte(this.bufferView.position() + index);
        return retVal;
    }

    /**
     * Copy bytes from the supplied InputStream into this array.
     *
     * @param in the random access file
     * @param length the length of the data to copy
     * @throws IOException if the copy could not be performed
     */
    public void loadFrom(HollowBlobInput in, long length) throws IOException {
        BlobByteBuffer buffer = in.getBuffer();
        this.maxIndex = length; // SNAP: can we model this in bufferView as capacity?
        buffer.position(in.getFilePointer());
        this.bufferView = buffer.duplicate();
        buffer.position(buffer.position() + length);
        in.seek(in.getFilePointer() + length);
    }

    @Override
    public void copy(ByteData src, long srcPos, long destPos, long length) {
        throw new UnsupportedOperationException("Operation not supported in shared-memory mode");
    }

    @Override
    public void orderedCopy(VariableLengthData src, long srcPos, long destPos, long length) {
        throw new UnsupportedOperationException("Operation not supported in shared-memory mode");
    }

// SNAP:
//    public void pp(BufferedWriter debug) throws IOException {
//        StringBuffer pp = new StringBuffer();
//
//        int segmentSize = 1 << log2OfSegmentSize;
//        long maxIndex = segments.length * segmentSize;
//
//
//        pp.append("\n\n EncodedByteBuffer deserializeFrom()s => ");
//        for (int g = 0; g < maxIndex; g ++) {
//            byte v = deserializeFrom(g);
//            pp.append(v+ " ");
//        }
//
//        pp.append("\n");
//        pp.append("\n EncodedByteBuffer raw bytes underneath:\n");
//        for (int i = 0; i < segments.length; i ++) {
//            if (segments[i] == null) {
//                pp.append("- - - - - NULL - - - - ");
//                pp.append("\n");
//                continue;
//            }
//
//            pp.append(String.format("EncodedByteBuffer i= %d/%d => ", i, segments.length-1));
//
//            for (int j = 0; j < segmentSize; j ++ ) {
//                byte v = segments[i].deserializeFrom(segments[i].position() + j);
//                pp.append(v + " ");
//            }
//            pp.append("\n");
//        }
//         debug.append(pp.toString());
//     }
}
