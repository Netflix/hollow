package com.netflix.hollow.diff.ui;

import com.netflix.hollow.diff.ui.webserver.HollowDiffUIServer;
import com.netflix.hollow.diffview.FakeHollowDiffGenerator;
import com.netflix.hollow.tools.diff.HollowDiff;
import org.junit.Test;

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
