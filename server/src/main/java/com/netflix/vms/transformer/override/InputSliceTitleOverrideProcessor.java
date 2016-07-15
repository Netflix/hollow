package com.netflix.vms.transformer.override;

import com.netflix.aws.file.FileStore;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.SimpleTransformer;
import com.netflix.vms.transformer.VMSTransformerWriteStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.input.VMSInputDataClient;
import com.netflix.vms.transformer.testutil.slice.DataSlicer;

import java.io.File;
import java.io.IOException;

/**
 * Generates Title Override based on Input Slice
 *
 * @author dsu
 */
public class InputSliceTitleOverrideProcessor extends AbstractTitleOverrideProcessor {

    private final VMSInputDataClient inputDataClient;

    public InputSliceTitleOverrideProcessor(String vip, FileStore fileStore, String localBlobStore, TransformerContext ctx) {
        super(vip, localBlobStore, ctx);

        this.inputDataClient = new VMSInputDataClient(fileStore, vip);
    }

    public InputSliceTitleOverrideProcessor(String vip, String baseProxyURL, String localBlobStore, TransformerContext ctx) {
        super(vip, localBlobStore, ctx);

        this.inputDataClient = new VMSInputDataClient(baseProxyURL, localBlobStore, vip);
    }

    @Override
    public HollowReadStateEngine process(long inputDataVersion, int topNode) throws Throwable {
        File localFile = getFile("output", inputDataVersion, topNode);
        if (!localFile.exists()) {
            File slicedInputFile = getFile("input", inputDataVersion, topNode);
            HollowReadStateEngine inputStateEngineSlice = fetchStateEngineSlice(slicedInputFile, inputDataVersion, topNode);

            VMSHollowInputAPI api = new VMSHollowInputAPI(inputStateEngineSlice);
            VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();
            new SimpleTransformer(api, outputStateEngine, ctx).transform();
            writeStateEngine(outputStateEngine, localFile);
        }

        return readStateEngine(localFile);
    }

    private HollowReadStateEngine fetchStateEngineSlice(File slicedFile, Long inputDataVersion, int topNode) throws IOException {
        if (!slicedFile.exists()) {
            long start = System.currentTimeMillis();
            HollowReadStateEngine inputStateEngine = readInputData(inputDataVersion);

            DataSlicer slicer = new DataSlicer(0, topNode);
            HollowWriteStateEngine slicedStateEngine = slicer.sliceInputBlob(inputStateEngine);

            writeStateEngine(slicedStateEngine, slicedFile);
            ctx.getLogger().info(TransformerLogTag.OverrideBlob, "Sliced[INPUT] videoId={} from vip={}, version={}, duration={}", topNode, vip, inputDataVersion, (System.currentTimeMillis() - start));
        }

        return readStateEngine(slicedFile);
    }

    private HollowReadStateEngine readInputData(long inputDataVersion) throws IOException {
        inputDataClient.triggerRefreshTo(inputDataVersion);
        return inputDataClient.getStateEngine();
    }
}
