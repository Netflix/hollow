package com.netflix.hollow.core.memory.encoding;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.memory.EncodedByteBuffer;
import com.netflix.hollow.core.memory.SegmentedByteArray;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests parity between on-heap and shared-memory consumer mode by reading test data from on-heap arrays vs. off-heap buffers
 */
public class OnHeapArrayVsOffHeapBufferAcceptanceTest {

    private static final String SCRATCH_DIR = System.getProperty("java.io.tmpdir");
    private static final int TEST_SINGLE_BUFFER_CAPACITY_BYTES =  16;

    @Test
    public void testParityBetweenFixedLengthDataModes() throws IOException {
        // Add some padding bytes at the beginning of file so that longs are written at unaligned locations

        int numLongsWritten = 14;

        // adding padding writes the test longs at a non-aligned byte
        for (int padding = 0; padding < Long.BYTES; padding ++) {
            // write a File of TEST_SINGLE_BUFFER_CAPACITY_BYTES*4 size, assuming TEST_SINGLE_BUFFER_CAPACITY_BYTES is 32
            File testFile = writeTestFileUnaligned("/fixed_length_data_modes", padding);
            testFile.deleteOnExit();

            readUsingFixedLengthDataModes(testFile, padding, numLongsWritten);
        }
    }

    private void readUsingFixedLengthDataModes(File testFile, int padding, int numLongsWritten) throws IOException {
        // test read parity between FixedLengthElementArray and EncodedLongBuffer for-
        //      aligned long,
        //      unaligned long,
        //      aligned and at spine boundary long,
        //      unaligned and at spine boundary long,
        //      partly out of bounds long but queried bits are within bounds
        //      partly out of bounds long and queried bits are out of bounds
        //      out of bounds long

        HollowBlobInput hbi1 = HollowBlobInput.serial(new FileInputStream(testFile));
        hbi1.skipBytes(padding + 16);        // skip past the first 16 bytes of test data written to file
        FixedLengthElementArray testLongArray = FixedLengthElementArray.newFrom(hbi1, WastefulRecycler.DEFAULT_INSTANCE, numLongsWritten);

        HollowBlobInput hbi2 = HollowBlobInput.randomAccess(testFile, TEST_SINGLE_BUFFER_CAPACITY_BYTES);
        hbi2.skipBytes(padding  + 16);       // skip past the first 16 bytes of test data written to file
        EncodedLongBuffer testLongBuffer = EncodedLongBuffer.newFrom(hbi2, numLongsWritten);

        // read each values starting at each bit index
        for (int i = 0; i< (numLongsWritten - 1) * Long.BYTES * 8; i ++) {

            // for bit length 1 to 60
            for (int j = 1; j < 61; j ++) {
                assertEquals(testLongArray.getElementValue(i, j), testLongBuffer.getElementValue(i, j));
            }

            // for bit length 1 to 64
            for (int j = 1; j <= 64; j ++) {
                assertEquals(testLongArray.getLargeElementValue(i, j), testLongBuffer.getLargeElementValue(i, j));
            }
        }

        // partly out of bounds long but queried bits are within bounds
        //
        // get a 15-bit element that is in the last 15 bits of the buffer, but it would enforce an 8-byte long read
        // starting at the last 2 bytes in buffer but extending past the end of the buffer
        assertEquals(testLongArray.getElementValue(numLongsWritten * Long.BYTES * 8 - 2 * 8, 16),
                     testLongBuffer.getElementValue(numLongsWritten * Long.BYTES * 8 - 2 * 8, 16));

        // partly out of bounds long and queried bits are out of bounds
        // get a 16-bit element that is in the last 15 bits of the buffer
        try {
            testLongBuffer.getElementValue(numLongsWritten * Long.BYTES * 8 - 2 * 8, 17);
            Assert.fail();
        } catch (IllegalStateException e) {
            // this is expected
        } catch (Exception e) {
            Assert.fail();
        }

        // out of bounds long
        try {
            testLongBuffer.getElementValue(numLongsWritten * Long.BYTES * 8, 60);
            Assert.fail();
        } catch (IllegalStateException e) {
            // this is expected
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testParityBetweenVariableLengthDataModes() throws IOException {
        // Add some padding bytes at the beginning of file so that longs are written at unaligned locations

        // adding padding writes the test longs at a non-aligned byte
        for (int padding = 0; padding < Long.BYTES; padding ++) {
            File testFile = writeTestFileUnaligned("/variable_length_data_modes", padding);
            testFile.deleteOnExit();

            readUsingVariableLengthDataModes(testFile, padding);
        }
    }

    private void readUsingVariableLengthDataModes(File testFile, int padding) throws IOException {
        // test read parity between SegmentedByteArray and EncodedByteBuffer for-
        //      aligned byte,
        //      unaligned byte,
        //      out of bounds byte

        SegmentedByteArray testByteArray = new SegmentedByteArray(WastefulRecycler.DEFAULT_INSTANCE);
        testByteArray.loadFrom(HollowBlobInput.serial(new FileInputStream(testFile)), testFile.length());

        EncodedByteBuffer testByteBuffer = new EncodedByteBuffer(null);
        testByteBuffer.loadFrom(HollowBlobInput.randomAccess(testFile, TEST_SINGLE_BUFFER_CAPACITY_BYTES), testFile.length());

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

        // uncomment to test BlobByteBufferTest::getByte()
        // try {
        //     testByteBuffer.get(testFile.length());
        //     Assert.fail();
        // } catch (IllegalStateException e) {
        //     // this is expected
        // } catch (Exception e) {
        //     Assert.fail();
        // }
    }

    // write a File of TEST_SINGLE_BUFFER_CAPACITY_BYTES*4 size, assuming TEST_SINGLE_BUFFER_CAPACITY_BYTES is 32
    private File writeTestFileUnaligned(String filename, int padding) throws IOException {
        File f = new File(Paths.get(SCRATCH_DIR).toString() + filename + ".test");
        DataOutputStream out = new DataOutputStream(new FileOutputStream(f));

        for (int i=0; i<padding; i++) {
            out.writeByte((byte) 0xff);
        }

        byte[] leadingBytes = new byte[] {0, 1, 0, 1, 0, 1, 0, 1};  // bytes 0-7
        for (int i=0; i<leadingBytes.length; i++) {
            out.writeByte(leadingBytes[i]);
        }

        out.writeUTF("abcdef");

        long[] values = {
                123456789000L, 234567891000L,   // bytes 16-31
                345678912000L, 456789123000L,   // bytes 32-47
                567891234000L, 678912345000L,   // bytes 48-63
                789123456000L, 891234567000L,   // bytes 64-79
                912345678000L, 123456789000L,   // bytes 80-95
                234567891000L, 345678912000L,   // bytes 96-111
                Long.MAX_VALUE, Long.MAX_VALUE,   // bytes 112-127
        };

        for (int i=0; i<values.length; i++) {
            out.writeLong(values[i]);
        }

        out.flush();
        out.close();
        f.deleteOnExit();
        return f;
    }
}
