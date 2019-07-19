package com.netflix.vms.transformer.override;

import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.UpstreamDatasetConfig.lookupDatasetForNamespace;

import com.netflix.cinder.consumer.CinderConsumerBuilder;
import com.netflix.gutenberg.s3access.S3Direct;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.common.slice.InputDataSlicer;
import com.netflix.vms.transformer.consumer.VMSInputDataConsumer;
import com.netflix.vms.transformer.input.UpstreamDatasetHolder;
import com.netflix.vms.transformer.input.datasets.slicers.SlicerFactory;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Generates Title Override based on Input Slice
 */
public class InputSlicePinTitleProcessor extends AbstractPinTitleProcessor {

    private final HollowConsumer inputConsumer;
    private final UpstreamDatasetHolder.Dataset dataset;

    public InputSlicePinTitleProcessor(Supplier<CinderConsumerBuilder> builder, S3Direct s3Direct,
            String namespace, String localBlobStore, boolean isProd, TransformerContext ctx) {
        super(builder, s3Direct, namespace, localBlobStore, Optional.of(isProd), ctx);

        this.dataset = lookupDatasetForNamespace(namespace, isProd);
        ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles,
                "Created InputSlicePinTitleProcessor instance for dataset={} api={}", dataset, dataset.getAPI());

        inputConsumer = VMSInputDataConsumer.getNewProxyConsumer(builder, namespace, localBlobStore,
                isProd, this.dataset.getAPI());
    }

    @Override
    public HollowReadStateEngine process(long inputDataVersion, int... topNodes) throws Throwable {
        throw new UnsupportedOperationException("Performing output slice using input pinning is not supported");
    }

    @Override
    public File process(TYPE type, long dataVersion, int... topNodes) throws Throwable {
        switch (type) {
            case OUTPUT:
                throw new UnsupportedOperationException("Performing output slice using input pinning is not supported");
            case INPUT:
                return performInputSlice(dataVersion, topNodes);
            default:
                throw new RuntimeException("Type " + type + " not supported");
        }
    }

    // loads slice of one input
    private File performInputSlice(Long inputDataVersion, int... topNodes) throws Exception {
        ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles,
                "Slicing input file for namespace={} version={}", namespace, inputDataVersion);

        File slicedFile = getFile(namespace, TYPE.INPUT, inputDataVersion, topNodes);
        if (!slicedFile.exists()) {
            long start = System.currentTimeMillis();
            HollowReadStateEngine inputStateEngine = readInputData(inputDataVersion);

            if (dataset.getSlicer() == null) {
                throw new UnsupportedOperationException("Input slicer missing for namespace= " + namespace);
            }

            InputDataSlicer inputDataSlicer = new SlicerFactory().getInputDataSlicer(dataset, topNodes);
            HollowWriteStateEngine slicedStateEngine = inputDataSlicer.sliceInputBlob(inputStateEngine);

            String blobID = PinTitleHelper.createBlobID("sliced_input_" + namespace, inputDataVersion, topNodes);
            writeStateEngine(slicedStateEngine, slicedFile, blobID, inputDataVersion, topNodes);
            ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles,
                    "Sliced[INPUT] videoId={} from namespace={}, version={}, duration={}",
                    Arrays.toString(topNodes), namespace, inputDataVersion, (System.currentTimeMillis() - start));
        }
        return slicedFile;
    }

    private HollowReadStateEngine readInputData(long inputDataVersion) {
        inputConsumer.triggerRefreshTo(inputDataVersion);
        return inputConsumer.getStateEngine();
    }
}
