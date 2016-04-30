package com.netflix.vms.transformer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.netflix.hollow.client.HollowClient;
import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.vms.transformer.common.PublicationHistoryConsumer;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerPlatformLibraries;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.input.VMSInputDataTransitionCreator;
import com.netflix.vms.transformer.logger.TransformerServerLogger;
import com.netflix.vms.transformer.util.LZ4VMSOutputStream;
import com.netflix.vms.transformer.util.TransformerServerCassandraHelper;
import com.netflix.vms.transformer.util.TransformerServerFiles;

public class TransformCycle {
    private final String vip;
    private final HollowClient inputClient;
    private final VMSTransformerWriteStateEngine outputStateEngine;
    private final TransformerContext ctx;
    private long cycleNumber = 0;

    public TransformCycle(TransformerPlatformLibraries platformLibraries, PublicationHistoryConsumer historyConsumer, String vip) {
        this.vip = vip;
        this.inputClient = new HollowClient(new VMSInputDataTransitionCreator(platformLibraries.getFileStore()));
        this.outputStateEngine = new VMSTransformerWriteStateEngine();
        this.ctx = new TransformerServerContext(new TransformerServerLogger(),
                new TransformerServerCassandraHelper(platformLibraries.getAstyanax(), "cass_dpt", "vms_poison_states", "poison_states"),
                new TransformerServerCassandraHelper(platformLibraries.getAstyanax(), "cass_dpt", "hollow_publish_workflow", "hollow_validation_stats"),
                new TransformerServerCassandraHelper(platformLibraries.getAstyanax(), "cass_dpt", "canary_validation", "canary_results"),
                new TransformerServerFiles(),
                platformLibraries,
                historyConsumer);
    }

    public void cycle() {
        updateTheInput();
        if(transformTheData()) {
            writeTheBlobFiles();
            submitToPublishWorkflow();
        }
    }

    private void updateTheInput() {
        inputClient.triggerRefresh();
    }

    private boolean transformTheData() {
        try {
            ctx.setNowMillis(1457384787807L);
            SimpleTransformer transformer = new SimpleTransformer((VMSHollowInputAPI)inputClient.getAPI(), outputStateEngine, ctx);
            transformer.transform();
        } catch(Throwable th) {
            ctx.getLogger().error("TransformCycleFailed", "Transformer failed cycle -- rolling back", th);
            outputStateEngine.resetToLastPrepareForNextCycle();
            return false;
        }

        return true;
    }


    private void writeTheBlobFiles() {
        try {
            HollowBlobWriter writer = new HollowBlobWriter(outputStateEngine);
            //HollowBlobKeybaseBuilder keybaseBuilder = new HollowBlobKeybaseBuilder(vip);

            OutputStream snapshotOutputStream = new LZ4VMSOutputStream(new FileOutputStream(new File(System.getProperty("java.io.tmpdir"), "vms." + vip + "-snapshot-" + cycleNumber)));
            try {
                writer.writeSnapshot(snapshotOutputStream);
            } finally {
                IOUtils.closeQuietly(snapshotOutputStream);
            }

            if(cycleNumber > 0) {
                OutputStream deltaOutputStream = new LZ4VMSOutputStream(new FileOutputStream(new File(System.getProperty("java.io.tmpdir"), "vms." + vip + "-delta-" + (cycleNumber-1) + "-" + cycleNumber)));
                try {
                    writer.writeDelta(deltaOutputStream);
                } finally {
                    IOUtils.closeQuietly(deltaOutputStream);
                }
            }
        } catch(IOException e) {
            /// MUST restore from previous state, (or reset to last prepare for next cycle if possible).
        }
    }


    public void submitToPublishWorkflow() {

    }

}
