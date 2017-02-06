package com.netflix.vms.transformer.override;

import com.netflix.aws.S3.S3Object;
import com.netflix.aws.file.FileStore;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.publish.workflow.HollowBlobFileNamer;
import com.netflix.vms.transformer.util.VMSProxyUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;

public abstract class AbstractPinTitleProcessor implements PinTitleProcessor {

    protected final String vip;
    protected final String localBlobStore;
    protected final TransformerContext ctx;

    protected FileStore pinTitleFileStore;
    protected String pinTitleProxyURL;

    protected AbstractPinTitleProcessor(String vip, String localBlobStore, TransformerContext ctx) {
        this.vip = vip;
        this.localBlobStore = localBlobStore;
        this.ctx = ctx;

        mkdir(localBlobStore);
    }

    @Override
    public String getVip() {
        return vip;
    }

    public void setPinTitleFileStore(FileStore pinTitleFileStore) {
        this.pinTitleFileStore = pinTitleFileStore;
    }

    public File getFile(String type, long version, int... topNodes) throws Exception {
        String fileVIP = vip + "_" + type;
        HollowBlobFileNamer namer = new HollowBlobFileNamer(fileVIP);

        File file = null;
        if (localBlobStore != null) {
            file = new File(localBlobStore, namer.getPinTitleFileName(version, false, topNodes));
        } else {
            file = new File(namer.getPinTitleFileName(version, true, topNodes));
        }

        // Determine whether the file exist in the cloud
        if (!file.exists()) {
            String keybase = file.getName();
            try {
                if (pinTitleFileStore != null) {
                    S3Object publishedFile = pinTitleFileStore.getPublishedFile(keybase);
                    if (publishedFile != null) {
                        pinTitleFileStore.copyFile(publishedFile, file);
                        ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles, "Fetched file:{}", file);
                    }
                } else if (pinTitleProxyURL != null) {
                    boolean isSuccess = VMSProxyUtil.download(pinTitleProxyURL, keybase, null, file, false);
                    if (isSuccess) ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles, "Fetched file:{} from {}", file, pinTitleProxyURL);
                }
            } catch (Exception ex) {
                ctx.getLogger().error(TransformerLogTag.CyclePinnedTitles, "Failed to Fetch file from keybase:{}", keybase, ex);
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

        // Try to published the Pinned Title blob
        if (pinTitleFileStore != null) {
            String keybase = outputFile.getName();
            try {
                pinTitleFileStore.publish(outputFile, keybase);
                ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles, "Published file:{}", outputFile);
            } catch (Exception ex) {
                ctx.getLogger().error(TransformerLogTag.CyclePinnedTitles, "Failed to Publish file to keybase:{}", keybase, ex);
            }
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