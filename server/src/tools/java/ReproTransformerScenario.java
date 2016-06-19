import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.util.StateEngineRoundTripper;
import com.netflix.vms.generated.notemplate.CompleteVideoHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.transformer.VMSTransformerWriteStateEngine;
import org.junit.Test;


public class ReproTransformerScenario {
    
    private static final String LOCAL_BLOB_STORE = "/space/local-input-blob-store";
    
    @Test
    public void repro() throws Throwable {
        TransformerScenario scenario = new TransformerScenario(LOCAL_BLOB_STORE, "boson", 20160617115752422L, 1466299730197L, 70216224);
        
        VMSTransformerWriteStateEngine transformedStateEngine = scenario.repro();
        
        HollowReadStateEngine finalStateEngine = StateEngineRoundTripper.roundTripSnapshot(transformedStateEngine);
        
        VMSRawHollowAPI finalAPI = new VMSRawHollowAPI(finalStateEngine);
        
        CompleteVideoHollow completeVideoHollow = finalAPI.getCompleteVideoHollow(0);
        
        System.out.println(completeVideoHollow._getId()._getValue());
    }
    
}
