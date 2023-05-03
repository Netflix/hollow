package com.netflix.hollow.explorer.ui;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import org.junit.Test;

public class HollowExplorerUIServerTest {
    @Test
    public void test() throws Exception {
        HollowExplorerUIServer server = new HollowExplorerUIServer(new HollowReadStateEngine(), 7890);

        server.start();
        server.stop();
    }

    @Test
    public void testBackwardsCompatibiltyWithJettyImplementation() throws Exception {
        com.netflix.hollow.explorer.ui.jetty.HollowExplorerUIServer server = new com.netflix.hollow.explorer.ui.jetty.HollowExplorerUIServer(new HollowReadStateEngine(), 7890);

        server.start();
        server.stop();
    }
}