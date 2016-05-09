package com.netflix.vms.transformer.common;

import com.netflix.hollow.read.engine.HollowReadStateEngine;

public interface HollowBlobDataIntrospector {

    public HollowReadStateEngine getProducedData();

}
