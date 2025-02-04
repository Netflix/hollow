package com.netflix.hollow.diffview;

import static com.netflix.hollow.diffview.FakeHollowHistoryUtil.assertUiParity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import com.netflix.hollow.history.ui.HollowHistoryUI;
import com.netflix.hollow.history.ui.jetty.HollowHistoryUIServer;
import com.netflix.hollow.test.InMemoryBlobStore;
import com.netflix.hollow.test.consumer.TestBlobRetriever;
import com.netflix.hollow.test.consumer.TestHollowConsumer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class HollowHistoryUITest {

    private final int PORT_EXPECTED = 7777;
    private final int PORT_ACTUAL = 7778;

    private TestBlobRetriever testBlobRetriever;
    private TestHollowConsumer consumerExpected;    // builds history using only deltas
    private TestHollowConsumer consumerFwd;
    private TestHollowConsumer consumerRev;
    private HollowHistoryUIServer historyUIServerExpected;
    private HollowHistoryUIServer historyUIServerActual;
    private HollowHistoryUI historyUiExpected;  // built using fwd deltas application only

    public HollowHistoryUITest() throws Exception {
        testBlobRetriever = new TestBlobRetriever();
        FakeHollowHistoryUtil.createDeltaChain(testBlobRetriever);
    }

    @Before
    public void init() {
        consumerExpected = new TestHollowConsumer.Builder()
                .withBlobRetriever(testBlobRetriever)
                .build();
        consumerExpected.triggerRefreshTo(1);
        historyUIServerExpected = new HollowHistoryUIServer(consumerExpected, PORT_EXPECTED);
        consumerExpected.triggerRefreshTo(2);
        consumerExpected.triggerRefreshTo(3);
        consumerExpected.triggerRefreshTo(4);
        consumerExpected.triggerRefreshTo(5);
        historyUiExpected = historyUIServerExpected.getUI();

        consumerFwd = new TestHollowConsumer.Builder()
                .withBlobRetriever(testBlobRetriever)
                .build();
        consumerRev = new TestHollowConsumer.Builder()
                .withBlobRetriever(testBlobRetriever)
                .build();
    }

    @Test
    public void historyUsingOnlyFwdConsumer() throws Exception  {
        consumerFwd.triggerRefreshTo(1);    // snapshot

        historyUIServerActual = new HollowHistoryUIServer(consumerFwd, PORT_ACTUAL);

        consumerFwd.triggerRefreshTo(2);    // delta
        consumerFwd.triggerRefreshTo(3);    // delta
        consumerFwd.triggerRefreshTo(4);    // delta
        consumerFwd.triggerRefreshTo(5);    // delta

        hostUisIfPairtyCheckFails();
    }

    @Test
    public void historyUsingFwdAndRevConsumer_onlyRevDeltasApplied() throws Exception  {
        consumerFwd.triggerRefreshTo(5);
        consumerRev.triggerRefreshTo(5);

        historyUIServerActual = new HollowHistoryUIServer(consumerFwd, consumerRev, PORT_ACTUAL);

        consumerRev.triggerRefreshTo(4);
        consumerRev.triggerRefreshTo(3);
        consumerRev.triggerRefreshTo(2);
        consumerRev.triggerRefreshTo(1);

        hostUisIfPairtyCheckFails();
    }

    @Test
    public void historyUsingFwdAndRevConsumer_bothFwdAndRevDeltasApplied_FwdFirst() throws Exception  {
        consumerFwd.triggerRefreshTo(3);
        consumerRev.triggerRefreshTo(3);

        historyUIServerActual = new HollowHistoryUIServer(consumerFwd, consumerRev, PORT_ACTUAL);

        consumerFwd.triggerRefreshTo(4);
            consumerRev.triggerRefreshTo(2);
        consumerFwd.triggerRefreshTo(5);
            consumerRev.triggerRefreshTo(1);

        hostUisIfPairtyCheckFails();
    }

    @Test
    public void historyUsingFwdAndRevConsumer_traversingStatesAlreadyVisited() throws Exception {
        consumerFwd.triggerRefreshTo(3);
        consumerRev.triggerRefreshTo(3);

        historyUIServerActual = new HollowHistoryUIServer(consumerFwd, consumerRev, PORT_ACTUAL);

        consumerFwd.triggerRefreshTo(4);
            consumerRev.triggerRefreshTo(2);
        consumerFwd.triggerRefreshTo(5);
        consumerFwd.triggerRefreshTo(4);
        consumerFwd.triggerRefreshTo(3);
        consumerFwd.triggerRefreshTo(4);
        consumerFwd.triggerRefreshTo(5);
            consumerRev.triggerRefreshTo(1);

        consumerExpected.triggerRefreshTo(4);
        consumerExpected.triggerRefreshTo(3);
        consumerExpected.triggerRefreshTo(4);
        consumerExpected.triggerRefreshTo(5);

        hostUisIfPairtyCheckFails();
    }

    @Test
    public void historyUsingFwdAndRevConsumer_bothFwdAndRevDeltasApplied_RevFirst() throws Exception  {
        consumerFwd.triggerRefreshTo(3);
        consumerRev.triggerRefreshTo(3);

        historyUIServerActual = new HollowHistoryUIServer(consumerFwd, consumerRev, PORT_ACTUAL);

        consumerRev.triggerRefreshTo(2);
            consumerFwd.triggerRefreshTo(4);
        consumerRev.triggerRefreshTo(1);
            consumerFwd.triggerRefreshTo(5);

        hostUisIfPairtyCheckFails();
    }

    @Test
    public void historyUsingFwdAndRevConsumer_doubleSnapshotInFwd() throws Exception {
        consumerFwd.triggerRefreshTo(3);
        consumerRev.triggerRefreshTo(3);

        historyUIServerActual = new HollowHistoryUIServer(consumerFwd, consumerRev, PORT_ACTUAL);

        consumerRev.triggerRefreshTo(2);
        consumerFwd.triggerRefreshTo(4);
        consumerRev.triggerRefreshTo(1);
        consumerFwd.triggerRefreshTo(5);

        consumerFwd.triggerRefreshTo(6);    // double snapshot on fwd consumer in bidirectional history (supported)
        consumerExpected.triggerRefreshTo(6);   // double snapshot for fwd-only history (supported)

        hostUisIfPairtyCheckFails();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void historyUsingFwdAndRevConsumer_doubleSnapshotInRev() {
        consumerFwd.triggerRefreshTo(3);
        consumerRev.triggerRefreshTo(3);

        historyUIServerActual = new HollowHistoryUIServer(consumerFwd, consumerRev, PORT_ACTUAL);

        consumerRev.triggerRefreshTo(2);
        consumerFwd.triggerRefreshTo(4);
        consumerRev.triggerRefreshTo(1);
        consumerFwd.triggerRefreshTo(5);

        consumerRev.triggerRefreshTo(0);   // double snapshot in rev direction (not supported)
    }

    @Test
    public void historyUsingFwdAndRevConsumer_backwardsCompatbileSchemaChange() throws Exception {
        consumerFwd.triggerRefreshTo(7);    // version in whcih actor type was introduced
        consumerRev.triggerRefreshTo(7);

        historyUIServerActual = new HollowHistoryUIServer(consumerFwd, consumerRev, PORT_ACTUAL);

        consumerRev.triggerRefreshTo(6);

        assertNotNull(historyUIServerActual.getUI().getHistory().getHistoricalState(7).getDataAccess()
                .getTypeDataAccess("Actor").getDataAccess());
    }

    @Test
    public void historyUsingFwdAndRevConsumer_removeOldestState() throws Exception {
        consumerFwd.triggerRefreshTo(3);
        consumerRev.triggerRefreshTo(3);

        historyUIServerActual = new HollowHistoryUIServer(consumerFwd, consumerRev, PORT_ACTUAL);

        consumerRev.triggerRefreshTo(2);
        consumerFwd.triggerRefreshTo(4);
        consumerRev.triggerRefreshTo(1);
        consumerFwd.triggerRefreshTo(5);

        // drop 1 state
        historyUIServerActual.getUI().getHistory().removeHistoricalStates(1);

        // expected history is built for versions 2 through 5
        consumerExpected = new TestHollowConsumer.Builder()
                .withBlobRetriever(testBlobRetriever)
                .build();
        consumerExpected.triggerRefreshTo(2);
        historyUIServerExpected = new HollowHistoryUIServer(consumerExpected, PORT_EXPECTED);
        consumerExpected.triggerRefreshTo(3);
        consumerExpected.triggerRefreshTo(4);
        consumerExpected.triggerRefreshTo(5);
        historyUiExpected = historyUIServerExpected.getUI();

        hostUisIfPairtyCheckFails();
    }

    @Test(expected=NullPointerException.class)
    public void historyUsingFwdAndRevConsumer_noPastVersionsAvailableAtInit()  {
        // consumerFwd and consumerRev haven't incurred snapshot load yet
        historyUIServerActual = new HollowHistoryUIServer(consumerFwd, consumerRev, PORT_ACTUAL);
    }

    @Test(expected=NullPointerException.class)
    public void historyUsingFwdOnly_noPastVersionsAvailableAtInit()  {
        // consumerFwd hasn't incurred snapshot load yet
        historyUIServerActual = new HollowHistoryUIServer(consumerFwd, PORT_ACTUAL);
    }

    @Test(expected=UnsupportedOperationException.class)
    public void historyUsingFwdAndRevConsumer_revConsumerMustBeInitialized() throws Exception  {

        TestHollowConsumer consumerFwd = new TestHollowConsumer.Builder()
                .withBlobRetriever(testBlobRetriever)
                .build();
        TestHollowConsumer consumerRev = new TestHollowConsumer.Builder()
                .withBlobRetriever(testBlobRetriever)
                .build();

        consumerFwd.triggerRefreshTo(5);
        historyUIServerActual = new HollowHistoryUIServer(consumerFwd, consumerRev, PORT_ACTUAL);

        // snapshots are only supported in the fwd direction, rev consumer should have been initialized
        consumerRev.triggerRefreshTo(5);
    }

    @Test(expected=IllegalStateException.class)
    public void historyUsingFwdAndRevConsumer_revAndFwdConsumersMustBeOnSameVersionAtInit()  {

        TestHollowConsumer consumerFwd = new TestHollowConsumer.Builder()
                .withBlobRetriever(testBlobRetriever)
                .build();
        TestHollowConsumer consumerRev = new TestHollowConsumer.Builder()
                .withBlobRetriever(testBlobRetriever)
                .build();

        consumerFwd.triggerRefreshTo(5);
        consumerRev.triggerRefreshTo(3);
        historyUIServerActual = new HollowHistoryUIServer(consumerFwd, consumerRev, PORT_ACTUAL);
    }

    private void hostUisIfPairtyCheckFails() throws Exception {
        try {
            assertUiParity(historyUiExpected, historyUIServerActual.getUI());
        } catch (AssertionError | Exception e) {
            System.out.println(String.format("Error when comparing expected and actual history UIs for parity. " +
                            "Expected and actual history UIs are hosted at ports %s and %s respectively. " +
                            "Be sure to open in different browsers for isolated sessions state stored in cookie which " +
                            "could affect the links generated in the output html",
                    PORT_EXPECTED, PORT_ACTUAL));
            e.printStackTrace();
            historyUIServerExpected.start();
            historyUIServerActual.start();
            historyUIServerActual.join();
        }
    }

    @Test
    public void testDeltaChainWithMultipleReshardingInvocations() throws Exception {

        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();
        int numCycles = 100;
        int numRecordsMin = 1;
        long v = 0;

        HollowProducer p = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager)
                .withTypeResharding(true).withTargetMaxTypeShardSize(32).build();

        HollowConsumer c = HollowConsumer.withBlobRetriever(blobStore)
                .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
                    @Override
                    public boolean allowDoubleSnapshot() {
                        return false;
                    }
                    @Override
                    public int maxDeltasBeforeDoubleSnapshot() {
                        return Integer.MAX_VALUE;
                    }
                })
                .build();

        long startVersion = 0;
        Map<String, Set<Integer>> numShardsExercised = new HashMap<>();
        for (int n = 0; n < numCycles; n ++) {
            int numRecords = numRecordsMin + new Random().nextInt(100);
            v = p.runCycle(state -> {
                for (int i = 0; i < numRecords; i ++) {
                    final long val = new Long(i);
                    state.add(new HasAllTypeStates(
                            new CustomReferenceType(val),
                            new HashSet<>(Arrays.asList("e" + val)),
                            Arrays.asList(i),
                            new HashMap<String, Long>(){{put("k"+val, new Long(val));}}
                    ));

                }
            });
            if (n == 0)
                startVersion = v;

            c.triggerRefreshTo(v);

            for (HollowTypeReadState type : c.getStateEngine().getTypeStates()) {
                String t = type.getSchema().getName();
                if (!numShardsExercised.containsKey(t)) {
                    numShardsExercised.put(t, new HashSet<>());
                }
                numShardsExercised.get(t).add(new Integer(type.numShards()));

            }
        }
        long endVersion = v;


        assertEquals(8, numShardsExercised.size());
        for (HollowTypeReadState type : c.getStateEngine().getTypeStates()) {
            assertTrue(numShardsExercised.get(type.getSchema().getName()).size() > 1);
        }

        com.netflix.hollow.history.ui.HollowHistoryUIServer server = new com.netflix.hollow.history.ui.HollowHistoryUIServer(c, 7890);
        server.start();
        c.triggerRefreshTo(startVersion);
        assertEquals(startVersion, c.getCurrentVersionId());
        c.triggerRefreshTo(endVersion);
        assertEquals(endVersion, c.getCurrentVersionId());
        c.triggerRefreshTo(startVersion);
        assertEquals(startVersion, c.getCurrentVersionId());
        server.join();
    }



    private static class HasNonObjectField {
        public Integer objectField;
        public Set<Long> nonObjectField;

        public HasNonObjectField(int objectField, Set<Long> nonObjectField) {
            this.objectField = objectField;
            this.nonObjectField = nonObjectField;
        }
    }

    @SuppressWarnings("unused")
    @HollowTypeName(name = "TestPojo")
    private static class TestPojoV1 {
        public int id;
        public int v1;

        TestPojoV1(int id, int v1) {
            super();
            this.id = id;
            this.v1 = v1;
        }
    }

    @SuppressWarnings("unused")
    @HollowTypeName(name = "TestPojo")
    private static class TestPojoV2 {
        int id;
        int v1;
        int v2;

        TestPojoV2(int id, int v1, int v2) {
            this.id = id;
            this.v1 = v1;
            this.v2 = v2;
        }
    }
}
