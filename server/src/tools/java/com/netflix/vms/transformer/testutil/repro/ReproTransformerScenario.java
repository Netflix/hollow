package com.netflix.vms.transformer.testutil.repro;
import com.google.inject.Inject;
import com.netflix.cinder.consumer.CinderConsumerBuilder;
import com.netflix.cinder.lifecycle.CinderConsumerModule;
import com.netflix.governator.guice.test.ModulesForTesting;
import com.netflix.governator.guice.test.junit4.GovernatorJunit4ClassRunner;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.runtime.lifecycle.RuntimeCoreModule;
import com.netflix.vms.generated.notemplate.CompleteVideoHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.transformer.VMSTransformerWriteStateEngine;
import java.util.function.Supplier;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(GovernatorJunit4ClassRunner.class)
@ModulesForTesting({CinderConsumerModule.class, RuntimeCoreModule.class})
public class ReproTransformerScenario {

    private static final String LOCAL_BLOB_STORE = "/space/local-input-blob-store";

    @Inject
    private Supplier<CinderConsumerBuilder> cinderConsumerBuilder;

    @Test
    public void run() throws Throwable {
        TransformerScenario scenario = new TransformerScenario(cinderConsumerBuilder, "vmsdev_sunjeetsn", LOCAL_BLOB_STORE, 20190628191123181L, 80152826, 80152831);

        VMSTransformerWriteStateEngine transformedStateEngine = scenario.repro();

        HollowReadStateEngine clientStateEngine = StateEngineRoundTripper.roundTripSnapshot(transformedStateEngine);
        VMSRawHollowAPI finalAPI = new VMSRawHollowAPI(clientStateEngine);
        CompleteVideoHollow completeVideoHollow = finalAPI.getCompleteVideoHollow(0);
        System.out.println(completeVideoHollow._getId()._getValue());
    }

}
