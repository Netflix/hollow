package com.netflix.vms.transformer.testutil.slice;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.CyclePinnedTitles;
import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.Dataset.CONVERTER;

import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.generated.notemplate.GlobalVideoHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.transformer.SimpleTransformer;
import com.netflix.vms.transformer.SimpleTransformerContext;
import com.netflix.vms.transformer.VMSTransformerWriteStateEngine;
import com.netflix.vms.transformer.common.input.InputState;
import com.netflix.vms.transformer.common.slice.DataSlicer;
import com.netflix.vms.transformer.input.CycleInputs;
import com.netflix.vms.transformer.input.UpstreamDatasetHolder;
import com.netflix.vms.transformer.input.VMSInputDataClient;
import com.netflix.vms.transformer.override.PinTitleHelper;
import com.netflix.vms.transformer.override.PinTitleHollowCombiner;
import com.netflix.vms.transformer.override.PinTitleManager;
import com.netflix.vms.transformer.publish.workflow.IndexDuplicateChecker;
import com.netflix.vms.transformer.testutil.migration.ShowMeTheProgressDiffTool;
import com.netflix.vms.transformer.util.slice.DataSlicerImpl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import org.junit.Assert;
import org.junit.Test;

public class TitleLevelPinningPOC {
    private static final String BASE_PROXY = VMSInputDataClient.TEST_PROXY_URL;
    private static final String LOCAL_BLOB_STORE = "/space/title-pinning";
    private static final String SLICED_INPUT_FILE = LOCAL_BLOB_STORE + "/fastlane_input-slice";
    private static final String FASTLANE_FILE = LOCAL_BLOB_STORE + "/fastlane_output-slice";
    private static final String SNAPSHOT_FILE = "/space/transformer-data/pinned-blobs/input-snapshot";

    @Test
    public void debug() throws Exception {
        SimpleTransformerContext ctx = new SimpleTransformerContext();
        VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();

        HollowReadStateEngine pinnedInput = loadStateEngine("/space/debug/vms.berlin_output-titleoverride-20160823190912102-70303291.log");
        List<HollowReadStateEngine> pinnedInputs = Collections.singletonList(pinnedInput);

        long[] versions = new long[] { 20160824214830013L, 20160824214900014L, 20160824214931015L };
        while (true) {
            boolean isFirstCycle = true;
            for (long v : versions) {
                HollowReadStateEngine fastlane = loadFastlane(v);

                PinTitleHollowCombiner combiner = new PinTitleHollowCombiner(ctx, outputStateEngine, fastlane, pinnedInputs);
                combiner.combine();
                String overrideBlobID = PinTitleHelper.getBlobID(outputStateEngine);
                String pinnedTitles = PinTitleHelper.getPinnedTitles(outputStateEngine);

                System.out.println(v + ": \t" + overrideBlobID + "\t" + pinnedTitles + "\t hasChanged=" + outputStateEngine.hasChangedSinceLastCycle());
                if (!isFirstCycle && outputStateEngine.hasChangedSinceLastCycle()) {
                    for (HollowTypeWriteState state : outputStateEngine.getOrderedTypeStates()) {
                        if (!isFirstCycle && state.hasChangedSinceLastCycle()) {
                            System.out.println("FASTLANE Type changed=" + state.getSchema().getName());
                        }
                    }

                    System.out.println("CHANGED");
                }

                String blobId = outputStateEngine.getHeaderTag("blobID") + "_" + v;
                outputStateEngine.addHeaderTag("blobID", blobId);

                if (blobId.length() % 100 == 0) {
                    System.out.println(blobId.length() + "\t" + blobId);
                }

                outputStateEngine.prepareForWrite();
                outputStateEngine.prepareForNextCycle();
                isFirstCycle = false;
            }
        }
    }

    public HollowReadStateEngine loadFastlane(long version) throws Exception {
        String fn = "/space/debug/fastlane_" + version + ".log";
        return loadStateEngine(fn);
    }

    @Test
    public void diff() throws Exception {
        HollowReadStateEngine fromState = loadStateEngine(LOCAL_BLOB_STORE + "/vms.berlin_output-titleoverride-20160812090000000_80093198");
        HollowReadStateEngine toState = loadStateEngine(LOCAL_BLOB_STORE + "/vms.berlin_output-titleoverride-20160812090815150_80093198");
        ShowMeTheProgressDiffTool.startTheDiff(fromState, toState);
    }

    @Test
    public void testTitlePinning() throws Throwable {
        final int FASTLANE_ID = 80093198;
        int[] fastlaneIds = new int[] { FASTLANE_ID };

        boolean isLoadSlide = true;
        if (isLoadSlide) {
            // Make sure slice file exist
            File sliceFile = new File(SLICED_INPUT_FILE);
            if (!sliceFile.exists()) {
                HollowReadStateEngine inputStateEngine = loadStateEngine(SNAPSHOT_FILE);
                DataSlicer.SliceTask slicer = new DataSlicerImpl().getSliceTask(0, fastlaneIds);
                HollowWriteStateEngine slicedStateEngine = slicer.sliceInputBlob(inputStateEngine);
                writeStateEngine(slicedStateEngine, sliceFile);
            }
        }

        final String INPUT_FN = isLoadSlide ? SLICED_INPUT_FILE : SNAPSHOT_FILE;
        final VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();
        HollowReadStateEngine inputStateEngine = loadStateEngine(INPUT_FN);

        SimpleTransformerContext ctx = new SimpleTransformerContext();
        ctx.setFastlaneIds(toSet(fastlaneIds));
        // Make sure to pin fastlane id
        //Set<String> overrideTitleSpecs = new HashSet<>(Arrays.asList(FASTLANE_ID + ":20160812090000000"));
        //Set<String> overrideTitleSpecs = new HashSet<>(Arrays.asList(FASTLANE_ID + ":20160812090815150"));
        Set<String> overrideTitleSpecs = new HashSet<>(Arrays.asList("20160915161802282:80097539"));

        {
            PinTitleManager mgr = new PinTitleManager(BASE_PROXY, "boson", "berlin", LOCAL_BLOB_STORE, ctx);
            mgr.submitJobsToProcessASync(overrideTitleSpecs);

            Map<UpstreamDatasetHolder.Dataset, InputState> inputs = new HashMap<>();
            inputs.put(CONVERTER, new InputState(inputStateEngine, 1l));   // TODO: Add all Cinder inputs
            CycleInputs cycleInputs = new CycleInputs(inputs, 1l);

            VMSTransformerWriteStateEngine fastlaneOutput = new VMSTransformerWriteStateEngine();
            SimpleTransformer transformer = new SimpleTransformer(cycleInputs, fastlaneOutput, ctx);
            transformer.transform();
            PinTitleHelper.addBlobID(fastlaneOutput, "FASTLANE");
            writeStateEngine(fastlaneOutput, new File(FASTLANE_FILE));

            List<HollowReadStateEngine> overrideTitleOutputs = mgr.getResults(true);

            PinTitleHollowCombiner combiner = new PinTitleHollowCombiner(ctx, outputStateEngine, fastlaneOutput, overrideTitleOutputs);
            combiner.combine();
            HollowReadStateEngine fastlaneBlob = loadStateEngine(FASTLANE_FILE);
            HollowReadStateEngine combinedBlob = roundTrip(outputStateEngine);

            String blobID = PinTitleHelper.getBlobID(combinedBlob);
            String pinnedTitles = PinTitleHelper.getPinnedTitles(combinedBlob);
            ctx.getLogger().info(CyclePinnedTitles, "Processed override titles={} blodId={} pinnedTitles={}", overrideTitleSpecs, blobID, pinnedTitles);

            validateAndDiff(fastlaneBlob, combinedBlob);
        }
    }

    private static Set<Integer> toSet(int... ids) {
        Set<Integer> set = new HashSet<>();
        for (int id : ids)
            set.add(id);
        return set;
    }

    private HollowReadStateEngine loadStateEngine(String resourceFilename) throws IOException {
        FileInputStream fio = new FileInputStream(new File(resourceFilename));
        InputStream is = new LZ4BlockInputStream(fio);

        HollowReadStateEngine stateEngine = new HollowReadStateEngine(WastefulRecycler.DEFAULT_INSTANCE);
        HollowBlobReader reader = new HollowBlobReader(stateEngine);
        reader.readSnapshot(is);

        return stateEngine;
    }


    public static HollowReadStateEngine roundTrip(HollowWriteStateEngine writeEngine) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        writer.writeSnapshot(baos);

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        reader.readSnapshot(is);

        return readEngine;
    }

    protected void writeStateEngine(HollowWriteStateEngine stateEngine, File outputFile) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        try (LZ4BlockOutputStream os = new LZ4BlockOutputStream(new FileOutputStream(outputFile))) {
            writer.writeSnapshot(os);
        }
    }

    public static void validateAndDiff(final HollowReadStateEngine fromState, final HollowReadStateEngine toState) throws Exception {

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // out summary
                    Set<Integer> videoIdFromState = output("fromState", fromState);
                    Set<Integer> videoIdToState = output("toState", toState);
                    printDiffSummary(videoIdFromState, videoIdToState);

                    // make sure there is no dups
                    IndexDuplicateChecker dupChecker = new IndexDuplicateChecker(toState);
                    dupChecker.checkDuplicates();
                    Assert.assertFalse("Duplicate keys found: " + dupChecker.getResults().keySet(), dupChecker.wasDupKeysDetected());

                    // lauch diff report
                    ShowMeTheProgressDiffTool.startTheDiff(fromState, toState);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        thread.join();
    }

    public static Set<Integer> output(String label, HollowReadStateEngine readStateEngine) throws IOException {
        VMSRawHollowAPI api = new VMSRawHollowAPI(readStateEngine);

        System.out.println("\n[" + label + "]");
        Set<Integer> videoIds = new HashSet<>();
        for (GlobalVideoHollow gv : api.getAllGlobalVideoHollow()) {
            Integer videoID = gv._getCompleteVideo()._getId()._getValue();
            videoIds.add(videoID);
            System.out.println("\t " + videoIds.size() + ") videoOrdinal=" + gv.getOrdinal() + "\t videoId=" + videoID);
        }

        System.out.println("***** \t Video Size=" + videoIds.size() + "\t Person Size=" + api.getAllGlobalPersonHollow().size());
        System.out.flush();
        return videoIds;
    }

    private static void printDiffSummary(Set<Integer> from, Set<Integer> to) {
        Set<Integer> missing = new HashSet<>(from);
        missing.removeAll(to);

        Set<Integer> extra = new HashSet<>(to);
        extra.removeAll(from);

        System.out.println("Video Diff Summary:"
                + "\n\t missing - (" + missing.size() + ") : " + missing
                + "\n\t extra   - (" + extra.size() + ") : " + extra);
    }

    // ------
}
