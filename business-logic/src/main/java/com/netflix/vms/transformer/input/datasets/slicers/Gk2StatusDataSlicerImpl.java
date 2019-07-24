package com.netflix.vms.transformer.input.datasets.slicers;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.slice.InputDataSlicer;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.Gk2StatusAPI;

public class Gk2StatusDataSlicerImpl extends DataSlicer implements InputDataSlicer {

    public Gk2StatusDataSlicerImpl(int... specificNodeIdsToInclude) {
        super(specificNodeIdsToInclude);
    }

    @Override
    public HollowWriteStateEngine sliceInputBlob(HollowReadStateEngine stateEngine) {

        clearOrdinalsToInclude();

        final Gk2StatusAPI inputAPI = new Gk2StatusAPI(stateEngine);

        findIncludedOrdinals(stateEngine, "Status", ordinal ->
                Integer.valueOf((int) inputAPI.getStatus(ordinal).getMovieId()));

        return populateFilteredBlob(stateEngine);
    }
}
