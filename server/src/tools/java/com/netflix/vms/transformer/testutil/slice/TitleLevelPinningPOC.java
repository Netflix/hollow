package com.netflix.vms.transformer.testutil.slice;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.TitleOverride;

import com.netflix.hollow.read.engine.HollowBlobReader;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.util.memory.WastefulRecycler;
import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.vms.generated.notemplate.CompleteVideoHollow;
import com.netflix.vms.generated.notemplate.GlobalPersonHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.transformer.SimpleTransformer;
import com.netflix.vms.transformer.SimpleTransformerContext;
import com.netflix.vms.transformer.VMSTransformerWriteStateEngine;
import com.netflix.vms.transformer.common.slice.DataSlicer;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.input.VMSInputDataClient;
import com.netflix.vms.transformer.override.TitleOverrideHelper;
import com.netflix.vms.transformer.override.TitleOverrideHollowCombiner;
import com.netflix.vms.transformer.override.TitleOverrideManager;
import com.netflix.vms.transformer.testutil.migration.ShowMeTheProgressDiffTool;
import com.netflix.vms.transformer.util.slice.DataSlicerImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;

public class TitleLevelPinningPOC {
    private static final String BASE_PROXY = VMSInputDataClient.TEST_PROXY_URL;
    private static final String LOCAL_BLOB_STORE = "/space/title-pinning";
    private static final String SNAPSHOT_FILE = "/space/transformer-data/pinned-blobs/input-snapshot";
    private static final String SLICE_FILE = "/space/transformer-data/pinned-blobs/input-slice";

    @Test
    public void testTitlePinning() throws Throwable {
        int[] fastlaneIds = new int[] { 80114758, 80118084, 80118085, 80118177, 80093198, 80116111, 80113225, 80110487, 80119317, 88000778, 80118387, 70136120, 80115931 };

        boolean isLoadSlide = true;
        if (isLoadSlide) {
            // Make sure slice file exist
            File sliceFile = new File(SLICE_FILE);
            if (!sliceFile.exists()) {
                HollowReadStateEngine inputStateEngine = loadStateEngine(SNAPSHOT_FILE);
                DataSlicer.SliceTask slicer = new DataSlicerImpl().getSliceTask(0, fastlaneIds);
                HollowWriteStateEngine slicedStateEngine = slicer.sliceInputBlob(inputStateEngine);
                writeStateEngine(slicedStateEngine, sliceFile);
            }
        }

        final String INPUT_FN = isLoadSlide ? SLICE_FILE : SNAPSHOT_FILE;
        final VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();
        HollowReadStateEngine inputStateEngine = loadStateEngine(INPUT_FN);
        VMSHollowInputAPI api = new VMSHollowInputAPI(inputStateEngine);

        SimpleTransformerContext ctx = new SimpleTransformerContext();
        ctx.setFastlaneIds(toSet(fastlaneIds));
        Set<String> overrideTitleSpecs = Collections.singleton("80123716:20160719171337051");

        {
            TitleOverrideManager mgr = new TitleOverrideManager(BASE_PROXY, "boson", "berlin", LOCAL_BLOB_STORE, ctx);
            mgr.processASync(overrideTitleSpecs);

            VMSTransformerWriteStateEngine fastlaneOutput = new VMSTransformerWriteStateEngine();
            SimpleTransformer transformer = new SimpleTransformer(api, fastlaneOutput, ctx);
            transformer.transform();
            TitleOverrideHelper.addBlobID(fastlaneOutput, "FASTLANE");

            List<HollowReadStateEngine> overrideTitleOutputs = mgr.waitForResults();

            TitleOverrideHollowCombiner combiner = new TitleOverrideHollowCombiner(ctx, outputStateEngine, fastlaneOutput, overrideTitleOutputs);
            combiner.combine();
            HollowReadStateEngine combinedBlob = roundTrip(outputStateEngine);

            String blobID = TitleOverrideHelper.getBlobID(combinedBlob);
            ctx.getLogger().info(TitleOverride, "Processed override titles={} blodId={}", overrideTitleSpecs, blobID);
            ShowMeTheProgressDiffTool.startTheDiff(roundTrip(fastlaneOutput), combinedBlob);
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

    public static void startTheDiff(final HollowReadStateEngine fromState, final HollowReadStateEngine toState) throws Exception {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    output(toState);
                    ShowMeTheProgressDiffTool.startTheDiff(fromState, toState);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public static void output(HollowReadStateEngine readStateEngine) throws IOException {
        VMSRawHollowAPI api = new VMSRawHollowAPI(readStateEngine);

        Set<Integer> videoIds = new HashSet<>();
        for (CompleteVideoHollow completeVideoHollow : api.getAllCompleteVideoHollow()) {
            if (!"US".equals(completeVideoHollow._getCountry()._getId())) continue;

            videoIds.add(completeVideoHollow._getId()._getValue());
            System.out.println("\t " + videoIds.size() + ") videoOrdinal=" + completeVideoHollow.getOrdinal() + "\t videoId=" + completeVideoHollow._getId()._getValue());
        }

        int i = 0;
        for (GlobalPersonHollow p : api.getAllGlobalPersonHollow()) {
            i++;
        }

        System.out.println("***** \t Video Size=" + videoIds.size() + "\t Person Size=" + i);
        System.out.flush();
    }

    // ------
}
