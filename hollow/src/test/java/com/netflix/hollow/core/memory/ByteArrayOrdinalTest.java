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

    static ByteDataBuffer createBuffer(String s) {
        return write(new ByteDataBuffer(), s);
    }

    static ByteDataBuffer write(ByteDataBuffer bdb, String s) {
        for (byte b : s.getBytes()) {
            bdb.write(b);
        }
        return bdb;
    }
}
