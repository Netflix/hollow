package com.netflix.vms.transformer.input.datasets.slicers;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.slice.InputDataSlicer;

public class NoOpDataSlicerImpl extends DataSlicer implements InputDataSlicer {

    public NoOpDataSlicerImpl(int numberOfRandomTopNodesToInclude, int... specificTopNodeIdsToInclude) {
        super(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude);
    }

    @Override
    public HollowWriteStateEngine sliceInputBlob(HollowReadStateEngine stateEngine) {

        return new HollowWriteStateEngine();
    }
}
