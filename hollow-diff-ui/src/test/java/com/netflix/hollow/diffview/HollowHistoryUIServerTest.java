package com.netflix.hollow.diffview;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.history.ui.HollowHistoryUIServer;
import com.netflix.hollow.tools.history.HollowHistory;
import org.junit.Test;

public class HollowHistoryUIServerTest {
    @Test
    public void test() throws Exception {
        HollowHistory hh = new HollowHistory(new HollowReadStateEngine(), Long.MAX_VALUE, 10);
        HollowHistoryUIServer server = new HollowHistoryUIServer(hh, 7882);
        server.start();
        server.stop();
    }

    @Test
    public void testBackwardsCompatibiltyWithJettyImplementation() throws Exception {
        HollowHistory hh = new HollowHistory(new HollowReadStateEngine(), Long.MAX_VALUE, 10);
        com.netflix.hollow.history.ui.jetty.HollowHistoryUIServer server = new com.netflix.hollow.history.ui.jetty.HollowHistoryUIServer(hh, 0);

        server.start();
        server.stop();
    }
}
