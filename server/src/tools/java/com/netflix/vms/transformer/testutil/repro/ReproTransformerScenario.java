package com.netflix.vms.transformer.testutil.repro;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.vms.generated.notemplate.CompleteVideoHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.transformer.VMSTransformerWriteStateEngine;
import org.junit.Test;


public class ReproTransformerScenario {

    private static final String LOCAL_BLOB_STORE = "/Users/ksatiya/local-input-blob-store";

    @Test
    public void run() throws Throwable {
        TransformerScenario scenario = new TransformerScenario(LOCAL_BLOB_STORE, "berlin", 20161224093314741L, 80152826, 80152831);

        VMSTransformerWriteStateEngine transformedStateEngine = scenario.repro();

        HollowReadStateEngine clientStateEngine = StateEngineRoundTripper.roundTripSnapshot(transformedStateEngine);
        VMSRawHollowAPI finalAPI = new VMSRawHollowAPI(clientStateEngine);
        CompleteVideoHollow completeVideoHollow = finalAPI.getCompleteVideoHollow(0);
        System.out.println(completeVideoHollow._getId()._getValue());
    }

}
