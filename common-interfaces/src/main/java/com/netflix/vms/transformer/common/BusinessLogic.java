package com.netflix.vms.transformer.common;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.slice.OutputDataSlicer;
import java.util.List;

public interface BusinessLogic {

    void transform(CycleInputs cycleInputs, HollowWriteStateEngine outputStateEngine, TransformerContext ctx)
            throws Exception;   // SNAP: Should this just be the SimpleTransformer signature?

    // @@@ List may be modified
    List<HollowSchema> getSchema();

    // @@@ This can be removed if we can get rid of HollowObjectHashCodeFinder use
    HollowObjectHashCodeFinder getHashCodeFinder();

    long getTargetMaxTypeShardSize();

    OutputDataSlicer getDataSlicer();   // SNAP: what about input slicing?

    String[] getStreamHollowTypes();
}
