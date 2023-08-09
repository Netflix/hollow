package com.netflix.hollow.diff.ui;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever;
import com.netflix.hollow.history.ui.HollowHistoryUIServer;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.Test;

public class FakeDataTest {

    @Test
    public void fakeHistory() throws Exception {
        String path = "/Users/ssingh/workspace/blob-cache/tmp5";
        HollowConsumer c = HollowConsumer
                .withBlobRetriever(new HollowFilesystemBlobRetriever(Paths.get(path)))
                .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
                    @Override
                    public boolean allowDoubleSnapshot() {
                        return false;
                    }
                    @Override
                    public int maxDeltasBeforeDoubleSnapshot() {
                        return Integer.MAX_VALUE;
                    }
                })
                .build();
        c.triggerRefreshTo(20230808144752001l);

        HollowHistoryUIServer historyUIServer = new HollowHistoryUIServer(c, 7002);
        historyUIServer.start();
        System.out.println("History is available at http://localhost:7002/");

        c.triggerRefreshTo(20230808145930100l);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long heapUsed =  Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                    System.out.println("heapUsed= " + (heapUsed/1024)/1024  + " MB");
                    try {
                        Thread.sleep(10 *1000);
                        System.gc();
                        System.out.println(" - - - Attempted GC invocation - - - ");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        t.run();

        historyUIServer.join();
    }
}
