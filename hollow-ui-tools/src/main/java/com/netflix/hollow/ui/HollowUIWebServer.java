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
package com.netflix.hollow.ui;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HollowUIWebServer {
    private HttpServer server;
    private final HttpHandlerWithServletSupport handler;
    private final int port;
    private ExecutorService threadPool;

    public HollowUIWebServer(HttpHandlerWithServletSupport handler, int port) {
        this.port = port;
        this.handler = handler;
        this.threadPool = Executors.newCachedThreadPool();
    }

    public void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this.handler);
        server.setExecutor(threadPool);
        server.start();
    }

    /* There is no native join facility available on the webserver, so wait for all the
       service threads in the webserver to complete*/
    public void join() throws InterruptedException {
        while (!threadPool.isShutdown()) {
            Thread.sleep(10);  // sleep for a few ms
        }
    }

    private void shutdownAndAwaitTermination(ExecutorService pool) {
       pool.shutdown(); // Disable new tasks from being submitted
       try {
         // Wait a while for existing tasks to terminate
         if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
           pool.shutdownNow(); // Cancel currently executing tasks
           // Wait a while for tasks to respond to being cancelled
           if (!pool.awaitTermination(5, TimeUnit.SECONDS))
               System.err.println("Http Server ThreadPool did not terminate");
         }
       } catch (InterruptedException ie) {
         // (Re-)Cancel if current thread also interrupted
         pool.shutdownNow();
       }
    }

    public void stop() throws Exception {
        shutdownAndAwaitTermination(threadPool);
        server.stop(0);
    }

}
