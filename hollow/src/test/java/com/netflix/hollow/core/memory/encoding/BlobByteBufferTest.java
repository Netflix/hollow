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
import java.util.Random;
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

    @Test
    public void readsLongsAtEveryAlignmentAcrossMappingsAndFinalPadding() throws Exception {
        Random random = new Random(42);
        for(int segmentSize : new int[] { 1, 2, 4, 8, 16, 32, 64, 128 }) {
            for(int prefix = 0; prefix < 10; prefix++) {
                // Fixed-length data is serialized as whole big-endian longs.
                for(int length : new int[] { 8, 16, 24, 32, 40, 64, 72 }) {
                    byte[] data = new byte[prefix + length];
                    random.nextBytes(data);
                    BlobByteBuffer buffer = map(data, segmentSize).position(prefix).duplicate();
                    for(int index = 0; index < length; index++) {
                        assertEquals("segment=" + segmentSize + " prefix=" + prefix
                                        + " length=" + length + " index=" + index,
                                expectedLong(data, prefix, index), buffer.getLong(prefix + index));
                        assertEquals(prefix, buffer.position());
                    }
                }
            }
        }
    }

    @Test
    public void readsLongsWithPartialFinalPhysicalWord() throws Exception {
        Random random = new Random(42);
        for(int segmentSize : new int[] { 1, 2, 4, 8, 16, 32, 64 }) {
            for(int prefix = 0; prefix < 10; prefix++) {
                for(int length = 1; length < 24; length++) {
                    byte[] data = new byte[prefix + length];
                    random.nextBytes(data);
                    BlobByteBuffer buffer = map(data, segmentSize).position(prefix);
                    // Unaligned reads within a truncated final word can exceed the permitted padding.
                    int lastWordStart = (length - 1) & ~(Long.BYTES - 1);
                    for(int index = 0; index <= lastWordStart; index++) {
                        assertEquals("segment=" + segmentSize + " prefix=" + prefix
                                        + " length=" + length + " index=" + index,
                                expectedLong(data, prefix, index), buffer.getLong(prefix + index));
                        assertEquals(prefix, buffer.position());
                    }
                }
            }
        }
    }

    @Test
    public void readsLongsConcurrentlyFromSharedMappedBuffers() throws Exception {
        byte[] data = new byte[96];
        new Random(42).nextBytes(data);
        BlobByteBuffer buffer = map(data);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            List<Future<?>> reads = new ArrayList<>();
            for(int thread = 0; thread < 4; thread++) {
                final int prefix = thread;
                reads.add(executor.submit(() -> {
                    BlobByteBuffer view = buffer.duplicate().position(prefix);
                    for(int i = 0; i < 1000; i++) {
                        int index = (i + prefix) % (data.length - prefix - 16);
                        assertEquals(expectedLong(data, prefix, index), view.getLong(prefix + index));
                        assertEquals(prefix, view.position());
                    }
                }));
            }
            for(Future<?> read : reads)
                read.get(10, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }
    }

    private static long expectedLong(byte[] data, int prefix, int index) {
        long value = 0;
        for(int i = 0; i < Long.BYTES; i++) {
            // Reverse byte order within each serialized word, independently of mapping boundaries.
            int physicalIndex = prefix + ((index + i) ^ 7);
            if(physicalIndex < data.length)
                value |= (data[physicalIndex] & 0xffL) << (i * 8);
        }
        return value;
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
        return map(data, 16);
    }

    private static BlobByteBuffer map(byte[] data, int segmentSize) throws Exception {
        File file = File.createTempFile("blob-byte-buffer", ".bin");
        file.deleteOnExit();
        try(FileOutputStream out = new FileOutputStream(file)) {
            out.write(data);
        }
        try(FileInputStream in = new FileInputStream(file);
            FileChannel channel = in.getChannel()) {
            return BlobByteBuffer.mmapBlob(channel, segmentSize);
        }
    }
}
