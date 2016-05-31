package com.netflix.vms.transformer.publish.workflow;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerFiles;

public class HollowBlobWorkflowPublisher {

    /* dependencies */
    private final TransformerFiles files;

    /* fields */
    private HollowWriteStateEngine stateEngine;
    private long previouslyProducedVersion;
    private final HollowPublishWorkflowStager publishWorkflowStager;
    private final HollowBlobFileNamer fileNamer;

    public HollowBlobWorkflowPublisher(String vip, TransformerContext ctx) {
        this.files = ctx.files();
        this.fileNamer = new HollowBlobFileNamer(vip);
        this.publishWorkflowStager = new HollowPublishWorkflowStager(ctx, vip);
    }

    public void initialize(HollowWriteStateEngine stateEngine) {
        this.stateEngine = stateEngine;
        previouslyProducedVersion = Long.MIN_VALUE;
    }

    public void publishBlobs(long newVersion) throws IOException {
        writeHollowBlobDataOntoDisk(newVersion);
        publishWorkflowStager.triggerPublish(previouslyProducedVersion, newVersion);

        previouslyProducedVersion = newVersion;
    }

    private void writeHollowBlobDataOntoDisk(long newVersion) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);

        if(shouldProduceDelta()) {
            String deltaFileName = fileNamer.getDeltaFileName(previouslyProducedVersion, newVersion);
            File deltaFile = new File(deltaFileName + ".incomplete");
            try (OutputStream os = files.newBlobOutputStream(deltaFile)) {
                writer.writeDelta(os);
            }
            deltaFile.renameTo(new File(deltaFileName));

            String reverseDeltaFileName = fileNamer.getReverseDeltaFileName(newVersion, previouslyProducedVersion);
            File reverseDeltaFile = new File(reverseDeltaFileName + ".incomplete");
            try (OutputStream os = files.newBlobOutputStream(reverseDeltaFile)) {
                writer.writeReverseDelta(os);
            }
            reverseDeltaFile.renameTo(new File(reverseDeltaFileName));
        }

        String snapshotFileName = fileNamer.getSnapshotFileName(newVersion);
        File snapshotFile = new File(snapshotFileName + ".incomplete");
        try (OutputStream os = files.newBlobOutputStream(snapshotFile)) {
            writer.writeSnapshot(os);
        }
        snapshotFile.renameTo(new File(snapshotFileName));
    }

    private boolean shouldProduceDelta() {
        return previouslyProducedVersion != Long.MIN_VALUE;
    }
}
