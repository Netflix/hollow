package com.netflix.vms.transformer.testutil.migration;

import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.SimpleTransformer;
import com.netflix.vms.transformer.SimpleTransformerContext;
import com.netflix.vms.transformer.VMSTransformerWriteStateEngine;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import net.jpountz.lz4.LZ4BlockInputStream;
import org.junit.Test;

public class ShowMeAllTheProgress {

    @Test
    public void start() throws Throwable {
        VMSHollowInputAPI api = new VMSHollowInputAPI(loadStateEngine());

        VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();

        SimpleTransformer transformer = new SimpleTransformer(api, outputStateEngine, new SimpleTransformerContext());

        transformer.transform();
/*        HollowReadStateEngine actualOutputReadStateEngine = roundTripOutputStateEngine(outputStateEngine);
        HollowReadStateEngine expectedOutputStateEngine = loadStateEngine("/expected-output.hollow", getDiffFilter(actualOutputReadStateEngine.getSchemas()));

        ShowMeTheProgressDiffTool.startTheDiff(expectedOutputStateEngine, actualOutputReadStateEngine);
*/    }


    private HollowReadStateEngine loadStateEngine() throws IOException {
        InputStream is = new LZ4BlockInputStream(new FileInputStream("/space/transformer-data/pinned-blobs/input-snapshot"));

        HollowReadStateEngine stateEngine = new HollowReadStateEngine(WastefulRecycler.DEFAULT_INSTANCE);

        HollowBlobReader reader = new HollowBlobReader(stateEngine);

        reader.readSnapshot(is);

        return stateEngine;
    }

}
