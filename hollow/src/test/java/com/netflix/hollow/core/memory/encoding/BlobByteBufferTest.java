package com.netflix.hollow.core.memory.encoding;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class BlobByteBufferTest {

    @Test
    public void copiesAcrossMappedBufferBoundaries() throws Exception {
        byte[] data = new byte[100];
        for(int i = 0; i < data.length; i++)
            data[i] = (byte)(i * 31);

        BlobByteBuffer buffer = map(data);
        byte[] destination = new byte[84];
        buffer.copyTo(10, destination, 2, 80);

        byte[] expected = new byte[84];
        System.arraycopy(data, 10, expected, 2, 80);
        assertArrayEquals(expected, destination);
    }

    @Test
    public void copiesEveryRangeWithoutChangingPosition() throws Exception {
        byte[] data = new byte[100];
        for(int i = 0; i < data.length; i++)
            data[i] = (byte)(i * 31);
        BlobByteBuffer buffer = map(data).position(7);

        for(int start = 0; start <= data.length; start++) {
            for(int length = 0; length <= data.length - start; length++) {
                byte[] destination = new byte[length + 4];
                Arrays.fill(destination, (byte)-1);
                byte[] expected = destination.clone();
                System.arraycopy(data, start, expected, 2, length);

                buffer.copyTo(start, destination, 2, length);

                assertArrayEquals(expected, destination);
                assertEquals(7, buffer.position());
            }
        }
    }

    @Test
    public void rejectsInvalidRangesBeforeCopying() throws Exception {
        BlobByteBuffer buffer = map(new byte[100]);
        assertCopyOutOfBounds(buffer, -1, 0, 1);
        assertCopyOutOfBounds(buffer, 0, 0, -1);
        assertCopyOutOfBounds(buffer, 99, 0, 2);
        assertCopyOutOfBounds(buffer, 101, 0, 0);
        assertCopyOutOfBounds(buffer, Long.MAX_VALUE, 0, 1);
        assertCopyOutOfBounds(buffer, 0, -1, 1);
        assertCopyOutOfBounds(buffer, 0, 9, 2);
        assertCopyOutOfBounds(buffer, 0, 11, 0);
        assertCopyOutOfBounds(buffer, 0, Integer.MAX_VALUE, 1);
        assertCopyOutOfBounds(buffer, 0, 0, Integer.MAX_VALUE);
    }

    @Test
    public void copiesConcurrentlyFromSharedMappedBuffers() throws Exception {
        byte[] data = new byte[100];
        for(int i = 0; i < data.length; i++)
            data[i] = (byte)(i * 31);
        BlobByteBuffer buffer = map(data);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            List<Future<?>> copies = new ArrayList<>();
            for(int thread = 0; thread < 4; thread++) {
                final int offset = thread;
                copies.add(executor.submit(() -> {
                    BlobByteBuffer view = buffer.duplicate().position(offset);
                    for(int i = 0; i < 1000; i++) {
                        int start = (i + offset) % data.length;
                        byte[] destination = new byte[data.length - start];
                        view.copyTo(start, destination, 0, destination.length);
                        assertArrayEquals(Arrays.copyOfRange(data, start, data.length), destination);
                        assertEquals(offset, view.position());
                    }
                }));
            }
            for(Future<?> copy : copies)
                copy.get(10, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }
    }

    private static void assertCopyOutOfBounds(BlobByteBuffer buffer, long start, int destPos, int length) {
        byte[] destination = new byte[10];
        Arrays.fill(destination, (byte)-1);
        byte[] expected = destination.clone();
        try {
            buffer.copyTo(start, destination, destPos, length);
            fail("Expected IndexOutOfBoundsException");
        } catch(IndexOutOfBoundsException expectedException) {
            assertArrayEquals(expected, destination);
        }
    }

    private static BlobByteBuffer map(byte[] data) throws Exception {
        File file = File.createTempFile("blob-byte-buffer", ".bin");
        file.deleteOnExit();
        try(FileOutputStream out = new FileOutputStream(file)) {
            out.write(data);
        }
        try(FileInputStream in = new FileInputStream(file);
            FileChannel channel = in.getChannel()) {
            return BlobByteBuffer.mmapBlob(channel, 16);
        }
    }
}
