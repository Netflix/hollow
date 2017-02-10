package com.netflix.vms.transformer.override;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.netflixspecific.blob.store.NetflixS3BlobRetriever;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.common.slice.DataSlicer;
import com.netflix.vms.transformer.input.VMSOutputDataClient;
import com.netflix.vms.transformer.util.slice.DataSlicerImpl;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Generates Title Override based on Output Slice
 *
 * @author dsu
 */
public class OutputSlicePinTitleProcessor extends AbstractPinTitleProcessor {
    private static final String TYPE = "output";

    private final VMSOutputDataClient outputDataClient;

    public OutputSlicePinTitleProcessor(String vip, NetflixS3BlobRetriever retriever, String localBlobStore, TransformerContext ctx) {
        super(vip, localBlobStore, ctx);

        this.outputDataClient = new VMSOutputDataClient(retriever);
    }

    public OutputSlicePinTitleProcessor(String vip, String baseProxyURL, String localBlobStore, TransformerContext ctx) {
        super(vip, localBlobStore, ctx);

        this.outputDataClient = new VMSOutputDataClient(baseProxyURL, localBlobStore, vip);
    }

    @Override
    public HollowReadStateEngine process(long version, int... topNodes) throws IOException {
        File localFile = getFile(TYPE, version, topNodes);
        if (!localFile.exists()) {
            long start = System.currentTimeMillis();
            outputDataClient.triggerRefreshTo(version);

            Set<String> excludedTypes = new HashSet<>();
            for (OutputTypeConfig type : OutputTypeConfig.FASTLANE_EXCLUDED_TYPES) {
                excludedTypes.add(type.getType());
            }

            DataSlicer.SliceTask slicer = new DataSlicerImpl().getSliceTask(excludedTypes, false, 0, topNodes);
            HollowWriteStateEngine slicedStateEngine = slicer.sliceOutputBlob(outputDataClient.getStateEngine());

            String blobID = PinTitleHelper.createBlobID("o", version, topNodes);
            writeStateEngine(slicedStateEngine, localFile, blobID, version, topNodes);
            ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles, "Sliced[OUTPUT] videoId={} from vip={}, version={}, duration={}", Arrays.toString(topNodes), vip, version, (System.currentTimeMillis() - start));
        }

        return readStateEngine(localFile);
    }
}
