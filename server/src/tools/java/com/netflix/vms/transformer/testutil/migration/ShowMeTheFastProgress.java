package com.netflix.vms.transformer.testutil.migration;

import com.netflix.aws.file.FileStore;
import com.netflix.governator.InjectorBuilder;
import com.netflix.governator.LifecycleInjector;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.runtime.lifecycle.RuntimeCoreModule;
import com.netflix.vms.transformer.SimpleTransformer;
import com.netflix.vms.transformer.SimpleTransformerContext;
import com.netflix.vms.transformer.VMSTransformerWriteStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.http.HttpHelper;
import com.netflix.vms.transformer.input.VMSInputDataClient;
import com.netflix.vms.transformer.input.VMSInputDataKeybaseBuilder;
import com.netflix.vms.transformer.override.InputSlicePinTitleProcessor;
import com.netflix.vms.transformer.override.OutputSlicePinTitleProcessor;
import com.netflix.vms.transformer.override.PinTitleHelper;
import com.netflix.vms.transformer.override.PinTitleProcessor.TYPE;
import com.netflix.vms.transformer.util.HollowBlobKeybaseBuilder;
import com.netflix.vms.transformer.util.OutputUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class ShowMeTheFastProgress {
    private static final boolean isProd = false;
    private static final boolean isPerformDiff = true;
    private static final boolean isPublishPinnedData = false;
    private static final boolean isUseRemotePinTitleSlicer = true;
    private static final boolean isFallBackToLocalSlicer = false;

    private static final String VIP_NAME = "feather";
    private static final String CONVERTER_VIP_NAME = "muon";
    private static final String VMSSLICER_INSTANCE = "vmstransformer-feather_override-muon";

    private static final String WORKING_DIR = "/space/transformer-data/fast";
    private static final String PROXY = isProd ? VMSInputDataClient.PROD_PROXY_URL : VMSInputDataClient.TEST_PROXY_URL;
    private static final String REMOTE_SLICER_URL = "http://discovery.cloudqa.netflix.net:7001/discovery/resolver/cluster/" + VMSSLICER_INSTANCE + ":7101/REST/vms/pintitleslicer"; // NOTE: SLICER must be in TEST env hence cloudqa
    private static final String PUBLISH_CYCLE_DATATS_HEADER = "publishCycleDataTS";

    private FileStore pinTitleFileStore;

    @Before
    public void setup() throws Exception {
        File workingDir = new File(WORKING_DIR);
        if (!workingDir.exists()) workingDir.mkdirs();

        if (isPublishPinnedData) {
            LifecycleInjector lInjector = InjectorBuilder.fromModules(new RuntimeCoreModule()).createInjector();
            pinTitleFileStore = lInjector.getInstance(FileStore.class);
        }
    }

    @Test
    public void getLatestTransformerVersion() {
        long version = getLatestVersion(new HollowBlobKeybaseBuilder(VIP_NAME).getSnapshotKeybase());
        System.out.println("getLatestTransformerVersion: " + version);
    }

    @Test
    public void getLatestConverterVersion() {
        long version = getLatestVersion(new VMSInputDataKeybaseBuilder(CONVERTER_VIP_NAME).getSnapshotKeybase());
        System.out.println("getLatestConverterVersion: " + version);
    }

    @Test
    public void diffTwoVips() throws Throwable {
        // NOTE: the specified transformerVersion must be valid or already in local HD; otherwise, run  getLatestTransformerVersion();
        String FROM_VIP = "feather";
        String TO_VIP = "whiplash";

        long FROM_VER = 20171106215605001L;
        long TO_VER = 20171106191033375L;
        int[] topNodes = { 80126024 };

        long start = System.currentTimeMillis();
        try {
            SimpleTransformerContext ctx = new SimpleTransformerContext();
            HollowReadStateEngine from_ReadStateEngine = loadTransformerEngine(ctx, FROM_VIP, FROM_VER, topNodes);
            HollowReadStateEngine to_ReadStateEngine = loadTransformerEngine(ctx, TO_VIP, TO_VER, topNodes);
            ShowMeTheProgressDiffTool.startTheDiff(to_ReadStateEngine, from_ReadStateEngine);
        } finally {
            trackDuration(start, "diffTwoVips: %s:%s vs %s:%s - topNodes=%s", FROM_VIP, FROM_VER, TO_VIP, TO_VER, toString(topNodes));
        }
    }

    @Test
    public void runFastLaneAndDiff() throws Throwable {
        // NOTE: the specified transformerVersion must be valid or already in local HD; otherwise, run  getLatestTransformerVersion();
        long transformerVersion = 20180131174740436L;
        int[] topNodes = { 80240512 };

        long start = System.currentTimeMillis();

        // Setup Context
        SimpleTransformerContext ctx = new SimpleTransformerContext();
        //ctx.overrideSupportedCountries("US");

        // Load Expected StateEngine
        HollowReadStateEngine expectedOutputStateEngine = loadTransformerEngine(ctx, VIP_NAME, transformerVersion, topNodes);
        String value = expectedOutputStateEngine.getHeaderTag(PUBLISH_CYCLE_DATATS_HEADER);
        long publishCycleDataTS = value != null ? Long.parseLong(value) : System.currentTimeMillis();
        long converterBlobVersion = Long.parseLong(expectedOutputStateEngine.getHeaderTag("sourceDataVersion"));

        // Load Transformer input based on converterBlobVersion
        VMSHollowInputAPI inputAPI = loadVMSHollowInputAPI(ctx, CONVERTER_VIP_NAME, converterBlobVersion, topNodes);

        // Setup Fastlane context and Output State Engine
        List<Integer> fastlaneIds = Arrays.stream(topNodes).boxed().collect(Collectors.toList());
        ctx.setFastlaneIds(new HashSet<>(fastlaneIds));
        VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();
        outputStateEngine.addHeaderTags(expectedOutputStateEngine.getHeaderTags());
        outputStateEngine.addHeaderTag(PUBLISH_CYCLE_DATATS_HEADER, String.valueOf(publishCycleDataTS));

        // Run Transformer
        SimpleTransformer transformer = new SimpleTransformer(inputAPI, null, outputStateEngine, ctx);
        transformer.setPublishCycleDataTS(publishCycleDataTS);
        transformer.transform();
        HollowReadStateEngine actualOutputReadStateEngine = roundTripOutputStateEngine(outputStateEngine);
        trackDuration(start, "Done transformerVersion=%s, topNodes=%s", transformerVersion, toString(topNodes));

        // Do Diff
        if (isPerformDiff) {
            ShowMeTheProgressDiffTool.startTheDiff(expectedOutputStateEngine, actualOutputReadStateEngine);
        }
    }

    @Test
    public void runFastLaneWithConverterData() throws Throwable {
        // NOTE: the specified converterVersion must be valid or already in local HD; otherwise, run  getLatestConverterVersion();
        int[] topNodes = { 80104350 };
        long converterVersion = 20171115044410018L;

        // Setup Context
        SimpleTransformerContext ctx = new SimpleTransformerContext();
        //ctx.overrideSupportedCountries("US");

        // Load Transformer input based on converterBlobVersion
        VMSHollowInputAPI inputAPI = loadVMSHollowInputAPI(ctx, CONVERTER_VIP_NAME, converterVersion, topNodes);

        // Setup Fastlane context and Output State Engine
        List<Integer> fastlaneIds = Arrays.stream(topNodes).boxed().collect(Collectors.toList());
        ctx.setFastlaneIds(new HashSet<>(fastlaneIds));
        VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();

        // Run Transformer
        SimpleTransformer transformer = new SimpleTransformer(inputAPI, null, outputStateEngine, ctx);
        transformer.transform();
        HollowReadStateEngine actualOutputReadStateEngine = roundTripOutputStateEngine(outputStateEngine);
        System.out.println(actualOutputReadStateEngine.getHeaderTags());
    }

    private static long getLatestVersion(String keybase) {
        String proxyUrl = PROXY + "/filestore-version?keybase=" + keybase;
        String version = HttpHelper.getStringResponse(proxyUrl);
        System.out.println(String.format(">>> getLatestVersion: keybase=%s, version=%s", keybase, version));
        return Long.parseLong(version);
    }

    private static void downloadSlice(File downloadTo, String baseURL, boolean isProd, boolean isOutput, String vipName, long version, int... topNodes) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        String proxyURL = String.format("%s?prod=%s&output=%s&vip=%s&version=%s&topnodes=%s", baseURL, isProd, isOutput, vipName, version, toString(topNodes));
        try {
            System.out.println(">>> Requesting a slice from: " + proxyURL);
            is = HttpHelper.getInputStream(proxyURL, false);
            os = new FileOutputStream(downloadTo);
            IOUtils.copy(is, os);
            System.out.println(">>> Done downloading from: " + proxyURL + " to: " + downloadTo);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    @SuppressWarnings("unused")
    private HollowReadStateEngine loadTransformerEngine(TransformerContext ctx, String vipName, long version, int... topNodes) throws Throwable {
        System.out.println("loadTransformerEngine: Loading version=" + version);
        long start = System.currentTimeMillis();
        try {
            OutputSlicePinTitleProcessor processor = new OutputSlicePinTitleProcessor(vipName, PROXY, WORKING_DIR, ctx);
            processor.setPinTitleFileStore(pinTitleFileStore);
            File slicedFile = processor.getFile(TYPE.OUTPUT, version, topNodes);
            if (isUseRemotePinTitleSlicer && !slicedFile.exists()) {
                try {
                    downloadSlice(slicedFile, REMOTE_SLICER_URL, isProd, true, vipName, version, topNodes);
                    return processor.readStateEngine(slicedFile);
                } catch (Exception ex) {
                    System.err.println("ERROR: Remote Slicer failure - " + ex.toString());
                    if (isFallBackToLocalSlicer) System.out.println("Failling back to local slicer."); else throw ex;
                }
            }
            return processor.process(version, topNodes);
        } finally {
            trackDuration(start, "loadTransformerEngine: vipName=%s, version=%s, topNodes=%s", vipName, version, toString(topNodes));
        }
    }

    @SuppressWarnings("unused")
    private VMSHollowInputAPI loadVMSHollowInputAPI(TransformerContext ctx, String vipName, long version, int... topNodes) throws Throwable {
        boolean isUseInputSlicing = true;
        System.out.println("loadVMSHollowInputAPI: Loading version=" + version);
        long start = System.currentTimeMillis();
        try {
            HollowReadStateEngine stateEngine = null;
            if (isUseInputSlicing) {
                InputSlicePinTitleProcessor processor = new InputSlicePinTitleProcessor(vipName, PROXY, WORKING_DIR, ctx);
                processor.setPinTitleFileStore(pinTitleFileStore);
                File slicedFile = processor.getFile(TYPE.INPUT, version, topNodes);
                if (isUseRemotePinTitleSlicer && !slicedFile.exists()) {
                    try {
                        downloadSlice(slicedFile, REMOTE_SLICER_URL, isProd, false, vipName, version, topNodes);
                    } catch (Exception ex) {
                        System.err.println("ERROR: Remote Slicer failure - " + ex.toString());
                        if (isFallBackToLocalSlicer) System.out.println("Failling back to local slicer."); else throw ex;
                    }
                }

                if (!slicedFile.exists()) slicedFile = processor.process(TYPE.INPUT, version, topNodes);
                stateEngine = processor.readStateEngine(slicedFile);
            } else {
                VMSInputDataClient inputClient = new VMSInputDataClient(PROXY, WORKING_DIR, vipName);
                inputClient.triggerRefreshTo(version);
                stateEngine = inputClient.getStateEngine();
            }
            return new VMSHollowInputAPI(stateEngine);
        } finally {
            trackDuration(start, "loadVMSHollowInputAPI: isUseInputSlicing=%s, version=%s, topNodes=%s", isUseInputSlicing, version, toString(topNodes));
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