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
