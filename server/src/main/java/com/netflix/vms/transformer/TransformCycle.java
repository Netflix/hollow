package com.netflix.vms.transformer;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.File;
import com.netflix.hollow.client.HollowClient;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.input.VMSInputDataTransitionCreator;
import com.netflix.vms.transformer.logger.TransformerServerLogger;
import com.netflix.vms.transformer.servlet.platform.PlatformLibraries;
import com.netflix.vms.transformer.util.HollowBlobKeybaseBuilder;
import com.netflix.vms.transformer.util.LZ4VMSOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class TransformCycle {

    private final String vip;
    private final HollowClient inputClient;
    private final VMSTransformerWriteStateEngine outputStateEngine;
    private final TransformerContext ctx;

    public TransformCycle(String vip) {
        this.vip = vip;
        this.inputClient = new HollowClient(new VMSInputDataTransitionCreator(PlatformLibraries.FILE_STORE));
        this.outputStateEngine = new VMSTransformerWriteStateEngine();
        this.ctx = new TransformerContext(new TransformerServerLogger());
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
            SimpleTransformer transformer = new SimpleTransformer((VMSHollowVideoInputAPI)inputClient.getAPI(), outputStateEngine);
            transformer.transform();
        } catch(Throwable th) {
            ctx.getLogger().error("TransformCycleFailed", "Transformer failed cycle -- rolling back", th);
            outputStateEngine.resetToLastPrepareForNextCycle();
            return false;
        }

        return true;
    }


    private void writeTheBlobFiles() throws IOException {
        HollowBlobKeybaseBuilder keybaseBuilder = new HollowBlobKeybaseBuilder(vip);

        OutputStream snapshotOutputStream = new LZ4VMSOutputStream(new FileOutputStream(new File(System.getProperty("java.io.tmpdir"), "vms.snapshot")));
    }


    public void submitToPublishWorkflow() {

    }

}
