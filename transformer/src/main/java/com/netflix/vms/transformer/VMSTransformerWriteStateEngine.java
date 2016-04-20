package com.netflix.vms.transformer;

import com.netflix.vms.transformer.util.VMSTransformerHashCodeFinder;

import com.netflix.hollow.write.HollowWriteStateEngine;

public class VMSTransformerWriteStateEngine extends HollowWriteStateEngine {

    public VMSTransformerWriteStateEngine() {
        super(new VMSTransformerHashCodeFinder());
    }


}