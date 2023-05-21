package com.netflix.hollow.core.memory.encoding;

import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;

public class EncodedLongBufferTest {

    @Test
    public void writeThenRead() throws IOException {
        int singleBufferCapacity = 1024 * 1024;
        File targetFile = new File("test-BlobByteBuffer-" + System.currentTimeMillis());
        targetFile.deleteOnExit();
        RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
        raf.setLength(17000000 << 3);
        raf.close();
        HollowBlobInput hbi = HollowBlobInput.randomAccess(targetFile, singleBufferCapacity);
        EncodedLongBuffer buf = EncodedLongBuffer.newFrom(hbi, 17000000 >> 6);
        int testValue = 53215;
        int numBitsPerElement = 17;
        long bitMask = (1L << numBitsPerElement) - 1;

        for(int i=0;i<1000000;i++) {
            buf.setElementValue(i*numBitsPerElement, numBitsPerElement, testValue);
        }

        for(int j=0;j<100;j++) {
            for(int i=0;i<1000000;i++) {
                if(testValue != buf.getElementValue(i*numBitsPerElement, numBitsPerElement, bitMask))
                    Assert.fail();
            }
        }
    }

    EncodedLongBuffer setupEncodedLongBuffer(int fileSizeInBytes, int singleBufferCapacity) throws IOException {
        File targetFile = new File("test-EncodedLongBuffer-" + System.currentTimeMillis() + "-" + RandomUtils.nextInt());
        targetFile.deleteOnExit();
        RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
        raf.setLength(fileSizeInBytes);
        raf.close();
        HollowBlobInput hbi = HollowBlobInput.randomAccess(targetFile, singleBufferCapacity);
        EncodedLongBuffer buf = EncodedLongBuffer.newFrom(hbi, (fileSizeInBytes >> 3));
        return buf;
    }

    @Test
    public void testCopyBitRange() throws IOException {
        int singleBufferCapacity = 1024 * 1024;
        for(int iteration = 0;iteration < 10;iteration++) { // TODO: up this count
            if(iteration % 1024 == 1023)
                System.out.println(iteration);

            Random rand = new Random();

            int totalBitsInArray = rand.nextInt(6400000);
            int totalBitsInCopyRange = rand.nextInt(totalBitsInArray);
            int copyFromRangeStartBit = rand.nextInt(totalBitsInArray - totalBitsInCopyRange);
            int copyToRangeStartBit = rand.nextInt(100000);

            EncodedLongBuffer source = setupEncodedLongBuffer((totalBitsInArray >> 3) + 1, singleBufferCapacity);
            EncodedLongBuffer dest = setupEncodedLongBuffer((totalBitsInArray + copyToRangeStartBit >> 3) + 1, singleBufferCapacity);

            int numLongs = (totalBitsInArray >>> 6);

            for(int i=0;i<=numLongs;i++) {
                source.set(i, rand.nextLong());
            }

            dest.copyBits(source, copyFromRangeStartBit, copyToRangeStartBit, totalBitsInCopyRange);

            /// compare the copy range.
            int compareBitStart = copyFromRangeStartBit;
            int copyToRangeOffset = copyToRangeStartBit - copyFromRangeStartBit;
            int numBitsLeftToCompare = totalBitsInCopyRange;

            while(numBitsLeftToCompare > 0) {
                int bitsToCompare = numBitsLeftToCompare > 56 ? 56 : numBitsLeftToCompare;
                long fromLong = source.getElementValue(compareBitStart, bitsToCompare);
                long toLong = dest.getElementValue(compareBitStart + copyToRangeOffset, bitsToCompare);

                if(fromLong != toLong)
                    Assert.fail();

                numBitsLeftToCompare -= bitsToCompare;
                compareBitStart += bitsToCompare;
            }
        }
    }

// TODO:    @Test
// TODO:    public void testCopySmallBitRange() {
// TODO:        FixedLengthElementArray arrFrom = new FixedLengthElementArray(WastefulRecycler.SMALL_ARRAY_RECYCLER, 64);
// TODO:        FixedLengthElementArray arrTo = new FixedLengthElementArray(WastefulRecycler.SMALL_ARRAY_RECYCLER, 128);
// TODO:
// TODO:        arrFrom.setElementValue(0, 64, -1L);
// TODO:
// TODO:        arrTo.copyBits(arrFrom, 10, 10, 10);
// TODO:
// TODO:        Assert.assertEquals(0, arrTo.getElementValue(0, 10));
// TODO:        Assert.assertEquals(1023, arrTo.getElementValue(10, 10));
// TODO:        Assert.assertEquals(0, arrTo.getLargeElementValue(20, 10));
// TODO:
// TODO:    }

    @Test
    public void testIncrement() throws IOException {
        int singleBufferCapacity = 1024;
        int numBits = 1000000;
        EncodedLongBuffer buf = setupEncodedLongBuffer((numBits >> 3) + 1, singleBufferCapacity);

        Random rand = new Random();

        long startVal = rand.nextInt(Integer.MAX_VALUE);
        int elementCount = 0;

        for(int i=0;i<1000000-64;i+=65) {
            buf.setElementValue(i, 60, startVal+i);
            elementCount++;
        }

        buf.incrementMany(0, 1000, 65, elementCount);

        for(int i=0;i<1000000-64;i+=65) {
            long val = buf.getElementValue(i, 60);
            Assert.assertEquals(startVal + i + 1000, val);
        }

        buf.incrementMany(0, -2000, 65, elementCount);

        for(int i=0;i<1000000-64;i+=65) {
            long val = buf.getElementValue(i, 60);
            Assert.assertEquals(startVal + i - 1000, val);
        }

    }
}
