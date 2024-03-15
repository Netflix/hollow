package com.netflix.hollow.core.memory;

import org.junit.Assert;
import org.junit.Test;

public class AtomicBitSetTest {
    @Test
    public void ABSTest() {
        int size = 504;
        AtomicBitSet abs = new AtomicBitSet(size);
        for(int i = 0; i < size; i++) {
            if(i%2==0 && i%6!=0)
                abs.lock(i);
        }

        for(int i = 0; i < size; i++) {
            boolean shouldBeLocked = i%2==0 && i%6!=0;
            Assert.assertEquals(shouldBeLocked, abs.isLocked(i));

            if(shouldBeLocked) {
                abs.unlock(i);
                Assert.assertNotEquals(shouldBeLocked, abs.isLocked(i));
            }
        }
    }
}
