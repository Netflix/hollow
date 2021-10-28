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
package com.netflix.hollow.history.ui.webserver;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.history.ui.HollowHistoryUI;
import com.netflix.hollow.history.ui.VersionTimestampConverter;
import com.netflix.hollow.tools.history.HollowHistory;
import com.netflix.hollow.ui.UIServer;
import java.util.TimeZone;

public class HollowHistoryUIServer {

    private final UIServer server;
    private final HollowHistoryUI ui;
    
    public HollowHistoryUIServer(HollowConsumer consumer, int port, TimeZone timeZone) {
        this(new HollowHistoryUI("", consumer, timeZone), port);
    }

    public HollowHistoryUIServer(HollowConsumer consumer, int port) {
        this(new HollowHistoryUI("", consumer), port);
    }

    public HollowHistoryUIServer(HollowConsumer consumer, int numStatesToTrack, int port, TimeZone timeZone) {
        this(new HollowHistoryUI("", consumer, numStatesToTrack, timeZone), port);
    }

    public HollowHistoryUIServer(HollowConsumer consumer, int numStatesToTrack, int port) {
        this(new HollowHistoryUI("", consumer, numStatesToTrack, VersionTimestampConverter.PACIFIC_TIMEZONE), port);
    }

    public HollowHistoryUIServer(HollowHistory history, int port) {
        this(new HollowHistoryUI("", history), port);
    }

    public HollowHistoryUIServer(HollowHistoryUI ui, int port) {
        this.server = new UIWebServer(ui, port);
        this.ui = ui;
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
        return ui;
    }

}
