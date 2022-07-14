package com.netflix.hollow.api.consumer;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.ProducerOptionalBlobPartConfig;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
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
            ws.add(2);
        });

        consumer = HollowConsumer.withBlobRetriever(bs)
                .withLocalBlobStore(localDir).build();

        consumer.triggerRefreshTo(v2);

        Assert.assertEquals(v2, consumer.getCurrentVersionId());
        assertNSnapshots(2, localDir);
        assertNDeltas(0, localDir);
    }

    @Test
    public void testBlobStoreOverride() throws Exception {
        File localDir = createLocalDir();
        InMemoryBlobStore bs = new InMemoryBlobStore(Collections.singleton("LONG"));

        HollowProducer producer = HollowProducer.withPublisher(bs)
                .withBlobStager(new HollowInMemoryBlobStager(optionalPartConfig()))
                .build();

        long v1 = producer.runCycle(ws -> {
            ws.add(1);
            ws.add(1L);
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(bs)
                .withLocalBlobStore(localDir, true).build();
        consumer.triggerRefreshTo(v1);
        Assert.assertEquals(v1, consumer.getCurrentVersionId());
        assertNSnapshots(1, localDir);

        long v2 = producer.runCycle(ws -> {
            ws.add(2);
            ws.add(2L);
        });

        consumer = HollowConsumer.withBlobRetriever(bs)
                .withLocalBlobStore(localDir, true).build();
        consumer.triggerRefreshTo(v2);

        Assert.assertEquals(v2, consumer.getCurrentVersionId());
        assertNSnapshots(1, localDir);
        assertNSnapshots(1, "LONG", localDir);
        assertNDeltas(1, localDir);
        assertNDeltas(1, "LONG", localDir);
    }

    @Test
    public void testBlobStoreOverrideOptionalPartNotLoaded() throws Exception {
        File localDir = createLocalDir();
        InMemoryBlobStore bs = new InMemoryBlobStore(Collections.emptySet());

        HollowProducer producer = HollowProducer.withPublisher(bs)
                .withBlobStager(new HollowInMemoryBlobStager(optionalPartConfig()))
                .build();

        long v1 = producer.runCycle(ws -> {
            ws.add(1);
            ws.add(1L);
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(bs)
                .withLocalBlobStore(localDir, true).build();
        consumer.triggerRefreshTo(v1);
        Assert.assertEquals(v1, consumer.getCurrentVersionId());
        assertNSnapshots(1, localDir);

        long v2 = producer.runCycle(ws -> {
            ws.add(2);
            ws.add(2L);
        });

        consumer = HollowConsumer.withBlobRetriever(bs)
                .withLocalBlobStore(localDir, true).build();
        consumer.triggerRefreshTo(v2);

        Assert.assertEquals(v2, consumer.getCurrentVersionId());
        assertNSnapshots(1, localDir);
        assertNSnapshots(0, "LONG", localDir);
        assertNDeltas(1, localDir);
        assertNDeltas(0, "LONG", localDir);
    }

    static ProducerOptionalBlobPartConfig optionalPartConfig() throws IOException {
        ProducerOptionalBlobPartConfig optionalPartConfig = new ProducerOptionalBlobPartConfig();
        optionalPartConfig.addTypesToPart("LONG", "Long");
        return optionalPartConfig;
    }

    static File createLocalDir() throws IOException {
        File localDir = Files.createTempDirectory("hollow").toFile();
        localDir.deleteOnExit();
        return localDir;
    }

    static void assertNSnapshots(int n, File localDir) throws IOException {
        long nSnapshots = Files.list(localDir.toPath()).filter(p -> p.getFileName().toString().startsWith("snapshot-"))
                .count();
        Assert.assertEquals(n, nSnapshots);
    }

    static void assertNDeltas(int n, File localDir) throws IOException {
        long nDeltas = Files.list(localDir.toPath()).filter(p -> p.getFileName().toString().startsWith("delta-"))
                .count();
        Assert.assertEquals(n, nDeltas);
    }

    static void assertNSnapshots(int n, String optionalPart, File localDir) throws IOException {
        long nSnapshots = Files.list(localDir.toPath()).filter(p -> p.getFileName().toString().startsWith("snapshot_" + optionalPart + "-"))
                .count();
        Assert.assertEquals(n, nSnapshots);
    }

    static void assertNDeltas(int n, String optionalPart, File localDir) throws IOException {
        long nDeltas = Files.list(localDir.toPath()).filter(p -> p.getFileName().toString().startsWith("delta_" + optionalPart + "-"))
                .count();
        Assert.assertEquals(n, nDeltas);
    }
}