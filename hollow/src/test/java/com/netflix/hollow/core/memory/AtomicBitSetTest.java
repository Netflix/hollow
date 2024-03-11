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
}
