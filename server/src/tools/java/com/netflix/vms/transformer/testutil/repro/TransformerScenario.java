package com.netflix.vms.transformer.testutil.repro;
import com.netflix.vms.transformer.testutil.slice.DataSlicer;

import com.netflix.hollow.read.engine.HollowBlobReader;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.util.HashCodes;
import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.SimpleTransformer;
import com.netflix.vms.transformer.SimpleTransformerContext;
import com.netflix.vms.transformer.VMSTransformerWriteStateEngine;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.input.VMSInputDataClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;

public class TransformerScenario {
    
    private final String localBlobStore;
    private final String converterVip;
    private final long inputDataVersion;
    private final long processTimestamp;
    private final int[] topNodesToProcess;

    public TransformerScenario(String localBlobStore, String converterVip, long inputDataVersion, long processTimestamp, int... topNodesToProcess) {
        this.localBlobStore = localBlobStore;
        this.converterVip = converterVip;
        this.inputDataVersion = inputDataVersion;
        this.processTimestamp = processTimestamp;
        this.topNodesToProcess = topNodesToProcess;
    }
    
    public VMSTransformerWriteStateEngine repro() throws Throwable {
        int scenarioHashCode = createTransformerScenarioHashCode();
        
        File scenarioInputFile = new File(localBlobStore, "scenario-" + Integer.toHexString(scenarioHashCode));
        
        HollowReadStateEngine inputStateEngineSlice;
        
        if(scenarioInputFile.exists()) {
            inputStateEngineSlice = readStateEngineSlice(scenarioInputFile);
        } else {
            inputStateEngineSlice = createStateEngineSlice(scenarioInputFile);
        }
        
        VMSHollowInputAPI api = new VMSHollowInputAPI(inputStateEngineSlice);
        VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();
        new SimpleTransformer(api, outputStateEngine, new SimpleTransformerContext()).transform();
        
        return outputStateEngine;
    }

    private HollowReadStateEngine createStateEngineSlice(File scenarioInputFile) throws IOException {
        VMSInputDataClient client = new VMSInputDataClient(VMSInputDataClient.PROD_PROXY_URL, localBlobStore, converterVip);
        
        client.triggerRefreshTo(inputDataVersion);
        
        DataSlicer slicer = new DataSlicer(0, topNodesToProcess);
        
        HollowWriteStateEngine slicedStateEngine = slicer.sliceInputBlob(client.getStateEngine());
        
        writeStateEngineSlice(slicedStateEngine, scenarioInputFile);

        return readStateEngineSlice(scenarioInputFile);
    }
    
    private int createTransformerScenarioHashCode() {
        int hashCode = HashCodes.hashLong(inputDataVersion);
        hashCode ^= HashCodes.hashLong(processTimestamp);
        for(int i : topNodesToProcess)
            hashCode ^= HashCodes.hashInt(i);
        return hashCode;
    }
    
    private HollowReadStateEngine readStateEngineSlice(File sliceFile) throws IOException {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(stateEngine);
        try(LZ4BlockInputStream is = new LZ4BlockInputStream(new FileInputStream(sliceFile))) {
            reader.readSnapshot(is);
        }
        return stateEngine;
    }
    
    private void writeStateEngineSlice(HollowWriteStateEngine slicedStateEngine, File sliceFile) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(slicedStateEngine);
        try(LZ4BlockOutputStream os = new LZ4BlockOutputStream(new FileOutputStream(sliceFile))) {
            writer.writeSnapshot(os);
        }
    }

}
