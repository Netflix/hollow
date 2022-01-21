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

public class HollowDiffUIServer {
    private static final UIServer.Factory FACTORY = new OptionalDependencyHelper().uiServerFactory();

    private final UIServer server;

    public HollowDiffUIServer() {
        this(8080);
    }

    public HollowDiffUIServer(int port) {
        this.server = FACTORY.newServer(port);
    }

    public HollowDiffUI addDiff(String diffPath, HollowDiff diff) {
        return addDiff(diffPath, diff, "FROM", "TO");
    }

    public HollowDiffUI addDiff(String diffPath, HollowDiff diff, String fromBlobName, String toBlobName) {
        return server.addDiff(diffPath, diff, fromBlobName, toBlobName);
    }

    public HollowDiffUIServer start() throws Exception {
        server.start();
        return this;
    }

    public void stop() throws Exception {
        server.stop();
    }

    public HollowDiffUIServer join() throws InterruptedException {
        server.join();
        return this;
    }

    static interface UIServer {
        void start() throws Exception;
        void stop() throws Exception;
        void join() throws InterruptedException;
        public HollowDiffUI addDiff(String diffPath, HollowDiff diff, String fromBlobName, String toBlobName);

        static interface Factory {
            UIServer newServer(int port);
        }
    }
}
