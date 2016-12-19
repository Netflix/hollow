package com.netflix.vms.transformer.testutil.repro;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.util.StateEngineRoundTripper;
import com.netflix.vms.generated.notemplate.CompleteVideoHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.transformer.VMSTransformerWriteStateEngine;
import org.junit.Test;


public class ReproTransformerScenario {

    private static final String LOCAL_BLOB_STORE = "/Users/ksatiya/local-input-blob-store";

    @Test
    public void repro() throws Throwable {
        TransformerScenario scenario = new TransformerScenario(LOCAL_BLOB_STORE, "vmsdev_imgSchedule", 20161215210836204L, 80152826, 80152831);

        VMSTransformerWriteStateEngine transformedStateEngine = scenario.repro();

        HollowReadStateEngine clientStateEngine = StateEngineRoundTripper.roundTripSnapshot(transformedStateEngine);
        VMSRawHollowAPI finalAPI = new VMSRawHollowAPI(clientStateEngine);
        CompleteVideoHollow completeVideoHollow = finalAPI.getCompleteVideoHollow(0);
        System.out.println(completeVideoHollow._getId()._getValue());
    }

}
