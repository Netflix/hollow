package com.netflix.hollow.core.memory;

import java.util.BitSet;
import org.junit.Assert;
import org.junit.Test;

public class ThreadSafeBitSetTest {

    @Test
    public void testBasicAPIs() {
        ThreadSafeBitSet tsbSet = new ThreadSafeBitSet();
        int[] ordinals = new int[] { 1, 5, 10 };

        // init
        for (int ordinal : ordinals) {
            tsbSet.set(ordinal);
        }

        // validate content
        for (int ordinal : ordinals) {
            Assert.assertTrue(tsbSet.get(ordinal));
        }
        Assert.assertEquals(ordinals.length, tsbSet.cardinality());

        tsbSet.clear(ordinals[0]);
        Assert.assertFalse(tsbSet.get(0));
        Assert.assertEquals(ordinals.length - 1, tsbSet.cardinality());
    }

    @Test
    public void testToBitSet() {
        BitSet bSet = new BitSet();
        ThreadSafeBitSet tsbSet = new ThreadSafeBitSet();
        int[] ordinals = new int[] { 1, 5, 10 };

        // init
        for (int ordinal : ordinals) {
            bSet.set(ordinal);
            tsbSet.set(ordinal);
        }

        // validate content
        for (int ordinal : ordinals) {
            Assert.assertEquals(bSet.get(ordinal), tsbSet.get(ordinal));
        }
        Assert.assertEquals(bSet.cardinality(), tsbSet.cardinality());

        // compare toBitSet
        BitSet bSet2 = tsbSet.toBitSet();
        Assert.assertEquals(bSet, bSet2);

        // compare toString
        Assert.assertEquals(bSet.toString(), bSet.toString());
    }

    @Test
    public void testOrAll() {
        BitSet bSet = new BitSet();

        ThreadSafeBitSet[] tsbSets = new ThreadSafeBitSet[3];
        int[] ordinals = new int[] { 1, 5, 10 };

        // init
        int i=0;
        for (int ordinal : ordinals) {
            tsbSets[i] = new ThreadSafeBitSet();
            tsbSets[i].set(ordinal);
            i++;

            bSet.set(ordinal);
        }

        // validate content
        ThreadSafeBitSet result = ThreadSafeBitSet.orAll(tsbSets);
        Assert.assertEquals(bSet.cardinality(), result.cardinality());
        Assert.assertEquals(bSet, result.toBitSet());
    }
    
    @Test
    public void testAndNot() {
        ThreadSafeBitSet tsbSet1 = new ThreadSafeBitSet();
        ThreadSafeBitSet tsbSet2 = new ThreadSafeBitSet();
        for(int i=0; i<3; i++) {
            tsbSet1.set(i);
            tsbSet2.set(i * 2);
        }

        // determine andNot
        BitSet andNot_bSet = new BitSet();
        ThreadSafeBitSet andNot_tsbSet = new ThreadSafeBitSet();

        int ordinal = tsbSet1.nextSetBit(0);
        while (ordinal!=-1) {
            if (!tsbSet2.get(ordinal)) {
                andNot_bSet.set(ordinal);
                andNot_tsbSet.set(ordinal);
            }
            ordinal = tsbSet1.nextSetBit(ordinal+1);
        }
        Assert.assertFalse(tsbSet1.equals(tsbSet2));
        Assert.assertNotEquals(tsbSet1, tsbSet2);
        Assert.assertNotEquals(tsbSet1.toBitSet(), tsbSet2.toBitSet());

        // validate content
        ThreadSafeBitSet result = tsbSet1.andNot(tsbSet2);
        Assert.assertEquals(andNot_tsbSet.cardinality(), result.cardinality());
        Assert.assertTrue(andNot_tsbSet.equals(result));
        Assert.assertEquals(andNot_tsbSet, result);
        Assert.assertEquals(andNot_bSet, result.toBitSet());
    }
}
