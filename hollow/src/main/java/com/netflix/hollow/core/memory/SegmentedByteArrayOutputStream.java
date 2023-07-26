package com.netflix.hollow.core.memory;

import java.io.IOException;
import java.io.OutputStream;

public class SegmentedByteArrayOutputStream extends OutputStream {
    private SegmentedByteArray segmentedByteArray;
    private long currentPosition;

    public SegmentedByteArrayOutputStream(SegmentedByteArray segmentedByteArray) {
        this.segmentedByteArray = segmentedByteArray;
        this.currentPosition = 0;
    }

    @Override
    public void write(int b) throws IOException {
        // Single byte write operation
        segmentedByteArray.set(currentPosition, (byte) b);
        currentPosition++;
    }

    @Override
    public void write(byte[] b) throws IOException {
        //load from HollowBlobInput
        segmentedByteArray.
    }

    @Override
    public void flush() throws IOException {
        // Optionally implement flushing behavior if needed
    }

    @Override
    public void close() throws IOException {
        // Optionally implement closing behavior if needed
    }

    public SegmentedByteArray getSegmentedByteArray() {
        return this.segmentedByteArray;
    }
}
