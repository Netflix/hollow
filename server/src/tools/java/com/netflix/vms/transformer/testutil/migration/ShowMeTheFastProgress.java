package com.netflix.vms.transformer.testutil.migration;

import static com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.DatasetIdentifier.CONVERTER;
import static com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.DatasetIdentifier.GATEKEEPER2;
import static com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.INPUT_VERSION_KEY_PREFIX;
import static com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.UpstreamDatasetConfig.getNamespacesforEnv;
import static com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.UpstreamDatasetConfig.lookupDatasetForNamespace;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Inject;
import com.netflix.cinder.consumer.CinderConsumerBuilder;
import com.netflix.cinder.lifecycle.CinderConsumerModule;
import com.netflix.governator.guice.test.ModulesForTesting;
import com.netflix.governator.guice.test.junit4.GovernatorJunit4ClassRunner;
import com.netflix.gutenberg.s3access.S3Direct;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.runtime.lifecycle.RuntimeCoreModule;
import com.netflix.vms.transformer.DynamicBusinessLogic;
import com.netflix.vms.transformer.SimpleTransformer;
import com.netflix.vms.transformer.SimpleTransformerContext;
import com.netflix.vms.transformer.VMSTransformerWriteStateEngine;
import com.netflix.vms.transformer.common.api.BusinessLogicAPI;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.input.InputState;
import com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition;
import com.netflix.vms.transformer.http.HttpHelper;
import com.netflix.vms.transformer.common.input.CycleInputs;
import com.netflix.vms.transformer.input.datasets.slicers.GlobalVideoBasedSelector;
import com.netflix.vms.transformer.override.InputSlicePinTitleProcessor;
import com.netflix.vms.transformer.override.OutputSlicePinTitleProcessor;
import com.netflix.vms.transformer.override.PinTitleHelper;
import com.netflix.vms.transformer.override.PinTitleProcessor.TYPE;
import com.netflix.vms.transformer.util.OutputUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GovernatorJunit4ClassRunner.class)
@ModulesForTesting({CinderConsumerModule.class, RuntimeCoreModule.class})
public class ShowMeTheFastProgress {
    private static final boolean isProd = false;
    private static final boolean isPerformDiff = true;
    private static final boolean isUseRemotePinTitleSlicer = true;
    private static final boolean isFallBackToLocalSlicer = false;

    private static final String OUTPUT_NAMESPACE = "vms-vmsdev_sunjeetsn";
    private static final String VMSSLICER_INSTANCE = "vmstransformer-vmsdev_sunjeetsn_override-muon";

    private static final String WORKING_DIR = "/space/transformer-data/fast";
    private static final String REMOTE_SLICER_URL = "http://discovery.cloudqa.netflix.net:7001/discovery/resolver/cluster/" + VMSSLICER_INSTANCE + ":7101/REST/vms/pintitleslicer"; // NOTE: SLICER must be in TEST env hence cloudqa
    private static final String PUBLISH_CYCLE_DATATS_HEADER = "publishCycleDataTS";

    @Inject
    private Supplier<CinderConsumerBuilder> cinderConsumerBuilder;

    @Inject
    private DynamicBusinessLogic dynamicLogic;

    @Inject
    private S3Direct s3Direct;


    @Before
    public void setup() throws Exception {
        File workingDir = new File(WORKING_DIR);
        if (!workingDir.exists()) workingDir.mkdirs();
    }

    @Test
    public void diffTwoVips() throws Throwable {
        // NOTE: the specified transformerVersion must be valid or already in local HD; otherwise, run  getLatestTransformerVersion();
        String FROM_NAMESPACE = "vms-vmsdev_sunjeetsn_override";
        String TO_NAMESPACE = "vms-vmsdev_sunjeetsn_override";

        long FROM_VER = 20190625235018013l;
        long TO_VER = 20190625235433014l;
        int[] topNodes = { 80191680 };

        long start = System.currentTimeMillis();
        try {
            SimpleTransformerContext ctx = new SimpleTransformerContext();
            HollowReadStateEngine from_ReadStateEngine = loadTransformerEngine(ctx, FROM_NAMESPACE, FROM_VER, topNodes);
            HollowReadStateEngine to_ReadStateEngine = loadTransformerEngine(ctx, TO_NAMESPACE, TO_VER, topNodes);
            ShowMeTheProgressDiffTool.startTheDiff(to_ReadStateEngine, from_ReadStateEngine);
        } finally {
            trackDuration(start, "diffTwoVips: %s:%s vs %s:%s - topNodes=%s", FROM_NAMESPACE, FROM_VER, TO_NAMESPACE, TO_VER, toString(topNodes));
        }
    }

    @Test
    public void runFastLaneAndDiff() throws Throwable {
        // NOTE: the specified transformerVersion must be valid or already in local HD; otherwise, run  getLatestTransformerVersion();
        long transformerVersion = 20190724182654073l;
        int[] topNodes = { 80133542 };

        long start = System.currentTimeMillis();

        // Setup Context
        SimpleTransformerContext ctx = new SimpleTransformerContext();
        //ctx.overrideSupportedCountries("US");

        // Load Expected state engines
        HollowReadStateEngine expectedOutputStateEngine = loadTransformerEngine(ctx, OUTPUT_NAMESPACE, transformerVersion, topNodes);

        // Get all videoIds constituting the topNodeIds in the output state engine, for input slicing.
        GlobalVideoBasedSelector videoSelector = new GlobalVideoBasedSelector(expectedOutputStateEngine);
        Set<Integer> videoIDs = videoSelector.findVideosForTopNodes(0, topNodes);

        String value = expectedOutputStateEngine.getHeaderTag(PUBLISH_CYCLE_DATATS_HEADER);
        long publishCycleDataTS = value != null ? Long.parseLong(value) : System.currentTimeMillis();
        Map<UpstreamDatasetDefinition.DatasetIdentifier, Long> inputVersions = getInputVersionsFromStateEngine(expectedOutputStateEngine);

        CycleInputs cycleInputs = getCycleInputs(inputVersions, ctx,
                                                 videoIDs.stream().mapToInt(Integer::intValue).toArray());

        // Setup Fastlane context and Output State Engine
        List<Integer> fastlaneIds = Arrays.stream(topNodes).boxed().collect(Collectors.toList());
        ctx.setFastlaneIds(new HashSet<>(fastlaneIds));
        VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();
        outputStateEngine.addHeaderTags(expectedOutputStateEngine.getHeaderTags());
        outputStateEngine.addHeaderTag(PUBLISH_CYCLE_DATATS_HEADER, String.valueOf(publishCycleDataTS));

        // Run Transformer
        SimpleTransformer transformer = new SimpleTransformer();
        ctx.setNowMillis(publishCycleDataTS);
        transformer.transform(cycleInputs, outputStateEngine, ctx);
        HollowReadStateEngine actualOutputReadStateEngine = roundTripOutputStateEngine(outputStateEngine);
        trackDuration(start, "Done transformerVersion=%s, topNodes=%s", transformerVersion, toString(topNodes));

        // Do Diff
        if (isPerformDiff) {
            ShowMeTheProgressDiffTool.startTheDiff(expectedOutputStateEngine, actualOutputReadStateEngine);
        }
    }

    @Test
    public void runFastLaneWithSpecificInputVersions() throws Throwable {
        // NOTE: the specified converterVersion must be valid or already in local HD; otherwise, run  getLatestConverterVersion();
        int[] videoIDs = { 80104350 };  // NOTE: videoIDs corresponding to topNodes below
        int[] topNodeIDs = { 80104350 };

        // NOTE: This map might not be up to date. There should be entries for all Cinder inputs
        Map<UpstreamDatasetDefinition.DatasetIdentifier, Long> inputVersions = new HashMap<>();
        inputVersions.put(CONVERTER, 20190619233752482l);
        inputVersions.put(GATEKEEPER2, 20190619233729375l);

        // Setup Context
        SimpleTransformerContext ctx = new SimpleTransformerContext();
        //ctx.overrideSupportedCountries("US");
        CycleInputs cycleInputs = getCycleInputs(inputVersions, ctx, videoIDs); // NOTE: Passing videoIDs not topNodes here

        // Setup Fastlane context and Output State Engine
        List<Integer> fastlaneIds = Arrays.stream(topNodeIDs).boxed().collect(Collectors.toList());
        ctx.setFastlaneIds(new HashSet<>(fastlaneIds));
        VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();

        new SimpleTransformer().transform(cycleInputs, outputStateEngine, ctx);
        HollowReadStateEngine actualOutputReadStateEngine = roundTripOutputStateEngine(outputStateEngine);
        System.out.println(actualOutputReadStateEngine.getHeaderTags());
    }

    private Map<UpstreamDatasetDefinition.DatasetIdentifier, Long> getInputVersionsFromStateEngine(HollowReadStateEngine stateEngine) {
        Map<UpstreamDatasetDefinition.DatasetIdentifier, Long> inputVersions = new HashMap<>();
        final BiMap<UpstreamDatasetDefinition.DatasetIdentifier, String> namespaces = HashBiMap.create(getNamespacesforEnv(isProd));
        stateEngine.getHeaderTags().forEach((k,v) -> {
            if (k.startsWith(INPUT_VERSION_KEY_PREFIX) && k.length() != INPUT_VERSION_KEY_PREFIX.length()) {
                String namespace = k.substring(INPUT_VERSION_KEY_PREFIX.length());
                inputVersions.put(lookupDatasetForNamespace(namespace, isProd), Long.valueOf(v));
            }
        });
        if (inputVersions.size() != namespaces.size()) {
            String msg = "There was a mismatch in inputs expected of the env and inputs present in the state engine "
                    + "(expected=" + namespaces.size() + ", actual=" + inputVersions.size() + ").";
            System.out.println("FATAL: " + msg);
            throw new IllegalArgumentException(msg);
        }
        return inputVersions;
    }

    // Load Transformer inputs based on input versions
    private CycleInputs getCycleInputs(Map<UpstreamDatasetDefinition.DatasetIdentifier, Long> inputVersions, SimpleTransformerContext ctx, int... videoIDs)
            throws Throwable{

        Map<UpstreamDatasetDefinition.DatasetIdentifier, InputState> inputs = new ConcurrentHashMap<>();
        SimultaneousExecutor executor = new SimultaneousExecutor(5, ShowMeTheFastProgress.class.getName());
        for (UpstreamDatasetDefinition.DatasetIdentifier datasetIdentifier : inputVersions.keySet()) {
            executor.execute(() -> {
                try {
                    HollowReadStateEngine stateEngine = loadInputState(ctx, getNamespacesforEnv(isProd).get(
                            datasetIdentifier),
                            inputVersions.get(datasetIdentifier), videoIDs);
                    inputs.put(datasetIdentifier, new InputState(stateEngine, inputVersions.get(datasetIdentifier)));
                } catch (Throwable t) {}
            });
        }
        executor.awaitSuccessfulCompletion();

        return new CycleInputs(inputs, 1l);
    }

    private static void downloadSlice(File downloadTo, String baseURL, boolean isProd, boolean isOutput, String namespace, long version, int... topNodes) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        String proxyURL = String.format("%s?prod=%s&output=%s&namespace=%s&version=%s&topnodes=%s", baseURL, isProd, isOutput, namespace, version, toString(topNodes));
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
    private HollowReadStateEngine loadTransformerEngine(TransformerContext ctx, String outputNamespace, long version, int... topNodes) throws Throwable {
        System.out.println("loadTransformerEngine: Loading version=" + version);
        long start = System.currentTimeMillis();
        try {
            DynamicBusinessLogic.CurrentBusinessLogicHolder logicAndMetadata = dynamicLogic.getLogicAndMetadata();
            BusinessLogicAPI businessLogic = logicAndMetadata.getLogic();
            OutputSlicePinTitleProcessor processor = new OutputSlicePinTitleProcessor(cinderConsumerBuilder, s3Direct,
                    outputNamespace, WORKING_DIR, isProd, ctx, businessLogic);
            File slicedFile = processor.getFile(outputNamespace, TYPE.OUTPUT, version, topNodes);
            if (isUseRemotePinTitleSlicer && !slicedFile.exists()) {
                try {
                    downloadSlice(slicedFile, REMOTE_SLICER_URL, isProd, true, outputNamespace, version, topNodes);
                    return processor.readStateEngine(slicedFile);
                } catch (Exception ex) {
                    System.err.println("ERROR: Remote Slicer failure - " + ex.toString());
                    if (isFallBackToLocalSlicer) System.out.println("Failling back to local slicer."); else throw ex;
                }
            }
            return processor.process(version, topNodes);
        } finally {
            trackDuration(start, "loadTransformerEngine: outputNamespace=%s, version=%s, topNodes=%s", outputNamespace, version, toString(topNodes));
        }
    }

    @SuppressWarnings("unused")
    private HollowReadStateEngine loadInputState(TransformerContext ctx, String inputNamespace, long inputDataVersion, int... videoIDs) throws Throwable {
        System.out.println("loadInputState: Loading inputNamespace=" + inputNamespace + " version=" + inputDataVersion);
        long start = System.currentTimeMillis();
        try {
            HollowReadStateEngine stateEngine;
            DynamicBusinessLogic.CurrentBusinessLogicHolder logicAndMetadata = dynamicLogic.getLogicAndMetadata();
            BusinessLogicAPI businessLogic = logicAndMetadata.getLogic();
            InputSlicePinTitleProcessor processor = new InputSlicePinTitleProcessor(cinderConsumerBuilder, s3Direct,
                    inputNamespace, WORKING_DIR, isProd, ctx, businessLogic);
            File slicedFile = processor.getFile(inputNamespace, TYPE.INPUT, inputDataVersion, videoIDs);
            if (isUseRemotePinTitleSlicer && !slicedFile.exists()) {
                try {
                    downloadSlice(slicedFile, REMOTE_SLICER_URL, isProd, false, inputNamespace, inputDataVersion, videoIDs);
                } catch (Exception ex) {
                    System.err.println("ERROR: Remote Slicer failure - " + ex.toString());
                    if (isFallBackToLocalSlicer) System.out.println("Failling back to local slicer."); else throw ex;
                }
            }

            if (!slicedFile.exists()) slicedFile = processor.process(TYPE.INPUT, inputDataVersion, videoIDs);
            stateEngine = processor.readStateEngine(slicedFile);

            return stateEngine;
        } finally {
            trackDuration(start, "loadInputState: inputNamespace=%s, inputDataVersion=%s, videoIDs=%s",
                    inputNamespace, inputDataVersion, toString(videoIDs));
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