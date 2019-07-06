package com.netflix.vms.transformer.override;

import com.netflix.cinder.consumer.CinderConsumerBuilder;
import com.netflix.gutenberg.grpc.shared.DataPointer;
import com.netflix.gutenberg.s3access.S3Direct;
import com.netflix.gutenberg.s3access.S3Util;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.publish.workflow.HollowBlobFileNamer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;

public abstract class AbstractPinTitleProcessor implements PinTitleProcessor {
    protected final Supplier<CinderConsumerBuilder> builder;
    protected final String namespace;
    protected final String localBlobStore;
    protected final Optional<Boolean> isProd;
    protected final TransformerContext ctx;
    protected final S3Direct pinTitleS3Access;

    protected AbstractPinTitleProcessor(Supplier<CinderConsumerBuilder> builder, S3Direct pinTitleS3Access,
            String namespace, String localBlobStore, Optional<Boolean> isProd, TransformerContext ctx) {
        this.namespace = namespace;
        this.builder = builder;
        this.pinTitleS3Access = pinTitleS3Access;
        this.localBlobStore = localBlobStore;
        this.isProd = isProd;
        this.ctx = ctx;

        mkdir(localBlobStore);
    }

    @Override
    public File getFile(String namespace, TYPE type, long version, int... topNodes) throws Exception {
        String fileNamespace = namespace + "_" + type.name().toLowerCase();
        HollowBlobFileNamer namer = new HollowBlobFileNamer(fileNamespace);

        File file = null;
        if (localBlobStore != null) {
            file = new File(localBlobStore, namer.getPinTitleFileName(version, false, topNodes));
        } else {
            file = new File(namer.getPinTitleFileName(version, true, topNodes));
        }

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

    public HollowReadStateEngine readStateEngine(File inputFile) throws IOException {
        ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles, "Read StateEngine file:{}", inputFile);

        HollowReadStateEngine stateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(stateEngine);
        try (LZ4BlockInputStream is = new LZ4BlockInputStream(new FileInputStream(inputFile))) {
            reader.readSnapshot(is);
        }

        return stateEngine;
    }

    protected void writeStateEngine(HollowWriteStateEngine stateEngine, File outputFile, String blobID, long version, int... topNodes) throws Exception {
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
            pinTitleS3Access.publish(outputFile, S3Util.getFileStoreCompatibleObjectMetadata(outputFile), outputS3DataPointer);
            ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles, "Published file:{}", outputFile);
        } catch (Exception ex) {
            ctx.getLogger().error(TransformerLogTag.CyclePinnedTitles, "Failed to Publish file to s3Key:{}", s3Key, ex);
        }
    }

    protected static void mkdir(String dirName) {
        if (dirName == null) return;

        File dir = new File(dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}