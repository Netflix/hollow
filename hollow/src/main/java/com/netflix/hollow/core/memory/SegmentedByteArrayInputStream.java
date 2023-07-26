package com.netflix.hollow.core.memory;

import java.io.IOException;
import java.io.InputStream;

public class SegmentedByteArrayInputStream extends InputStream {
    private SegmentedByteArray segmentedByteArray;
    private long currentPosition;

    public SegmentedByteArrayInputStream(SegmentedByteArray segmentedByteArray) {
        this.segmentedByteArray = segmentedByteArray;
        this.currentPosition = 0;
    }

    @Override
    public int read() throws IOException {
        // Read a single byte
        byte b = segmentedByteArray.get(currentPosition);
        if (b == 0)
            return -1;

        currentPosition++;
        return b & 0xFF; // Convert to an unsigned int
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        // Read a portion of the byte array
        if (len <= 0)
            return 0;

        int bytesRead = 0;
        while (bytesRead < len) {
            byte currentByte = segmentedByteArray.get(currentPosition);
            if (currentByte == 0)
                break; // End of stream reached

            b[off + bytesRead] = currentByte;
            currentPosition++;
            bytesRead++;
        }

        return bytesRead > 0 ? bytesRead : -1;
    }

    @Override
    public void close() throws IOException {
        // not required
    }
}

