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
import java.io.OutputStream;

/**
 * {@code BlobByteBuffer} based implementation of variable length byte data that only supports read.    // TODO: update when supports write
 */
public class EncodedByteBuffer implements VariableLengthData {

    private BlobByteBuffer bufferView;
    private long size;

    public EncodedByteBuffer() {
        this.size = 0;
    }

    public BlobByteBuffer getBufferView() {
        return bufferView;
    }

    @Override
    public byte get(long index) {
        if (index >= this.size) {
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
        this.size = length;
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
    public void orderedCopy(VariableLengthData src, long srcPos, long destPos, long length) throws IOException {
        throw new UnsupportedOperationException("Underlying data can only be mutated using " + VariableLengthDataFactory.StagedVariableLengthData.class.getName());
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public void set(long index, byte value) {
        throw new UnsupportedOperationException("Operation not supported in shared-memory mode");

    }

    /**
     * Write a portion of this data to an OutputStream.
     *
     * @param os the output stream to write to
     * @param startPosition the position to begin copying from this array
     * @param len the length of the data to copy
     * @throws IOException if the write to the output stream could not be performed
     */
    @Override
    public void writeTo(OutputStream os, long startPosition, long len) throws IOException {
        throw new UnsupportedOperationException("Not supported for shared memory mode, supports the type filter feature");
    }
}
