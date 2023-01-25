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
package com.netflix.hollow.diff.ui.jetty;

import com.netflix.hollow.diff.ui.HollowDiffUI;
import com.netflix.hollow.tools.diff.HollowDiff;
/**
 * @deprecated
 *
 * Use {@link com.netflix.hollow.history.ui.HollowHistoryUIServer}. This is deprecated because package name
 * contains "jetty" but jetty-server dep is no longer required. Instead, this class lives on as an adapter
 * over {@link com.netflix.hollow.history.ui.HollowHistoryUIServer}.
 */
@Deprecated
public class HollowDiffUIServer {

    private final com.netflix.hollow.diff.ui.HollowDiffUIServer server;

    public HollowDiffUIServer() {
        server = new com.netflix.hollow.diff.ui.HollowDiffUIServer();
    }

    public HollowDiffUIServer(int port) {
        server = new com.netflix.hollow.diff.ui.HollowDiffUIServer(port);
    }

    public HollowDiffUI addDiff(String diffPath, HollowDiff diff) {
        return server.addDiff(diffPath, diff);
    }

    public HollowDiffUI addDiff(String diffPath, HollowDiff diff, String fromBlobName, String toBlobName) {
        return server.addDiff(diffPath, diff, fromBlobName, toBlobName);
    }

    public HollowDiffUIServer start() throws Exception {
        server.start();
        return this;
    }

    public HollowDiffUIServer join() throws InterruptedException {
        server.join();
        return this;
    }

    public void stop() throws Exception {
        server.stop();
    }

}
