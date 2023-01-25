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
package com.netflix.hollow.diff.ui;

import com.netflix.hollow.tools.diff.HollowDiff;

public class HollowDiffUIServer {

    private final DiffUIServer server;

    public HollowDiffUIServer() {
        this(8080);
    }

    public HollowDiffUIServer(int port) {
        this.server = new DiffUIWebServer(new HollowDiffUIRouter(), port);
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

    public HollowDiffUIServer join() throws InterruptedException {
        server.join();
        return this;
    }

    public void stop() throws Exception {
        server.stop();
    }

}
