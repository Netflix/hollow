package com.netflix.vms.transformer.testutil.migration;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.BlobRetriever;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.HollowPrimaryKeyValueDeriver;
import com.netflix.hollow.core.memory.ThreadSafeBitSet;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.history.ui.jetty.HollowHistoryUIServer;
import com.netflix.hollow.tools.combine.HollowCombiner;
import com.netflix.hollow.tools.combine.HollowCombinerIncludeOrdinalsCopyDirector;
import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;
import com.netflix.hollow.tools.traverse.TransitiveSetTraverser;
import com.netflix.internal.hollow.factory.HollowBlobRetrieverFactory;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.StreamDeploymentHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.input.VMSInputDataClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import org.junit.Test;

public class DebugConverterData {
    private static final String CONVERTER_VIP_NAME = "muon";
    private static final String CONVERTER_NAMESPACE = "vmsconverter-muon";
    private static final String WORKING_DIR = "/space/converter-data/debug";
    private static final Path WORKING_PATH = Paths.get(WORKING_DIR);
    private static final String WORKING_DIR_FOR_INPUTCLIENT = "/space/converter-data/inputclient";

    public void setup() throws Exception {
        for (String folder : Arrays.asList(WORKING_DIR, WORKING_DIR_FOR_INPUTCLIENT)) {
            File workingDir = new File(folder);
            if (!workingDir.exists()) workingDir.mkdirs();
        }
    }

    @Test
    public void debugStreamDeploymentS3PathToStreamIds() {
        long version = 20170824034503068L;
        VMSInputDataClient inputClient = new VMSInputDataClient(VMSInputDataClient.PROD_PROXY_URL, WORKING_DIR_FOR_INPUTCLIENT, CONVERTER_VIP_NAME);
        inputClient.triggerRefreshTo(version);

        Map<String, Set<Long>> map = new TreeMap<>();
        VMSHollowInputAPI api = inputClient.getAPI();

        for (PackageStreamHollow stream : api.getAllPackageStreamHollow()) {
            StreamDeploymentHollow deployment = stream._getDeployment();
            if (deployment==null) continue;

            StringHollow s3PathComponent = deployment._getS3PathComponent();
            if (s3PathComponent == null) continue;

            String path = s3PathComponent._getValue();
            Set<Long> ids = map.get(path);
            if (ids==null) {
                ids = new HashSet<>();
                map.put(path, ids);
            }
            ids.add(stream._getDownloadableIdBoxed());
        }

        int i = 1;
        for (Map.Entry<String, Set<Long>> entry : map.entrySet()) {
            System.out.println(String.format("%d) %s = [size=%d] downloadIds=%s", i++, entry.getKey(), entry.getValue().size(), entry.getValue()));
        }

        // FOUND: 1) Celeste Holm (1961-1996) = [size=2] downloadIds=[572674263, 572672107]
    }

    @Test
    public void debugVersionsWithBadPackageStream() {
        long[] versions = new long[] { 20170824030803390L, 20170824030803390L, 20170824031131543L, 20170824031347430L, 20170824031546123L, 20170824032458038L, 20170824032722275L, 20170824032941389L, 20170824033200595L, 20170824033533733L, 20170824033754309L, 20170824034009909L, 20170824034238345L, 20170824034503068L };
        HollowRecordStringifier stringifier = new HollowRecordStringifier(true, true, false);

        // FOUND: 1) Celeste Holm (1961-1996) = [size=2] downloadIds=[572674263, 572672107]
        int i = 1;
        long badDownloadableId = 572674263L; // long badDownloadableId2 = 572672107L;
        VMSInputDataClient inputClient = new VMSInputDataClient(VMSInputDataClient.PROD_PROXY_URL, WORKING_DIR_FOR_INPUTCLIENT, CONVERTER_VIP_NAME);
        for (long version : versions) {
            inputClient.triggerRefreshTo(version);
            HollowPrimaryKeyIndex index = new HollowPrimaryKeyIndex(inputClient.getStateEngine(), "PackageStream", "downloadableId");
            int ordinal = index.getMatchingOrdinal(badDownloadableId);

            VMSHollowInputAPI api = inputClient.getAPI();
            PackageStreamHollow stream = api.getPackageStreamHollow(ordinal);
            if (stream._getDeployment()._getS3PathComponent() != null) {
                System.out.println(String.format("%d) version=%s, dId=%s, deployment=%s", i++, version, stream._getDownloadableId(), stringifier.stringify(stream._getDeployment())));
            }
        }

        /*
         * VERSIONS:
         * 1) version=20170824033754309, dId=572674263, deployment=(StreamDeployment) (ordinal 2345573)
         * 2) version=20170824034009909, dId=572674263, deployment=(StreamDeployment) (ordinal 2345573)
         * 3) version=20170824034238345, dId=572674263, deployment=(StreamDeployment) (ordinal 2345573)
         * 4) version=20170824034503068, dId=572674263, deployment=(StreamDeployment) (ordinal 2345573)
         */
    }

    @Test
    public void debugConverterWithHistory() throws Exception {
        long version = 20170824030803390L;
        long toVersion = 20170824034503068L;

        // FOUND: 1) Celeste Holm (1961-1996) = [size=2] downloadIds=[572674263, 572672107]
        BlobRetriever blobRetriever = HollowBlobRetrieverFactory.localProxyForProdEnvironment().getForNamespace(CONVERTER_NAMESPACE);
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobRetriever).withLocalBlobStore(new File(WORKING_DIR)).build();
        HollowHistoryUIServer historyUI = new HollowHistoryUIServer(consumer, 7777);
        historyUI.start();
        consumer.triggerRefreshTo(version);
        consumer.triggerRefreshTo(toVersion);
        historyUI.join();
    }

    @Test
    public void walkthroughConverterVersionsWithHollowConsumer() throws Exception {
        long[] versions = new long[] { 20170824030803390L, 20170824030803390L, 20170824031131543L, 20170824031347430L, 20170824031546123L, 20170824032458038L, 20170824032722275L, 20170824032941389L, 20170824033200595L, 20170824033533733L, 20170824033754309L, 20170824034009909L, 20170824034238345L, 20170824034503068L };

        int badPackageStreamOrdinal = 54076780;
        BlobRetriever blobRetriever = HollowBlobRetrieverFactory.localProxyForProdEnvironment().getForNamespace(CONVERTER_NAMESPACE);
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobRetriever).withLocalBlobStore(new File(WORKING_DIR)).withGeneratedAPIClass(VMSHollowInputAPI.class).build();

        for (long version : versions) {
            System.out.println("Refreshing to version=" + version);
            consumer.triggerRefreshTo(version);
            VMSHollowInputAPI api = (VMSHollowInputAPI) consumer.getAPI();

            BitSet ordinals = consumer.getStateEngine().getTypeState("StreamDeployment").getPopulatedOrdinals();
            System.out.println("\t StreamDeployment ordinal 2345573 exists? " + ordinals.get(2345573));

            PackageStreamHollow stream = api.getPackageStreamHollow(badPackageStreamOrdinal);

            StreamDeploymentHollow deployment = stream._getDeployment();
            System.out.println("\t DeploymentInfo ordinal=" + deployment._getDeploymentInfo().getOrdinal());
            //            System.out.println(stringifier.stringify(stream));

            if (deployment._getS3FullPath() != null) {
                StringHollow hStr = deployment._getS3FullPath();
                System.out.println(String.format("\t s3FullPath=%s, ordinal=%d", hStr._getValue(), hStr.getOrdinal()));
            }

            if (deployment._getS3PathComponent() != null) {
                StringHollow hStr = deployment._getS3PathComponent();
                System.out.println(String.format("\t s3PathComponent=%s, ordinal=%d", hStr._getValue(), hStr.getOrdinal()));
            }
        }
    }

    private void doesStreamDeploymentOrdinalExists(HollowReadStateEngine rEngine, int ordinal) {
        HollowTypeReadState typeState = rEngine.getTypeState("StreamDeployment");
        BitSet currSet = typeState.getPopulatedOrdinals();
        BitSet prevSet = typeState.getPreviousOrdinals();
        System.out.println(String.format("\n------\n\t StreamDeployment ordinal %d exists in (curr:%s, prev:%s)", ordinal, currSet.get(ordinal), prevSet.get(ordinal)));
    }

    private void debugPackageStream(String label, HollowConsumer consumer, long streamID) {
        HollowReadStateEngine rEngine = consumer.getStateEngine();
        VMSHollowInputAPI api = (VMSHollowInputAPI) consumer.getAPI();
        HollowRecordStringifier stringifier = new HollowRecordStringifier(true, true, false);

        System.out.println(String.format("\n\n---- debugPackageStream [%s] @ version=%s", label, consumer.getCurrentVersionId()));
        {
            HollowPrimaryKeyIndex index = new HollowPrimaryKeyIndex(rEngine, "PackageStream", "downloadableId");
            int ordinal = index.getMatchingOrdinal(streamID);

            PackageStreamHollow packageStreamHollow = api.getPackageStreamHollow(ordinal);
            System.out.println(stringifier.stringify(packageStreamHollow));
            //System.out.println(stringifier.stringify(rEngine, "PackageStream", ordinal));

            doesStreamDeploymentOrdinalExists(rEngine, packageStreamHollow._getDeployment().getOrdinal());
        }


        { // Found out number of StreamDeployments referenced by PackageStream that are no longer valid (marked deleted)
            Set<Long> streamIdsSet = new HashSet<>();

            BitSet badOrdinals = new BitSet();
            BitSet populatedOrdinals = rEngine.getTypeState("StreamDeployment").getPopulatedOrdinals();
            for (PackageStreamHollow stream : api.getAllPackageStreamHollow()) {
                int ordinal = stream._getDeployment().getOrdinal();
                if (ordinal == -1) continue;

                if (populatedOrdinals.get(ordinal)) continue;

                badOrdinals.set(ordinal);
                streamIdsSet.add(stream._getDownloadableId());
            }
            System.out.println("\t # of PackageStream referring invalid (deleted?) StreamDeployment: " + streamIdsSet.size() + ", ids=" + streamIdsSet);
            System.out.println("\t # of Bad StreamDeployment (referenced by PackageStream but no longer exists): " + badOrdinals.cardinality() + ", ordinals=" + badOrdinals);
        }
    }

    @Test
    public void debugConverterBeforeAndAfter() throws Exception {
        BlobRetriever blobRetriever = HollowBlobRetrieverFactory.localProxyForProdEnvironment().getForNamespace(CONVERTER_NAMESPACE);
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobRetriever).withLocalBlobStore(new File(WORKING_DIR)).withGeneratedAPIClass(VMSHollowInputAPI.class).build();

        // FOUND: 1) Celeste Holm (1961-1996) = [size=2] downloadIds=[572674263, 572672107]
        long badDownloadableId = 572674263L;
        consumer.triggerRefreshTo(20170824033200595L);
        debugPackageStream("GOOD STATE", consumer, badDownloadableId);

        consumer.triggerRefreshTo(20170824033533733L); // This state already seems to be bad since STreamDeployment ordinal is already removed
        debugPackageStream("BAD STATE with PackageStream pointing to ghost StreamDeployment", consumer, badDownloadableId);

        consumer.triggerRefreshTo(20170824033754309L); // First State with S3Path = Celeste Holm (1961-1996)
        debugPackageStream("BAD STATE with PackageStream pointing to ghost StreamDeployment with bad S3PATH = Celeste Holm (1961-1996)", consumer, badDownloadableId);
    }

    @Test
    public void reproduceConverterIssueSimulatingEvents_step1() throws Exception {
        long goodStateVersion = 20170824033200595L;
        long suspeciousStateVersion = 20170824033533733L;
        long badDownloadableId = 572674263L;

        BlobRetriever blobRetriever = HollowBlobRetrieverFactory.localProxyForProdEnvironment().getForNamespace(CONVERTER_NAMESPACE);
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobRetriever).withLocalBlobStore(new File(WORKING_DIR)).withGeneratedAPIClass(VMSHollowInputAPI.class).build();

        // Start at good state
        consumer.triggerRefreshTo(goodStateVersion);
        debugPackageStream("GOOD STATE", consumer, badDownloadableId);

        // Go to bad state
        consumer.triggerRefreshTo(suspeciousStateVersion);
        debugPackageStream("BAD STATE", consumer, badDownloadableId);

        // Determine the modified Packages
        HollowTypeReadState typeState = consumer.getStateEngine().getTypeState("Package");
        BitSet modifiedSet = getModifiedBitSet(typeState.getPopulatedOrdinals(), typeState.getPreviousOrdinals());
        HollowReadStateEngine stateEngine = consumer.getStateEngine();
        HollowPrimaryKeyValueDeriver valDeriver = new HollowPrimaryKeyValueDeriver(((HollowObjectSchema) stateEngine.getSchema("Package")).getPrimaryKey(), stateEngine);
        try(PrintWriter pw = new PrintWriter(Files.newBufferedWriter(WORKING_PATH.resolve("modified-keys")))) {
            Set<String> pkSet = new HashSet<>();
            int ordinal = modifiedSet.nextSetBit(0);
            while (ordinal != -1) {
                Object[] recordKey = valDeriver.getRecordKey(ordinal); // @PrimaryKey(packageId, movieId) (long, long)
                String line = String.format("%d,%d\n", recordKey[0], recordKey[1]);
                if (!pkSet.contains(line)) {
                    pkSet.add(line);
                    pw.format(line);
                }
                ordinal = modifiedSet.nextSetBit(ordinal + 1);
            }
            System.out.println("ModifiedKeys Size=" + pkSet.size());
        }
    }

    private void debugWriteStateEngine(String label, HollowWriteStateEngine wEngine) throws IOException {
        System.out.println("\n----\n WriteStateEngine: " + label);
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(WORKING_PATH.resolve("writeStateEngineStat_" + label)))) {
            NumberFormat percentInstance = NumberFormat.getPercentInstance();
            for (HollowTypeWriteState typeState : wEngine.getOrderedTypeStates()) {
                BitSet populatedBitSet = toBitSet(typeState.getPopulatedBitSet());
                BitSet previousBitSet = toBitSet(typeState.getPreviousCyclePopulatedBitSet());
                BitSet modifiedBitSet = getModifiedBitSet(populatedBitSet, previousBitSet);

                double percent = modifiedBitSet.cardinality() == 0 ? 0 : (populatedBitSet.cardinality() == 0 ? 1 : modifiedBitSet.cardinality() / populatedBitSet.cardinality());
                String line = String.format("\t State=%s, populatedBitSet=%s, previousBitSet=%s, modifiedSet=%s (%s percent)", typeState.getSchema().getName(), populatedBitSet.cardinality(), previousBitSet.cardinality(), modifiedBitSet.cardinality(), percentInstance.format(percent));
                System.out.println(line);
                pw.println(line);
            }
        }
    }

    @Test
    public void reproduceConverterIssueSimulatingEvents_step2() throws Exception {
        long goodStateVersion = 20170824033200595L;

        BlobRetriever blobRetriever = HollowBlobRetrieverFactory.localProxyForProdEnvironment().getForNamespace(CONVERTER_NAMESPACE);
        HollowConsumer consumerWithGoodState = HollowConsumer.withBlobRetriever(blobRetriever).withLocalBlobStore(new File(WORKING_DIR)).withGeneratedAPIClass(VMSHollowInputAPI.class).build();
        consumerWithGoodState.triggerRefreshTo(goodStateVersion);

        List<Object[]> modifiedKeys = new ArrayList<>();
        { // Load modified keys from Step 1
            try(Scanner scanner = new Scanner(WORKING_PATH.resolve("modified-keys"))) {
                while(scanner.hasNextLine()) {
                    String[] s = scanner.nextLine().split(",");
                    Long[] key = new Long[]{
                            Long.parseLong(s[0]),
                            Long.parseLong(s[1])
                    };
                    modifiedKeys.add(key);
                }
            }
            System.out.println("ModifiedKeys Size=" + modifiedKeys.size());
        }

        long badDownloadableId = 572674263L;
        HollowReadStateEngine rEngine = consumerWithGoodState.getStateEngine();
        debugPackageStream("GOOD STATE", consumerWithGoodState, badDownloadableId);

        HollowWriteStateEngine wEngine = HollowWriteStateCreator.recreateAndPopulateUsingReadEngine(rEngine);
        debugWriteStateEngine("Phase1_initialState", wEngine);

        { // Simulate Converter in removing records from state engine that are in events
            wEngine.prepareForWrite();
            wEngine.prepareForNextCycle();

            wEngine.addAllObjectsFromPreviousCycle();
            removeEventsData(rEngine, wEngine, modifiedKeys);
        }
        debugWriteStateEngine("Phase2_removeEventsData", wEngine);

        { // Simulate Converter in adding events to blob
            String packageType = "Package";
            BitSet goodStateModifiedSet = new BitSet();
            HollowPrimaryKeyIndex packageIndex = new HollowPrimaryKeyIndex(rEngine, ((HollowObjectSchema) rEngine.getSchema(packageType)).getPrimaryKey());
            for (Object[] keys : modifiedKeys) {
                int packageOrdinal = packageIndex.getMatchingOrdinal(keys);
                if (packageOrdinal == -1) {
                    System.out.println("Not found package @PrimaryKey(packageId, movieId) :" + Arrays.toString(keys));
                    continue;
                }

                //                HollowRecord rec = GenericHollowRecordHelper.instantiate(rEngine, packageType, packageOrdinal);
                //                wEngine.add(packageType, rec);
                goodStateModifiedSet.set(packageOrdinal);
            }
            Map<String, BitSet> ordinalsToInclude = Collections.singletonMap("Package", goodStateModifiedSet);
            System.out.println(String.format("\n\n ====\n ModifiedKeys Size=%s, oridinalToinclude=%s diff=%s\n ====\n", modifiedKeys.size(), goodStateModifiedSet.cardinality(), (modifiedKeys.size() - goodStateModifiedSet.cardinality())));
            HollowCombiner combiner = new HollowCombiner(new HollowCombinerIncludeOrdinalsCopyDirector(ordinalsToInclude), wEngine, rEngine);
            combiner.combine();
        }
        debugWriteStateEngine("Phase3_combined", wEngine);

        // Write to File
        wEngine.prepareForWrite();
        //wEngine.prepareForNextCycle();
        debugWriteStateEngine("Phase4_write", wEngine);
        writeStateEngineToFile(wEngine, WORKING_PATH.resolve("reproBadState.blob").toFile());
    }

    @Test
    public void reproduceConverterIssueSimulatingEvents_step3() throws Exception {
        long badDownloadableId = 572674263L;
        HollowRecordStringifier stringifier = new HollowRecordStringifier(true, true, false);

        /*
         * FROM: reproduceConverterIssueSimulatingEvents_step2
         *
         * StreamDeployment ordinal 2345573 exists in (curr:false, prev:true)
         * # of PackageStream referring invalid (deleted?) StreamDeployment: 112, ids=[572673571, 575720530, 599369146, 594949745, 594035810, 594952060, 575638596, 599622833, 575718750, 583546330,
         * 583545309, 599623580, 599625375, 594952285, 594949469, 572223024, 572225072, 590891016, 590888203, 583543293, 594032479, 575719790, 594949175, 599622652, 575717398, 599622139, 572672107,
         * 599623159, 572222039, 537047630, 599624433, 594039589, 594033447, 594038841, 575718913, 590889067, 599625956, 537045340, 590890598, 594039816, 594950932, 594952725, 583546795, 594037260,
         * 572223358, 599625179, 594949918, 572224370, 537045609, 537048169, 590887255, 575638075, 583545532, 594950659, 590887501, 594039327, 572221536, 575718188, 590890308, 537049217, 575720406,
         * 594953457, 575639238, 599625524, 575720155, 575719387, 575637955, 575640030, 594034687, 572559031, 594955244, 575639763, 572561596, 572221316, 594951895, 594035653, 537045174, 594038495,
         * 583547519, 575719659, 594033066, 594036650, 594033323, 594951349, 583547141, 594037926, 599624563, 575640473, 572223948, 572223682, 583546385, 572224961, 572562941, 594035383, 590889703,
         * 575638444, 575639467, 572673991, 583544109, 537045988, 575718583, 572559559, 594953628, 537047272, 537048809, 572561107, 572674263, 575638969, 590888655, 594035092, 575718060, 572562399]
         * Bad StreamDeployment (referenced by PackageStream but no longer exists) count: 6, ordinals={1382879, 2138852, 2336947, 2337328, 2338927, 2345573}
         */
        HollowReadStateEngine badReadStateEngine = readStateEngine(WORKING_PATH.resolve("reproBadState.blob").toFile());
        {
            HollowPrimaryKeyIndex index = new HollowPrimaryKeyIndex(badReadStateEngine, "PackageStream", "downloadableId");
            int ordinal = index.getMatchingOrdinal(badDownloadableId);
            System.out.println(stringifier.stringify(badReadStateEngine, "PackageStream", ordinal));
            //doesStreamDeploymentOrdinalExists(badReadStateEngine, packageStreamHollow._getDeployment().getOrdinal());
        }

        Set<Integer> badOrdinals = new HashSet<>(Arrays.asList(1382879, 2138852, 2336947, 2337328, 2338927, 2345573));
        HollowTypeReadState typeState = badReadStateEngine.getTypeState("StreamDeployment");
        BitSet populatedOrdinals = typeState.getPopulatedOrdinals();
        BitSet previousOrdinals = typeState.getPreviousOrdinals();
        for (int o : badOrdinals) {
            System.out.println(String.format("Bad ordinal=%d, exists(curr:%s, prev:%s)", o, populatedOrdinals.get(o), previousOrdinals.get(o)));
        }
        //debugPackageStream("GOOD STATE", consumerWithGoodState, badDownloadableId);

        //            BitSet newDataSet = new BitSet();
        //            int o = populatedOrdinals.nextSetBit(0);
        //            HollowPrimaryKeyValueDeriver valDeriver = new HollowPrimaryKeyValueDeriver(((HollowObjectSchema) consumerToTransitionToBadState.getStateEngine().getSchema("Package")).getPrimaryKey(), consumerToTransitionToBadState.getStateEngine());
        //            while (o != -1) {
        //                if (!previousOrdinals.get(o)) {
        //                    // new records
        //                    newDataSet.set(o);
        //                }
        //                o = populatedOrdinals.nextSetBit(o + 1);
        //            }
        // @TODO need to add the data with ordinals in newDataSet to write state engine


        int badStreamDeploymentOrdinal = 2345573;
        // @TODO then look at SteamDeployment with badStreamDeploymentOrdinal and see if it was able to reproduce issue
    }

    // Ported from ConverterStateEngine.removeEventsData
    private void removeEventsData(HollowReadStateEngine rEngine, HollowWriteStateEngine wEngine, List<Object[]> modifiedKeys) {
        HollowPrimaryKeyIndex coldstartIdx = new HollowPrimaryKeyIndex(rEngine, ((HollowObjectSchema) rEngine.getSchema("Package")).getPrimaryKey());

        BitSet eventOverriddenTypeOrdinals = new BitSet();
        Map<String, BitSet> ordinalsToRemove = new HashMap<>();
        for (Object[] recKey : modifiedKeys) {
            int ordinal = coldstartIdx.getMatchingOrdinal(recKey);
            if (ordinal != -1)
                eventOverriddenTypeOrdinals.set(ordinal);
        }
        ordinalsToRemove.put("Package", eventOverriddenTypeOrdinals);

        TransitiveSetTraverser.addTransitiveMatches(rEngine, ordinalsToRemove);
        TransitiveSetTraverser.removeReferencedOutsideClosure(rEngine, ordinalsToRemove);
        removeOrdinalsFromThisCycle(wEngine, ordinalsToRemove);
    }

    // Ported from ConverterStateEngine.removeOrdinalsFromThisCycle
    private void removeOrdinalsFromThisCycle(HollowWriteStateEngine wEngine, Map<String, BitSet> recordsToRemove) {
        System.out.println("\n ----\n removeOrdinalsFromThisCycle:");
        recordsToRemove.entrySet().stream().forEach(entry -> {
            HollowTypeWriteState typeWriteState = wEngine.getTypeState(entry.getKey());
            int ordinal = entry.getValue().nextSetBit(0);
            if (ordinal != -1) System.out.println(String.format("\t remove: type=%s, cardinality=%d", entry.getKey(), entry.getValue().cardinality()));
            while (ordinal != -1) {
                typeWriteState.removeOrdinalFromThisCycle(ordinal);
                ordinal = entry.getValue().nextSetBit(ordinal + 1);
            }
        });
    }

    private void writeStateEngineToFile(HollowWriteStateEngine wEngine, File blobFile) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(wEngine);
        try (LZ4BlockOutputStream os = new LZ4BlockOutputStream(new FileOutputStream(blobFile))) {
            writer.writeSnapshot(os);
        }
    }

    private HollowReadStateEngine readStateEngine(File blobFile) throws IOException {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(stateEngine);
        try (LZ4BlockInputStream is = new LZ4BlockInputStream(new FileInputStream(blobFile))) {
            reader.readSnapshot(is);
        }

        return stateEngine;
    }

    private BitSet getModifiedBitSet(BitSet populatedOrdinals, BitSet previousOrdinals) {
        BitSet modifiedSet = new BitSet();
        modifiedSet.or(populatedOrdinals);
        modifiedSet.xor(previousOrdinals);
        return modifiedSet;
    }

    private BitSet toBitSet(ThreadSafeBitSet t) {
        BitSet bitSet = new BitSet();

        int ordinal = t.nextSetBit(0);
        while (ordinal != -1) {
            bitSet.set(ordinal);
            ordinal = t.nextSetBit(ordinal + 1);
        }
        return bitSet;
    }
}