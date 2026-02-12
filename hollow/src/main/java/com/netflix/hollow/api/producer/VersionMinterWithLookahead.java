package com.netflix.hollow.api.producer;

import com.netflix.hollow.api.producer.HollowProducer.VersionMinter;

public class VersionMinterWithLookahead implements VersionMinter {

    protected final VersionMinter delegateTo;
    protected long peekValue = -1L;

    public VersionMinterWithLookahead(VersionMinter delegateTo) {
        this.delegateTo = delegateTo;
    }

    public long peek() {
        if(peekValue == -1L) {
            peekValue = delegateTo.mint();
        }
        return peekValue;
    }

    public boolean wasPeeked() {
        return peekValue != -1L;
    }

    @Override
    public long mint() {
        long peekedValue = peek();
        peekValue = -1L;
        return peekedValue;
    }

}