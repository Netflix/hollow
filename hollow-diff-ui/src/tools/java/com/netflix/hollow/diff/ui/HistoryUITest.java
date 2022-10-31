package com.netflix.hollow.diff.ui;

import static java.util.concurrent.TimeUnit.HOURS;

import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import com.netflix.hollow.diff.ui.temp.MyEntity;
import com.netflix.hollow.diff.ui.temp.MyNamespaceAPI;
import com.netflix.hollow.diff.ui.temp.core.HInteger;
import com.netflix.hollow.diff.ui.temp.core.MyEntityRankIndexTypeAPI;
import com.netflix.hollow.diff.ui.temp.core.MyEntityTypeAPI;
import com.netflix.hollow.explorer.ui.jetty.HollowExplorerUIServer;
import com.netflix.hollow.history.ui.jetty.HollowHistoryUIServer;
import com.netflix.hollow.test.HollowWriteStateEngineBuilder;
import com.netflix.hollow.test.consumer.TestBlobRetriever;
import com.netflix.hollow.test.consumer.TestHollowConsumer;
import com.netflix.hollow.tools.history.HollowHistory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.junit.Test;

/**
 * A tool to create a simple delta chain with fake data and spin up history UIs
 * that build history in (a) purely fwd direction, and (b) both fwd and reverse
 * directions simultaneously. This is not run as a part of the test suite.
 *
 * Ordinal maps for data used in this test-
 *
 *
 * V0
 *
 * 0: 3, 13
 * 1: 4, 44
 * 2: 15, 150
 * 3: 16, 160
 *
 *
 * V1
 *
 * 4: 1, 1
 * 5: 2, 2
 * 6: 3, 3
 * 7: 4, 4
 * 8: 5, 5
 * 9: 6, 6
 *
 *
 * V2
 *
 * 0: 2, 7
 * 1: 5, 8
 * 2: 7, 9
 * 3: 8, 10
 * 6: 3, 3
 * 9: 6, 6
 *
 *
 * V3
 *
 * 0: 2, 7
 * 3: 8, 10
 * 4: 1, 1
 * 5: 3, 11
 * 7: 6, 12
 * 8: 7, 13
 *
 *
 * V4
 * 0: 2, 7
 * 1: 1, 18
 * 2: 3, 19
 * 3: 8, 10
 * 6: 15, 13
 * 7: 6, 12
 * 9: 18, 10
 * 10: 28, 90
 *
 */
public class HistoryUITest {

    private static final String CUSTOM_VERSION_TAG = "myVersion";
    private final int MAX_STATES = 10;
    private HollowObjectSchema schema;

    static class MyEntityImpl {
        com.netflix.hollow.diff.ui.temp.MyEntity myEntityHollow;

        MyEntityImpl(com.netflix.hollow.diff.ui.temp.MyEntity myEntityHollow) {
            this.myEntityHollow = myEntityHollow;
        }
    }

    static class Result {
        Map<Integer, MyEntityImpl> result;
    }

    Map<Integer, MyEntityImpl> cacheState(MyNamespaceAPI myNamespaceAPI) {
        com.netflix.hollow.diff.ui.temp.MyEntityRankIndex entityRankIndex = myNamespaceAPI.getAllMyEntityRankIndex().iterator().next();

        Map<Integer, MyEntityImpl> result = new HashMap<>();
        for (Map.Entry<com.netflix.hollow.diff.ui.temp.MyEntity, HInteger> entry : entityRankIndex.getIndex().entrySet()) {
            result.put(
                    entry.getValue().getValue(),
                    new MyEntityImpl(entry.getKey())    // custom type that holds a reference to Hollow type
                    // new MyEntity(entry.getKey().getId(), entry.getKey().getName(), entry.getKey().getProfileId())
            );
        }
        return result;
    }

    // @Test
    public void justExplore() throws Exception {
        TestHollowConsumer longConsumer = createHollowConsumer(true);
        HollowWriteStateEngine snapshotEngine = createSnapshot(0, 5, "snapshot");
        longConsumer.applySnapshot(0, snapshotEngine);
        Result results[] = new Result[6];
        for (int i=0; i<6; i++) {
            results[i] = new Result();
        }

        HollowExplorerUIServer server = new HollowExplorerUIServer(longConsumer.getStateEngine(), 7777);
        server.start();
        server.join();

    }

    // SNAP: test
    @Test
    public void testGetAllWithApiLongevity() throws Exception {
                TestHollowConsumer longConsumer = createHollowConsumer(true);
        HollowWriteStateEngine snapshotEngine = createSnapshot(0, 5, "snapshot");
        longConsumer.applySnapshot(0, snapshotEngine);

        // HollowWriteStateEngine delta1Engine = createSnapshot(5, 12, "delta");
        // longConsumer.applyDelta(1, delta1Engine);

        HollowAPI api1 = longConsumer.getAPI();
        Thread t = new Thread(() -> {
            doSomething(api1, 1);
        });
        t.start();
        Thread.sleep(1 * 1000);

        HollowWriteStateEngine delta2Engine = createSnapshot(3, 10, "delta");
        longConsumer.applyDelta(2, delta2Engine);

        HollowAPI api2 = longConsumer.getAPI();
        doSomething(api2, 2);


        // doSomething(api1, 3);

        HollowExplorerUIServer explorer1 = new HollowExplorerUIServer(longConsumer.getStateEngine(), 7771);
        explorer1.start();
        explorer1.join();
    }

    private void doSomething(HollowAPI api, int round) {
        MyNamespaceAPI myNamespaceAPI = (MyNamespaceAPI) api;
        Set<Integer> x = myNamespaceAPI.getAllMyEntity()
                .stream()
                .map(e -> {
                    try {
                        Thread.sleep(1 * 1000);
                    } catch (Exception ex) {
                    }
                    return e.getId();
                })
                .collect(Collectors.toSet());

        // HollowTypeDataAccess tda = Objects.requireNonNull(api.getDataAccess().getTypeDataAccess("MyEntity"), "type not loaded or does not exist in dataset; type=MyEntity");
        // Set<Integer> x = getAllEntityTypeOrdinals(tda, round)
        //         .stream()
        //         .collect(Collectors.toSet());
        System.out.println("SNAP: Round " + round + " set= " + x);
    }

    private Collection<Integer> getAllEntityTypeOrdinals(HollowTypeDataAccess tda, final int round) {
        return new AllHollowRecordCollection<Integer>(tda.getTypeState()) {
            protected Integer getForOrdinal(int ordinal) {
                try {
                    Thread.sleep(1 * 1000);
                    System.out.println("SNAP: [Round " + round + "] ordinal= " + ordinal);
                } catch (Exception e) {

                }
                return ordinal;
            }
        };
    }

    // SNAP: test
    @Test
    public void testApiLongevity() throws Exception {
        TestHollowConsumer longConsumer = createHollowConsumer(true);
        HollowWriteStateEngine snapshotEngine = createSnapshot(0, 5, "snapshot");
        longConsumer.applySnapshot(0, snapshotEngine);
        Result results[] = new Result[6];
        for (int i=0; i<6; i++) {
            results[i] = new Result();
        }

        HollowHistoryUIServer server = new HollowHistoryUIServer(longConsumer, 7779);
        server.start();

        HollowExplorerUIServer explorer0 = new HollowExplorerUIServer(longConsumer.getStateEngine(), 7770);
        explorer0.start();

        results[0].result = cacheState((MyNamespaceAPI) longConsumer.getAPI());
        HollowAPI oldAPI = longConsumer.getAPI();

        // MyEntityDelegate delegate0_0 = (MyEntityDelegate) api0.getMyEntity(0).getDelegate();
        // MyEntityDelegate delegate0_1 = (MyEntityDelegate) api0.getMyEntity(1).getDelegate();
        // com.netflix.hollow.diff.ui.temp.MyEntity entity0 = api0.getMyEntity(4);
        // assign Hollow collection on profile ids for each version
        // profileIds[0] = new AllHollowRecordCollection<Integer>(api0.getDataAccess().getTypeDataAccess("ProfileId").getTypeState()) {
        //     protected Integer getForOrdinal(int ordinal) {
        //         return Integer.valueOf(typeReadState.readInt(ordinal, 0));
        //     }
        // };

//        profileIds[0] = new AllHollowRecordCollection<ProfileId>(api0.get().getTypeState()) {
//            protected MapOfDownloadableKeyToDownloadableOffload getForOrdinal(int ordinal) {
//                return getMapOfDownloadableKeyToDownloadableOffload(ordinal);
//            }
//        };

        HollowWriteStateEngine delta1Engine = createSnapshot(5, 10, "delta");
        longConsumer.applyDelta(1, delta1Engine);

        HollowExplorerUIServer explorer1 = new HollowExplorerUIServer(longConsumer.getStateEngine(), 7771);
        explorer1.start();

        results[1].result = cacheState((MyNamespaceAPI) longConsumer.getAPI());

        HollowWriteStateEngine delta2Engine = createSnapshot(10, 15, "delta");
        applyRefreshAsyncWhileInspectingCacheStatesThenBlockOnRefresh(longConsumer, 2, delta2Engine, results);
        // MyNamespaceAPI api1 = (MyNamespaceAPI) longConsumer.getAPI();
        // com.netflix.hollow.diff.ui.temp.MyEntity entity1 = api0.getMyEntity(4);
        // MyEntityDelegate delegate1_0 = (MyEntityDelegate) api1.getMyEntity(0).getDelegate();
        // MyEntityDelegate delegate1_1 = (MyEntityDelegate) api1.getMyEntity(1).getDelegate();
        results[2].result = cacheState((MyNamespaceAPI) longConsumer.getAPI());
        // MyNamespaceAPI api2 = (MyNamespaceAPI) longConsumer.getAPI();
        // com.netflix.hollow.diff.ui.temp.MyEntity entity2 = api0.getMyEntity(4);

        HollowWriteStateEngine delta3Engine = createSnapshot(15, 16, "delta");
        applyRefreshAsyncWhileInspectingCacheStatesThenBlockOnRefresh(longConsumer, 3, delta3Engine, results);
        results[3].result = cacheState((MyNamespaceAPI) longConsumer.getAPI());

        // Now try to hydrate objects from latest and past states

        // MyNamespaceAPI api3 = (MyNamespaceAPI) longConsumer.getAPI();
        // com.netflix.hollow.diff.ui.temp.MyEntity entity3 = api0.getMyEntity(4);
        // MyEntityDelegate delegate3_0 = (MyEntityDelegate) api3.getMyEntity(0).getDelegate();
        // MyEntityDelegate delegate3_1 = (MyEntityDelegate) api3.getMyEntity(1).getDelegate();

        // this part looks good
        //        for (int o = 0; o < 15; o ++) {
            //            MyEntityDelegate d = (MyEntityDelegate) api0.getMyEntity(o).getDelegate();
            //            assert (d.getTypeAPI().getProfileIdTypeAPI().getValue(o)
                    //                    == d.getTypeAPI().getAPI().getProfileId(o).getValue());
            //
            //            com.netflix.hollow.diff.ui.temp.MyEntity e = d.getTypeAPI().getAPI().getMyEntity(o);
            //            assert(e.getProfileId() == d.getTypeAPI().getProfileIdTypeAPI().getValue(o));
            //        }
        //        assert(entity0.getId() == entity1.getId());
        //        assert(entity0.getName().equals(entity1.getName()));
        //        assert(entity0.getProfileId() == entity1.getProfileId());
        //        assert(entity0.getId() == entity2.getId());
        //        assert(entity0.getName().equals(entity2.getName()));
        //        assert(entity0.getProfileId() == entity2.getProfileId());
        //        assert(entity0.getId() == entity3.getId());
        //        assert(entity0.getName().equals(entity3.getName()));
        //        assert(entity0.getProfileId() == entity3.getProfileId());

        server.join();
    }

    private void applyRefreshAsyncWhileInspectingCacheStatesThenBlockOnRefresh(TestHollowConsumer longConsumer, long v, HollowWriteStateEngine deltaEngine,
                                                                               Result[] results) throws Exception {
        System.out.println("SNAP: applying delta to V" + v);
        CompletableFuture<Void> refreshToV = CompletableFuture.runAsync(() -> {
            try {
                longConsumer.applyDelta(v, deltaEngine);
            } catch (Exception ex) {
                System.out.println("SNAP: Refresh exception" + ex);
            }
        });

        System.out.println("SNAP: meanwhile printing V0 to V" + (v-1) + " states while blocking for refresh to V" + v);
        while (!refreshToV.isDone()) {
            for (int i = 0; i <v; i ++) {
                // Thread.sleep(5);
                checkCacheState(results, i);
                // printCacheState(results, i);
            }
        }

        System.out.println("SNAP: done refreshing to V" + v);



    }

    private void checkCacheState(Result[] results, int v) {
        Result r = results[v];
        // System.out.println("SNAP: Checking cache state for version = " + v);
        Map<Integer, MyEntityImpl> m = r.result;
        for (Map.Entry<Integer, MyEntityImpl> e : m.entrySet()) {
            MyEntityImpl myEntityImpl = e.getValue();
            com.netflix.hollow.diff.ui.temp.MyEntity myEntityHollow = myEntityImpl.myEntityHollow;
            if (myEntityHollow.getProfileId() == Integer.MIN_VALUE) {
                throw new IllegalStateException("reached buggy state");
            }
            // System.out.println(String.format("%s = [%s, %s, %s] (ProfileId ordinal %s)", e.getKey(), myEntityHollow.getId(), myEntityHollow.getName(), myEntityHollow.getProfileId(), myEntityHollow.getTypeDataAccess().readOrdinal(myEntityHollow.getOrdinal(), 2)));
        }
    }
    private void printCacheState(Result[] results, int v) {
        Result r = results[v];
        System.out.println("SNAP: version = " + v);
        Map<Integer, MyEntityImpl> m = r.result;
        for (Map.Entry<Integer, MyEntityImpl> e : m.entrySet()) {
            MyEntityImpl myEntityImpl = e.getValue();
            com.netflix.hollow.diff.ui.temp.MyEntity myEntityHollow = myEntityImpl.myEntityHollow;
            System.out.println(String.format("%s = [%s, %s, %s] (ProfileId ordinal %s)", e.getKey(), myEntityHollow.getId(), myEntityHollow.getName(), myEntityHollow.getProfileId(), myEntityHollow.getTypeDataAccess().readOrdinal(myEntityHollow.getOrdinal(), 2)));
        }
    }

    // @Test
    public void generateClientCode() {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.initializeTypeState(MyEntityRankIndex.class);
        HollowAPIGenerator.Builder builder = new HollowAPIGenerator.Builder();
        String dir = "/Users/ssingh/workspace/cinder/hollow/hollow-diff-ui/src/tools/java/com/netflix/hollow/diff/ui/temp";
        builder.withAPIClassname("MyNamespaceAPI")
                .withPackageName("com.netflix.hollow.diff.ui.temp")
                .withDataModel(writeEngine)
                .withPackageGrouping()
                .withErgonomicShortcuts()
                .withDestination(dir)
                .build();
        HollowAPIGenerator generator = builder.build();
        try {
            generator.generateSourceFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Original schema: https://stash.corp.netflix.com/users/akhaku/repos/ocp/browse/c4/common/src/test/java/com/netflix/streaming/c4/resources/DownloadableKey.java
    @SuppressWarnings("FieldCanBeLocal")
    @HollowPrimaryKey(fields = {"id.value", "name.value", "profileId.value"})
    public static class MyEntity {
        private final Integer id;
        private final String name;
        @HollowTypeName(name = "profileId")
        private final Integer profileId;

        public MyEntity(Integer id, String name, Integer profileId) {
            this.id = id;
            this.name = name;
            this.profileId = profileId;
        }
    }

    public static class MyEntityRankIndex {
        private Map<MyEntity, Integer> index;

        public MyEntityRankIndex(Map<MyEntity, Integer> index) {
            this.index = index;
        }

        public Map<MyEntity, Integer> getIndex() {
            return index;
        }
    }

    private static HollowWriteStateEngine createSnapshot(int start, int end, String name) {
        Map<MyEntity, Integer> m = new HashMap<MyEntity, Integer>();

        for (int id=start; id<end; id++) {
            m.put(new MyEntity(id, String.format("%s_%s", name, id), id), id);
        }

        MyEntityRankIndex topLevelObject = new MyEntityRankIndex(m);
        // Object[] objects = IntStream.range(1, 2)
        //         .mapToObj(id -> m)
        //         .toArray();

        return new HollowWriteStateEngineBuilder(Collections.singleton(MyEntityRankIndex.class)).add(topLevelObject).build();
    }

    private static TestHollowConsumer createHollowConsumer(boolean longevity) {
        return new TestHollowConsumer.Builder()
                .withBlobRetriever(new TestBlobRetriever())
                .withGeneratedAPIClass(MyNamespaceAPI.class)
                .withObjectLongevityConfig(
                        new HollowConsumer.ObjectLongevityConfig() {
                            public long usageDetectionPeriodMillis() {
                                return 1_000L;
                            }

                            public long gracePeriodMillis() {
                                return HOURS.toMillis(2);
                            }

                            public boolean forceDropData() {
                                return true;
                            }

                            public boolean enableLongLivedObjectSupport() {
                                return longevity;
                            }

                            public boolean enableExpiredUsageStackTraces() {
                                return false;
                            }

                            public boolean dropDataAutomatically() {
                                return true;
                            }
                        })
                .build();

    }

    @Test
    public void startServerOnPorts7777And7778() throws Exception {

        HollowHistory historyD = createHistoryD();
        HollowHistoryUIServer serverD = new HollowHistoryUIServer(historyD, 7777);
        serverD.start();

        HollowHistory historyR = createHistoryBidirectional();
        HollowHistoryUIServer serverR = new HollowHistoryUIServer(historyR, 7778);
        serverR.start();

        // optionally, test dropping the oldest state
        // historyR.removeHistoricalStates(1);

        serverD.join();
        serverR.join();
    }

    private HollowHistory createHistoryBidirectional() throws IOException {
        HollowHistory history;
        HollowWriteStateEngine stateEngine;

        {
            schema = new HollowObjectSchema("TypeA", 2);
            stateEngine = new HollowWriteStateEngine();
            schema.addField("a1", HollowObjectSchema.FieldType.INT);
            schema.addField("a2", HollowObjectSchema.FieldType.INT);
            stateEngine.addTypeState(new HollowObjectTypeWriteState(schema));

            // v0
            stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v0");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 13 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 4, 44 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 15, 150 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 16, 160 });
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v0 = new ByteArrayOutputStream();
            HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
            writer.writeSnapshot(baos_v0);
            stateEngine.prepareForNextCycle();

            // v1
            stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v1");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 2 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 3 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 4, 4 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 5, 5 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 6 });
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v1 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v0_to_v1 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v1_to_v0 = new ByteArrayOutputStream();
            writer = new HollowBlobWriter(stateEngine);
            writer.writeSnapshot(baos_v1);
            writer.writeDelta(baos_v0_to_v1);
            writer.writeReverseDelta(baos_v1_to_v0);
            stateEngine.prepareForNextCycle();

            // v2
            stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v2");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 3 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 5, 8 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 6 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 7, 9 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v2 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v1_to_v2 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v2_to_v1 = new ByteArrayOutputStream();
            writer.writeSnapshot(baos_v2);
            writer.writeDelta(baos_v1_to_v2);
            writer.writeReverseDelta(baos_v2_to_v1);
            stateEngine.prepareForNextCycle();

            // v3
            stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v3");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 11 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 12 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 7, 13 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v2_to_v3 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v3_to_v2 = new ByteArrayOutputStream();
            writer.writeDelta(baos_v2_to_v3);
            writer.writeReverseDelta(baos_v3_to_v2);

            // v4
            stateEngine.prepareForNextCycle();
            stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v4");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 18 });  // 0
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });   // 1
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 19 });  // 2
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 12 });  // 3
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 15, 13 }); // 4
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });  // 5
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 18, 10 }); // 6
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 28, 90 }); // 7
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v4 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v4_to_v3 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v3_to_v4 = new ByteArrayOutputStream();
            writer.writeSnapshot(baos_v4);
            writer.writeDelta(baos_v3_to_v4);
            writer.writeReverseDelta(baos_v4_to_v3);

            // build history bi-directionally
            HollowReadStateEngine fwdReadStateEngine = new HollowReadStateEngine();
            HollowReadStateEngine revReadStateEngine = new HollowReadStateEngine();
            HollowBlobReader fwdReader = new HollowBlobReader(fwdReadStateEngine);
            HollowBlobReader revReader = new HollowBlobReader(revReadStateEngine);
            fwdReader.readSnapshot(HollowBlobInput.serial(baos_v2.toByteArray()));
            System.out.println("Ordinals populated in fwdReadStateEngine: ");
            exploreOrdinals(fwdReadStateEngine);
            revReader.readSnapshot(HollowBlobInput.serial(baos_v2.toByteArray()));
            System.out.println("Ordinals populated in revReadStateEngine (same as fwdReadStateEngine): ");
            exploreOrdinals(revReadStateEngine);
            history = new HollowHistory(fwdReadStateEngine, 2L, MAX_STATES, true);
            history.getKeyIndex().addTypeIndex("TypeA", "a1");
            history.getKeyIndex().indexTypeField("TypeA", "a1");
            history.initializeReverseStateEngine(revReadStateEngine, 2L);

            fwdReader.applyDelta(HollowBlobInput.serial(baos_v2_to_v3.toByteArray()));
            exploreOrdinals(fwdReadStateEngine);
            history.deltaOccurred(3L);

            revReader.applyDelta(HollowBlobInput.serial(baos_v2_to_v1.toByteArray()));
            exploreOrdinals(revReadStateEngine);
            history.reverseDeltaOccurred(1L);

            fwdReader.applyDelta(HollowBlobInput.serial(baos_v3_to_v4.toByteArray()));
            exploreOrdinals(fwdReadStateEngine);
            history.deltaOccurred(4L);

            revReader.applyDelta(HollowBlobInput.serial(baos_v1_to_v0.toByteArray()));
            exploreOrdinals(revReadStateEngine);
            history.reverseDeltaOccurred(0L);
        }

        return history;
    }

    private void exploreOrdinals(HollowReadStateEngine readStateEngine) {
        System.out.println("CUSTOM_VERSION_TAG= " + readStateEngine.getHeaderTags().get(CUSTOM_VERSION_TAG));
        for (HollowTypeReadState typeReadState : readStateEngine.getTypeStates()) {
            BitSet populatedOrdinals = typeReadState.getPopulatedOrdinals();
            System.out.println("SNAP: PopulatedOrdinals= " + populatedOrdinals);
            int ordinal = populatedOrdinals.nextSetBit(0);
            while (ordinal != -1) {
                HollowObjectTypeReadState o = (HollowObjectTypeReadState) typeReadState;
                System.out.println(String.format("%s: %s, %s", ordinal, o.readInt(ordinal, 0), o.readInt(ordinal, 1)));
                ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
            }
        }
    }

    private static void addRec(HollowWriteStateEngine stateEngine, HollowObjectSchema schema, String[] names, int[] vals) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        for (int i = 0; i < names.length; i++) {
            rec.setInt(names[i], vals[i]);
        }
        stateEngine.add(schema.getName(), rec);
    }

    private HollowHistory createHistoryD() throws IOException {
        HollowHistory history;
        HollowReadStateEngine readStateEngine;
        HollowBlobReader reader;
        HollowWriteStateEngine stateEngine;

        {
            schema = new HollowObjectSchema("TypeA", 2);
            stateEngine = new HollowWriteStateEngine();
            schema.addField("a1", HollowObjectSchema.FieldType.INT);
            schema.addField("a2", HollowObjectSchema.FieldType.INT);

            //attach schema to write state engine
            stateEngine.addTypeState(new HollowObjectTypeWriteState(schema));

            // v0
            stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v0");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 13 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 4, 44 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 15, 150 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 16, 160 });
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v0 = new ByteArrayOutputStream();
            HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
            writer.writeSnapshot(baos_v0);
            stateEngine.prepareForNextCycle();

            // v1
            stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v1");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 2 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 3 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 4, 4 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 5, 5 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 6 });
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v0_to_v1 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v1_to_v0 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v1 = new ByteArrayOutputStream();
            writer = new HollowBlobWriter(stateEngine);
            writer.writeSnapshot(baos_v1);
            writer.writeDelta(baos_v0_to_v1);
            writer.writeReverseDelta(baos_v1_to_v0);

            stateEngine.prepareForNextCycle();

            // v2
            stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v2");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 3 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 5, 8 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 6 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 7, 9 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v2 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v1_to_v2 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v2_to_v1 = new ByteArrayOutputStream();
            writer.writeSnapshot(baos_v2);
            writer.writeDelta(baos_v1_to_v2);
            writer.writeReverseDelta(baos_v2_to_v1);
            stateEngine.prepareForNextCycle();

            // v3
            stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v3");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 1 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 11 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 12 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 7, 13 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v3 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v2_to_v3 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v3_to_v2 = new ByteArrayOutputStream();
            writer.writeSnapshot(baos_v3);
            writer.writeDelta(baos_v2_to_v3);
            writer.writeReverseDelta(baos_v3_to_v2);
            stateEngine.prepareForNextCycle();

            // v4
            stateEngine.addHeaderTag(CUSTOM_VERSION_TAG, "v4");
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 1, 18 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 2, 7 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 3, 19 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 6, 12 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 15, 13 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 8, 10 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 18, 10 });
            addRec(stateEngine, schema, new String[] { "a1", "a2" }, new int[] { 28, 90 });
            stateEngine.prepareForWrite();
            ByteArrayOutputStream baos_v4 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v3_to_v4 = new ByteArrayOutputStream();
            ByteArrayOutputStream baos_v4_to_v3 = new ByteArrayOutputStream();
            writer.writeDelta(baos_v3_to_v4);
            writer.writeReverseDelta(baos_v4_to_v3);
            writer.writeSnapshot(baos_v4);

            // Build history
            readStateEngine = new HollowReadStateEngine();
            reader = new HollowBlobReader(readStateEngine);
            reader.readSnapshot(HollowBlobInput.serial(baos_v0.toByteArray()));
            history = new HollowHistory(readStateEngine, 0L, MAX_STATES);
            history.getKeyIndex().addTypeIndex("TypeA", "a1");
            reader.applyDelta(HollowBlobInput.serial(baos_v0_to_v1.toByteArray()));
            history.deltaOccurred(1L);
            reader.applyDelta(HollowBlobInput.serial(baos_v1_to_v2.toByteArray()));
            history.deltaOccurred(2L);
            reader.applyDelta(HollowBlobInput.serial(baos_v2_to_v3.toByteArray()));
            history.deltaOccurred(3L);
            reader.applyDelta(HollowBlobInput.serial(baos_v3_to_v4.toByteArray()));
            history.deltaOccurred(4L);
        }

        return history;
    }
}
