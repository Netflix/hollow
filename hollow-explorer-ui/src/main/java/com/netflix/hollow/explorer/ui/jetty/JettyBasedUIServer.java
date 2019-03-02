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

import com.netflix.hollow.explorer.ui.HollowExplorerUI;
import com.netflix.hollow.explorer.ui.jetty.HollowExplorerUIServer.UIServer;
import org.eclipse.jetty.server.Server;

final class JettyBasedUIServer implements UIServer {
    private final Server server;
    private final HollowExplorerHandler handler;

    private JettyBasedUIServer(HollowExplorerUI ui, int port) {
        this.server = new Server(port);
        this.handler = new HollowExplorerHandler(ui);
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

    public static final class Factory implements UIServer.Factory {
        @Override
        public UIServer newServer(HollowExplorerUI ui, int port) {
            return new JettyBasedUIServer(ui, port);
        }
    }
}
