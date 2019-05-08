package com.netflix.hollow.api.consumer;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;

public class FailedTransitionTest {

    @Test
    public void testSnapshotBlobFailure() {
        InMemoryBlobStore bs = new InMemoryBlobStore();

        HollowProducer producer = HollowProducer.withPublisher(bs)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long version = producer.runCycle(ws -> {
            ws.add(1);
        });

        HollowConsumer consumer = HollowConsumer
                .withBlobRetriever(new FailingBlobRetriever(1, bs))
                .withAnnouncementWatcher(new FixedAnnouncementWatcher(version))
                .build();

        try {
            consumer.triggerRefresh();
            Assert.fail();
        } catch (Exception e) {
            Throwable cause = e.getCause();
            Assert.assertNotNull(cause);
            Assert.assertTrue(cause instanceof IOException);
            Assert.assertEquals("FAILED", cause.getMessage());
            Assert.assertEquals(1, consumer.getNumFailedSnapshotTransitions());
        }

        try {
            consumer.triggerRefresh();
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertEquals(1, consumer.getNumFailedSnapshotTransitions());
        }

        try {
            consumer.triggerRefresh();
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertEquals(1, consumer.getNumFailedSnapshotTransitions());
        }
    }

    @Test
    public void testSnapshotBlobFailureNoDoubleSnapshot() {
        InMemoryBlobStore bs = new InMemoryBlobStore();

        HollowProducer producer = HollowProducer.withPublisher(bs)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long version = producer.runCycle(ws -> {
            ws.add(1);
        });

        HollowConsumer consumer = HollowConsumer
                .withBlobRetriever(new FailingBlobRetriever(1, bs))
                .withAnnouncementWatcher(new FixedAnnouncementWatcher(version))
                .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
                    @Override public boolean allowDoubleSnapshot() {
                        return false;
                    }

                    @Override public int maxDeltasBeforeDoubleSnapshot() {
                        return 32;
                    }
                })
                .build();

        try {
            consumer.triggerRefresh();
            Assert.fail();
        } catch (Exception e) {
            Throwable cause = e.getCause();
            Assert.assertNotNull(cause);
            Assert.assertTrue(cause instanceof IOException);
            Assert.assertEquals("FAILED", cause.getMessage());
            Assert.assertEquals(1, consumer.getNumFailedSnapshotTransitions());
        }

        try {
            consumer.triggerRefresh();
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertEquals(1, consumer.getNumFailedSnapshotTransitions());
        }

        try {
            consumer.triggerRefresh();
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertEquals(1, consumer.getNumFailedSnapshotTransitions());
        }
    }

    static class FailingBlobRetriever implements HollowConsumer.BlobRetriever {
        final int failLimit;
        final HollowConsumer.BlobRetriever br;

        FailingBlobRetriever(int failLimit, HollowConsumer.BlobRetriever br) {
            this.failLimit = failLimit;
            this.br = br;
        }

        @Override public HollowConsumer.Blob retrieveSnapshotBlob(long desiredVersion) {
            return new HollowConsumer.Blob(desiredVersion) {
                int c;

                @Override public InputStream getInputStream() throws IOException {
                    if (c++ < failLimit) {
                        throw new IOException("FAILED");
                    }
                    return br.retrieveSnapshotBlob(desiredVersion).getInputStream();
                }
            };
        }

        @Override public HollowConsumer.Blob retrieveDeltaBlob(long currentVersion) {
            return null;
        }

        @Override public HollowConsumer.Blob retrieveReverseDeltaBlob(long currentVersion) {
            return null;
        }
    }

    static class FixedAnnouncementWatcher implements HollowConsumer.AnnouncementWatcher {
        final long version;

        FixedAnnouncementWatcher(long version) {
            this.version = version;
        }

        @Override public long getLatestVersion() {
            return version;
        }

        @Override public void subscribeToUpdates(HollowConsumer consumer) {

        }
    }
}
