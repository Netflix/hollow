package com.netflix.vms.transformer.testutil.slice;

import com.netflix.hollow.combine.HollowCombiner;
import com.netflix.hollow.read.engine.HollowBlobReader;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.util.StateEngineRoundTripper;
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
import com.netflix.vms.transformer.testutil.migration.ShowMeTheProgressDiffTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;

public class TitleLevelPinningPOC {
    private static final String VIP = "boson";
    private static final String BASE_PROXY = VMSInputDataClient.TEST_PROXY_URL;
    private static final String LOCAL_BLOB_STORE = "/space/title-pinning";

    private final String converterVip;
    private final String proxyURL;
    private final String localBlobStore;
    private final VMSTransformerWriteStateEngine outputStateEngine;
    private final SimpleTransformerContext ctx;

    public TitleLevelPinningPOC(String converterVip, String proxyURL, String localBlobStore) {
        this.converterVip = converterVip;
        this.proxyURL = proxyURL;
        this.localBlobStore = localBlobStore;
        mkdir(this.localBlobStore);

        outputStateEngine = new VMSTransformerWriteStateEngine();
        ctx = new SimpleTransformerContext();
    }

    /**
     * Process specified topNode for input data version
     *
     * @return the file pointing to the processed data
     */
    public File process(Long inputDataVersion, int topNode) throws Throwable {
        File slicedOutputFile = createSlicedFile("ouput", inputDataVersion, topNode);
        if (slicedOutputFile.exists()) {
            System.out.println(String.format("Skipping vip=%s, version=%s, videoIds=%s [output file exists=%s]", converterVip, inputDataVersion, topNode, slicedOutputFile));
            return slicedOutputFile;
        }

        long start = System.currentTimeMillis();
        System.out.println(String.format("Processing vip=%s, version=%s, videoIds=%s", converterVip, inputDataVersion, topNode));
        File slicedInputFile = createSlicedFile("input", inputDataVersion, topNode);
        HollowReadStateEngine inputStateEngineSlice = fetchStateEngineSlice(slicedInputFile, inputDataVersion, topNode);

        VMSHollowInputAPI api = new VMSHollowInputAPI(inputStateEngineSlice);
        VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();
        new SimpleTransformer(api, outputStateEngine, ctx).transform();
        writeStateEngine(outputStateEngine, slicedOutputFile);

        System.out.println(String.format("***** \t Processed in=%s, out=%s, duration=%s", slicedInputFile, slicedOutputFile, (System.currentTimeMillis() - start)));
        return slicedOutputFile;
    }

    // -- DEBUG API
    public static void main(String[] args) throws Throwable {
        long start = System.currentTimeMillis();

        //// PROCESSING
        TitleLevelPinningPOC pinner = new TitleLevelPinningPOC(VIP, BASE_PROXY, LOCAL_BLOB_STORE);

        // Wonder Man
        File out1 = pinner.process(20160711163815697L, 1133891);

        // HoC
        File out2 = pinner.process(20160711163815697L, 70178217);

        // Chelsea
        File out3 = pinner.process(20160711162231381L, 80049872);

        System.out.println("ALL PROCESSING DONE, duration=" + (System.currentTimeMillis() - start));


        //// DIFFING
        {
            System.out.println("\n\n============================\nDIFFING\n");
            start = System.currentTimeMillis();
            HollowReadStateEngine baseState = new HollowReadStateEngine();
            baseState.setHeaderTags(Collections.singletonMap("vip", "baseline"));

            HollowReadStateEngine state1 = readStateEngine(out1);
            state1.setHeaderTags(Collections.singletonMap("vip", "Wonder Man"));
            startTheDiff(baseState, state1);

            HollowReadStateEngine state2 = readStateEngine(out2);
            state2.setHeaderTags(Collections.singletonMap("vip", "HoC"));
            startTheDiff(state1, state2);

            HollowReadStateEngine state3 = readStateEngine(out3);
            state3.setHeaderTags(Collections.singletonMap("vip", "Chelsea"));
            startTheDiff(state2, state3);

            System.out.println("ALL DIFFING DONE, duration=" + (System.currentTimeMillis() - start));
        }

        System.out.flush();
    }

    public static Thread startTheDiff(final HollowReadStateEngine fromState, final HollowReadStateEngine actual) throws Exception {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    HollowReadStateEngine toState = actual;

                    if (!fromState.getSchemas().isEmpty()) {
                        HollowCombiner combiner = new HollowCombiner(fromState, actual);
                        combiner.addIgnoredTypes(HollowCombinerWithNamedList.NAMEDLIST_TYPE_STATE_NAME);
                        combiner.combine();
                        new HollowCombinerWithNamedList(combiner.getCombinedStateEngine(), fromState, actual).combine();
                        HollowWriteStateEngine combinedStateEngine = combiner.getCombinedStateEngine();
                        combinedStateEngine.addHeaderTags(actual.getHeaderTags());
                        toState = StateEngineRoundTripper.roundTripSnapshot(combinedStateEngine);
                    }

                    output(toState);
                    ShowMeTheProgressDiffTool.startTheDiff(fromState, toState);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        return thread;
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
    private File createSlicedFile(String type, Long inputDataVersion, int topNode) {
        long version = inputDataVersion != null ? inputDataVersion : System.currentTimeMillis();
        return new File(localBlobStore, "vms.hollow" + type + ".blob." + converterVip + ".slice_" + version + "_" + topNode);
    }

    private HollowReadStateEngine fetchStateEngineSlice(File slicedFile, Long inputDataVersion, int topNode) throws IOException {
        if (!slicedFile.exists()) {
            HollowReadStateEngine inputStateEngine = readInputData(inputDataVersion);

            long start = System.currentTimeMillis();
            DataSlicer slicer = new DataSlicer(0, topNode);
            HollowWriteStateEngine slicedStateEngine = slicer.sliceInputBlob(inputStateEngine);

            writeStateEngine(slicedStateEngine, slicedFile);
            System.out.println(String.format("Sliced videoId=%s from vip=%s, version=%s, duration=", topNode, converterVip, inputDataVersion, (System.currentTimeMillis() - start)));
        }

        return readStateEngine(slicedFile);
    }

    private HollowReadStateEngine readInputData(Long inputDataVersion) throws IOException {
        long start = System.currentTimeMillis();

        // Load Input File
        VMSInputDataClient client = new VMSInputDataClient(proxyURL, localBlobStore, converterVip);
        if (inputDataVersion == null) {
            client.triggerRefresh();
        } else {
            client.triggerRefreshTo(inputDataVersion);
        }

        System.out.println(String.format("readInputData vip=%s, version=%s, duration=%s", converterVip, inputDataVersion, (System.currentTimeMillis() - start)));
        return client.getStateEngine();
    }

    private static HollowReadStateEngine readStateEngine(File inputFile) throws IOException {
        System.out.println("Read StateEngine: " + inputFile);
        HollowReadStateEngine stateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(stateEngine);
        try (LZ4BlockInputStream is = new LZ4BlockInputStream(new FileInputStream(inputFile))) {
            reader.readSnapshot(is);
        }

        return stateEngine;
    }

    private static void writeStateEngine(HollowWriteStateEngine stateEngine, File outputFile) throws IOException {
        System.out.println("Write StateEngine: " + outputFile);
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        try (LZ4BlockOutputStream os = new LZ4BlockOutputStream(new FileOutputStream(outputFile))) {
            writer.writeSnapshot(os);
        }
    }

    private static void mkdir(String dirName) {
        File dir = new File(dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
