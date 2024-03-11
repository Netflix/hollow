package com.netflix.hollow.core.memory;

import org.junit.Assert;
import org.junit.Test;

public class AtomicBitSetTest {
    @Test
    public void ABSTest() {
        AtomicBitSet abs = new AtomicBitSet(500);
        for(int i = 0; i < 500; i++) {
            if(i%2==0)
                abs.lock(i);
        }

        for(int i = 0; i < 500; i++) {
            boolean shouldBeLocked = i%2==0;
            Assert.assertEquals(shouldBeLocked, abs.isLocked(i));

            if(shouldBeLocked) {
                abs.unlock(i);
                Assert.assertNotEquals(shouldBeLocked, abs.isLocked(i));
            }
        }
    }
    @Test
    public void ABSLargeTest() {
        long size = 5_000_000_000L;
        AtomicBitSet abs = new AtomicBitSet(size);
        for(long i = 0; i < size; i++) {
            if(i%100_000_000==0)
                System.out.println((float)i/size);
            abs.lock(i);
        }
        for(long i = 0; i < size; i++) {
            if(i%100_000_000==0)
                System.out.println((float)i/size);
            Assert.assertTrue(abs.isLocked(i));
            abs.unlock(i);
            Assert.assertFalse(abs.isLocked(i));
        }
    }
}
