package com.netflix.hollow.diff.ui;

import static com.netflix.hollow.diffview.FakeHollowHistoryUtil.assertUiParity;

import com.netflix.hollow.diffview.FakeHollowHistoryUtil;
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
    private TestHollowConsumer consumerFwd;
    private TestHollowConsumer consumerRev;
    private HollowHistoryUIServer historyUIServerExpected;
    private HollowHistoryUIServer historyUIServerActual;
    private HollowHistoryUI historyUiExpected;  // built using fwd deltas application only

    public HollowHistoryUITest() throws Exception {
        testBlobRetriever = new TestBlobRetriever();
        FakeHollowHistoryUtil.createDeltaChain(testBlobRetriever);
        TestHollowConsumer consumerFwdOnly = new TestHollowConsumer.Builder()
                .withBlobRetriever(testBlobRetriever)
                .build();
        consumerFwdOnly.triggerRefreshTo(1);    // snapshot

        historyUIServerExpected = new HollowHistoryUIServer(consumerFwdOnly, PORT_EXPECTED);
        consumerFwdOnly.triggerRefreshTo(2);    // delta
        consumerFwdOnly.triggerRefreshTo(3);    // delta
        consumerFwdOnly.triggerRefreshTo(4);    // delta
        consumerFwdOnly.triggerRefreshTo(5);    // delta
        historyUiExpected = historyUIServerExpected.getUI();
    }

    @Before
    public void init() {
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
    public void historyUsingFwdAndRevConsumer_revAndFwdConsumersMustBeOnSameVersionAtInit() throws Exception  {

        TestHollowConsumer consumerFwd = new TestHollowConsumer.Builder()
                .withBlobRetriever(testBlobRetriever)
                .build();
        TestHollowConsumer consumerRev = new TestHollowConsumer.Builder()
                .withBlobRetriever(testBlobRetriever)
                .build();

        consumerFwd.triggerRefreshTo(5);
        consumerRev.triggerRefreshTo(4);
        historyUIServerActual = new HollowHistoryUIServer(consumerFwd, consumerRev, PORT_ACTUAL);
    }

    private void hostUisIfPairtyCheckFails() throws Exception {
        try {
            assertUiParity(historyUiExpected, historyUIServerActual.getUI());
        } catch (Exception e) {
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
