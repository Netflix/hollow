package com.netflix.vmsserver;

import org.junit.Test;

/// NOTE:  This has a dependency on videometadata-common (for LZ4VMSInputStream)
/// NOTE:  This has a dependency on vms-hollow-generated-notemplate (for output blob API)

public class FilterToSmallDataSubset {

    private static final String WORKING_DIR = "/space/transformer-data";

    private static final int TARGET_NUMBER_OF_TOPNODES = 1000;
    private static final String ORIGINAL_OUTPUT_BLOB_LOCATION = WORKING_DIR + "/pinned-blobs/berlin-snapshot";
    private static final String ORIGINAL_INPUT_BLOB_LOCATION = WORKING_DIR + "/pinned-blobs/input-snapshot";

    private static final String FILTERED_OUTPUT_BLOB_LOCATION = WORKING_DIR + "/pinned-subsets/control-output";
    private static final String FILTERED_INPUT_BLOB_LOCATION = WORKING_DIR + "/pinned-subsets/filtered-input";


    @Test
    public void doFilter() throws Exception {
        DataSlicer slicer = new DataSlicer(
                ORIGINAL_INPUT_BLOB_LOCATION,
                ORIGINAL_OUTPUT_BLOB_LOCATION,
                FILTERED_INPUT_BLOB_LOCATION,
                FILTERED_OUTPUT_BLOB_LOCATION,
                TARGET_NUMBER_OF_TOPNODES,
                80074321, 80006146 // These two topnodes are a strange case: the same episodes are included in two different show hierarchies for different countries.
                , 80115503, 80101868, 80038305, 80038400, 80038302, 80020110, 70258608, 70178217, 70293581 // These video not available in output data so use isFilterBasedOnInputData=true
                , 70309714, 70016909, 80042504
                );

        boolean isFilterBasedOnInputData = true;
        slicer.setIsDebugMode(true);
        slicer.slice(isFilterBasedOnInputData);
    }
}