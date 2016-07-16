package com.netflix.vms.transformer.override;

import com.netflix.aws.file.FileStore;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.common.slice.DataSlicer;
import com.netflix.vms.transformer.input.VMSOutputDataClient;
import com.netflix.vms.transformer.util.slice.DataSlicerImpl;

import java.io.File;
import java.io.IOException;

/**
 * Generates Title Override based on Output Slice
 *
 * @author dsu
 */
public class OutputSliceTitleOverrideProcessor extends AbstractTitleOverrideProcessor {
    private static final String TYPE = "output";

    private final VMSOutputDataClient outputDataClient;

    public OutputSliceTitleOverrideProcessor(String vip, FileStore fileStore, String localBlobStore, TransformerContext ctx) {
        super(vip, localBlobStore, ctx);

        this.outputDataClient = new VMSOutputDataClient(fileStore, vip);
    }

    public OutputSliceTitleOverrideProcessor(String vip, String baseProxyURL, String localBlobStore, TransformerContext ctx) {
        super(vip, localBlobStore, ctx);

        this.outputDataClient = new VMSOutputDataClient(baseProxyURL, localBlobStore, vip);
    }

    @Override
    public HollowReadStateEngine process(long version, int topNode) throws IOException {
        File localFile = getFile(TYPE, version, topNode);
        if (!localFile.exists()) {
            long start = System.currentTimeMillis();
            outputDataClient.triggerRefreshTo(version);

            DataSlicer.SliceTask slicer = new DataSlicerImpl().getSliceTask(0, topNode);
            HollowWriteStateEngine slicedStateEngine = slicer.sliceOutputBlob(outputDataClient.getStateEngine());

            writeStateEngine(slicedStateEngine, localFile);
            ctx.getLogger().info(TransformerLogTag.OverrideTitle, "Sliced[OUTPUT] videoId={} from vip={}, version={}, duration={}", topNode, vip, version, (System.currentTimeMillis() - start));
        }

        return readStateEngine(localFile);
    }
}
