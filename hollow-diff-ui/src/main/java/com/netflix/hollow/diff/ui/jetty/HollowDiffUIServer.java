/*
 *
 *  Copyright 2016 Netflix, Inc.
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
import org.eclipse.jetty.server.Server;

public class HollowDiffUIServer {

    private final Server server;
    private final HollowDiffHandler handler;

    public HollowDiffUIServer() {
        this(8080);
    }

    public HollowDiffUIServer(int port) {
        this.server = new Server(port);
        this.handler = new HollowDiffHandler();
    }

    public HollowDiffUI addDiff(String diffPath, HollowDiff diff) {
        return addDiff(diffPath, diff, "FROM", "TO");
    }

    public HollowDiffUI addDiff(String diffPath, HollowDiff diff, String fromBlobName, String toBlobName) {
        return handler.getRouter().addDiff(diffPath, diff, fromBlobName, toBlobName);
    }

    public void start() throws Exception {
        server.setHandler(handler);
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }

    public void join() throws InterruptedException {
        server.join();
    }

}
