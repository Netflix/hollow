package com.netflix.vms.transformer.testutil.slice;

import com.netflix.hollow.read.engine.HollowBlobReader;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.vms.generated.notemplate.CompleteVideoHollow;
import com.netflix.vms.generated.notemplate.GlobalPersonHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.transformer.SimpleTransformerContext;
import com.netflix.vms.transformer.VMSTransformerWriteStateEngine;
import com.netflix.vms.transformer.input.VMSInputDataClient;
import com.netflix.vms.transformer.override.OverrideHollowCombiner;
import com.netflix.vms.transformer.override.InputSliceTitleOverrideProcessor;
import com.netflix.vms.transformer.override.OutputSliceTitleOverrideProcessor;
import com.netflix.vms.transformer.override.TitleOverrideProcessor;
import com.netflix.vms.transformer.testutil.migration.ShowMeTheProgressDiffTool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TitleLevelPinningPOC {
    private static final String VIP = "boson";
    private static final String BASE_PROXY = VMSInputDataClient.TEST_PROXY_URL;
    private static final String LOCAL_BLOB_STORE = "/space/title-pinning";

    private final String converterVip;
    private final String proxyURL;
    private final String localBlobStore;
    private static final VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();
    private final SimpleTransformerContext ctx;
    private final boolean isInputBased;

    public TitleLevelPinningPOC(String converterVip, String proxyURL, String localBlobStore, boolean isInputBased) {
        this.converterVip = converterVip;
        this.localBlobStore = localBlobStore;
        this.proxyURL = proxyURL;
        this.isInputBased = isInputBased;

        ctx = new SimpleTransformerContext();
    }

    /**
     * Process specified topNode for input data version
     *
     * @return the file pointing to the processed data
     */
    public HollowReadStateEngine process(Long dataVersion, int topNode) throws Throwable {
        TitleOverrideProcessor processor = isInputBased
                ? new InputSliceTitleOverrideProcessor("boson", proxyURL, localBlobStore, ctx)
                        : new OutputSliceTitleOverrideProcessor("berlin", proxyURL, localBlobStore, ctx);

                return processor.process(dataVersion, topNode);
    }

    // -- DEBUG API
    public static void main(String[] args) throws Throwable {
        long start = System.currentTimeMillis();

        //// PROCESSING
        TitleLevelPinningPOC pinner = new TitleLevelPinningPOC(VIP, BASE_PROXY, LOCAL_BLOB_STORE, false);

        // Wonder Man
        HollowReadStateEngine out1 = pinner.process(20160715180125072L, 1133891);

        // HoC
        HollowReadStateEngine out2 = pinner.process(20160715180125072L, 70178217);

        // Chelsea
        HollowReadStateEngine out3 = pinner.process(20160715190125076L, 80049872);

        System.out.println("ALL PROCESSING DONE, duration=" + (System.currentTimeMillis() - start));


        //// DIFFING
        {
            System.out.println("\n\n============================\nDIFFING\n");
            start = System.currentTimeMillis();
            HollowReadStateEngine baseState = new HollowReadStateEngine();
            baseState.setHeaderTags(Collections.singletonMap("vip", "baseline"));

            HollowReadStateEngine state1 = combine(outputStateEngine, out1);
            state1.setHeaderTags(Collections.singletonMap("vip", "Wonder Man"));
            startTheDiff(baseState, state1);

            HollowReadStateEngine state2 = combine(outputStateEngine, out2);
            state2.setHeaderTags(Collections.singletonMap("vip", "HoC"));
            startTheDiff(state1, state2);

            HollowReadStateEngine state3 = combine(outputStateEngine, out3);
            state3.setHeaderTags(Collections.singletonMap("vip", "Chelsea"));
            startTheDiff(state2, state3);

            System.out.println("ALL DIFFING DONE, duration=" + (System.currentTimeMillis() - start));
        }

        System.out.flush();
    }

    public static HollowReadStateEngine combine(HollowWriteStateEngine output, HollowReadStateEngine input) throws Exception {
        OverrideHollowCombiner combiner = new OverrideHollowCombiner(output, input);

        combiner.combine();
        output.addHeaderTags(input.getHeaderTags());
        HollowReadStateEngine state = roundTripSnapshot(output);
        output.prepareForNextCycle();
        output.addAllObjectsFromPreviousCycle();

        return state;
    }

    public static HollowReadStateEngine roundTripSnapshot(HollowWriteStateEngine writeEngine) throws IOException {
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
