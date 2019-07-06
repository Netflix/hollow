package com.netflix.vms.transformer.common.slice;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowWriteStateEngine;

public interface InputDataSlicer {

    HollowWriteStateEngine sliceInputBlob(HollowReadStateEngine inputStateEngine);
}
