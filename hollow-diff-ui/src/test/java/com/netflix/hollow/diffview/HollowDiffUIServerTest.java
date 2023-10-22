package com.netflix.hollow.diffview;

import com.netflix.hollow.diff.ui.HollowDiffUIServer;
import com.netflix.hollow.tools.diff.HollowDiff;
import org.junit.Test;

public class HollowDiffUIServerTest {

    @Test
    public void test() throws Exception {
        HollowDiff testDiff = new FakeHollowDiffGenerator().createFakeDiff();

        HollowDiffUIServer server = new HollowDiffUIServer();

        server.addDiff("diff", testDiff);

        // server.start();
        // server.stop();
    }

    @Test
    public void testBackwardsCompatibiltyWithJettyImplementation() throws Exception {
        HollowDiff testDiff = new FakeHollowDiffGenerator().createFakeDiff();

        com.netflix.hollow.diff.ui.jetty.HollowDiffUIServer server = new com.netflix.hollow.diff.ui.jetty.HollowDiffUIServer();

        server.addDiff("diff", testDiff);

        // server.start();
        // server.stop();
    }
}
