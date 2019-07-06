package com.netflix.vms.transformer.testutil.migration;

import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.Dataset.CONVERTER;

import com.google.inject.Inject;
import com.netflix.cinder.consumer.CinderConsumerBuilder;
import com.netflix.cinder.consumer.NFHollowBlobRetriever;
import com.netflix.cinder.lifecycle.CinderConsumerModule;
import com.netflix.governator.guice.test.ModulesForTesting;
import com.netflix.governator.guice.test.junit4.GovernatorJunit4ClassRunner;
import com.netflix.gutenberg.consumer.GutenbergFileConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.BlobRetriever;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever;
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
import com.netflix.runtime.lifecycle.RuntimeCoreModule;
import com.netflix.vms.transformer.consumer.VMSInputDataConsumer;
import com.netflix.vms.transformer.hollowinput.PackageHollow;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.StreamDeploymentHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VmsAttributeFeedEntryHollow;
import com.netflix.vms.transformer.util.OutputUtil;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GovernatorJunit4ClassRunner.class)
@ModulesForTesting({CinderConsumerModule.class, RuntimeCoreModule.class})
public class DebugConverterData {
    private HollowRecordStringifier stringifier = new HollowRecordStringifier(true, true, false);
    private NumberFormat percentFormat = NumberFormat.getPercentInstance();

    private static final String MODIFIED_PACKAGE_KEY_FILENAME = "modified-package-keys.txt";
    private static final String IMPACTED_PACKAGE_KEY_FILENAME = "impacted-package-keys.txt";
    private static final String CONVERTER_NAMESPACE = "vmsconverter-muon";
    private static final String WORKING_DIR = "/space/converter-data/debug";
    private static final String REPRO_DIR = WORKING_DIR + "/repro";
    private static final Path REPRO_PATH = Paths.get(REPRO_DIR);
    private static final String WORKING_DIR_FOR_INPUTCONSUMER = "/space/converter-data/inputclient";

    @Inject
    private Supplier<CinderConsumerBuilder> cinderConsumerBuilder;

    @Before
    public void setup() {
        percentFormat.setMinimumFractionDigits(1);
        for (String folder : Arrays.asList(WORKING_DIR, WORKING_DIR_FOR_INPUTCONSUMER, REPRO_DIR)) {
            File workingDir = new File(folder);
            if (!workingDir.exists()) workingDir.mkdirs();
        }
    }

    @Test
    public void show_DABorDOB_ConverterData() {
        long version = 20180131173015308L;
        HollowConsumer inputConsumer = VMSInputDataConsumer.getNewProxyConsumer(cinderConsumerBuilder,
                CONVERTER_NAMESPACE, WORKING_DIR_FOR_INPUTCONSUMER, false, CONVERTER.getAPI());
        inputConsumer.triggerRefreshTo(version);

        Set<Long> newHasRollingEpisodes = new HashSet<>();
        VMSHollowInputAPI api = (VMSHollowInputAPI) inputConsumer.getAPI();
        for (VmsAttributeFeedEntryHollow contractAttributes : api.getAllVmsAttributeFeedEntryHollow()) {
            long videoId = contractAttributes._getMovieId()._getValue();
            String countryCode = contractAttributes._getCountryCode()._getValue();

            boolean dob = contractAttributes._getDayOfBroadcast()._getValue();
            boolean dab = contractAttributes._getDayAfterBroadcast()._getValue();
            if (dob) {
                System.out.printf("videoId=%s, countryCode=%s, dob=%s, dab=%s\n", videoId, countryCode, dob, dab);
                if (!dab) {
                    newHasRollingEpisodes.add(videoId);
                }
            }
        }

        System.out.printf("NEW hasRollingEpisodes=%d : %s\n", newHasRollingEpisodes.size(), newHasRollingEpisodes);
    }

    @Test
    public void debugStreamDeploymentS3PathToStreamIds() {
        long version = 20170824034503068L;
        HollowConsumer inputConsumer = VMSInputDataConsumer.getNewProxyConsumer(cinderConsumerBuilder,
                CONVERTER_NAMESPACE, WORKING_DIR_FOR_INPUTCONSUMER, true, CONVERTER.getAPI());
        inputConsumer.triggerRefreshTo(version);

        Map<String, Set<Long>> map = new TreeMap<>();
        VMSHollowInputAPI api = (VMSHollowInputAPI) inputConsumer.getAPI();

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
            System.out.printf("%d) %s = [size=%d] downloadIds=%s \n", i++, entry.getKey(), entry.getValue().size(), entry.getValue());
        }

        // FOUND: 1) Celeste Holm (1961-1996) = [size=2] downloadIds=[572674263, 572672107]
    }

    @Test
    public void debugVersionsWithBadPackageStream() {
        long[] versions = new long[] { 20170824030803390L, 20170824030803390L, 20170824031131543L, 20170824031347430L, 20170824031546123L, 20170824032458038L, 20170824032722275L, 20170824032941389L, 20170824033200595L, 20170824033533733L, 20170824033754309L, 20170824034009909L, 20170824034238345L, 20170824034503068L };

        // FOUND: 1) Celeste Holm (1961-1996) = [size=2] downloadIds=[572674263, 572672107]
        int i = 1;
        long badDownloadableId = 572674263L; // long badDownloadableId2 = 572672107L;
        HollowConsumer inputConsumer = VMSInputDataConsumer.getNewProxyConsumer(cinderConsumerBuilder,
                CONVERTER_NAMESPACE, WORKING_DIR_FOR_INPUTCONSUMER, true, CONVERTER.getAPI());

        for (long version : versions) {
            inputConsumer.triggerRefreshTo(version);
            HollowPrimaryKeyIndex index = new HollowPrimaryKeyIndex(inputConsumer.getStateEngine(), "PackageStream", "downloadableId");
            int ordinal = index.getMatchingOrdinal(badDownloadableId);

            VMSHollowInputAPI api = (VMSHollowInputAPI) inputConsumer.getAPI();
            PackageStreamHollow stream = api.getPackageStreamHollow(ordinal);
            if (stream._getDeployment()._getS3PathComponent() != null) {
                System.out.printf("%d) version=%s, dId=%s, deployment=%s\n", i++, version, stream._getDownloadableId(), stringifier.stringify(stream._getDeployment()));
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
        BlobRetriever blobRetriever = new NFHollowBlobRetriever(
                GutenbergFileConsumer.localProxyForProdEnvironment(), CONVERTER_NAMESPACE);
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobRetriever).withLocalBlobStore(new File(WORKING_DIR)).build();
        HollowHistoryUIServer historyUI = new HollowHistoryUIServer(consumer, 7777);
        historyUI.start();
        consumer.triggerRefreshTo(version);
        consumer.triggerRefreshTo(toVersion);
        historyUI.join();
    }

    @Test
    public void walkthroughConverterVersionsWithHollowConsumer() {
        long[] versions = new long[] { 20170824030803390L, 20170824030803390L, 20170824031131543L, 20170824031347430L, 20170824031546123L, 20170824032458038L, 20170824032722275L, 20170824032941389L, 20170824033200595L, 20170824033533733L, 20170824033754309L, 20170824034009909L, 20170824034238345L, 20170824034503068L };

        int badPackageStreamOrdinal = 54076780;
        BlobRetriever blobRetriever = new NFHollowBlobRetriever(
                GutenbergFileConsumer.localProxyForProdEnvironment(), CONVERTER_NAMESPACE);
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
                System.out.printf("\t s3FullPath=%s, ordinal=%d\n", hStr._getValue(), hStr.getOrdinal());
            }

            if (deployment._getS3PathComponent() != null) {
                StringHollow hStr = deployment._getS3PathComponent();
                System.out.printf("\t s3PathComponent=%s, ordinal=%d\n", hStr._getValue(), hStr.getOrdinal());
            }
        }
    }

    private void doesStreamDeploymentOrdinalExists(HollowReadStateEngine rEngine, long version, int ordinal) {
        HollowTypeReadState typeState = rEngine.getTypeState("StreamDeployment");
        BitSet currSet = typeState.getPopulatedOrdinals();
        BitSet prevSet = typeState.getPreviousOrdinals();
        System.out.printf("\n------\n\t [version=%s] StreamDeployment ordinal %d exists in (curr:%s, prev:%s)\n", version, ordinal, currSet.get(ordinal), prevSet.get(ordinal));
    }

    private Set<Integer> debugPackage(String label, HollowConsumer consumer, long streamID) {
        HollowReadStateEngine rEngine = consumer.getStateEngine();
        VMSHollowInputAPI api = (VMSHollowInputAPI) consumer.getAPI();

        System.out.printf("\n\n---- debugPackageStream [%s] @ version=%s\n", label, consumer.getCurrentVersionId());
        {
            HollowPrimaryKeyIndex index = new HollowPrimaryKeyIndex(rEngine, "PackageStream", "downloadableId");
            int ordinal = index.getMatchingOrdinal(streamID);
            PackageStreamHollow packageStreamHollow = api.getPackageStreamHollow(ordinal);
            System.out.println(stringifier.stringify(packageStreamHollow));
            //System.out.println(stringifier.stringify(rEngine, "PackageStream", ordinal));

            if (packageStreamHollow._getDeployment() == null) {
                System.err.printf("Deployment does not exists for StreamID=%s", streamID);
            } else {
                doesStreamDeploymentOrdinalExists(rEngine, consumer.getCurrentVersionId(), packageStreamHollow._getDeployment().getOrdinal());
            }
        }

        Map<Integer, Integer> streamToPackageOrdinalMap = new HashMap<>();
        { // Find out number of PackageStream referenced by Package that are no longer valid (marked deleted)
            Set<Long> packageIdSet = new HashSet<>();
            Set<Long> streamIdsSet = new HashSet<>();

            BitSet badOrdinals = new BitSet();
            BitSet populatedOrdinals = rEngine.getTypeState("PackageStream").getPopulatedOrdinals();
            for (PackageHollow rec : api.getAllPackageHollow()) {
                for (PackageStreamHollow stream : rec._getDownloadables()) {
                    int ordinal = stream.getOrdinal();
                    if (ordinal == -1) continue;

                    streamToPackageOrdinalMap.put(ordinal, rec.getOrdinal());
                    if (populatedOrdinals.get(ordinal)) continue;

                    badOrdinals.set(ordinal);
                    streamIdsSet.add(stream._getDownloadableId());
                    packageIdSet.add(rec._getPackageIdBoxed());
                }
            }
            System.out.println("\t # of Package referring invalid (deleted?) Stream: " + packageIdSet.size() + ", ids=" + packageIdSet);
            System.out.println("\t # of Bad PackageStream (referenced by Package but no longer exists): " + streamIdsSet.size() + ", ids=" + streamIdsSet + ", ordinals=" + badOrdinals);
        }

        { // Find out number of StreamDeployments referenced by PackageStream that are no longer valid (marked deleted)
            Set<Integer> packageOrdinalSet = new HashSet<>();
            Set<Long> streamIdsSet = new HashSet<>();

            BitSet badOrdinals = new BitSet();
            BitSet populatedOrdinals = rEngine.getTypeState("StreamDeployment").getPopulatedOrdinals();
            for (PackageStreamHollow stream : api.getAllPackageStreamHollow()) {
                if (stream._getDeployment() == null) continue;

                int ordinal = stream._getDeployment().getOrdinal();
                if (ordinal == -1) continue;

                if (populatedOrdinals.get(ordinal)) continue;

                badOrdinals.set(ordinal);
                streamIdsSet.add(stream._getDownloadableId());
                packageOrdinalSet.add(streamToPackageOrdinalMap.get(stream.getOrdinal()));
            }
            System.out.println("\t ---");
            System.out.println("\t # of Bad StreamDeployment (referenced by PackageStream but no longer exists): " + badOrdinals.cardinality() + ", ordinals=" + badOrdinals);
            System.out.println("\t # of PackageStream referring invalid (deleted?) StreamDeployment: " + streamIdsSet.size() + ", ids=" + streamIdsSet);
            System.out.println("\t # of Package impacted by PackageStream with invalid child reference: " + packageOrdinalSet.size() + ", ordinals=" + packageOrdinalSet);
            return packageOrdinalSet;
        }
    }

    @Test
    public void debugConverterBeforeAndAfter() {
        BlobRetriever blobRetriever = new NFHollowBlobRetriever(
                GutenbergFileConsumer.localProxyForProdEnvironment(), CONVERTER_NAMESPACE);
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobRetriever).withLocalBlobStore(new File(WORKING_DIR)).withGeneratedAPIClass(VMSHollowInputAPI.class).build();

        // FOUND: 1) Celeste Holm (1961-1996) = [size=2] downloadIds=[572674263, 572672107]
        long badDownloadableId = 572674263L;
        consumer.triggerRefreshTo(20170824033200595L);
        debugPackage("GOOD STATE", consumer, badDownloadableId);

        consumer.triggerRefreshTo(20170824033533733L); // This state already seems to be bad since STreamDeployment ordinal is already removed
        debugPackage("BAD STATE with PackageStream pointing to ghost StreamDeployment", consumer, badDownloadableId);

        consumer.triggerRefreshTo(20170824033754309L); // First State with S3Path = Celeste Holm (1961-1996)
        debugPackage("BAD STATE with PackageStream pointing to ghost StreamDeployment with bad S3PATH = Celeste Holm (1961-1996)", consumer, badDownloadableId);
    }

    @Test
    public void reproduceConverterIssueSimulatingEvents_step1() throws Exception {
        long start = System.currentTimeMillis();
        long goodStateVersion = 20170824033200595L;
        long suspeciousStateVersion = 20170824033533733L;
        long badDownloadableId = 572674263L;

        BlobRetriever blobRetriever = new NFHollowBlobRetriever(
                GutenbergFileConsumer.localProxyForProdEnvironment(), CONVERTER_NAMESPACE);
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobRetriever).withLocalBlobStore(new File(WORKING_DIR)).withGeneratedAPIClass(VMSHollowInputAPI.class).build();

        // Start at good state
        consumer.triggerRefreshTo(goodStateVersion);
        debugPackage("GOOD STATE", consumer, badDownloadableId);

        // Go to bad state
        consumer.triggerRefreshTo(suspeciousStateVersion);
        Set<Integer> impactedPackageOrdinals = debugPackage("BAD STATE", consumer, badDownloadableId);

        // Determine the modified Packages
        System.out.println("\n\n -----\n Find modified Package Keys between versions: " + goodStateVersion + " to " + suspeciousStateVersion);
        HollowTypeReadState typeState = consumer.getStateEngine().getTypeState("Package");
        BitSet modifiedBitSet = getModifiedBitSet(typeState.getPopulatedOrdinals(), typeState.getPreviousOrdinals());
        HollowReadStateEngine stateEngine = consumer.getStateEngine();
        HollowPrimaryKeyValueDeriver valDeriver = new HollowPrimaryKeyValueDeriver(((HollowObjectSchema) typeState.getSchema()).getPrimaryKey(), stateEngine);
        BitSet populatedOrdinals = typeState.getPopulatedOrdinals();
        try (
                PrintWriter modKeyPW = new PrintWriter(Files.newBufferedWriter(REPRO_PATH.resolve(MODIFIED_PACKAGE_KEY_FILENAME)));
                PrintWriter impKeyPW = new PrintWriter(Files.newBufferedWriter(REPRO_PATH.resolve(IMPACTED_PACKAGE_KEY_FILENAME)))) {
            int count = 0;
            Map<String, List<Integer>> modPKMap = new HashMap<>();
            Set<String> dupPKSet = new HashSet<>();
            int ordinal = modifiedBitSet.nextSetBit(0);
            while (ordinal != -1) {
                Object[] recordKey = valDeriver.getRecordKey(ordinal); // @PrimaryKey(packageId, movieId) (long, long)
                String line = String.format("%d,%d\n", recordKey[0], recordKey[1]);
                if (impactedPackageOrdinals.contains(ordinal)) {
                    System.out.printf("\t %s) Impacted ordinal=%s:%s, @PrimaryKey(packageId, movieId): %s,%s\n", ++count, ordinal, populatedOrdinals.get(ordinal), recordKey[0], recordKey[1]);
                    impKeyPW.format(line);
                }

                if (!modPKMap.containsKey(line)) {
                    List<Integer> ordinals = new ArrayList<>();
                    ordinals.add(ordinal);
                    modPKMap.put(line, ordinals);
                    modKeyPW.format(line);
                } else {
                    dupPKSet.add(line);
                    List<Integer> ordinals = modPKMap.get(line);
                    ordinals.add(ordinal);
                }
                ordinal = modifiedBitSet.nextSetBit(ordinal + 1);
            }
            System.out.println("\n\t # Impacted Package: " + impactedPackageOrdinals.size());
            System.out.println("\n\t Total Modified Package Keys: " + modPKMap.size() + ", ordinals=" + modifiedBitSet.cardinality());

            count = 0;
            System.out.println("\n\t Duplicated Package Keys: " + dupPKSet.size());
            for (String dup : dupPKSet) {
                List<Integer> list = modPKMap.get(dup);
                if (list.size() == 2) continue; // Expected since hollow mod=remove + add, so two ordinals modified
                System.out.printf("\t\t %s) %s: size=%s, ordinals=", ++count, dup.replace('\n', ' '), list.size());
                for (int o : list) {
                    System.out.printf("%s:%s; ", o, populatedOrdinals.get(o));
                }
                System.out.println();
            }
        }
        System.out.printf("\n\n----\n Total duration=%s\n", OutputUtil.formatDuration(System.currentTimeMillis() - start, true));
    }

    private void debugWriteStateEngine(String label, HollowWriteStateEngine wEngine, boolean isShowModifiedOnly) throws IOException {
        System.out.println("\n----\n WriteStateEngine: " + label);

        Map<String, String> modMap = new TreeMap<>();
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(REPRO_PATH.resolve("writeStateEngineStat_" + label + ".txt")))) {
            for (HollowTypeWriteState typeState : wEngine.getOrderedTypeStates()) {
                BitSet populatedBitSet = toBitSet(typeState.getPopulatedBitSet());
                BitSet previousBitSet = toBitSet(typeState.getPreviousCyclePopulatedBitSet());
                BitSet modifiedBitSet = getModifiedBitSet(populatedBitSet, previousBitSet);

                boolean isEmpty = populatedBitSet.cardinality() == 0 && previousBitSet.cardinality() == 0;
                double percent = isEmpty ? 0 : previousBitSet.cardinality() == 0 ? 1 : (double) modifiedBitSet.cardinality() / (double) previousBitSet.cardinality();
                boolean isModified = percent > 0;
                String type = typeState.getSchema().getName();
                String line = String.format("\t %s State=%s, populatedBitSet=%s, previousBitSet=%s, modifiedSet=%s (%s percent)", isModified ? "***" : "   ", type, populatedBitSet.cardinality(), previousBitSet.cardinality(), modifiedBitSet.cardinality(), percentFormat.format(percent));
                if (isModified) modMap.put(type, line);
                if (!isShowModifiedOnly) System.out.println(line);
                pw.println(line);
            }
        }

        if (isShowModifiedOnly && !modMap.isEmpty()) {
            System.out.println("\n\t Modified Types: " + label);
            printMap(modMap);
        }
    }

    private Map<String, Object[]> readKeysFromFile(String filename) throws IOException {
        Map<String, Object[]> map = new HashMap<>();
        { // Load modified keys from Step 1
            try (Scanner scanner = new Scanner(REPRO_PATH.resolve(filename))) {
                while (scanner.hasNextLine()) {
                    String[] s = scanner.nextLine().split(",");
                    Long[] key = new Long[] { Long.parseLong(s[0]), Long.parseLong(s[1]) };
                    map.put(Arrays.toString(key), key);
                }
            }
            System.out.println("Keys for " + filename + " Size=" + map.size());
        }
        return map;
    }

    @Test
    public void validateImpactedKeysAreModified() throws IOException {
        Map<String, Object[]> modifiedKeys = readKeysFromFile(MODIFIED_PACKAGE_KEY_FILENAME);
        Map<String, Object[]> impactedKeys = readKeysFromFile(IMPACTED_PACKAGE_KEY_FILENAME);
        int i = 0;
        for (String key : impactedKeys.keySet()) {
            boolean containsKey = modifiedKeys.containsKey(key);
            System.out.println(String.format("\t %d) key=%s, isModified=%s", ++i, key, containsKey));
            Assert.assertTrue(containsKey);
        }
    }

    @Test
    public void reproduceConverterIssueSimulatingEvents_step2() throws Exception {
        long goodStateVersion = 20170824033200595L;
        long suspeciousStateVersion = 20170824033533733L;

        boolean isUseSupeciousStateVersionData = false;
        long initVersion = goodStateVersion;
        long addDataVersion = isUseSupeciousStateVersionData ? suspeciousStateVersion : goodStateVersion;
        long reproVersion = 20180101010000000L;
        //long reproVersion = new VersionMinterWithCounter().mint();

        long initStart = System.currentTimeMillis();
        long start = initStart;
        BlobRetriever blobRetriever = new NFHollowBlobRetriever(
                GutenbergFileConsumer.localProxyForProdEnvironment(), CONVERTER_NAMESPACE);
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobRetriever).withLocalBlobStore(new File(WORKING_DIR)).withGeneratedAPIClass(VMSHollowInputAPI.class).build();
        consumer.triggerRefreshTo(initVersion);
        System.out.printf("\n\n----\n Init to version=%s, duration=%s\n", consumer.getCurrentVersionId(), OutputUtil.formatDuration(System.currentTimeMillis() - start, true));

        Map<String, Object[]> modifiedKeys = readKeysFromFile(MODIFIED_PACKAGE_KEY_FILENAME);
        Map<String, Object[]> impactedKeys = readKeysFromFile(IMPACTED_PACKAGE_KEY_FILENAME);

        long badDownloadableId = 572674263L;
        long badPackageId = 1069117L;
        long badMovieId = 80011653L;

        debugPackage("Initial State", consumer, badDownloadableId);

        start = System.currentTimeMillis();
        HollowWriteStateEngine wEngine = HollowWriteStateCreator.recreateAndPopulateUsingReadEngine(consumer.getStateEngine());
        writeStateEngineToFile(wEngine, REPRO_PATH.resolve("snapshot-" + consumer.getCurrentVersionId()).toFile(), true);
        System.out.printf("\n\n----\n Create Write Engine, duration=%s\n", OutputUtil.formatDuration(System.currentTimeMillis() - start, true));
        debugWriteStateEngine("Phase1_initialState", wEngine, false);

        { // Simulate Converter in removing records from state engine that are in events
            start = System.currentTimeMillis();
            HollowReadStateEngine rEngine = consumer.getStateEngine();
            wEngine.prepareForWrite();
            wEngine.prepareForNextCycle();
            wEngine.addAllObjectsFromPreviousCycle();

            removeEventsData(rEngine, wEngine, modifiedKeys.values());
            debugWriteStateEngine("Phase2_removeEventsData", wEngine, true);
            System.out.printf("\n\n----\n removeEventsData, duration=%s\n", OutputUtil.formatDuration(System.currentTimeMillis() - start, true));
        }

        { // Simulate Converter in adding events to blob
            consumer.triggerRefreshTo(addDataVersion);
            HollowReadStateEngine rEngine = consumer.getStateEngine();

            start = System.currentTimeMillis();
            int impactedRecordCount = 0;
            String packageType = "Package";
            BitSet modifiedPackateOrdinals = new BitSet();
            HollowPrimaryKeyIndex packageIndex = new HollowPrimaryKeyIndex(rEngine, ((HollowObjectSchema) rEngine.getSchema(packageType)).getPrimaryKey());
            for (String key : modifiedKeys.keySet()) {
                Object[] keys = modifiedKeys.get(key);
                int packageOrdinal = packageIndex.getMatchingOrdinal(keys);
                if (impactedKeys.containsKey(key) || (((Long) keys[0]).longValue() == badPackageId && ((Long) keys[1]).longValue() == badMovieId)) {
                    System.out.printf("\t %s) Found Bad/Impacted Record Key @PrimaryKey(packageId, movieId) :%s\n", ++impactedRecordCount, Arrays.toString(keys));
                    //System.out.println(stringifier.stringify(rEngine, "Package", packageOrdinal));
                }
                if (packageOrdinal == -1) {
                    System.out.println("** Not Found @PrimaryKey(packageId, movieId) :" + Arrays.toString(keys));
                    continue;
                }

                //                HollowRecord rec = GenericHollowRecordHelper.instantiate(rEngine, packageType, packageOrdinal);
                //                wEngine.add(packageType, rec);
                modifiedPackateOrdinals.set(packageOrdinal);
            }
            Map<String, BitSet> ordinalsToInclude = Collections.singletonMap(packageType, modifiedPackateOrdinals);
            System.out.printf("\n\n====\n ModifiedKeys Size=%s, oridinalToinclude=%s diff=%s\n ====\n", modifiedKeys.size(), modifiedPackateOrdinals.cardinality(), (modifiedKeys.size() - modifiedPackateOrdinals.cardinality()));

            HollowCombiner combiner = new HollowCombiner(new HollowCombinerIncludeOrdinalsCopyDirector(ordinalsToInclude), wEngine, rEngine);
            combiner.combine();

            debugWriteStateEngine("Phase3_combined", wEngine, true);
            System.out.printf("\n\n----\n addEventsData, duration=%s\n", OutputUtil.formatDuration(System.currentTimeMillis() - start, true));
        }

        { // Write to File
            //StateEngineRoundTripper.roundTripSnapshot(wEngine);
            wEngine.prepareForWrite();
            //wEngine.prepareForNextCycle();
            debugWriteStateEngine("Phase4_write", wEngine, true);
            String snapshotFN = String.format("snapshot-%s", reproVersion);
            String deltaFN = String.format("delta-%s-%s", initVersion, reproVersion);

            writeStateEngineToFile(wEngine, REPRO_PATH.resolve(snapshotFN).toFile(), true);
            writeStateEngineToFile(wEngine, REPRO_PATH.resolve(deltaFN).toFile(), false);
        }
        System.out.printf("\n\n----\n Total duration=%s\n", OutputUtil.formatDuration(System.currentTimeMillis() - initStart, true));
    }

    @Test
    public void reproduceConverterIssueSimulatingEvents_step3() throws Exception {
        /*
         * FROM: reproduceConverterIssueSimulatingEvents_step1
         * ------
         * [version=20170824033533733] StreamDeployment ordinal 2345573 exists in (curr:false, prev:true)
         * # of Package referring invalid (deleted?) Stream: 0, ids=[]
         * # of Bad PackageStream (referenced by Package but no longer exists): 0, ids=[], ordinals={}
         * ---
         * # of Bad StreamDeployment (referenced by PackageStream but no longer exists): 6, ordinals={1382879, 2138852, 2336947, 2337328, 2338927, 2345573}
         * # of PackageStream referring invalid (deleted?) StreamDeployment: 112, ids=[572673571, 575720530, 599369146, 594949745, 594035810, 594952060, 575638596, 599622833, 575718750, 583546330,
         * 583545309, 599623580, 599625375, 594952285, 594949469, 572223024, 572225072, 590891016, 590888203, 583543293, 594032479, 575719790, 594949175, 599622652, 575717398, 599622139, 572672107,
         * 599623159, 572222039, 537047630, 599624433, 594039589, 594033447, 594038841, 575718913, 590889067, 599625956, 537045340, 590890598, 594039816, 594950932, 594952725, 583546795, 594037260,
         * 572223358, 599625179, 594949918, 572224370, 537045609, 537048169, 590887255, 575638075, 583545532, 594950659, 590887501, 594039327, 572221536, 575718188, 590890308, 537049217, 575720406,
         * 594953457, 575639238, 599625524, 575720155, 575719387, 575637955, 575640030, 594034687, 572559031, 594955244, 575639763, 572561596, 572221316, 594951895, 594035653, 537045174, 594038495,
         * 583547519, 575719659, 594033066, 594036650, 594033323, 594951349, 583547141, 594037926, 599624563, 575640473, 572223948, 572223682, 583546385, 572224961, 572562941, 594035383, 590889703,
         * 575638444, 575639467, 572673991, 583544109, 537045988, 575718583, 572559559, 594953628, 537047272, 537048809, 572561107, 572674263, 575638969, 590888655, 594035092, 575718060, 572562399]
         * # of Package impacted by PackageStream with invalid child reference: 11, ordinals=[142034, 181682, 172531, 163604, 163892, 184171, 101227, 161192, 133646, 64061, 175055]
         *
         *
         * -----
         * Find modified Package Keys between versions: 20170824033200595 to 20170824033533733
         * 1) Impacted @PrimaryKey(packageId, movieId): 391020,70248300
         * 2) Impacted @PrimaryKey(packageId, movieId): 1197312,80093149
         * 3) Impacted @PrimaryKey(packageId, movieId): 391021,70248301
         * 4) Impacted @PrimaryKey(packageId, movieId): 1069117,80011653
         * 5) Impacted @PrimaryKey(packageId, movieId): 1243457,80113750
         * 6) Impacted @PrimaryKey(packageId, movieId): 1035671,70309569
         * 7) Impacted @PrimaryKey(packageId, movieId): 1035624,70309561
         * 8) Impacted @PrimaryKey(packageId, movieId): 1035654,70309530
         * 9) Impacted @PrimaryKey(packageId, movieId): 1035622,70309563
         * 10) Impacted @PrimaryKey(packageId, movieId): 1035663,70309523
         * 11) Impacted @PrimaryKey(packageId, movieId): 1035640,70309544
         *
         * # Impacted Package: 11
         *
         * Total Modified Package Keys: 15957
         */
        long initStart = System.currentTimeMillis();
        long badDownloadableId = 572674263L;

        boolean isDebugWithDelta = true;
        long goodStateVersion = 20170824033200595L;
        long reproVersion = 20180101010000000L;

        File reporDir = new File(REPRO_DIR);
        BlobRetriever blobRetriever = new HollowFilesystemBlobRetriever(reporDir.toPath());
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobRetriever).withLocalBlobStore(reporDir).withGeneratedAPIClass(VMSHollowInputAPI.class).build();
        if (isDebugWithDelta) consumer.triggerRefreshTo(goodStateVersion);
        consumer.triggerRefreshTo(reproVersion);

        String packageTypeName = "Package";
        HollowTypeReadState packageTypeState = consumer.getStateEngine().getTypeState(packageTypeName);
        System.out.printf("Current version=%s\n", consumer.getCurrentVersionId());
        System.out.printf("Package populatedOrdinals=%s, previousOrdinals=%s\n", packageTypeState.getPopulatedOrdinals().cardinality(), packageTypeState.getPreviousOrdinals().cardinality());

        HollowReadStateEngine badReadStateEngine = consumer.getStateEngine(); //readStateEngine(REPRO_PATH.resolve("reproBadState.blob").toFile());
        HollowPrimaryKeyIndex packageIndex = new HollowPrimaryKeyIndex(badReadStateEngine, packageTypeName, "packageId", "movieId");

        int i = 0;
        VMSHollowInputAPI api = (VMSHollowInputAPI) consumer.getAPI();
        Map<String, Object[]> impactedKeys = readKeysFromFile(IMPACTED_PACKAGE_KEY_FILENAME);
        for (String key : impactedKeys.keySet()) {
            Object[] keys = impactedKeys.get(key);
            int ordinal = packageIndex.getMatchingOrdinal(keys);
            System.out.printf("%d) Package Key=%s, Ordinal=%s\n", ++i, key, ordinal);
            //System.out.println(stringifier.stringify(badReadStateEngine, "Package", ordinal));
        }
        debugPackage("REPRO STATE", consumer, badDownloadableId);

        //        {
        //            HollowPrimaryKeyIndex index = new HollowPrimaryKeyIndex(badReadStateEngine, "PackageStream", "packageId, movieId");
        //            int ordinal = index.getMatchingOrdinal(badDownloadableId);
        //            System.out.println(stringifier.stringify(badReadStateEngine, "PackageStream", ordinal));
        //            //doesStreamDeploymentOrdinalExists(badReadStateEngine, consumer.getCurrentVersionId(), packageStreamHollow._getDeployment().getOrdinal());
        //        }

        Set<Integer> badStreamDeploymentOrdinals = new HashSet<>(Arrays.asList(1382879, 2138852, 2336947, 2337328, 2338927, 2345573));
        HollowTypeReadState typeState = badReadStateEngine.getTypeState("StreamDeployment");
        BitSet populatedOrdinals = typeState.getPopulatedOrdinals();
        BitSet previousOrdinals = typeState.getPreviousOrdinals();
        System.out.println("\n\n");
        for (int o : badStreamDeploymentOrdinals) {
            System.out.printf("Bad badStreamDeploymentOrdinals=%d, exists(curr:%s, prev:%s)\n", o, populatedOrdinals.get(o), previousOrdinals.get(o));
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
        System.out.printf("\n\n----\n Total duration=%s\n", OutputUtil.formatDuration(System.currentTimeMillis() - initStart, true));
    }

    // Ported from ConverterStateEngine.removeEventsData
    private void removeEventsData(HollowReadStateEngine rEngine, HollowWriteStateEngine wEngine, Collection<Object[]> modifiedKeys) {
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

        Map<String, String> statMap = new TreeMap<>();
        recordsToRemove.entrySet().stream().forEach(entry -> {
            HollowTypeWriteState typeWriteState = wEngine.getTypeState(entry.getKey());
            BitSet bitset = entry.getValue();
            int ordinal = bitset.nextSetBit(0);
            int priorToRemoveCardinality = typeWriteState.getPopulatedBitSet().cardinality();

            boolean removedData = false;
            while (ordinal != -1) {
                typeWriteState.removeOrdinalFromThisCycle(ordinal);
                removedData = true;
                ordinal = bitset.nextSetBit(ordinal + 1);
            }

            if (removedData) {
                double percent = (double) bitset.cardinality() / (double) priorToRemoveCardinality;
                String line = String.format("\t remove: type=%s, priorToRemoveCardinality=%d, afterRemoveCardinality=%d, removeCardinality=%d, percentageRemoved=%s\n", entry.getKey(), priorToRemoveCardinality, typeWriteState.getPopulatedBitSet().cardinality(), bitset.cardinality(), percentFormat.format(percent));
                statMap.put(entry.getKey(), line);
            }
        });
        printMap(statMap);
    }

    private void printMap(Map<?, ?> map) {
        int i=0;
        for(Map.Entry<?, ?> entry : map.entrySet()) {
            System.out.println(++i + ") " + entry.getValue());
        }
    }

    private void writeStateEngineToFile(HollowWriteStateEngine wEngine, File blobFile, boolean isWriteSnapshot) throws IOException {
        long start = System.currentTimeMillis();
        HollowBlobWriter writer = new HollowBlobWriter(wEngine);
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(blobFile))) {
            if (isWriteSnapshot) {
                writer.writeSnapshot(os);
            } else {
                writer.writeDelta(os);
            }
        }
        System.out.printf("\n\n----\n writeStateEngineToFile: %s, duration=%s\n", blobFile, OutputUtil.formatDuration(System.currentTimeMillis() - start, true));
    }

    private HollowReadStateEngine readStateEngine(File blobFile) throws IOException {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(stateEngine);
        try (InputStream is = new BufferedInputStream(new FileInputStream(blobFile))) {
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
