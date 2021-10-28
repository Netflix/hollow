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
package com.netflix.hollow.diff.ui.webserver;


import com.netflix.hollow.diff.ui.HollowDiffUI;
import com.netflix.hollow.diff.ui.HollowDiffUIRouter;
import com.netflix.hollow.tools.diff.HollowDiff;
import com.netflix.hollow.ui.HttpHandlerWithServletSupport;
import com.netflix.hollow.ui.UIBaseWebServer;

final class UIWebServer implements DiffUIServer {
    private final HollowDiffUIRouter router;
    private final UIBaseWebServer server;

    public UIWebServer(HollowDiffUIRouter theRouter, int port) {
        this.router = theRouter;
        server = new UIBaseWebServer(new HttpHandlerWithServletSupport(theRouter), port);
    }

    public HollowDiffUI addDiff(String diffPath, HollowDiff diff, String fromBlobName, String toBlobName) {
        return this.router.addDiff(diffPath, diff, fromBlobName, toBlobName);
    }

    public void start() throws Exception {
        server.start();
    }
    public void stop() throws Exception {
        server.stop();
    }
    public void join() throws InterruptedException {
        server.join();
    }
}
