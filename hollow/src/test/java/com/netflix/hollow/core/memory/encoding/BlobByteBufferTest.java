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
        int padBytes = 8;
        int singleBufferCapacity = 1024;

        File targetFile = new File("test-BlobByteBuffer-" + System.currentTimeMillis());
        targetFile.deleteOnExit();
        RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
        raf.setLength((14 * Long.BYTES) + padBytes);
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

        for (int offset = 0; offset < padBytes; offset ++) {
            for (int i = 0; i < values.length; i ++) {
                buf.putLong(offset + i * Long.BYTES, values[i]);
            }

            for (int i = 0; i < values.length; i ++) {
                long actual = buf.getLong(offset + i * Long.BYTES);
                assertEquals(values[i], actual);
            }
        }
        raf.close();

    }
}
