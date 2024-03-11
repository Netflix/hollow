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
package com.netflix.hollow.core.memory;

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import org.junit.Assert;
import org.junit.Test;

public class ByteArrayOrdinalTest {

    @Test
    public void testResize() {
        ByteArrayOrdinalMap m = new ByteArrayOrdinalMap();

        int[] ordinals = new int[179];
        for (int i = 0; i < ordinals.length; i++) {
            ordinals[i] = m.getOrAssignOrdinal(createBuffer("TEST" + i));
        }

        m.resize(4096);

        int[] newOrdinals = new int[ordinals.length];
        for (int i = 0; i < ordinals.length; i++) {
            newOrdinals[i] = m.get(createBuffer("TEST" + i));
        }

        Assert.assertArrayEquals(ordinals, newOrdinals);
    }

    void writeNumToBDA(int num, ByteDataArray bda) {
        bda.reset();
        byte b1 = (byte) ((num >> 24) & 0xFF);
        byte b2 = (byte) ((num >> 16) & 0xFF);
        byte b3 = (byte) ((num >> 8) & 0xFF);
        byte b4 = (byte) (num & 0xFF);
        bda.write(b1);
        bda.write(b2);
        bda.write(b3);
        bda.write(b4);
    }

    int numFromBDA(ByteDataArray bda) {
        int ans = 0;
        ans |= bda.get(0) << 24;
        ans |= bda.get(1) << 16;
        ans |= bda.get(2) << 8;
        ans |= bda.get(3);
        return ans;
    }

    @Test
    public void testAtomic() throws InterruptedException {
        ByteArrayOrdinalMap newMap = new ByteArrayOrdinalMap();
        SmallByteArrayOrdinalMap oldMap = new SmallByteArrayOrdinalMap();

        Thread t1 = new Thread(() -> {
            ByteDataArray bda = new ByteDataArray();
            for(int i = 0; i < 10_000; i++) {
                writeNumToBDA(i, bda);
                oldMap.getOrAssignOrdinal(bda);
                newMap.getOrAssignOrdinal(bda);
            }
        });
        Thread t2 = new Thread(() -> {
            ByteDataArray bda = new ByteDataArray();
            for(int i = 0; i < 1_000; i++) {
                writeNumToBDA(i, bda);
                oldMap.getOrAssignOrdinal(bda);
                newMap.getOrAssignOrdinal(bda);
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        ByteDataArray bda = new ByteDataArray();
        for(int i = 0; i < 10_000; i++) {
            writeNumToBDA(i, bda);
            Assert.assertEquals(oldMap.getOrAssignOrdinal(bda), newMap.getOrAssignOrdinal(bda));
        }
    }

    public void testOneBil() {
        ByteDataArray arr = new ByteDataArray();
        int size = 560_000_000;
        ByteArrayOrdinalMap baom = new ByteArrayOrdinalMap(size*2);
        for(int i = 0; i < size; i++) {
            if(i%1_000_000==0)
                System.out.println((float)i/(size*2));
            byte first = (byte) ((i>>>24) & 0xFF);
            byte second = (byte) (i>>>16 & 0xFF);
            byte third = (byte) (i>>>8 & 0xFF);
            byte fourth = (byte) (i & 0xFF);
            arr.write(first);
            arr.write(second);
            arr.write(third);
            arr.write(fourth);
            baom.getOrAssignOrdinal(arr);

            arr.reset();
        }

        for(int i = 0; i < size; i++) {
            if(i%1_000_000==0)
                System.out.println((float)(i+size)/(size*2));
            byte first = (byte) ((i>>>24) & 0xFF);
            byte second = (byte) (i>>>16 & 0xFF);
            byte third = (byte) (i>>>8 & 0xFF);
            byte fourth = (byte) (i & 0xFF);
            arr.write(first);
            arr.write(second);
            arr.write(third);
            arr.write(fourth);
            Assert.assertEquals(i, baom.getOrAssignOrdinal(arr));
            arr.reset();
        }


    }



    @Test
    public void testResizeWhenEmpty() {
        ByteArrayOrdinalMap m = new ByteArrayOrdinalMap();
        m.resize(4096);

        int[] ordinals = new int[179];
        for (int i = 0; i < ordinals.length; i++) {
            ordinals[i] = m.getOrAssignOrdinal(createBuffer("TEST" + i));
        }

        m.resize(16384);

        int[] newOrdinals = new int[ordinals.length];
        for (int i = 0; i < ordinals.length; i++) {
            newOrdinals[i] = m.get(createBuffer("TEST" + i));
        }

        Assert.assertArrayEquals(ordinals, newOrdinals);
    }

    static ByteDataArray createBuffer(String s) {
        return write(new ByteDataArray(), s);
    }

    static ByteDataArray write(ByteDataArray bdb, String s) {
        for (byte b : s.getBytes()) {
            bdb.write(b);
        }
        return bdb;
    }
}
