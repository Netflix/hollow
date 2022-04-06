package com.netflix.hollow.diff.ui;

import com.netflix.hollow.diffview.FakeHollowHistoryGenerator;
import com.netflix.hollow.history.ui.jetty.HollowHistoryUIServer;
import com.netflix.hollow.test.consumer.TestBlobRetriever;
import com.netflix.hollow.test.consumer.TestHollowConsumer;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

public class HollowHistoryUITest {

    private TestBlobRetriever testBlobRetriever;

    @Before
    public void init() throws IOException {

        testBlobRetriever = new TestBlobRetriever();
        FakeHollowHistoryGenerator.createDeltaChain(testBlobRetriever);
    }

    @Test
    public void fooRev() throws Exception {
        TestHollowConsumer consumerFooRev = new TestHollowConsumer.Builder()
                .withBlobRetriever(testBlobRetriever)
                .build();
        consumerFooRev.triggerRefreshTo(5);

        consumerFooRev.triggerRefreshTo(4);
        consumerFooRev.triggerRefreshTo(5);
        consumerFooRev.triggerRefreshTo(4);

        consumerFooRev.triggerRefreshTo(3);
        consumerFooRev.triggerRefreshTo(2);
        consumerFooRev.triggerRefreshTo(1);
    }

    @Test
    public void historyUsingOnlyFwdConsumer() throws Exception  {
        TestHollowConsumer consumerFwd = new TestHollowConsumer.Builder()
                .withBlobRetriever(testBlobRetriever)
                .build();
        consumerFwd.triggerRefreshTo(1);    // snapshot
        HollowHistoryUIServer historyUIServer = new HollowHistoryUIServer(consumerFwd, 7777);
        historyUIServer.start();

        consumerFwd.triggerRefreshTo(2);    // delta
        consumerFwd.triggerRefreshTo(3);    // delta
        consumerFwd.triggerRefreshTo(4);    // delta
        consumerFwd.triggerRefreshTo(5);    // delta

        historyUIServer.join();
    }

    @Test
    public void historyUsingFwdAndRevConsumer_onlyRevDeltasApplied() throws Exception  {

        TestHollowConsumer consumerFwd = new TestHollowConsumer.Builder()
                .withBlobRetriever(testBlobRetriever)
                .build();
        TestHollowConsumer consumerRev = new TestHollowConsumer.Builder()
                .withBlobRetriever(testBlobRetriever)
                .build();

        consumerFwd.triggerRefreshTo(5);
        consumerRev.triggerRefreshTo(5);

        HollowHistoryUIServer historyUIServer = new HollowHistoryUIServer(consumerFwd, consumerRev, 7777);
        historyUIServer.start();

        consumerRev.triggerRefreshTo(4);
        consumerRev.triggerRefreshTo(3);
        consumerRev.triggerRefreshTo(2);
        consumerRev.triggerRefreshTo(1);

        historyUIServer.join();
    }
}
