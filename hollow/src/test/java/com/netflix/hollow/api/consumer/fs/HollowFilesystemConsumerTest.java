package com.netflix.hollow.api.consumer.fs;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemAnnouncer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemPublisher;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

public class HollowFilesystemConsumerTest {

    @Test
    public void testHollowFilesystemConsumer() throws IOException {
        File localDir = createLocalDir();
        HollowFilesystemPublisher pub = new HollowFilesystemPublisher(localDir.toPath());

        HollowProducer producer = HollowProducer.withPublisher(pub)
                .withNumStatesBetweenSnapshots(2)
                .build();

        producer.runCycle(state -> {
            state.add(new Entity(1));
            state.add(new Entity(2));
            state.add(new Entity(3));
        });

        long deltaVersion = producer.runCycle(state -> {
            state.add(new Entity(1));
            state.add(new Entity(2));
            state.add(new Entity(3));
            state.add(new Entity(4));
        });

        HollowFilesystemBlobRetriever retriever = new HollowFilesystemBlobRetriever(localDir.toPath());
        HollowConsumer consumer = HollowConsumer.newHollowConsumer().withBlobRetriever(retriever).build();
        consumer.triggerRefreshTo(deltaVersion);

        GenericHollowObject obj1 = new GenericHollowObject(consumer.getStateEngine(), "Entity", 0);
        GenericHollowObject obj4 = new GenericHollowObject(consumer.getStateEngine(), "Entity", 3);

        Assert.assertEquals(1, obj1.getInt("id"));
        Assert.assertEquals(4, obj4.getInt("id"));
    }

    @Test
    public void testConsumerIsRefreshedOnSubscribeWithoutWaitingForNextAnnouncement() throws Exception {
        File localDir = createLocalDir();
        HollowFilesystemPublisher pub = new HollowFilesystemPublisher(localDir.toPath());
        HollowFilesystemAnnouncer announcer = new HollowFilesystemAnnouncer(localDir.toPath());

        HollowProducer producer = HollowProducer.withPublisher(pub)
                .withAnnouncer(announcer)
                .build();

        long version = producer.runCycle(state -> {
            state.add(new Entity(1));
            state.add(new Entity(2));
        });

        // The announcement file already exists with a version by the time the watcher is created,
        // so a consumer subscribing to it should be refreshed right away, without needing to wait
        // for the watcher to observe a subsequent change to the announcement file.
        HollowFilesystemAnnouncementWatcher watcher = new HollowFilesystemAnnouncementWatcher(localDir.toPath());
        HollowFilesystemBlobRetriever retriever = new HollowFilesystemBlobRetriever(localDir.toPath());
        HollowConsumer consumer = HollowConsumer.newHollowConsumer()
                .withBlobRetriever(retriever)
                .withAnnouncementWatcher(watcher)
                .build();

        long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10);
        while (consumer.getCurrentVersionId() != version && System.currentTimeMillis() < deadline) {
            Thread.sleep(50);
        }

        Assert.assertEquals(version, consumer.getCurrentVersionId());

        GenericHollowObject obj1 = new GenericHollowObject(consumer.getStateEngine(), "Entity", 0);
        Assert.assertEquals(1, obj1.getInt("id"));
    }

    static File createLocalDir() throws IOException {
        File localDir = Files.createTempDirectory("hollow_fs").toFile();
        localDir.deleteOnExit();
        return localDir;
    }

    @HollowPrimaryKey(fields="id")
    public static class Entity {
        @SuppressWarnings("unused")
        private final int id;

        public Entity(int id) {
            this.id = id;
        }
    }
}
