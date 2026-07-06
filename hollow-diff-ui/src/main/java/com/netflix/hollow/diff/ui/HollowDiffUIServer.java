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
import com.netflix.hollow.tools.diff.report.HollowDiffReportMetadata;
import com.netflix.hollow.tools.diff.report.HollowDiffReportOptions;

public class HollowDiffUIServer {

    static final String DEFAULT_FROM_LABEL = "FROM";
    static final String DEFAULT_TO_LABEL = "TO";

    private final DiffUIServer server;

    public HollowDiffUIServer() {
        this(8080);
    }

    public HollowDiffUIServer(int port) {
        this.server = new DiffUIWebServer(new HollowDiffUIRouter(), port);
    }

    public HollowDiffUI addDiff(String diffPath, HollowDiff diff) {
        return addDiff(diffPath, diff, DEFAULT_FROM_LABEL, DEFAULT_TO_LABEL);
    }

    public HollowDiffUI addDiff(String diffPath, HollowDiff diff, String fromBlobName, String toBlobName) {
        return server.addDiff(diffPath, diff, fromBlobName, toBlobName, null, null);
    }

    public HollowDiffUI addDiff(
            String diffPath,
            HollowDiff diff,
            HollowDiffReportMetadata metadata,
            HollowDiffReportOptions options) {
        return server.addDiff(
                diffPath,
                diff,
                blobLabel(metadata != null ? metadata.getFromNamespace() : null, DEFAULT_FROM_LABEL),
                blobLabel(metadata != null ? metadata.getToNamespace() : null, DEFAULT_TO_LABEL),
                metadata,
                options);
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

    private static String blobLabel(String namespace, String defaultLabel) {
        return namespace != null ? namespace : defaultLabel;
    }
}
