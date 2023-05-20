package com.netflix.hollow.core.memory.encoding;

import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.junit.Assert;
import org.junit.Test;

public class EncodedLongBufferTest {

    @Test
    public void writeThenRead() throws IOException {
        int singleBufferCapacity = 1024 * 1024;
        long[] values = {
                123456789000L, 234567891000L,
                345678912000L, 456789123000L,
                567891234000L, 678912345000L,
                789123456000L, 891234567000L,
                912345678000L, 123456789000L,
                234567891000L, 345678912000L,
                Long.MAX_VALUE, Long.MAX_VALUE,
        };

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
}
