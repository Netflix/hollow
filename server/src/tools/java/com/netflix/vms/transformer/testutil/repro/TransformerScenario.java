package com.netflix.vms.transformer.testutil.repro;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.SimpleTransformer;
import com.netflix.vms.transformer.SimpleTransformerContext;
import com.netflix.vms.transformer.VMSTransformerWriteStateEngine;
import com.netflix.vms.transformer.common.slice.DataSlicer;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.http.HttpHelper;
import com.netflix.vms.transformer.input.VMSInputDataClient;
import com.netflix.vms.transformer.util.HollowBlobKeybaseBuilder;
import com.netflix.vms.transformer.util.slice.DataSlicerImpl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;

public class TransformerScenario {

    private final String localBlobStore;
    private final String transformerVip;
    private final long outputDataVersion;
    private final int[] topNodesToProcess;

    private String proxyURL = VMSInputDataClient.PROD_PROXY_URL;

    private String converterVip;
    private long inputDataVersion;
    private long processTimestamp;

    public TransformerScenario(String localBlobStore, String transformerVip, long outputDataVersion, int... topNodesToProcess) {
        this.localBlobStore = localBlobStore;
        this.transformerVip = transformerVip;
        this.outputDataVersion = outputDataVersion;
        this.topNodesToProcess = topNodesToProcess;
    }

    public TransformerScenario(String proxyURL, String localBlobStore, String transformerVip, long outputDataVersion, int... topNodesToProcess) {
        this(localBlobStore, transformerVip, outputDataVersion, topNodesToProcess);
        this.proxyURL = proxyURL;
    }

    public VMSTransformerWriteStateEngine repro() throws Throwable {
        File scenarioInputFile = scenarioInputDataFile();

        HollowReadStateEngine inputStateEngineSlice;

        if(scenarioInputFile.exists()) {
            inputStateEngineSlice = readStateEngineSlice(scenarioInputFile);
        } else {
            inputStateEngineSlice = createStateEngineSlice(scenarioInputFile);
        }

        VMSHollowInputAPI api = new VMSHollowInputAPI(inputStateEngineSlice);
        VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();
        SimpleTransformerContext ctx = new SimpleTransformerContext();
        ctx.setNowMillis(processTimestamp);
        new SimpleTransformer(api, null, outputStateEngine, ctx).transform();

        return outputStateEngine;
    }

    private HollowReadStateEngine createStateEngineSlice(File scenarioInputFile) throws IOException {
        determineInputParameters();

        VMSInputDataClient client = new VMSInputDataClient(proxyURL, localBlobStore, converterVip);

        client.triggerRefreshTo(inputDataVersion);

        DataSlicer.SliceTask slicer = new DataSlicerImpl().getSliceTask(0, topNodesToProcess);

        HollowWriteStateEngine slicedStateEngine = slicer.sliceInputBlob(client.getStateEngine());

        slicedStateEngine.addHeaderTag("publishCycleDataTS", String.valueOf(processTimestamp));

        writeStateEngineSlice(slicedStateEngine, scenarioInputFile);

        return readStateEngineSlice(scenarioInputFile);
    }

    private void determineInputParameters() throws IOException {
        HollowBlobKeybaseBuilder keybaseBuilder = new HollowBlobKeybaseBuilder(transformerVip);

        Properties dataProps = new Properties();
        InputStream is = HttpHelper.getInputStream(proxyURL + "/filestore-attribute?keybase=" + keybaseBuilder.getReverseDeltaKeybase() + "&version=" + outputDataVersion);
        dataProps.load(is);

        this.converterVip = dataProps.getProperty("converterVip");
        this.inputDataVersion = Long.parseLong(dataProps.getProperty("inputVersion"));
        this.processTimestamp = Long.parseLong(dataProps.getProperty("publishCycleDataTS"));
    }

    private File scenarioInputDataFile() {
        return new File(localBlobStore, "scenario-" + transformerVip + "-" + outputDataVersion + "-" + Integer.toHexString(inputVideosHashCode()));
    }

    private int inputVideosHashCode() {
        int hashCode = 0;
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

        this.processTimestamp = Long.parseLong(stateEngine.getHeaderTag("publishCycleDataTS"));

        return stateEngine;
    }

    private void writeStateEngineSlice(HollowWriteStateEngine slicedStateEngine, File sliceFile) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(slicedStateEngine);
        try(LZ4BlockOutputStream os = new LZ4BlockOutputStream(new FileOutputStream(sliceFile))) {
            writer.writeSnapshot(os);
        }
    }

}
