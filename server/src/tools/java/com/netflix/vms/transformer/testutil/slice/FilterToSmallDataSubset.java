package com.netflix.vms.transformer.testutil.slice;

import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.slice.InputDataSlicer;
import com.netflix.vms.transformer.input.datasets.slicers.ConverterDataSlicerImpl;
import com.netflix.vms.transformer.input.datasets.slicers.TransformerOutputDataSlicer;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import net.jpountz.lz4.LZ4BlockInputStream;
import org.junit.Before;
import org.junit.Test;

/// NOTE:  This has a dependency on vmstransformer-io (for LZ4VMSInputStream)
/// NOTE:  This has a dependency on vms-hollow-generated-notemplate (for output blob API)

public class FilterToSmallDataSubset {

    private static final String WORKING_DIR = "/space/transformer-data";

    private static final String ORIGINAL_OUTPUT_BLOB_LOCATION = WORKING_DIR + "/pinned-blobs/berlin-snapshot";
    private static final String ORIGINAL_INPUT_BLOB_LOCATION = WORKING_DIR + "/pinned-blobs/input-snapshot";

    private static final String FILTERED_OUTPUT_BLOB_LOCATION = WORKING_DIR + "/pinned-subsets/control-output";
    private static final String FILTERED_INPUT_BLOB_LOCATION = WORKING_DIR + "/pinned-subsets/filtered-input";
    
    private static final boolean IS_FILTER_BASED_ON_INPUT_DATA = true;
    
    private static final int TARGET_NUMBER_OF_TOPNODES = 1000;

    private InputDataSlicer inputSlicer;
    private TransformerOutputDataSlicer outputSlicer;

    @Before
    public void setUp() {
        inputSlicer = new ConverterDataSlicerImpl(80097047,70305883);

        outputSlicer = new TransformerOutputDataSlicer(TARGET_NUMBER_OF_TOPNODES, 80097047,70305883);
    }

    @Test
    public void doFilter() throws Exception {
        if (IS_FILTER_BASED_ON_INPUT_DATA) {
            sliceInputBlob();
            sliceOutputBlob();
        } else {
            sliceOutputBlob();
            sliceInputBlob();
        }
    }

    private void sliceInputBlob() throws IOException {
        HollowReadStateEngine inputStateEngine = readInputStateEngine();
        HollowWriteStateEngine slicedInputStateEngine = inputSlicer.sliceInputBlob(inputStateEngine);
        inputStateEngine = null;
        writeBlob(FILTERED_INPUT_BLOB_LOCATION, slicedInputStateEngine);
    }

    private void sliceOutputBlob() throws IOException {
        HollowReadStateEngine outputStateEngine = readOutputStateEngine();
        HollowWriteStateEngine slicedOutputStateEngine = outputSlicer.sliceOutputBlob(outputStateEngine);
        outputStateEngine = null;
        writeBlob(FILTERED_OUTPUT_BLOB_LOCATION, slicedOutputStateEngine);
    }

    private HollowReadStateEngine readInputStateEngine() throws IOException {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(stateEngine);
        try (LZ4BlockInputStream is = new LZ4BlockInputStream(new FileInputStream(ORIGINAL_INPUT_BLOB_LOCATION))) {
            reader.readSnapshot(is);
            return stateEngine;
        }
    }

    private final HollowReadStateEngine readOutputStateEngine() throws IOException {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(stateEngine);
        try (LZ4BlockInputStream is = new LZ4BlockInputStream(new FileInputStream(ORIGINAL_OUTPUT_BLOB_LOCATION))) {
            reader.readSnapshot(is);
            return stateEngine;
        }
    }

    private static void writeBlob(String filename, HollowWriteStateEngine stateEngine) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(filename))){
            writer.writeSnapshot(os);
        }
    }


    
}