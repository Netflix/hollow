package com.netflix.hollow.core.memory;

import java.util.concurrent.atomic.AtomicLongArray;

public class AtomicBitSet {
    private final AtomicLongArray locks;

    public AtomicBitSet(int bits) {
        locks = new AtomicLongArray(bits>>>3);
    }

    public void lock(int index) {
        int whichLong = index >> 6;
        int whichBit = index & 0x3f;

        while(true) {
            long curr = locks.get(whichLong);
            if ((curr & (1L << whichBit)) != 0)
                continue;
            long newC = curr | (1L << whichBit);
            if(locks.compareAndSet(whichLong, curr, newC))
                break;
        }
    }

    public boolean isLocked(int index) {
        int whichLong = index >> 6;
        int whichBit = index & 0x3f;
        return (locks.get(whichLong) & (1L<<whichBit))!=0;
    }

    public void unlock(int index) {
        int whichLong = index >> 6;
        int whichBit = index & 0x3f;

        while(true) {
            long curr = locks.get(whichLong);
            assert (curr & (1L << whichBit)) != 0;
            long newC = curr & ~(1L << whichBit);
            if(locks.compareAndSet(whichLong, curr, newC))
                break;
        }
    }
}
