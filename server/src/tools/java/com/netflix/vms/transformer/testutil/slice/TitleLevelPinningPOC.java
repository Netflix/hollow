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
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.input.VMSInputDataClient;
import com.netflix.vms.transformer.override.TitleOverrideHelper;
import com.netflix.vms.transformer.override.TitleOverrideHollowCombiner;
import com.netflix.vms.transformer.override.TitleOverrideManager;
import com.netflix.vms.transformer.testutil.migration.ShowMeTheProgressDiffTool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import net.jpountz.lz4.LZ4BlockInputStream;

public class TitleLevelPinningPOC {
    private static final String BASE_PROXY = VMSInputDataClient.TEST_PROXY_URL;
    private static final String LOCAL_BLOB_STORE = "/space/title-pinning";

    @Test
    public void testTitlePinning() throws Throwable {
        final String INPUT_FN = "/space/transformer-data/pinned-blobs/input-snapshot";
        final VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();

        HollowReadStateEngine inputStateEngine = loadStateEngine(INPUT_FN);
        VMSHollowInputAPI api = new VMSHollowInputAPI(inputStateEngine);

        SimpleTransformerContext ctx = new SimpleTransformerContext();
        Set<String> overrideTitleSpecs = Collections.singleton("80049872:20160716152214023");
        ctx.setFastlaneIds(new HashSet<>(Arrays.asList(80115503)));

        {
            TitleOverrideManager mgr = new TitleOverrideManager(BASE_PROXY, "boson", "berlin", LOCAL_BLOB_STORE, ctx);
            mgr.processASync(overrideTitleSpecs);

            VMSTransformerWriteStateEngine fastlaneOutput = new VMSTransformerWriteStateEngine();
            SimpleTransformer transformer = new SimpleTransformer(api, fastlaneOutput, ctx);
            transformer.transform();

            List<HollowReadStateEngine> overrideTitleOutputs = mgr.waitForResults();

            TitleOverrideHollowCombiner combiner = new TitleOverrideHollowCombiner(ctx, outputStateEngine, fastlaneOutput, overrideTitleOutputs);
            combiner.combine();

            String blobID = TitleOverrideHelper.getBlobID(roundTrip(outputStateEngine));
            ctx.getLogger().info(TitleOverride, "Processed override titles={} blodId={}", overrideTitleSpecs, blobID);
        }
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
