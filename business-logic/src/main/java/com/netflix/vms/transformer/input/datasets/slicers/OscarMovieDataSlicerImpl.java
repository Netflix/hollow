package com.netflix.vms.transformer.input.datasets.slicers;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.slice.InputDataSlicer;
import com.netflix.vms.transformer.input.api.gen.oscar.OscarAPI;

public class OscarMovieDataSlicerImpl extends DataSlicer implements InputDataSlicer {

    public OscarMovieDataSlicerImpl(int... specificNodeIdsToInclude) {
        super(specificNodeIdsToInclude);
    }

    @Override
    public HollowWriteStateEngine sliceInputBlob(HollowReadStateEngine stateEngine) {

        clearOrdinalsToInclude();

        final OscarAPI inputAPI = new OscarAPI(stateEngine);

        findIncludedOrdinals(stateEngine, "Movie", (ordinal)->
                Integer.valueOf((int) inputAPI.getMovie(ordinal).getMovieId()));

        return populateFilteredBlob(stateEngine);
    }
}
