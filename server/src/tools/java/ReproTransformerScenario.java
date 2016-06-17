import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.util.StateEngineRoundTripper;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.vms.generated.notemplate.CompleteVideoHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.transformer.SimpleTransformer;
import com.netflix.vms.transformer.SimpleTransformerContext;
import com.netflix.vms.transformer.VMSTransformerWriteStateEngine;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.input.VMSInputDataClient;
import com.netflix.vms.transformer.testutil.DataSlicer;
import org.junit.Test;


public class ReproTransformerScenario {
    
    private static final String LOCAL_BLOB_STORE = "/space/local-input-blob-store";
    
    @Test
    public void repro() throws Throwable {
        VMSInputDataClient client = new VMSInputDataClient(VMSInputDataClient.PROD_PROXY_URL, LOCAL_BLOB_STORE, "boson");
        
        client.triggerRefreshTo(20160617115752422L);
        
        DataSlicer slicer = new DataSlicer(0, 70216224);
        
        HollowWriteStateEngine inputWriteStateEngine = slicer.sliceInputBlob(client.getStateEngine());
        
        HollowReadStateEngine inputStateEngineSlice = StateEngineRoundTripper.roundTripSnapshot(inputWriteStateEngine);
        
        VMSHollowInputAPI api = new VMSHollowInputAPI(inputStateEngineSlice);
        
        VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();
        
        new SimpleTransformer(api, outputStateEngine, new SimpleTransformerContext()).transform();
        
        HollowReadStateEngine finalStateEngine = StateEngineRoundTripper.roundTripSnapshot(outputStateEngine);
        
        VMSRawHollowAPI finalAPI = new VMSRawHollowAPI(finalStateEngine);
        
        CompleteVideoHollow completeVideoHollow = finalAPI.getCompleteVideoHollow(0);
        
        System.out.println(completeVideoHollow._getId()._getValue());
    }

}
