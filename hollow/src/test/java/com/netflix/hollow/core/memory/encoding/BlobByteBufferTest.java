package com.netflix.hollow.core.memory.encoding;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.memory.EncodedByteBuffer;
import com.netflix.hollow.core.memory.HollowUnsafeHandle;
import com.netflix.hollow.core.memory.SegmentedByteArray;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import sun.misc.Unsafe;

public class BlobByteBufferTest {

    private static final Unsafe unsafe = HollowUnsafeHandle.getUnsafe();
    private static final String SCRATCH_DIR = System.getProperty("java.io.tmpdir");
    private static final int TEST_SINGLE_BUFFER_CAPACITY_BYTES =  32;

    @Test
    public void testParityBetweenFixedLengthDataModes() throws IOException {
        // Add some padding bytes at the beginning of file so that longs are written at unaligned locations

        int numLongsWritten = 14;
        int bitsPerLong = 60; // should be less than 61 or else result is undefined
        // TODO: Test different bitsPerLong and longs written to unaligned bits

        // adding padding writes the test longs at a non-aligned byte
        for (int padding = 0; padding < Long.BYTES; padding ++) {
            // write a File of TEST_SINGLE_BUFFER_CAPACITY_BYTES*4 size, assuming TEST_SINGLE_BUFFER_CAPACITY_BYTES is 32
            Path testFile = writeTestFileUnaligned("/fixed_length_data_modes", padding);
            testFile.toFile().deleteOnExit();

            readUsingFixedLengthDataModes(testFile, padding, numLongsWritten, bitsPerLong);
        }

    }

    @Test
    public void testParityBetweenVariableLengthDataModes() throws IOException {
        // Add some padding bytes at the beginning of file so that longs are written at unaligned locations

        // adding padding writes the test longs at a non-aligned byte
        for (int padding = 0; padding < Long.BYTES; padding ++) {
            Path testFile = writeTestFileUnaligned("/variable_length_data_modes", padding);
            testFile.toFile().deleteOnExit();

            readUsingVariableLengthDataModes(testFile, padding);
        }

        // TODO: Can write be at unaligned bit?
    }


    private void readUsingFixedLengthDataModes(Path testFile, int padding, int numLongsWritten, int bitsPerLong) throws IOException {
        // test read parity between SegmentedLongArray and EncodedByteBuffer for-
        //      aligned long,
        //      unaligned longs,
        //      aligned and at spine boundary long,
        //      unaligned and at spine boundary long,
        //      out of bounds long
        //      overlapping with out of bounds long
        //      first long (aligned)
        //      first long (unaligned)

        RandomAccessFile raf = new RandomAccessFile(testFile.toFile(), "r");
        FileChannel channel = raf.getChannel();
        BlobByteBuffer testBuffer = BlobByteBuffer.mmapBlob(channel, TEST_SINGLE_BUFFER_CAPACITY_BYTES);

        //
        // SNAP: SegmentedByteArray vs. EncodedByteBuffer
        //
        SegmentedByteArray testByteArray = new SegmentedByteArray(WastefulRecycler.DEFAULT_INSTANCE);
        testByteArray.loadFrom(HollowBlobInput.dataInputStream(new FileInputStream(testFile.toFile())), testFile.toFile().length());

        // aligned bytes - BlobByteBuffer vs. SegmentedByteArray
        assertEquals(testByteArray.get(0 + padding), testBuffer.getByte(0 + padding));
        assertEquals(testByteArray.get(8 + padding), testBuffer.getByte(8 + padding));
        assertEquals(testByteArray.get(16 + padding), testBuffer.getByte(16 + padding));
        assertEquals(testByteArray.get(23 + padding), testBuffer.getByte(23 + padding));
        assertEquals(testByteArray.get(1 + padding), testBuffer.getByte(1 + padding));

        //  unaligned bytes - BlobByteBuffer vs. SegmentedByteArray
        assertEquals(testByteArray.get(1 + padding), testBuffer.getByte(1 + padding));
        assertEquals(testByteArray.get(13 + padding), testBuffer.getByte(13 + padding));
        assertEquals(testByteArray.get(127 + padding), testBuffer.getByte(127 + padding));

        //
        // SNAP: FixedLengthElementArray vs. EncodedLongBuffer
        //
        // unaligned long - BlobByteBuffer vs. SegmentedLongArray
        HollowBlobInput hbi1 = HollowBlobInput.dataInputStream(new FileInputStream(testFile.toFile()));
        hbi1.skipBytes(padding + 16);        // skip past the first 16 bytes of test data written to file
        FixedLengthElementArray testLongArray = FixedLengthElementArray.deserializeFrom(hbi1, WastefulRecycler.DEFAULT_INSTANCE, numLongsWritten);

        HollowBlobInput hbi2 = HollowBlobInput.randomAccessFile(testFile.toFile());
        hbi2.skipBytes(padding  + 16);       // skip past the first 16 bytes of test data written to file
        EncodedLongBuffer testLongBuffer = EncodedLongBuffer.deserializeFrom(hbi2, numLongsWritten);
        for (int i=0; i<numLongsWritten; i++) {
            assertEquals(testLongArray.get(i), testLongBuffer.getElementValue(i*Long.BYTES*8, bitsPerLong));
        }
        // SNAP: TODO: This doesn't hold because of the way we're writing out the longs- inconsistent byte order

        // TODO: Test long read across spine boundary
    }

    private void readUsingVariableLengthDataModes(Path testFile, int padding) throws IOException {
        // test read parity between SegmentedByteArray and EncodedLongBuffer for-
        //      aligned byte,
        //      unaligned byte,
        //      out of bounds byte
        //      first byte

        //
        // SNAP: SegmentedByteArray vs. EncodedByteBuffer
        //
        SegmentedByteArray testByteArray = new SegmentedByteArray(WastefulRecycler.DEFAULT_INSTANCE);
        testByteArray.loadFrom(HollowBlobInput.dataInputStream(new FileInputStream(testFile.toFile())), testFile.toFile().length());

        EncodedByteBuffer testByteBuffer = new EncodedByteBuffer();
        testByteBuffer.loadFrom(HollowBlobInput.randomAccessFile(testFile.toFile()), testFile.toFile().length());

        // aligned bytes - BlobByteBuffer vs. SegmentedByteArray
        assertEquals(testByteArray.get(0 + padding), testByteBuffer.get(0 + padding));
        assertEquals(testByteArray.get(8 + padding), testByteBuffer.get(8 + padding));
        assertEquals(testByteArray.get(16 + padding), testByteBuffer.get(16 + padding));
        assertEquals(testByteArray.get(23 + padding), testByteBuffer.get(23 + padding));
        assertEquals(testByteArray.get(1 + padding), testByteBuffer.get(1 + padding));

        //  unaligned bytes - BlobByteBuffer vs. SegmentedByteArray
        assertEquals(testByteArray.get(1 + padding), testByteBuffer.get(1 + padding));
        assertEquals(testByteArray.get(13 + padding), testByteBuffer.get(13 + padding));
        assertEquals(testByteArray.get(127 + padding), testByteBuffer.get(127 + padding));
    }

    // write a File of TEST_SINGLE_BUFFER_CAPACITY_BYTES*4 size, assuming TEST_SINGLE_BUFFER_CAPACITY_BYTES is 32
    private Path writeTestFileUnaligned(String filename, int padding) throws IOException {
        File f = new File(Paths.get(SCRATCH_DIR).toString() + filename + ".test");
        DataOutputStream out = new DataOutputStream(new FileOutputStream(f));
        // Path testFile = Files.createTempFile(Paths.get(SCRATCH_DIR), filename,"test");

        for (int i=0; i<padding; i++) {
            out.writeByte((byte) 0xff);
        }

        byte[] leadingBytes = new byte[] {0, 1, 0, 1, 0, 1, 0, 1};  // bytes 0-7
        for (int i=0; i<leadingBytes.length; i++) {
            out.writeByte(leadingBytes[i]);
        }

        // byte[] utfLen = new byte[] {0, 1};   // bytes corresponding to a short of value 1
        // short utfLen = 1;
        //out.writeShort(utfLen);
        out.writeUTF("abcdef");

        long[] values = {
                123456789000L, 234567891000L,   // bytes 16-31
                345678912000L, 456789123000L,   // bytes 32-47
                567891234000L, 678912345000L,   // bytes 48-63
                789123456000L, 891234567000L,   // bytes 64-79
                912345678000L, 123456789000L,   // bytes 80-95
                234567891000L, 345678912000L,   // bytes 96-111
                456789123000L, 567891234000L,   // bytes 112-127
        };

        for (int i=0; i<values.length; i++) {
            out.writeLong(values[i]);
        }

        out.flush();
        out.close();
        f.deleteOnExit();
        return f.toPath();
    }


    //    @Test
    //    public void testMmapBlob() throws IOException {
    //
    //
    //        Path testFile = writeTestFileAligned("hollowalignedread", 0);
    //        testFile.toFile().deleteOnExit();
    //
    //        RandomAccessFile raf = new RandomAccessFile(testFile.toFile(), "r");
    //        FileChannel channel = raf.getChannel();
    //        BlobByteBuffer testBuffer = BlobByteBuffer.mmapBlob(channel, TEST_SINGLE_BUFFER_CAPACITY_BYTES);
    //
    //        byte[] testBytes = Files.readAllBytes(testFile);
    //
    //        // aligned bytes - BlobByteBuffer vs. SegmentedByteArray
    //        SegmentedByteArray testByteArray = new SegmentedByteArray(WastefulRecycler.DEFAULT_INSTANCE);
    //        testByteArray.loadFrom(HollowBlobInput.dataInputStream(new FileInputStream(testFile.toFile())), testBytes.length);
    //        assertEquals(testByteArray.get(0), testBuffer.getByte(0));
    //        assertEquals(testByteArray.get(8), testBuffer.getByte(8));
    //        assertEquals(testByteArray.get(16), testBuffer.getByte(16));
    //        assertEquals(testByteArray.get(23), testBuffer.getByte(23));
    //
    //        //  unaligned bytes - BlobByteBuffer vs. SegmentedByteArray
    //        assertEquals(testByteArray.get(1), testBuffer.getByte(1));
    //        assertEquals(testByteArray.get(13), testBuffer.getByte(13));
    //        assertEquals(testByteArray.get(127), testBuffer.getByte(127));
    //
    //        // aligned long - BlobByteBuffer vs. SegmentedLongArray
    //        SegmentedLongArray testLongArray = new SegmentedLongArray(WastefulRecycler.DEFAULT_INSTANCE, 14);
    //        testLongArray.readFrom(HollowBlobInput.dataInputStream(new FileInputStream(testFile.toFile())), WastefulRecycler.DEFAULT_INSTANCE, 14);
    //        assertEquals(testLongArray.get(2), testBuffer.getLong(16));
    //
    //        // aligned long in view buffer - BlobByteBuffer vs. SegmentedLongArray
    //        BlobByteBuffer testDuplicate = testBuffer.position(8).duplicate();    // testDuplicate is a view into testBuffer from position 4
    //        assertEquals(testLongArray.get(2), testDuplicate.getLong(16));  // indexing into view is with position in backing buffer
    //    }


    /**
     * Creates a file with specified name and writes padding bytes + 128 bytes of test data to it.
     */
    //    private Path writeTestFileAligned(String filename, int padding) throws IOException {
    //        Path testFile = Files.createTempFile(Paths.get(SCRATCH_DIR), filename,"test");
    //
    //        byte[] paddingBytes = new byte[padding];
    //        Files.write(testFile, paddingBytes);
    //
    //        byte[] leadingBytes = new byte[] {0, 1, 0, 1, 0, 1, 0, 1};  // bytes 0-7
    //        Files.write(testFile, leadingBytes, StandardOpenOption.APPEND);
    //
    //        byte[] utfLen = new byte[] {0, 1};   // bytes corresponding to a short of value 1
    //        Files.write(testFile, utfLen, StandardOpenOption.APPEND);
    //        Files.write(testFile, "abcdef".getBytes(), StandardOpenOption.APPEND);  // bytes 8-15
    //
    //        long[] values = {
    //            123456789000L, 234567891000L,   // bytes 16-31
    //            345678912000L, 456789123000L,   // bytes 32-47
    //            567891234000L, 678912345000L,   // bytes 48-63
    //            789123456000L, 891234567000L,   // bytes 64-79
    //            912345678000L, 123456789000L,   // bytes 80-95
    //            234567891000L, 345678912000L,   // bytes 96-111
    //            456789123000L, 567891234000L,   // bytes 112-127
    //        };
    //
    //        Arrays.stream(values).forEach(v -> {
    //                try {
    //                    Files.write(testFile, ByteBuffer.allocate(Long.SIZE).putLong(v).array(), StandardOpenOption.APPEND);
    //                } catch (IOException e) {
    //                    throw new RuntimeException("Could not serialize test values to test file");
    //                }
    //        });
    //
    //        return testFile;
    //    }



    // SNAP: lesson learnt: in.readLong() is not consistent with unsafe.putLong. Should try out.writeLong()
    //    private Path writeTestFileUnaligned(String filename, int padding) throws IOException {
    //        Path testFile = Files.createTempFile(Paths.get(SCRATCH_DIR), filename,"test");
    //        final int maxBytes = 128 + padding;
    //
    //        byte[] fileBytes = new byte[maxBytes + padding];
    //        for (int i=0; i<fileBytes.length; i++) {
    //            fileBytes[i] = (byte) 0xff;
    //        }
    //        int fileBytesPos = 0 + Unsafe.ARRAY_BYTE_BASE_OFFSET;
    //
    //        for (int i=0; i<padding; i++) {
    //            unsafe.putByte(fileBytes, (long) fileBytesPos, (byte) 0);
    //            fileBytesPos ++;
    //        }
    //
    //        byte[] leadingBytes = new byte[] {0, 1, 0, 1, 0, 1, 0, 1};  // bytes 0-7
    //        for (int i=0; i<leadingBytes.length; i++) {
    //            unsafe.putByte(fileBytes, fileBytesPos, leadingBytes[i]);
    //            fileBytesPos ++;
    //        }
    //
    //        // byte[] utfLen = new byte[] {0, 1};   // bytes corresponding to a short of value 1
    //        short utfLen = 1;
    //        unsafe.putShort(fileBytes, fileBytesPos, utfLen);
    //        // System.arraycopy(utfLen, 0, fileBytes, fileBytesPos, utfLen.length);
    //        fileBytesPos += Short.BYTES;
    //
    //        // SNAP: TODO: zeros in place of UTF for now
    //        for (int i=0; i<"abcdef".length(); i++) {
    //            unsafe.putByte(fileBytes, fileBytesPos, (byte) 0);
    //            fileBytesPos ++;
    //        }
    //
    //        long[] values = {
    //                123456789000L, 234567891000L,   // bytes 16-31
    //                345678912000L, 456789123000L,   // bytes 32-47
    //                567891234000L, 678912345000L,   // bytes 48-63
    //                789123456000L, 891234567000L,   // bytes 64-79
    //                912345678000L, 123456789000L,   // bytes 80-95
    //                234567891000L, 345678912000L,   // bytes 96-111
    //                456789123000L, 567891234000L,   // bytes 112-127
    //        };
    //
    //        for (int i=0; i<values.length; i++) {
    //            unsafe.putLong(fileBytes, fileBytesPos, values[i]);
    //            fileBytesPos += Long.BYTES;
    //        }
    //
    //        Files.write(testFile, fileBytes);
    //        return testFile;
    //    }
}
