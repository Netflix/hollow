package com.netflix.vms.transformer.input.datasets.slicers;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.slice.InputDataSlicer;
import com.netflix.vms.transformer.input.api.gen.topn.TopNAPI;

public class TopNDataSlicerImpl extends DataSlicer implements InputDataSlicer {

    public TopNDataSlicerImpl(int... specificTopNodeIdsToInclude) {
        super(specificTopNodeIdsToInclude);
    }

    @Override
    public HollowWriteStateEngine sliceInputBlob(HollowReadStateEngine stateEngine) {

        clearOrdinalsToInclude();

        final TopNAPI inputAPI = new TopNAPI(stateEngine);

        findIncludedOrdinals(stateEngine, "TopN", (ordinal)->
                Integer.valueOf((int) inputAPI.getTopN(ordinal).getVideoId()));

        return populateFilteredBlob(stateEngine);
    }
}