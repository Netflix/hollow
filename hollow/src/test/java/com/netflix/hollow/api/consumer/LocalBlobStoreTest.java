package com.netflix.hollow.api.consumer;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.Assert;
import org.junit.Test;

public class LocalBlobStoreTest {
    @Test
    public void testBlobStore() throws Exception {
        File localDir = createLocalDir();
        InMemoryBlobStore bs = new InMemoryBlobStore();

        HollowProducer producer = HollowProducer.withPublisher(bs)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = producer.runCycle(ws -> {
            ws.add(1);
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(bs)
                .withLocalBlobStore(localDir).build();
        consumer.triggerRefreshTo(v1);
        Assert.assertEquals(v1, consumer.getCurrentVersionId());
        assertNSnapshots(1, localDir);

        long v2 = producer.runCycle(ws -> {
            ws.add(1);
        });

        consumer.triggerRefreshTo(v2);
        Assert.assertEquals(v2, consumer.getCurrentVersionId());
        assertNSnapshots(2, localDir);
    }

    @Test
    public void testBlobStoreOverride() throws Exception {
        File localDir = createLocalDir();
        InMemoryBlobStore bs = new InMemoryBlobStore();

        HollowProducer producer = HollowProducer.withPublisher(bs)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = producer.runCycle(ws -> {
            ws.add(1);
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(bs)
                .withLocalBlobStore(localDir, true).build();
        consumer.triggerRefreshTo(v1);
        Assert.assertEquals(v1, consumer.getCurrentVersionId());
        assertNSnapshots(1, localDir);

        long v2 = producer.runCycle(ws -> {
            ws.add(1);
        });

        consumer.triggerRefreshTo(v2);
        // v2 is not loaded from fallback since v1 exists in local cache
        Assert.assertEquals(v1, consumer.getCurrentVersionId());
        assertNSnapshots(1, localDir);
    }

    static File createLocalDir() throws IOException {
        File localDir = Files.createTempDirectory("hollow").toFile();
        localDir.deleteOnExit();
        return localDir;
    }

    static void assertNSnapshots(int n, File localDir) throws IOException {
        long nSnapshots = Files.list(localDir.toPath()).filter(p -> p.getFileName().toString().startsWith("snapshot"))
                .count();
        Assert.assertEquals(1, nSnapshots);
    }
}