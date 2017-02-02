package com.netflix.vms.transformer.testutil.migration;

import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.SimpleTransformer;
import com.netflix.vms.transformer.SimpleTransformerContext;
import com.netflix.vms.transformer.VMSTransformerWriteStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.http.HttpHelper;
import com.netflix.vms.transformer.input.VMSInputDataClient;
import com.netflix.vms.transformer.override.OutputSlicePinTitleProcessor;
import com.netflix.vms.transformer.override.PinTitleHelper;
import com.netflix.vms.transformer.util.HollowBlobKeybaseBuilder;
import com.netflix.vms.transformer.util.OutputUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;

public class ShowMeTheFastProgress {
    private static final boolean isProd = true;
    private static final String VIP_NAME = "newnoevent";
    private static final String CONVERTER_VIP_NAME = "noevent";
    private static final String WORKING_DIR = "/space/transformer-data/fast";
    private static final String PROXY = isProd ? VMSInputDataClient.PROD_PROXY_URL : VMSInputDataClient.TEST_PROXY_URL;
    private static final String PUBLISH_CYCLE_DATATS_HEADER = "publishCycleDataTS";

    @Test
    public void getLatestTransformerVersion() {
        long version = getLatestTransformerVersion(VIP_NAME);
        System.out.println("getLatestTransformerVersion: " + version);
    }

    @Test
    public void start() throws Throwable {
        // NOTE: the specified transformerVersion must be valid or already in local HD; otherwise, run  getLatestTransformerVersion();
        long transformerVersion = 20170202180324869L;
        int[] topNodes = { 80115503, 70143860 };

        long start = System.currentTimeMillis();
        setup();

        // Load Expected StateEngine
        SimpleTransformerContext ctx = new SimpleTransformerContext();
        HollowReadStateEngine expectedOutputStateEngine = loadTransformerEngine(ctx, VIP_NAME, transformerVersion, topNodes);
        String value = expectedOutputStateEngine.getHeaderTag(PUBLISH_CYCLE_DATATS_HEADER);
        long publishCycleDataTS = value != null ? Long.parseLong(value) : System.currentTimeMillis();
        long converterBlobVersion = Long.parseLong(expectedOutputStateEngine.getHeaderTag("sourceDataVersion"));

        // Load Transformer input based on converterBlobVersion
        VMSHollowInputAPI inputAPI = loadVMSHollowInputAPI(converterBlobVersion);

        // Setup Fastlane context and Output State Engine
        List<Integer> fastlaneIds = Arrays.stream(topNodes).boxed().collect(Collectors.toList());
        ctx.setFastlaneIds(new HashSet<>(fastlaneIds));
        VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();
        outputStateEngine.addHeaderTags(expectedOutputStateEngine.getHeaderTags());
        outputStateEngine.addHeaderTag(PUBLISH_CYCLE_DATATS_HEADER, String.valueOf(publishCycleDataTS));

        // Run Transformer
        SimpleTransformer transformer = new SimpleTransformer(inputAPI, outputStateEngine, ctx);
        transformer.setPublishCycleDataTS(publishCycleDataTS);
        transformer.transform();
        HollowReadStateEngine actualOutputReadStateEngine = roundTripOutputStateEngine(outputStateEngine);
        trackDuration(start, "Done transformerVersion=%s, topNodes=%s", transformerVersion, toString(topNodes));

        // Do Diff
        ShowMeTheProgressDiffTool.startTheDiff(expectedOutputStateEngine, actualOutputReadStateEngine);
    }

    public void setup() {
        File workingDir = new File(WORKING_DIR);
        if (!workingDir.exists()) workingDir.mkdirs();
    }

    public long getLatestTransformerVersion(String vip) {
        return getLatestVersion(new HollowBlobKeybaseBuilder(vip).getSnapshotKeybase());
    }

    private static long getLatestVersion(String keybase) {
        String proxyUrl = PROXY + "/filestore-version?keybase=" + keybase;
        String version = HttpHelper.getStringResponse(proxyUrl);
        System.out.println(String.format(">>> getLatestVersion: keybase=%s, version=%s", keybase, version));
        return Long.parseLong(version);
    }

    private HollowReadStateEngine loadTransformerEngine(TransformerContext ctx, String vipName, long version, int... topNodes) throws IOException {
        System.out.println("loadTransformerEngine: Loading version=" + version);
        long start = System.currentTimeMillis();
        try {
            OutputSlicePinTitleProcessor processor = new OutputSlicePinTitleProcessor(vipName, PROXY, WORKING_DIR, ctx);
            return processor.process(version, topNodes);
        } finally {
            trackDuration(start, "loadTransformerEngine: vipName=%s, version=%s, topNodes=%s", vipName, version, toString(topNodes));
        }
    }

    private static VMSHollowInputAPI loadVMSHollowInputAPI(long version) {
        System.out.println("loadVMSHollowInputAPI: Loading version=" + version);
        long start = System.currentTimeMillis();
        try {
            VMSInputDataClient inputClient = new VMSInputDataClient(PROXY, WORKING_DIR, CONVERTER_VIP_NAME);
            inputClient.triggerRefreshTo(version);
            return new VMSHollowInputAPI(inputClient.getStateEngine());
        } finally {
            trackDuration(start, "loadVMSHollowInputAPI: version=%s", version);
        }
    }

    private HollowReadStateEngine roundTripOutputStateEngine(HollowWriteStateEngine stateEngine) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.writeSnapshot(baos);

        HollowReadStateEngine actualOutputStateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(actualOutputStateEngine);
        reader.readSnapshot(new ByteArrayInputStream(baos.toByteArray()));
        return actualOutputStateEngine;
    }

    private static String toString(int... values) {
        return PinTitleHelper.toString(values);
    }

    private static void trackDuration(long start, String format, Object... args) {
        String msg = String.format(format, args);
        long duration = System.currentTimeMillis() - start;
        System.out.println(">>> " + msg + ", duration=" + OutputUtil.formatDuration(duration, true));
    }
}