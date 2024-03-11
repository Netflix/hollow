package com.netflix.hollow.core.memory;

import java.util.concurrent.atomic.AtomicLongArray;

public class AtomicBitSet {
    private final AtomicLongArray locks;

    public AtomicBitSet(long bits) {
        assert bits>>>6 <= Integer.MAX_VALUE;
        locks = new AtomicLongArray((int)(bits>>>6));
    }

    public void lock(long index) {
        assert index >>> 6 <= locks.length();

        int whichLong = (int) (index >>> 6);
        int whichBit = (int) (index & 0x3f);

        while(true) {
            long curr = locks.get(whichLong);
            if ((curr & (1L << whichBit)) != 0)
                continue;
            long newC = curr | (1L << whichBit);
            if(locks.compareAndSet(whichLong, curr, newC))
                break;
        }
    }

    public boolean isLocked(long index) {
        assert index >>> 6 <= locks.length();

        int whichLong = (int) (index >>> 6);
        int whichBit = (int) (index & 0x3f);
        return (locks.get(whichLong) & (1L<<whichBit))!=0;
    }

    public void unlock(long index) {
        assert index >>> 6 <= locks.length();

        int whichLong = (int) (index >>> 6);
        int whichBit = (int) (index & 0x3f);

        while(true) {
            long curr = locks.get(whichLong);
            assert (curr & (1L << whichBit)) != 0;
            long newC = curr & ~(1L << whichBit);
            if(locks.compareAndSet(whichLong, curr, newC))
                break;
        }
    }
}
