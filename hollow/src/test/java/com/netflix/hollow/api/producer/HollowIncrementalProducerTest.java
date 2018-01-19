/*
 *
 *  Copyright 2017 Netflix, Inc.
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
package com.netflix.hollow.api.producer;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.InMemoryBlobStore;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.producer.HollowProducer.Populator;
import com.netflix.hollow.api.producer.HollowProducer.WriteState;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import com.netflix.hollow.core.write.objectmapper.RecordPrimaryKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HollowIncrementalProducerTest {

    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    @Test
    public void publishAndLoadASnapshot() {
        HollowProducer producer = createInMemoryProducer();

        /// initialize the data -- classic producer creates the first state in the delta chain. 
        initializeData(producer);

        /// now we'll be incrementally updating the state by mutating individual records
        HollowIncrementalProducer incrementalProducer = HollowIncrementalProducer.withHollowProducer(producer).build();

        incrementalProducer.addOrModify(new TypeA(1, "one", 100));
        incrementalProducer.addOrModify(new TypeA(2, "two", 2));
        incrementalProducer.addOrModify(new TypeA(3, "three", 300));
        incrementalProducer.addOrModify(new TypeA(3, "three", 3));
        incrementalProducer.addOrModify(new TypeA(4, "five", 6));
        incrementalProducer.delete(new TypeA(5, "five", 5));

        incrementalProducer.delete(new TypeB(2, "3"));
        incrementalProducer.addOrModify(new TypeB(5, "5"));
        incrementalProducer.addOrModify(new TypeB(5, "6"));
        incrementalProducer.delete(new RecordPrimaryKey("TypeB", new Object[] { 3 }));

        /// .runCycle() flushes the changes to a new data state.
        long nextVersion = incrementalProducer.runCycle();

        incrementalProducer.addOrModify(new TypeA(1, "one", 1000));

        /// another new state with a single change
        long finalVersion = incrementalProducer.runCycle();

        /// now we read the changes and assert
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(nextVersion);

        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "TypeA", "id1", "id2");
        Assert.assertFalse(idx.containsDuplicates());

        assertTypeA(idx, 1, "one", 100L);
        assertTypeA(idx, 2, "two", 2L);
        assertTypeA(idx, 3, "three", 3L);
        assertTypeA(idx, 4, "four", 4L);
        assertTypeA(idx, 4, "five", 6L);
        assertTypeA(idx, 5, "five", null);

        idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "TypeB", "id");
        Assert.assertFalse(idx.containsDuplicates());

        assertTypeB(idx, 1, "1");
        assertTypeB(idx, 2, null);
        assertTypeB(idx, 3, null);
        assertTypeB(idx, 4, "4");
        assertTypeB(idx, 5, "6");

        consumer.triggerRefreshTo(finalVersion);

        idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "TypeA", "id1", "id2");
        Assert.assertFalse(idx.containsDuplicates());

        assertTypeA(idx, 1, "one", 1000L);
        assertTypeA(idx, 2, "two", 2L);
        assertTypeA(idx, 3, "three", 3L);
        assertTypeA(idx, 4, "four", 4L);
        assertTypeA(idx, 4, "five", 6L);
        assertTypeA(idx, 5, "five", null);
    }
    
    @Test
    public void continuesARestoredState() {
        HollowProducer genesisProducer = createInMemoryProducer();

        /// initialize the data -- classic producer creates the first state in the delta chain. 
        long originalVersion = genesisProducer.runCycle(new Populator() {
            public void populate(WriteState state) throws Exception {
                state.add(new TypeA(1, "one", 1));
            }
        });
        
        /// now at some point in the future, we will start up and create a new classic producer 
        /// to back the HollowIncrementalProducer.
        HollowProducer backingProducer = HollowProducer.withPublisher(blobStore)
                                                   .withBlobStager(new HollowInMemoryBlobStager())
                                                   .build();
        
        /// adding a new type this time (TypeB).
        backingProducer.initializeDataModel(TypeA.class, TypeB.class);
         
        /// now create our HollowIncrementalProducer
        HollowIncrementalProducer incrementalProducer = HollowIncrementalProducer.withHollowProducer(backingProducer).build();
        incrementalProducer.restore(originalVersion, blobStore);
        
        incrementalProducer.addOrModify(new TypeA(1, "one", 2));
        incrementalProducer.addOrModify(new TypeA(2, "two", 2));
        incrementalProducer.addOrModify(new TypeB(3, "three"));
        
        long version = incrementalProducer.runCycle();

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(originalVersion);
        consumer.triggerRefreshTo(version);

        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "TypeA", "id1", "id2");
        Assert.assertFalse(idx.containsDuplicates());

        assertTypeA(idx, 1, "one", 2L);
        assertTypeA(idx, 2, "two", 2L);
        
        /// consumers with established data models don't have visibility into new types.
        consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(version);
        
        idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "TypeB", "id");
        Assert.assertFalse(idx.containsDuplicates());
        
        assertTypeB(idx, 3, "three");
    }


    @Test
    public void publishUsingThreadConfig() {
        HollowProducer producer = createInMemoryProducer();

        /// initialize the data -- classic producer creates the first state in the delta chain.
        initializeData(producer);

        /// now we'll be incrementally updating the state by mutating individual records
        HollowIncrementalProducer incrementalProducer = HollowIncrementalProducer.withHollowProducer(producer)
                .withThreadsPerCpu(2.0d)
                .build();

        incrementalProducer.addOrModify(new TypeA(1, "one", 100));
        incrementalProducer.addOrModify(new TypeA(2, "two", 2));
        incrementalProducer.addOrModify(new TypeA(3, "three", 300));
        incrementalProducer.addOrModify(new TypeA(3, "three", 3));
        incrementalProducer.addOrModify(new TypeA(4, "five", 6));
        incrementalProducer.delete(new TypeA(5, "five", 5));

        incrementalProducer.delete(new TypeB(2, "3"));
        incrementalProducer.addOrModify(new TypeB(5, "5"));
        incrementalProducer.addOrModify(new TypeB(5, "6"));
        incrementalProducer.delete(new RecordPrimaryKey("TypeB", new Object[] { 3 }));

        /// .runCycle() flushes the changes to a new data state.
        long nextVersion = incrementalProducer.runCycle();

        incrementalProducer.addOrModify(new TypeA(1, "one", 1000));

        /// another new state with a single change
        long finalVersion = incrementalProducer.runCycle();

        /// now we read the changes and assert
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(nextVersion);

        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "TypeA", "id1", "id2");
        Assert.assertFalse(idx.containsDuplicates());

        assertTypeA(idx, 1, "one", 100L);
        assertTypeA(idx, 2, "two", 2L);
        assertTypeA(idx, 3, "three", 3L);
        assertTypeA(idx, 4, "four", 4L);
        assertTypeA(idx, 4, "five", 6L);
        assertTypeA(idx, 5, "five", null);

        idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "TypeB", "id");
        Assert.assertFalse(idx.containsDuplicates());

        assertTypeB(idx, 1, "1");
        assertTypeB(idx, 2, null);
        assertTypeB(idx, 3, null);
        assertTypeB(idx, 4, "4");
        assertTypeB(idx, 5, "6");

        consumer.triggerRefreshTo(finalVersion);

        idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "TypeA", "id1", "id2");
        Assert.assertFalse(idx.containsDuplicates());

        assertTypeA(idx, 1, "one", 1000L);
        assertTypeA(idx, 2, "two", 2L);
        assertTypeA(idx, 3, "three", 3L);
        assertTypeA(idx, 4, "four", 4L);
        assertTypeA(idx, 4, "five", 6L);
        assertTypeA(idx, 5, "five", null);
    }

    @Test
    public void discardChanges() {
        HollowProducer producer = createInMemoryProducer();

        initializeData(producer);

        HollowIncrementalProducer incrementalProducer = HollowIncrementalProducer.withHollowProducer(producer).build();

        incrementalProducer.addOrModify(new TypeB(1, "one"));

        long nextVersion = incrementalProducer.runCycle();

        /// now we read the changes and assert
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(nextVersion);

        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "TypeB", "id");

        assertTypeB(idx, 1, "one");

        incrementalProducer.delete(new TypeB(1, "one"));

        Assert.assertTrue(incrementalProducer.hasChanges());

        //Discard with an object
        incrementalProducer.discard(new TypeB(1, "one"));

        Assert.assertFalse(incrementalProducer.hasChanges());

        long version = incrementalProducer.runCycle();

        consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(version);

        assertTypeB(idx, 1, "one");

        incrementalProducer.delete(new TypeB(1, "one"));

        Assert.assertTrue(incrementalProducer.hasChanges());

        //Discard with a PrimaryKey
        incrementalProducer.discard(new RecordPrimaryKey("TypeB", new Object[]{ 1 }));

        Assert.assertFalse(incrementalProducer.hasChanges());

        long finalVersion = incrementalProducer.runCycle();

        consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(finalVersion);

        assertTypeB(idx, 1, "one");
    }

    @Test
    public void clearMutations() {
        HollowProducer producer = createInMemoryProducer();

        /// initialize the data -- classic producer creates the first state in the delta chain.
        initializeData(producer);

        /// now we'll be incrementally updating the state by mutating individual records
        HollowIncrementalProducer incrementalProducer = HollowIncrementalProducer.withHollowProducer(producer).build();

        incrementalProducer.addOrModify(new TypeA(1, "one", 100));
        incrementalProducer.addOrModify(new TypeA(2, "two", 2));
        incrementalProducer.addOrModify(new TypeA(3, "three", 300));
        incrementalProducer.addOrModify(new TypeA(3, "three", 3));
        incrementalProducer.addOrModify(new TypeA(4, "five", 6));
        incrementalProducer.delete(new TypeA(5, "five", 5));

        incrementalProducer.delete(new TypeB(2, "3"));
        incrementalProducer.addOrModify(new TypeB(5, "5"));
        incrementalProducer.addOrModify(new TypeB(5, "6"));
        incrementalProducer.delete(new RecordPrimaryKey("TypeB", new Object[] { 3 }));

        Assert.assertTrue(incrementalProducer.hasChanges());

        /// .runCycle() flushes the changes to a new data state.
        incrementalProducer.runCycle();

        Assert.assertFalse(incrementalProducer.hasChanges());
    }

    @Test
    public void fireSuccessListener() {
        HollowProducer producer = createInMemoryProducer();

        /// initialize the data -- classic producer creates the first state in the delta chain.
        initializeData(producer);

        FakeIncrementalCycleListener listener = new FakeIncrementalCycleListener();

        /// now we'll be incrementally updating the state by mutating individual records
        HollowIncrementalProducer incrementalProducer = HollowIncrementalProducer
                .withHollowProducer(producer)
                .withListener(listener)
                .build();

        incrementalProducer.addOrModify(new TypeA(1, "one", 100));
        incrementalProducer.addOrModify(new TypeA(2, "two", 2));
        incrementalProducer.addOrModify(new TypeA(3, "three", 300));
        incrementalProducer.delete(new TypeA(5, "five", 5));

        /// .runCycle() flushes the changes to a new data state.
        long nextVersion = incrementalProducer.runCycle();


        Assert.assertEquals(nextVersion, listener.getVersion());
        Assert.assertEquals(IncrementalCycleListener.Status.SUCCESS, listener.getStatus());
        Assert.assertEquals(3L, listener.getRecordsAddedOrModified());
        Assert.assertEquals(1L, listener.getRecordsRemoved());
        Assert.assertNull(listener.getCause());

        incrementalProducer.addOrModify(new TypeA(1, "one", 1000));

        /// another new state with a single change
        long finalVersion = incrementalProducer.runCycle();

        Assert.assertEquals(finalVersion, listener.getVersion());
        Assert.assertEquals(IncrementalCycleListener.Status.SUCCESS, listener.getStatus());
        Assert.assertEquals(1L, listener.getRecordsAddedOrModified());
        Assert.assertEquals(0L, listener.getRecordsRemoved());
        Assert.assertNull(listener.getCause());
    }

    @Test
    public void fireFailureListener() {
        HollowProducer producer = createInMemoryProducer();

        /// initialize the data -- classic producer creates the first state in the delta chain.
        initializeData(producer);

        FakeIncrementalCycleListener listener = new FakeIncrementalCycleListener();
        HollowProducer fakeHollowProducer = FakeHollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withVersionMinter(new TestVersionMinter())
                .build();


        /// now we'll be incrementally updating the state by mutating individual records
        HollowIncrementalProducer incrementalProducer = HollowIncrementalProducer
                .withHollowProducer(fakeHollowProducer)
                .withListener(listener)
                .build();


        incrementalProducer.addOrModify(new TypeA(1, "one", 100));
        incrementalProducer.addOrModify(new TypeA(2, "two", 2));
        incrementalProducer.addOrModify(new TypeA(3, "three", 300));
        incrementalProducer.addOrModify(new TypeA(3, "three", 3));
        incrementalProducer.addOrModify(new TypeA(4, "five", 6));
        incrementalProducer.delete(new TypeA(5, "five", 5));


        /// .runCycle() flushes the changes to a new data state.
        long nextVersion = incrementalProducer.runCycle();


        Assert.assertEquals(nextVersion, listener.getVersion());
        Assert.assertEquals(IncrementalCycleListener.Status.FAIL, listener.getStatus());
        Assert.assertEquals(4L, listener.getRecordsAddedOrModified());
        Assert.assertEquals(1L, listener.getRecordsRemoved());
        Assert.assertNotNull(listener.getCause());
    }

    private HollowProducer createInMemoryProducer() {
        return HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withVersionMinter(new TestVersionMinter())
                .build();
    }

    private void initializeData(HollowProducer producer) {
        producer.runCycle(new Populator() {
            public void populate(WriteState state) throws Exception {
                state.add(new TypeA(1, "one", 1));
                state.add(new TypeA(2, "two", 2));
                state.add(new TypeA(3, "three", 3));
                state.add(new TypeA(4, "four", 4));
                state.add(new TypeA(5, "five", 5));

                state.add(new TypeB(1, "1"));
                state.add(new TypeB(2, "2"));
                state.add(new TypeB(3, "3"));
                state.add(new TypeB(4, "4"));
            }
        });
    }

    private void assertTypeA(HollowPrimaryKeyIndex typeAIdx, int id1,
            String id2, Long expectedValue) {
        int ordinal = typeAIdx.getMatchingOrdinal(id1, id2);

        if (expectedValue == null) {
            Assert.assertEquals(-1, ordinal);
        } else {
            Assert.assertNotEquals(-1, ordinal);
            GenericHollowObject obj = new GenericHollowObject(
                    typeAIdx.getTypeState(), ordinal);
            Assert.assertEquals(expectedValue.longValue(), obj.getLong("value"));
        }
    }

    private void assertTypeB(HollowPrimaryKeyIndex typeBIdx, int id1,
            String expectedValue) {
        int ordinal = typeBIdx.getMatchingOrdinal(id1);

        if (expectedValue == null) {
            Assert.assertEquals(-1, ordinal);
        } else {
            Assert.assertNotEquals(-1, ordinal);
            GenericHollowObject obj = new GenericHollowObject(
                    typeBIdx.getTypeState(), ordinal);
            Assert.assertEquals(expectedValue, obj.getObject("value")
                    .getString("value"));
        }
    }

    @SuppressWarnings("unused")
    @HollowPrimaryKey(fields = { "id1", "id2" })
    private static class TypeA {
        int id1;
        String id2;
        long value;

        public TypeA(int id1, String id2, long value) {
            this.id1 = id1;
            this.id2 = id2;
            this.value = value;
        }
    }

    @SuppressWarnings("unused")
    @HollowPrimaryKey(fields = "id")
    private static class TypeB {
        int id;
        @HollowTypeName(name = "TypeBValue")
        String value;

        public TypeB(int id, String value) {
            this.id = id;
            this.value = value;
        }
    }

    private class FakeIncrementalCycleListener extends AbstractIncrementalCycleListener {
        private long recordsRemoved;
        private long recordsAddedOrModified;
        private long version;
        private Status status;
        private Throwable cause;

        @Override
        public void onCycleComplete(IncrementalCycleStatus status, long elapsed, TimeUnit unit) {
            this.status = status.getStatus();
            this.recordsAddedOrModified = status.getRecordsAddedOrModified();
            this.recordsRemoved = status.getRecordsRemoved();
            this.version = status.getVersion();
        }

        @Override
        public void onCycleFail(IncrementalCycleStatus status, long elapsed, TimeUnit unit) {
            this.status = status.getStatus();
            this.recordsAddedOrModified = status.getRecordsAddedOrModified();
            this.recordsRemoved = status.getRecordsRemoved();
            this.version = status.getVersion();
            this.cause = status.getCause();
        }

        public long getRecordsRemoved() {
            return recordsRemoved;
        }

        public long getRecordsAddedOrModified() {
            return recordsAddedOrModified;
        }

        public Status getStatus() {
            return status;
        }

        public long getVersion() {
            return version;
        }

        public Throwable getCause() {
            return cause;
        }
    }

    private static final class TestVersionMinter implements HollowProducer.VersionMinter  {
        private static int versionCounter = 0;

        @Override
        public long mint() {
            return versionCounter++;
        }
    }

    private static final class FakeHollowProducer extends HollowProducer {

        public FakeHollowProducer(Publisher publisher, Announcer announcer) {
            super(publisher, announcer);
        }

        public long runCycle() {
            return runCycle(new Populator() {
                @Override
                public void populate(WriteState newState) throws Exception {
                    throw new Exception("something went wrong");
                }
            });
        }
    }
}
