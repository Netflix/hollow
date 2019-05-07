package com.netflix.vms.transformer.testutil.slice;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.CyclePinnedTitles;

import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.generated.notemplate.GlobalVideoHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.transformer.SimpleTransformer;
import com.netflix.vms.transformer.SimpleTransformerContext;
import com.netflix.vms.transformer.VMSTransformerWriteStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.slice.DataSlicer;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import org.junit.Assert;
import org.junit.Test;

public class PinTitleTest {
    private static final String LOCAL_BLOB_STORE = "/space/title-pinning";

    private Environment env = Environment.PROD;
    private static boolean reuseSliceFiles = true;

    @Test
    public void testTitlePinning() throws Throwable {
        final String CONVERTER_VIP = "muon";
        final String TRANSFORMER_VIP = "feather";
        final long version = 20180205231304559L;
        final int PINTITLE_ID = 80167498;
        final int FASTLANE_ID = 80049872;

        int[] fastlaneIds = new int[] { FASTLANE_ID };
        Set<String> pinTitleSpecs = Collections.singleton(String.format("%s:%s", version, PINTITLE_ID));
        SimpleTransformerContext ctx = new SimpleTransformerContext();
        ctx.setFastlaneIds(toSet(fastlaneIds));
        ctx.setPinTitleSpecs(pinTitleSpecs);

        // Fetch Input State Engine
        HollowReadStateEngine inputStateEngine = fetchInputStateEngine(CONVERTER_VIP, 20180205231110642L, FASTLANE_ID, PINTITLE_ID);
        VMSHollowInputAPI api = new VMSHollowInputAPI(inputStateEngine);

        {
            PinTitleManager mgr = new PinTitleManager(env.proxyURL, CONVERTER_VIP, TRANSFORMER_VIP, LOCAL_BLOB_STORE, ctx);
            mgr.submitJobsToProcessASync(pinTitleSpecs);

            final VMSTransformerWriteStateEngine fastlaneOutput = new VMSTransformerWriteStateEngine();
            SimpleTransformer transformer = new SimpleTransformer(api, null, fastlaneOutput, ctx);
            transformer.transform();
            PinTitleHelper.addBlobID(fastlaneOutput, "FASTLANE");

            List<HollowReadStateEngine> overrideTitleOutputs = mgr.getResults(true);
            final VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();
            PinTitleHollowCombiner combiner = new PinTitleHollowCombiner(ctx, outputStateEngine, fastlaneOutput, overrideTitleOutputs);
            combiner.combine();

            HollowReadStateEngine fastlaneStateEngine = roundTrip(fastlaneOutput);
            HollowReadStateEngine pinTitleStateEngine = roundTrip(outputStateEngine);

            String blobID = PinTitleHelper.getBlobID(pinTitleStateEngine);
            String pinnedTitles = PinTitleHelper.getPinnedTitles(pinTitleStateEngine);
            ctx.getLogger().info(CyclePinnedTitles, "Processed override titles={} blodId={} pinnedTitles={}", pinTitleSpecs, blobID, pinnedTitles);

            validateAndDiff(fastlaneStateEngine, pinTitleStateEngine);
        }
        /*
         * // Just FastLane
         * ctx.setFastlaneIds(toSet(fastlaneIds));
         * HollowReadStateEngine fastlaneStateEngine = transform(CONVERTER_VIP + "_fastlane", ctx, api, version, FASTLANE_ID);
         *
         * // Fastlane + PinTitle
         * ctx.setPinTitleSpecs(pinTitleSpecs);
         * HollowReadStateEngine pinTitleStateEngine = transform(CONVERTER_VIP + "_pinTitle", ctx, api, version, FASTLANE_ID, PINTITLE_ID);
         *
         * validateAndDiff(fastlaneStateEngine, pinTitleStateEngine);
         */
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

    private HollowReadStateEngine fetchInputStateEngine(String converterVip, long version, int... specificTopNodeIdsToInclude) throws Exception {
        boolean isSlicing = specificTopNodeIdsToInclude != null && specificTopNodeIdsToInclude.length > 0;

        String filename = createFileName("input", converterVip, version, specificTopNodeIdsToInclude);
        File blobFile = env.localBlobStore().resolve(filename).toFile();
        if (blobFile.exists() && !reuseSliceFiles) blobFile.delete();
        if (blobFile.exists()) return readStateEngine(blobFile);

        VMSInputDataClient client = new VMSInputDataClient(env.proxyURL(), env.localBlobStore().toString(), converterVip);
        client.triggerRefreshTo(version);
        HollowWriteStateEngine writeStateEngine = null;
        if (isSlicing) {
            DataSlicer.SliceTask slicer = new DataSlicerImpl().getSliceTask(0, specificTopNodeIdsToInclude);
            writeStateEngine = slicer.sliceInputBlob(client.getStateEngine());
        } else {
            client.triggerRefreshTo(version);
            writeStateEngine = HollowWriteStateCreator.recreateAndPopulateUsingReadEngine(client.getStateEngine());
        }

        writeStateEngine(writeStateEngine, blobFile);
        return readStateEngine(blobFile);
    }

    private String createFileName(String prefix, String namespace, long version, int... specificTopNodeIdsToInclude) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix).append("_").append(namespace).append("_").append(version);
        if (specificTopNodeIdsToInclude != null && specificTopNodeIdsToInclude.length > 0) {
            for (int topNode : specificTopNodeIdsToInclude) {
                sb.append("_").append(topNode);
            }
        }

        return sb.toString();
    }

    private HollowReadStateEngine transform(String name, TransformerContext ctx, VMSHollowInputAPI api, long version, int... specificTopNodeIdsToInclude) throws Throwable {
        String filename = createFileName("output", name, version, specificTopNodeIdsToInclude);
        File blobFile = env.localBlobStore().resolve(filename).toFile();

        VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();
        SimpleTransformer transformer = new SimpleTransformer(api, null, outputStateEngine, ctx);
        transformer.transform();
        PinTitleHelper.addBlobID(outputStateEngine, name);

        writeStateEngine(outputStateEngine, blobFile);
        return readStateEngine(blobFile);
    }

    private static Set<Integer> toSet(int... ids) {
        Set<Integer> set = new HashSet<>();
        for (int id : ids)
            set.add(id);
        return set;
    }

    private HollowReadStateEngine loadStateEngine(String file) throws IOException {
        return readStateEngine(new File(file));
    }

    private HollowReadStateEngine readStateEngine(File blobFile) throws IOException {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(stateEngine);
        try (LZ4BlockInputStream is = new LZ4BlockInputStream(new FileInputStream(blobFile))) {
            reader.readSnapshot(is);
        }
        return stateEngine;
    }

    private void writeStateEngine(HollowWriteStateEngine stateEngine, File outputFile) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        try (LZ4BlockOutputStream os = new LZ4BlockOutputStream(new FileOutputStream(outputFile))) {
            writer.writeSnapshot(os);
        }
    }

    private HollowReadStateEngine loadFastlane(long version) throws Exception {
        String fn = "/space/debug/fastlane_" + version + ".log";
        return loadStateEngine(fn);
    }

    private static void validateAndDiff(final HollowReadStateEngine fromState, final HollowReadStateEngine toState) throws Exception {
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

    private static Set<Integer> output(String label, HollowReadStateEngine readStateEngine) throws IOException {
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

    private static enum Environment {
        TEST("http://discovery.cloudqa.netflix.net:7001/discovery/resolver/cluster/vmshollowloaderblobproxy-vmstools-test"), PROD("http://discovery.cloud.netflix.net:7001/discovery/resolver/cluster/vmshollowloaderblobproxy-vmstools-prod");

        private final String proxyURL;

        private Environment(String proxyURL) {
            this.proxyURL = proxyURL;
        }

        String proxyURL() {
            return proxyURL;
        }

        Path localBlobStore() {
            try {
                Path path = Paths.get(LOCAL_BLOB_STORE, name().toLowerCase());
                Files.createDirectories(path);
                return path;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    // ------
}
