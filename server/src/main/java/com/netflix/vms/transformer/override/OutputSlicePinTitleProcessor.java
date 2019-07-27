package com.netflix.vms.transformer.override;

import com.netflix.cinder.consumer.CinderConsumerBuilder;
import com.netflix.gutenberg.s3access.S3Direct;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.BusinessLogic;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.consumer.VMSOutputDataConsumer;
import com.netflix.vms.transformer.input.datasets.slicers.TransformerOutputDataSlicer;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Generates Title Override based on Output Slice
 */
public class OutputSlicePinTitleProcessor extends AbstractPinTitleProcessor {
    private final HollowConsumer outputDataConsumer;

    public OutputSlicePinTitleProcessor(Supplier<CinderConsumerBuilder> builder, S3Direct s3Direct,
            String namespace, TransformerContext ctx, BusinessLogic businessLogic) {
        super(builder, s3Direct, namespace, null, null, ctx, businessLogic);
        this.outputDataConsumer = VMSOutputDataConsumer.getNewConsumer(builder, namespace);
    }

    public OutputSlicePinTitleProcessor(Supplier<CinderConsumerBuilder> builder, S3Direct s3Direct,
            String namespace, String localBlobStore, boolean isProd, TransformerContext ctx, BusinessLogic businessLogic) {
        super(builder, s3Direct, namespace, localBlobStore, Optional.of(isProd), ctx, businessLogic);
        this.outputDataConsumer = VMSOutputDataConsumer.getNewProxyConsumer(builder, namespace, isProd);
    }

    @Override
    public HollowReadStateEngine process(long version, int... topNodes) throws Throwable {
        File localFile = performOutputSlice(version, topNodes);
        return readStateEngine(localFile);
    }

    @Override
    public File process(TYPE type, long dataVersion, int... topNodes) throws Throwable {
        switch (type) {
            case OUTPUT:
                return performOutputSlice(dataVersion, topNodes);
            default:
                throw new RuntimeException("Type " + type + " not supported");
        }
    }

    private File performOutputSlice(long version, int... topNodes) throws Exception {
        File localFile = getFile(namespace, TYPE.OUTPUT, version, topNodes);
        if (!localFile.exists()) {
            long start = System.currentTimeMillis();
            outputDataConsumer.triggerRefreshTo(version);

            Set<String> excludedTypes = new HashSet<>();
            for (OutputTypeConfig type : OutputTypeConfig.FASTLANE_EXCLUDED_TYPES) {
                excludedTypes.add(type.getType());
            }

            TransformerOutputDataSlicer transformerOutputDataSlicer = new TransformerOutputDataSlicer(excludedTypes, false, 0, topNodes);
            HollowWriteStateEngine slicedStateEngine = transformerOutputDataSlicer.sliceOutputBlob(outputDataConsumer.getStateEngine());

            String blobID = PinTitleHelper.createBlobID("o", version, topNodes);
            writeStateEngine(slicedStateEngine, localFile, blobID, version, topNodes);
            ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles, "Sliced[OUTPUT] videoId={} from namespace={}, version={}, duration={}", Arrays.toString(topNodes), namespace, version, (System.currentTimeMillis() - start));
        }
        return localFile;
    }
}
