package com.netflix.vms.transformer.override;

import com.netflix.aws.file.FileStore;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.SimpleTransformer;
import com.netflix.vms.transformer.VMSTransformerWriteStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.common.slice.DataSlicer;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.input.VMSInputDataClient;
import com.netflix.vms.transformer.util.slice.DataSlicerImpl;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Generates Title Override based on Input Slice
 *
 * @author dsu
 */
public class InputSlicePinTitleProcessor extends AbstractPinTitleProcessor {

    private final VMSInputDataClient inputDataClient;

    public InputSlicePinTitleProcessor(String vip, FileStore fileStore, String localBlobStore, TransformerContext ctx) {
        super(vip, localBlobStore, ctx);

        this.pinTitleFileStore = fileStore;
        this.inputDataClient = new VMSInputDataClient(fileStore, vip);
    }

    public InputSlicePinTitleProcessor(String vip, String baseProxyURL, String localBlobStore, TransformerContext ctx) {
        super(vip, localBlobStore, ctx);

        this.pinTitleProxyURL = baseProxyURL;
        this.inputDataClient = new VMSInputDataClient(baseProxyURL, localBlobStore, vip);
    }

    @Override
    public HollowReadStateEngine process(long inputDataVersion, int... topNodes) throws Throwable {
        File localFile = fetchOutputSlice(inputDataVersion, topNodes);
        return readStateEngine(localFile);
    }

    public File fetchOutputSlice(long inputDataVersion, int... topNodes) throws Exception, Throwable {
        File localFile = getFile("output", inputDataVersion, topNodes);
        if (!localFile.exists()) {
            HollowReadStateEngine inputStateEngineSlice = fetchInputStateEngineSlice(inputDataVersion, topNodes);

            VMSHollowInputAPI api = new VMSHollowInputAPI(inputStateEngineSlice);
            VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();
            new SimpleTransformer(api, outputStateEngine, ctx).transform();

            String blobID = PinTitleHelper.createBlobID("i", inputDataVersion, topNodes);
            writeStateEngine(outputStateEngine, localFile, blobID, inputDataVersion, topNodes);
        }
        return localFile;
    }

    public HollowReadStateEngine fetchInputStateEngineSlice(Long inputDataVersion, int... topNodes) throws Exception {
        File slicedFile = fetchInputSlice(inputDataVersion, topNodes);
        return readStateEngine(slicedFile);
    }

    public File fetchInputSlice(Long inputDataVersion, int... topNodes) throws Exception, IOException {
        File slicedFile = getFile("input", inputDataVersion, topNodes);
        if (!slicedFile.exists()) {
            long start = System.currentTimeMillis();
            HollowReadStateEngine inputStateEngine = readInputData(inputDataVersion);

            DataSlicer.SliceTask slicer = new DataSlicerImpl().getSliceTask(0, topNodes);
            HollowWriteStateEngine slicedStateEngine = slicer.sliceInputBlob(inputStateEngine);

            String blobID = PinTitleHelper.createBlobID("sliced_input", inputDataVersion, topNodes);
            writeStateEngine(slicedStateEngine, slicedFile, blobID, inputDataVersion, topNodes);
            ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles, "Sliced[INPUT] videoId={} from vip={}, version={}, duration={}", Arrays.toString(topNodes), vip, inputDataVersion, (System.currentTimeMillis() - start));
        }
        return slicedFile;
    }

    private HollowReadStateEngine readInputData(long inputDataVersion) throws IOException {
        inputDataClient.triggerRefreshTo(inputDataVersion);
        return inputDataClient.getStateEngine();
    }
}
