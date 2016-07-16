package com.netflix.vms.transformer.override;

import com.netflix.hollow.read.engine.HollowBlobReader;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.publish.workflow.HollowBlobFileNamer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;

public abstract class AbstractTitleOverrideProcessor implements TitleOverrideProcessor {

    protected final String vip;
    protected final String localBlobStore;
    protected final TransformerContext ctx;

    protected AbstractTitleOverrideProcessor(String vip, String localBlobStore, TransformerContext ctx) {
        this.vip = vip;
        this.localBlobStore = localBlobStore;
        this.ctx = ctx;

        mkdir(localBlobStore);
    }

    protected File getFile(String type, long version, int topNode) {
        if (localBlobStore != null) {
            return new File(localBlobStore, "vms.hollow" + type + ".blob." + vip + ".slice_" + version + "_" + topNode);
        } else {
            return new File(new HollowBlobFileNamer(vip).getTitleOverrideFileName(version, topNode));
        }
    }

    protected HollowReadStateEngine readStateEngine(File inputFile) throws IOException {
        ctx.getLogger().info(TransformerLogTag.OverrideTitle, "Read StateEngine file:{}", inputFile);

        HollowReadStateEngine stateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(stateEngine);
        try (LZ4BlockInputStream is = new LZ4BlockInputStream(new FileInputStream(inputFile))) {
            reader.readSnapshot(is);
        }

        return stateEngine;
    }

    protected void writeStateEngine(HollowWriteStateEngine stateEngine, File outputFile) throws IOException {
        ctx.getLogger().info(TransformerLogTag.OverrideTitle, "Write StateEngine file:{}", outputFile);

        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        try (LZ4BlockOutputStream os = new LZ4BlockOutputStream(new FileOutputStream(outputFile))) {
            writer.writeSnapshot(os);
        }
    }

    protected static void mkdir(String dirName) {
        File dir = new File(dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}