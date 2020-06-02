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

public class EncodedByteBuffer implements VariableLengthData {

    private BlobByteBuffer bufferView;
    private long maxIndex;

    public EncodedByteBuffer() {
        this.maxIndex = -1;
    }

    @Override
    public byte get(long index) {
        if (index > this.maxIndex) {
            throw new IllegalStateException();
        }

        byte retVal = this.bufferView.getByte(this.bufferView.position() + index);
        return retVal;
    }

    /**
     * {@inheritDoc}
     * This is achieved by initializing a {@code BlobByteBuffer} that is a view on the underlying {@code BlobByteBuffer}
     * and advancing the position of the underlying buffer by <i>length</i> bytes.
     */
    @Override
    public void loadFrom(HollowBlobInput in, long length) throws IOException {
        BlobByteBuffer buffer = in.getBuffer();
        this.maxIndex = length - 1;
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

    @Override
    public long size() {
        return maxIndex + 1;
    }
}
