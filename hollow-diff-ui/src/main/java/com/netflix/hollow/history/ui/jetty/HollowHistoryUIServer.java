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
package com.netflix.hollow.history.ui.jetty;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.history.ui.HollowHistoryUI;
import com.netflix.hollow.tools.history.HollowHistory;
import java.util.TimeZone;

/**
 * @deprecated
 * This class is replaced by {@link com.netflix.hollow.history.ui.webserver.HollowHistoryUIServer}
 *
 * It now acts as an adapter for the replacement, and no longer depends on jetty-server.
 *
 */
@Deprecated
public class HollowHistoryUIServer {

    private final com.netflix.hollow.history.ui.webserver.HollowHistoryUIServer server;

    /**
     * HollowHistoryUIServer that builds history using a consumer that transitions forwards i.e. in increasing version
     * order (v1, v2, v3...). This constructor defaults time zone to PST.
     *
     * @param consumer HollowConsumer (already initialized with data) that will be traversing forward deltas
     * @param port server port
     */
    public HollowHistoryUIServer(HollowConsumer consumer, int port) {
        server = new com.netflix.hollow.history.ui.webserver.HollowHistoryUIServer(consumer, port);
    }

    /**
     * Serves HollowHistoryUI that supports building history in both directions simultaneously.
     * Fwd and rev consumers should be initialized to the same version before calling this constructor.
     * Attempting double snapshots or forward version transitions on consumerRev will have unintended consequences on history.
     * This constructor defaults max states to 1024 and time zone to PST.
     *
     * @param consumerFwd HollowConsumer (already initialized with data) that will be traversing forward deltas
     * @param consumerRev HollowConsumer (also initialized to the same version as consumerFwd) that will be traversing reverse deltas
     * @param port server port
     */
    public HollowHistoryUIServer(HollowConsumer consumerFwd, HollowConsumer consumerRev, int port) {
        this(consumerFwd, consumerRev, 1024, port, TimeZone.getTimeZone("America/Los_Angeles"));
    }

    public HollowHistoryUIServer(HollowConsumer consumerFwd, HollowConsumer consumerRev, int numStatesToTrack, int port, TimeZone timeZone) {
        this(new HollowHistoryUI("", consumerFwd, consumerRev, numStatesToTrack, timeZone), port);
    }

    public HollowHistoryUIServer(HollowConsumer consumer, int port, TimeZone timeZone) {
            server = new com.netflix.hollow.history.ui.webserver.HollowHistoryUIServer(consumer, port, timeZone);
        }

    public HollowHistoryUIServer(HollowConsumer consumer, int numStatesToTrack, int port, TimeZone timeZone) {
        server = new com.netflix.hollow.history.ui.webserver.HollowHistoryUIServer( consumer, numStatesToTrack, port, timeZone);
    }

    public HollowHistoryUIServer(HollowConsumer consumer, int numStatesToTrack, int port) {
        server = new com.netflix.hollow.history.ui.webserver.HollowHistoryUIServer( consumer, numStatesToTrack, port);
    }

    public HollowHistoryUIServer(HollowHistory history, int port) {
        server = new com.netflix.hollow.history.ui.webserver.HollowHistoryUIServer( history, port);
    }

    public HollowHistoryUIServer(HollowHistoryUI ui, int port) {
       server = new com.netflix.hollow.history.ui.webserver.HollowHistoryUIServer(ui, port);
    }

    public HollowHistoryUIServer start() throws Exception {
        server.start();
        return this;
    }

    public HollowHistoryUIServer join() throws InterruptedException {
        server.join();
        return this;
    }

    public void stop() throws Exception {
        server.stop();
    }

    public HollowHistoryUI getUI() {
        return server.getUI();
    }

}
