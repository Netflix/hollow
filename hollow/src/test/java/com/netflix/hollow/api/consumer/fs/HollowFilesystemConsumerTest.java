package com.netflix.hollow.api.consumer.fs;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemPublisher;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
