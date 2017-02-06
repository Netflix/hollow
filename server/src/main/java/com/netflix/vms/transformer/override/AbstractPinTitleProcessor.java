package com.netflix.vms.transformer.override;

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
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;

public abstract class AbstractPinTitleProcessor implements PinTitleProcessor {

    protected final String vip;
    protected final String localBlobStore;
    protected final TransformerContext ctx;

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

    public File getFile(String type, long version, int... topNodes) {
        String fileVIP = vip + "_" + type;
        HollowBlobFileNamer namer = new HollowBlobFileNamer(fileVIP);
        if (localBlobStore != null) {
            return new File(localBlobStore, namer.getPinTitleFileName(version, false, topNodes));
        } else {
            return new File(namer.getPinTitleFileName(version, true, topNodes));
        }
    }

    protected HollowReadStateEngine readStateEngine(File inputFile) throws IOException {
        ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles, "Read StateEngine file:{}", inputFile);

        HollowReadStateEngine stateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(stateEngine);
        try (LZ4BlockInputStream is = new LZ4BlockInputStream(new FileInputStream(inputFile))) {
            reader.readSnapshot(is);
        }

        return stateEngine;
    }

    protected void writeStateEngine(HollowWriteStateEngine stateEngine, File outputFile, String blobID, long version, int... topNodes) throws IOException {
        ctx.getLogger().info(TransformerLogTag.CyclePinnedTitles, "Write StateEngine file:{}", outputFile);

        if (blobID == null) blobID = outputFile.getName();
        PinTitleHelper.addBlobID(stateEngine, blobID);
        PinTitleHelper.addPinnedTitles(stateEngine, version, topNodes);

        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        try (LZ4BlockOutputStream os = new LZ4BlockOutputStream(new FileOutputStream(outputFile))) {
            writer.writeSnapshot(os);
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