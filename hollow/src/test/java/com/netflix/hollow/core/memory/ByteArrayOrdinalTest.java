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
