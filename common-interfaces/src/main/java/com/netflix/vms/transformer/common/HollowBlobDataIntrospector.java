package com.netflix.vms.transformer.common;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public interface HollowBlobDataIntrospector {

    public HollowReadStateEngine getProducedData();

}
