package com.netflix.vms.transformer.override;

import com.netflix.cinder.consumer.CinderConsumerBuilder;
import com.netflix.config.NetflixConfiguration;
import com.netflix.gutenberg.grpc.shared.DataPointer;
import com.netflix.gutenberg.s3access.S3Direct;
import com.netflix.gutenberg.s3access.S3Util;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.api.BusinessLogicAPI;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.common.slice.OutputDataSlicer;
import com.netflix.vms.transformer.common.slice.SlicerFactory;
import com.netflix.vms.transformer.consumer.VMSOutputDataConsumer;
import com.netflix.vms.transformer.publish.workflow.HollowBlobFileNamer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;

/**
 * Generates Title Override based on Output Slice
 */
public class PinTitleProcessor {
    public static final String PIN_TITLE_S3_BUCKET = "netflix.bulkdata." + ("prod".equals(NetflixConfiguration.getEnvironment()) ? "prod" : "test");
    public static final String PIN_TITLE_S3_PATH = "pin_title_cache";
    public static final String PIN_TITLE_S3_REGION = NetflixConfiguration.RegionEnum.US_EAST_1.key();

    private final TransformerContext ctx;
    private final String outputNamespace;
    private final HollowConsumer outputDataConsumer;
    private final S3Direct pinTitleS3Access;
    private final BusinessLogicAPI businessLogic;

    public PinTitleProcessor(Supplier<CinderConsumerBuilder> builder, S3Direct s3Direct,
            String namespace, TransformerContext ctx, BusinessLogicAPI businessLogic) {
        this.outputDataConsumer = VMSOutputDataConsumer.getNewConsumer(builder, namespace);
        this.outputNamespace = namespace;
        this.pinTitleS3Access = s3Direct;
        this.ctx = ctx;
        this.businessLogic = businessLogic;
    }

    public HollowReadStateEngine process(long version, int... topNodes) throws Throwable {
        File localFile = performOutputSlice(version, topNodes);
        return readStateEngine(localFile);
    }

    public File processToFile(long dataVersion, int... topNodes) throws Throwable {
        return performOutputSlice(dataVersion, topNodes);
    }

    private File performOutputSlice(long version, int... topNodes) throws Exception {
        File localFile = getFile(outputNamespace, version, topNodes);
        if (!localFile.exists()) {
            long start = System.currentTimeMillis();
            outputDataConsumer.triggerRefreshTo(version);

            Set<String> excludedTypes = new HashSet<>();
            for (OutputTypeConfig type : OutputTypeConfig.FASTLANE_EXCLUDED_TYPES) {
                excludedTypes.add(type.getType());
            }

            OutputDataSlicer transformerOutputDataSlicer = new SlicerFactory().getOutputDataSlicer(businessLogic.getOutputSlicer(), excludedTypes, false, 0, topNodes);
            HollowWriteStateEngine slicedStateEngine = transformerOutputDataSlicer.sliceOutputBlob(outputDataConsumer.getStateEngine());

            String blobID = PinTitleHelper.createBlobID("o", version, topNodes);
            writeStateEngine(ctx, pinTitleS3Access, slicedStateEngine, localFile, blobID, version, topNodes);
            ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles, "Sliced[OUTPUT] videoId={} from outputNamespace={}, version={}, duration={}", Arrays.toString(topNodes),
                    outputNamespace, version, (System.currentTimeMillis() - start));
        }
        return localFile;
    }

    File getFile(String namespace, long version, int... topNodes) throws Exception {
        String fileNamespace = namespace + "_output";
        HollowBlobFileNamer namer = new HollowBlobFileNamer(fileNamespace);

        File file = new File(namer.getPinTitleFileName(version, true, topNodes));

        // If the file already exists in the cloud then download it
        if (!file.exists()) {
            String s3Key = file.getName();
            try {
                pinTitleS3Access.retrieve(PIN_TITLE_S3_BUCKET, PIN_TITLE_S3_PATH + "/" + s3Key, file);
                ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles, "Fetched file:{}", file);
            } catch (Exception ex) {
                ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles, "Did not find S3 object for key:{}. "
                        + "It might not have existed, no worries, this lookup is just as an optimization", s3Key);
            }
        }

        return file;
    }

    HollowReadStateEngine readStateEngine(File inputFile) throws IOException {
        ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles, "Read StateEngine file:{}", inputFile);

        HollowReadStateEngine stateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(stateEngine);
        try (LZ4BlockInputStream is = new LZ4BlockInputStream(new FileInputStream(inputFile))) {
            reader.readSnapshot(is);
        }

        return stateEngine;
    }

    void writeStateEngine(TransformerContext ctx, S3Direct s3Access, HollowWriteStateEngine stateEngine, File outputFile, String blobID, long version, int... topNodes) throws Exception {
        ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles, "Write StateEngine file:{}", outputFile);

        if (blobID == null) blobID = outputFile.getName();
        PinTitleHelper.addBlobID(stateEngine, blobID);
        PinTitleHelper.addPinnedTitles(stateEngine, version, topNodes);

        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        try (LZ4BlockOutputStream os = new LZ4BlockOutputStream(new FileOutputStream(outputFile))) {
            writer.writeSnapshot(os);
        }

        // Cache the Pinned Title blob to S3
        String s3Key = outputFile.getName();
        try {
            DataPointer.S3DataPointer outputS3DataPointer = DataPointer.S3DataPointer.newBuilder()
                    .addS3Objects(DataPointer.S3DataPointer.S3ObjectInfo.newBuilder()
                            .setBucket(PIN_TITLE_S3_BUCKET)
                            .setKey(PIN_TITLE_S3_PATH + "/" + s3Key)
                            .setRegion(PIN_TITLE_S3_REGION)
                            .build())
                    .build();
            s3Access.publish(outputFile, S3Util.getFileStoreCompatibleObjectMetadata(outputFile), outputS3DataPointer);
            ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles, "Published file:{}", outputFile);
        } catch (Exception ex) {
            ctx.getLogger().error(TransformerLogTag.CyclePinnedTitles, "Failed to Publish file to s3Key:{}", s3Key, ex);
        }
    }

    public static HollowReadStateEngine readInputData(HollowConsumer inputConsumer, long inputDataVersion) {
        inputConsumer.triggerRefreshTo(inputDataVersion);
        return inputConsumer.getStateEngine();
    }
}
