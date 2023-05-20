package com.netflix.hollow.core.memory.encoding;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import org.junit.Test;

public class BlobByteBufferTest {

    @Test
    public void writeThenRead() throws IOException {
        int leadingBytes = 8;
        int padBytes = 0;
        int singleBufferCapacity = 1024;

        File targetFile = new File("test-BlobByteBuffer-" + System.currentTimeMillis());
        targetFile.deleteOnExit();
        RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
        raf.setLength((14 * Long.BYTES) + leadingBytes + padBytes);
        FileChannel channel = raf.getChannel();
        BlobByteBuffer buf = BlobByteBuffer.mmapBlob(channel, singleBufferCapacity);

        long[] values = {
                123456789000L, 234567891000L,
                345678912000L, 456789123000L,
                567891234000L, 678912345000L,
                789123456000L, 891234567000L,
                912345678000L, 123456789000L,
                234567891000L, 345678912000L,
                Long.MAX_VALUE, Long.MAX_VALUE,
        };

        for (int i = 0; i < values.length; i ++) {
            buf.putLong(i * Long.BYTES, values[i]);
        }

        for (int i = 0; i < values.length; i ++) {
            long actual = buf.getLong(i * Long.BYTES);
            assertEquals(values[i], actual);
        }
        raf.close();

    }
}
