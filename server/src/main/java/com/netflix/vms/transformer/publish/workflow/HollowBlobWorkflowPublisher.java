package com.netflix.vms.transformer.publish.workflow;

import com.netflix.vms.transformer.common.TransformerLogger;
import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.util.LZ4VMSOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HollowBlobWorkflowPublisher {

    private HollowWriteStateEngine stateEngine;
    private long previouslyProducedVersion;
    private final HollowPublishWorkflowStager publishWorkflowStager;
    private final HollowBlobFileNamer fileNamer;

    public HollowBlobWorkflowPublisher(String vip, PublishWorkflowConfig circuitBreakerConfig, TransformerLogger logger) {
        this.fileNamer = new HollowBlobFileNamer(vip);
        this.publishWorkflowStager = new HollowPublishWorkflowStager(vip, circuitBreakerConfig, logger);
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
            OutputStream os = new LZ4VMSOutputStream(new FileOutputStream(deltaFile));
            try {
                writer.writeDelta(os);
            } finally {
                os.close();
            }
            deltaFile.renameTo(new File(deltaFileName));

            String reverseDeltaFileName = fileNamer.getReverseDeltaFileName(newVersion, previouslyProducedVersion);
            File reverseDeltaFile = new File(reverseDeltaFileName + ".incomplete");
            os = new LZ4VMSOutputStream(new FileOutputStream(reverseDeltaFile));
            try {
                writer.writeReverseDelta(os);
            } finally {
                os.close();
            }
            reverseDeltaFile.renameTo(new File(reverseDeltaFileName));
        }

        String snapshotFileName = fileNamer.getSnapshotFileName(newVersion);
        File snapshotFile = new File(snapshotFileName + ".incomplete");
        OutputStream os = new LZ4VMSOutputStream(new FileOutputStream(snapshotFile));
        try {
            writer.writeSnapshot(os);
        } finally {
            os.close();
        }
        snapshotFile.renameTo(new File(snapshotFileName));
    }

    private boolean shouldProduceDelta() {
        return previouslyProducedVersion != Long.MIN_VALUE;
    }
}
