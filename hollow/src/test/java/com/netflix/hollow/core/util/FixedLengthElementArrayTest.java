/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.core.util;

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

public class FixedLengthElementArrayTest {

    @Test
    public void testSetAndGet() {
        int testValue = 53215;
        int numBitsPerElement = 17;
        long bitMask = (1L << numBitsPerElement) - 1;

        FixedLengthElementArray arr = new FixedLengthElementArray(WastefulRecycler.SMALL_ARRAY_RECYCLER, 17000000);

        for(int i=0;i<1000000;i++) {
            arr.setElementValue(i*numBitsPerElement, numBitsPerElement, testValue);
        }

        for(int j=0;j<100;j++) {
            for(int i=0;i<1000000;i++) {
                if(testValue != arr.getElementValue(i*numBitsPerElement, numBitsPerElement, bitMask))
                    Assert.fail();
            }
        }
    }

    @Test
    public void testGetEmpty() {
        FixedLengthElementArray arr = new FixedLengthElementArray(
                WastefulRecycler.SMALL_ARRAY_RECYCLER, 17000);
        Assert.assertEquals(0, arr.getElementValue(0, 4));
    }

    @Test
    public void testSetAndGetLargeValues() {
        long testValue = 1913684435138312210L;
        int numBitsPerElement = 61;

        FixedLengthElementArray arr = new FixedLengthElementArray(WastefulRecycler.SMALL_ARRAY_RECYCLER, 610000);

        for(int i=0;i<10000;i++) {
            arr.setElementValue(i*numBitsPerElement, numBitsPerElement, testValue);
        }

        for(int j=0;j<100;j++) {
            for(int i=0;i<10000;i++) {
                if(testValue != arr.getLargeElementValue(i*numBitsPerElement, numBitsPerElement))
                    Assert.fail();
            }
        }
    }

    @Test
    public void testCopyBitRange() {
        for(int iteration = 0;iteration < 100;iteration++) {
            if(iteration % 1024 == 1023)
                System.out.println(iteration);

            Random rand = new Random();

            int totalBitsInArray = rand.nextInt(6400000);
            int totalBitsInCopyRange = rand.nextInt(totalBitsInArray);
            int copyFromRangeStartBit = rand.nextInt(totalBitsInArray - totalBitsInCopyRange);
            int copyToRangeStartBit = rand.nextInt(100000);

            FixedLengthElementArray source = new FixedLengthElementArray(WastefulRecycler.SMALL_ARRAY_RECYCLER, totalBitsInArray + 64);
            FixedLengthElementArray dest = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, totalBitsInArray + copyToRangeStartBit);

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

    @Test
    public void testCopySmallBitRange() {
        FixedLengthElementArray arrFrom = new FixedLengthElementArray(WastefulRecycler.SMALL_ARRAY_RECYCLER, 64);
        FixedLengthElementArray arrTo = new FixedLengthElementArray(WastefulRecycler.SMALL_ARRAY_RECYCLER, 128);

        arrFrom.setElementValue(0, 64, -1L);

        arrTo.copyBits(arrFrom, 10, 10, 10);

        Assert.assertEquals(0, arrTo.getElementValue(0, 10));
        Assert.assertEquals(1023, arrTo.getElementValue(10, 10));
        Assert.assertEquals(0, arrTo.getLargeElementValue(20, 10));

    }

    @Test
    public void testIncrement() {
        FixedLengthElementArray arr = new FixedLengthElementArray(WastefulRecycler.SMALL_ARRAY_RECYCLER, 1000000);

        Random rand = new Random();

        long startVal = rand.nextInt(Integer.MAX_VALUE);
        int elementCount = 0;

        for(int i=0;i<1000000;i+=65) {
            arr.setElementValue(i, 60, startVal+i);
            elementCount++;
        }

        arr.incrementMany(0, 1000, 65, elementCount);

        for(int i=0;i<1000000;i+=65) {
            long val = arr.getElementValue(i, 60);
            Assert.assertEquals(startVal + i + 1000, val);
        }

        arr.incrementMany(0, -2000, 65, elementCount);

        for(int i=0;i<1000000;i+=65) {
            long val = arr.getElementValue(i, 60);
            Assert.assertEquals(startVal + i - 1000, val);
        }

    }

    @Test
    public void doesNotThrowSIGSEGV() {
        FixedLengthElementArray arr = new FixedLengthElementArray(new WastefulRecycler(2, 2), 1793);

        for(int i=0;i<2500;i++) {
            try {
                arr.setElementValue(i, 2, 3);
            } catch(ArrayIndexOutOfBoundsException acceptable) { }
        }
    }
    
    @Test
    public void testArrayIndexOutOfBoundsEdgeCase() {
        FixedLengthElementArray arr = new FixedLengthElementArray(new WastefulRecycler(2, 2), 256);
        
        arr.copyBits(arr, 256, 10, 0);
    }

    @Test
    public void convenienceMethodForNumberOfBitsRequiredForValue() {
        Assert.assertEquals(1, FixedLengthElementArray.bitsRequiredToRepresentValue(0));
        Assert.assertEquals(1, FixedLengthElementArray.bitsRequiredToRepresentValue(1));
        Assert.assertEquals(2, FixedLengthElementArray.bitsRequiredToRepresentValue(2));
        Assert.assertEquals(2, FixedLengthElementArray.bitsRequiredToRepresentValue(3));
        Assert.assertEquals(3, FixedLengthElementArray.bitsRequiredToRepresentValue(4));
        Assert.assertEquals(5, FixedLengthElementArray.bitsRequiredToRepresentValue(16));
        Assert.assertEquals(5, FixedLengthElementArray.bitsRequiredToRepresentValue(31));
        Assert.assertEquals(63, FixedLengthElementArray.bitsRequiredToRepresentValue(Long.MAX_VALUE));
    }

}
