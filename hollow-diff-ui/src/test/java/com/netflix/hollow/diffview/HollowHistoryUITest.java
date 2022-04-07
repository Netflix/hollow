package com.netflix.hollow.diffview;

import static com.netflix.hollow.diffview.FakeHollowHistoryUtil.assertUiParity;

import com.netflix.hollow.history.ui.HollowHistoryUI;
import com.netflix.hollow.history.ui.jetty.HollowHistoryUIServer;
import com.netflix.hollow.test.consumer.TestBlobRetriever;
import com.netflix.hollow.test.consumer.TestHollowConsumer;
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
            consumerRev.triggerRefreshTo(2);
            consumerRev.triggerRefreshTo(1);

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
}
