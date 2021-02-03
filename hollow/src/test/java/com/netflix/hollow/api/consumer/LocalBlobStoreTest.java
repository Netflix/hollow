package com.netflix.hollow.api.consumer;

import static com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever.PARTIAL_DOWNLOAD;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.when;

import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.write.HollowBlobWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class LocalBlobStoreTest {

    @Mock HollowFilesystemBlobRetriever mockBlobRetriever;
    @Mock InputStream mockInputStream;

    static int callCount = 0;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBlobStoreDownloadResume() throws Exception {
        final File localDir = createLocalDir();

        // produce a write state
        InMemoryBlobStore bs = new InMemoryBlobStore();
        HollowProducer producer = HollowProducer.withPublisher(bs)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();
        final long testVersion = producer.runCycle(ws -> {
            ws.add(1); ws.add(2); ws.add(3);
        });

        // write state to disk
        final Path testBlob = Paths.get(localDir.getAbsolutePath(), testVersion + ".blob");
        HollowBlobWriter blobWriter = new HollowBlobWriter(producer.getWriteEngine());
        try (OutputStream os = Files.newOutputStream(testBlob)) {
            blobWriter.writeSnapshot(os);
        } catch (Exception ex) {
            throw new IOException("Failed to write snapshot to disk: ", ex);
        }

        // The first consumer refresh returns mockInputStream (which fails read midway of reading the blob file), successive refreshes return the blob file
        HollowConsumer.Blob blob = new HollowConsumer.Blob(HollowConstants.VERSION_NONE, testVersion) {
            @Override public InputStream getInputStream() throws IOException {
                if (callCount == 0) {
                    callCount ++;
                    return mockInputStream;
                }
                else {
                    return Files.newInputStream(testBlob);
                }
            }
        };
        when(mockBlobRetriever.retrieveSnapshotBlob(anyLong())).thenReturn(blob);

        // The first blob read returns the first byte of blob, the second read throws an exception to simulate failure
        when(mockInputStream.read(anyObject())).thenAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                if (count++ == 0) { // first invocation
                    Object[] args = invocation.getArguments();
                    try {
                        InputStream is = Files.newInputStream(testBlob);
                        is.read(((byte[])args[0]), 0, 1);   // read 1 byte
                        return 1;
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to read snapshot file");
                    }
                }

                // successive invocations
                throw new RuntimeException("Simulating download failure");
            }
        });

        // Consumer tries to refresh to published version, fails midway
        HollowConsumer.Builder consumerBuilder = HollowConsumer
                .withBlobRetriever(mockBlobRetriever)
                .withLocalBlobStore(localDir);
        consumerBuilder.noFallBackForExistingSnapshot = false;
        HollowConsumer consumer = consumerBuilder.build();
        boolean downloadInterrupted = false;
        try {
            consumer.triggerRefreshTo(testVersion);
        } catch (RuntimeException ex) {
            downloadInterrupted = true;
        }
        if (!downloadInterrupted) {
            Assert.fail("Download wasn't interrupted, unit test is broken");
        }
        assertNPartialFiles(1, localDir);
        assertNSnapshots(0, localDir);

        // New consumer tries to refresh to the same version using the partially downloaded file
        HollowConsumer.Builder restartConsumerBuilder = HollowConsumer
                .withBlobRetriever(mockBlobRetriever)
                .withLocalBlobStore(localDir);
        restartConsumerBuilder.noFallBackForExistingSnapshot = false;
        HollowConsumer restartConsumer = restartConsumerBuilder.build();

        restartConsumer.triggerRefreshTo(testVersion);

        Assert.assertEquals(testVersion, restartConsumer.getCurrentVersionId());
        assertNSnapshots(1, localDir);
        assertNPartialFiles(0, localDir);
    }

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
        assertNSnapshots(1, localDir);
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
        long nMatches = Files.list(localDir.toPath())
                .filter(p -> p.getFileName().toString().startsWith("snapshot"))
                .count();
        Assert.assertEquals(n, nMatches);
    }

    static void assertNPartialFiles(int n, File localDir) throws IOException {
        long nMatches = Files.list(localDir.toPath())
                .filter(p -> p.getFileName().toString().startsWith(PARTIAL_DOWNLOAD))
                .count();
        Assert.assertEquals(n, nMatches);
    }
}