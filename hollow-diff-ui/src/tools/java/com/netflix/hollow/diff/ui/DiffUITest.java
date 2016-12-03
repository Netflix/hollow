package com.netflix.hollow.diff.ui;

import org.junit.Test;

import com.netflix.hollow.diff.ui.jetty.HollowDiffUIServer;
import com.netflix.hollow.diffview.FakeHollowDiffGenerator;
import com.netflix.hollow.tools.diff.HollowDiff;


public class DiffUITest {

    @Test
    public void test() throws Exception {
        HollowDiff testDiff = new FakeHollowDiffGenerator().createFakeDiff();

        HollowDiffUIServer server = new HollowDiffUIServer();

        server.addDiff("diff", testDiff);

        server.start();
        server.join();
    }

}
