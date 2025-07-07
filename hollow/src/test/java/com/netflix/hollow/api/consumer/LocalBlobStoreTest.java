package com.netflix.hollow.api.consumer;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.ProducerOptionalBlobPartConfig;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Collections;

import com.netflix.hollow.test.InMemoryBlobStore;
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
    public void testBlobStoreOverrideLowMaxBlobAge() throws Exception {
        File localDir = createLocalDir();
        InMemoryBlobStore bs = new InMemoryBlobStore();

        HollowProducer producer = HollowProducer.withPublisher(bs)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = producer.runCycle(ws -> {
            ws.add(1);
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(bs)
                .withLocalBlobStore(localDir, true, Duration.ofNanos(0)).build();
        consumer.triggerRefreshTo(v1);
        Assert.assertEquals(v1, consumer.getCurrentVersionId());
        assertNSnapshots(1, localDir);
        Assert.assertEquals(v1, getLastSnapshotVersion(localDir));

        long v2 = producer.runCycle(ws -> {
            ws.add(2);
        });

        consumer = HollowConsumer.withBlobRetriever(bs)
                .withLocalBlobStore(localDir, true, Duration.ofNanos(0)).build();
        consumer.triggerRefreshTo(v2);

        Assert.assertEquals(v2, consumer.getCurrentVersionId());
        assertNSnapshots(1, localDir);
        // Since the max blob age is 0, we don't expect the stale snapshot to be used,
        // so there should be a new snapshot pulled with the latest version.
        assertNDeltas(0, localDir);
        Assert.assertEquals(v2, getLastSnapshotVersion(localDir));
    }

    @Test
    public void testBlobStoreOverrideHighMaxBlobAge() throws Exception {
        File localDir = createLocalDir();
        InMemoryBlobStore bs = new InMemoryBlobStore();

        HollowProducer producer = HollowProducer.withPublisher(bs)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = producer.runCycle(ws -> {
            ws.add(1);
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(bs)
                .withLocalBlobStore(localDir, true, Duration.ofDays(10)).build();
        consumer.triggerRefreshTo(v1);
        Assert.assertEquals(v1, consumer.getCurrentVersionId());
        assertNSnapshots(1, localDir);
        Assert.assertEquals(v1, getLastSnapshotVersion(localDir));

        long v2 = producer.runCycle(ws -> {
            ws.add(2);
        });

        consumer = HollowConsumer.withBlobRetriever(bs)
                .withLocalBlobStore(localDir, true, Duration.ofDays(10)).build();
        consumer.triggerRefreshTo(v2);

        Assert.assertEquals(v2, consumer.getCurrentVersionId());
        assertNSnapshots(1, localDir);
        assertNDeltas(1, localDir);
        // Since the max blob age is high, we expect the stale snapshot
        // to be used along with a delta.
        Assert.assertEquals(v1, getLastSnapshotVersion(localDir));
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

    static long getLastSnapshotVersion(File localDir) throws IOException {
        return Files.list(localDir.toPath())
                .filter(p -> p.getFileName().toString().startsWith("snapshot-"))
                .mapToLong(p -> Long.parseLong(p.getFileName().toString().split("-")[1]))
                .max()
                .orElseThrow(() -> new IOException("No snapshots found in directory: " + localDir));
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