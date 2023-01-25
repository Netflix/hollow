/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.explorer.ui.jetty;

import com.netflix.hollow.api.client.HollowClient;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.explorer.ui.HollowExplorerUI;

/**
 * @deprecated
 *
 * Use {@link com.netflix.hollow.history.ui.HollowHistoryUIServer}. This is deprecated because package name
 * contains "jetty" but jetty-server dep is no longer required. Instead, this class lives on as an adapter
 * over {@link com.netflix.hollow.history.ui.HollowHistoryUIServer}.
 */
@Deprecated
public class HollowExplorerUIServer {

    private final com.netflix.hollow.explorer.ui.HollowExplorerUIServer server;

    public HollowExplorerUIServer(HollowReadStateEngine readEngine, int port) {
        server = new com.netflix.hollow.explorer.ui.HollowExplorerUIServer( readEngine, port);
    }

    public HollowExplorerUIServer(HollowConsumer consumer, int port) {
        server = new com.netflix.hollow.explorer.ui.HollowExplorerUIServer( consumer, port);
    }

    public HollowExplorerUIServer(HollowClient client, int port) {
        server = new com.netflix.hollow.explorer.ui.HollowExplorerUIServer( client, port);
    }

    public HollowExplorerUIServer(HollowExplorerUI ui, int port) {
        server = new com.netflix.hollow.explorer.ui.HollowExplorerUIServer(ui, port);
    }

    public HollowExplorerUIServer start() throws Exception {
        server.start();
        return this;
    }

    public HollowExplorerUIServer join() throws InterruptedException {
        server.join();
        return this;
    }

    public void stop() throws Exception {
        server.stop();
    }

    public HollowExplorerUI getUI() {
        return server.getUI();
    } 

}
