package com.netflix.vms.transformer;

import java.io.FileInputStream;

import java.io.BufferedInputStream;
import com.netflix.hollow.filter.HollowFilterConfig;
import com.netflix.hollow.read.engine.HollowBlobReader;
import com.netflix.hollow.util.memory.WastefulRecycler;
import java.io.IOException;
import java.io.InputStream;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import org.junit.Test;

public class ShowMeAllTheProgress {

    @Test
    public void start() throws Exception {
        VMSHollowVideoInputAPI api = new VMSHollowVideoInputAPI(loadStateEngine());

        VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();

        SimpleTransformer transformer = new SimpleTransformer(api, outputStateEngine);

        transformer.transform();
/*        HollowReadStateEngine actualOutputReadStateEngine = roundTripOutputStateEngine(outputStateEngine);
        HollowReadStateEngine expectedOutputStateEngine = loadStateEngine("/expected-output.hollow", getDiffFilter(actualOutputReadStateEngine.getSchemas()));

        ShowMeTheProgressDiffTool.startTheDiff(expectedOutputStateEngine, actualOutputReadStateEngine);
*/    }


    private HollowReadStateEngine loadStateEngine() throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream("/space/transformer-data/pinned-blobs/vms.input-snapshot-20160307205744289"));

        HollowReadStateEngine stateEngine = new HollowReadStateEngine(WastefulRecycler.DEFAULT_INSTANCE);

        HollowBlobReader reader = new HollowBlobReader(stateEngine);

        reader.readSnapshot(is);

        return stateEngine;
    }

}
