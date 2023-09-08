package com.netflix.hollow.core.memory.encoding;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.UUID;
import org.junit.Test;

public class BlobByteBufferTest {

    @Test
    public void writeThenRead() throws IOException {
        int padBytes = 8;
        int singleBufferCapacity = 1024;

        File targetFile = new File("test-BlobByteBuffer-" + System.currentTimeMillis() + "-" + UUID.randomUUID());
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

        assertEquals(1, buf.getReferenceCount().get());

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

        buf.unmapBlob();
        assertEquals(0, buf.getReferenceCount().get());
    }

    @Test
    public void testReferenceCounting() throws IOException {
        File targetFile = new File("test-BlobByteBuffer-" + System.currentTimeMillis() + "-" + UUID.randomUUID());
        targetFile.deleteOnExit();
        int singleBufferCapacity = 64;
        RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
        raf.setLength(14 * Long.BYTES);
        FileChannel channel = raf.getChannel();
        BlobByteBuffer buf = BlobByteBuffer.mmapBlob(channel, singleBufferCapacity);
        raf.close();

        assertEquals(1, buf.getReferenceCount().get());

        BlobByteBuffer dupBuf = buf.duplicate();
        assertEquals(2, buf.getReferenceCount().get());

        // can unmap in same order as init
        buf.unmapBlob();
        assertEquals(1, buf.getReferenceCount().get());

        dupBuf.unmapBlob();
        assertEquals(0, buf.getReferenceCount().get());
    }
}
