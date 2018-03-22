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
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.producer.HollowProducer.Populator;
import com.netflix.hollow.api.producer.HollowProducer.WriteState;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import com.netflix.hollow.core.write.objectmapper.RecordPrimaryKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

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
        HollowIncrementalProducer incrementalProducer = new HollowIncrementalProducer(producer);

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
    public void publishAndLoadASnapshotDirectly() {
        // Producer is created but not initialized IncrementalProducer will directly initialize the first snapshot
        HollowProducer producer = createInMemoryProducer();

        /// add/modify state of a producer with an empty previous state. delete requests for non-existent records will be ignored
        HollowIncrementalProducer incrementalProducer = new HollowIncrementalProducer(producer);

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
        incrementalProducer.addOrModify(new TypeA(4, "four", 4));

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

        // backing producer was never initialized, so only records added to the incremental producer are here
        assertTypeB(idx, 1, null);
        assertTypeB(idx, 2, null);
        assertTypeB(idx, 3, null);
        assertTypeB(idx, 4, null);
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
    public void publishDirectlyAndRestore() {
        // Producer is created but not initialized. IncrementalProducer will directly initialize the first snapshot
        /// add/modify state of a producer with an empty previous state. delete requests for non-existent records will be ignored
        HollowIncrementalProducer incrementalProducer = new HollowIncrementalProducer(createInMemoryProducer());

        incrementalProducer.addOrModify(new TypeA(1, "one", 100));
        incrementalProducer.addOrModify(new TypeA(2, "two", 2));
        incrementalProducer.addOrModify(new TypeA(3, "three", 300));
        incrementalProducer.addOrModify(new TypeA(3, "three", 3));
        incrementalProducer.addOrModify(new TypeA(4, "five", 6));
        incrementalProducer.delete(new TypeA(5, "five", 5));

        incrementalProducer.delete(new TypeB(2, "2"));
        incrementalProducer.addOrModify(new TypeB(4, "four"));
        incrementalProducer.addOrModify(new TypeB(5, "6"));
        incrementalProducer.addOrModify(new TypeB(5, "5"));
        incrementalProducer.delete(new RecordPrimaryKey("TypeB", new Object[] { 4 }));
        incrementalProducer.addOrModify(new TypeB(6, "6"));

        /// .runCycle() flushes the changes to a new data state.
        long nextVersion = incrementalProducer.runCycle();

        /// now we read the changes and assert
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(nextVersion);

        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "TypeA", "id1", "id2");
        Assert.assertFalse(idx.containsDuplicates());

        assertTypeA(idx, 1, "one", 100L);
        assertTypeA(idx, 2, "two", 2L);
        assertTypeA(idx, 3, "three", 3L);
        assertTypeA(idx, 4, "five", 6L);
        assertTypeA(idx, 5, "five", null);

        idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "TypeB", "id");
        Assert.assertFalse(idx.containsDuplicates());

        // backing producer was never initialized, so only records added to the incremental producer are here
        assertTypeB(idx, 1, null);
        assertTypeB(idx, 2, null);
        assertTypeB(idx, 3, null);
        assertTypeB(idx, 4, null);
        assertTypeB(idx, 5, "5");
        assertTypeB(idx, 6, "6");

        // Create NEW incremental producer which will restore from the state left by the previous incremental producer
        /// adding a new type this time (TypeB).
        HollowProducer backingProducer = createInMemoryProducer();
        backingProducer.initializeDataModel(TypeA.class, TypeB.class);
        backingProducer.restore(nextVersion, blobStore);

        HollowIncrementalProducer incrementalProducer2 = new HollowIncrementalProducer(backingProducer);

        incrementalProducer2.delete(new TypeA(1, "one", 100));
        incrementalProducer2.delete(new TypeA(2, "one", 100));
        incrementalProducer2.addOrModify(new TypeA(5, "five", 5));

        incrementalProducer2.addOrModify(new TypeB(1, "1"));
        incrementalProducer2.addOrModify(new TypeB(2, "2"));
        incrementalProducer2.addOrModify(new TypeB(3, "3"));
        incrementalProducer2.addOrModify(new TypeB(4, "4"));
        incrementalProducer2.delete(new TypeB(5, "ignored"));

        /// .runCycle() flushes the changes to a new data state.
        long finalVersion = incrementalProducer2.runCycle();

        /// now we read the changes and assert
        consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(finalVersion);

        idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "TypeA", "id1", "id2");
        Assert.assertFalse(idx.containsDuplicates());

        assertTypeA(idx, 1, "one", null);
        assertTypeA(idx, 2, "two", 2L);
        assertTypeA(idx, 3, "three", 3L);
        assertTypeA(idx, 4, "five", 6L);
        assertTypeA(idx, 5, "five", 5L);

        idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "TypeB", "id");
        Assert.assertFalse(idx.containsDuplicates());

        // backing producer was never initialized, so only records added to the incremental producer are here
        assertTypeB(idx, 1, "1");
        assertTypeB(idx, 2, "2");
        assertTypeB(idx, 3, "3");
        assertTypeB(idx, 4, "4");
        assertTypeB(idx, 5, null);
        assertTypeB(idx, 6, "6");

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
        HollowIncrementalProducer incrementalProducer = new HollowIncrementalProducer(backingProducer);
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
        HollowIncrementalProducer incrementalProducer = new HollowIncrementalProducer(producer, 2.0d);

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

        HollowIncrementalProducer incrementalProducer = new HollowIncrementalProducer(producer);

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
        HollowIncrementalProducer incrementalProducer = new HollowIncrementalProducer(producer);

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
    public void resumeWorkAfterAnnouncementFail() {
        FakeAnnouncer fakeAnnouncer = new FakeAnnouncer();
        FakeAnnouncer fakeAnnouncerSpy = Mockito.spy(fakeAnnouncer);
        HollowProducer producer =  HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withAnnouncer(fakeAnnouncerSpy)
                .withVersionMinter(new HollowProducer.VersionMinter() {
                    long counter = 0;
                    public long mint() {
                        return ++counter;
                    }
                })
                .withNumStatesBetweenSnapshots(5)
                .build();

        initializeData(producer);

        HollowIncrementalProducer incrementalProducer = new HollowIncrementalProducer(producer);
        incrementalProducer.addOrModify(new TypeA(11, "eleven", 11));
        incrementalProducer.runCycle();

        incrementalProducer.addOrModify(new TypeA(1, "one", 100));
        incrementalProducer.addOrModify(new TypeA(2, "two", 2));
        incrementalProducer.addOrModify(new TypeA(3, "three", 300));

        //Fail announcement on this cycle
        Mockito.doThrow(new RuntimeException("oops")).when(fakeAnnouncerSpy).announce(3);

        try {
            incrementalProducer.runCycle();
        } catch (RuntimeException e) {
        }

        //Incremental producer still has changes
        Assert.assertTrue(incrementalProducer.hasChanges());

        incrementalProducer.addOrModify(new TypeA(10, "ten", 100));
        long version = incrementalProducer.runCycle();

        Assert.assertFalse(incrementalProducer.hasChanges());

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(version);

        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "TypeA", "id1", "id2");
        Assert.assertFalse(idx.containsDuplicates());

        assertTypeA(idx, 10, "ten", 100L);
    }


    @Test
    public void removeOrphanObjectsWithTypeInSnapshot() {
        HollowProducer producer = createInMemoryProducer();
        producer.runCycle(new Populator() {
            public void populate(WriteState state) throws Exception {
                state.add(new TypeC(1, new TypeD(1, "one")));
            }
        });

        HollowIncrementalProducer incrementalProducer = new HollowIncrementalProducer(producer);

        TypeD typeD2 = new TypeD(2, "two");
        TypeC typeC2 = new TypeC(2, typeD2);
        incrementalProducer.addOrModify(typeC2);

        long nextVersion = incrementalProducer.runCycle();

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(nextVersion);


        Collection<HollowObject> allHollowObjectsTypeD =  getAllHollowObjects(consumer, "TypeD");
        List<String> typeDNames = new ArrayList<>();
        for(HollowObject hollowObject : allHollowObjectsTypeD) {
            typeDNames.add(((GenericHollowObject) hollowObject).getObject("value").toString());
        }

        Assert.assertTrue(typeDNames.contains("two"));

        TypeD typeD3 = new TypeD(3, "three");
        typeC2 = new TypeC(2, typeD3);

        incrementalProducer.addOrModify(typeC2);
        long finalVersion = incrementalProducer.runCycle();

        consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(finalVersion);


        allHollowObjectsTypeD = getAllHollowObjects(consumer, "TypeD");
        List<String> finalTypeDNames = new ArrayList<>();
        for(HollowObject hollowObject : allHollowObjectsTypeD) {
            finalTypeDNames.add(((GenericHollowObject) hollowObject).getObject("value").toString());
        }

        Assert.assertFalse(finalTypeDNames.contains("two"));
    }

    @Test
    public void removeOrphanObjectsWithoutTypeInDelta() {
        HollowProducer producer = createInMemoryProducer();

        producer.initializeDataModel(TypeC.class);

        producer.runCycle(new Populator() {
            public void populate(WriteState state) throws Exception {
                state.add(new TypeA(1, "one", 1));
            }
        });

        HollowIncrementalProducer incrementalProducer = new HollowIncrementalProducer(producer);

        TypeD typeD2 = new TypeD(2, "two");
        TypeC typeC2 = new TypeC(2, typeD2);
        incrementalProducer.addOrModify(typeC2);

        incrementalProducer.runCycle();

        TypeD typeD3 = new TypeD(3, "three");
        typeC2 = new TypeC(2, typeD3);

        //Modify typeC2 to point to a new TypeD object
        incrementalProducer.addOrModify(typeC2);

        //Cycle writes a snapshot
        long finalVersion = incrementalProducer.runCycle();

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(finalVersion);

        Collection<HollowObject> allHollowObjectsTypeD = getAllHollowObjects(consumer, "TypeD");
        List<String> finalTypeDNames = new ArrayList<>();
        for(HollowObject hollowObject : allHollowObjectsTypeD) {
            finalTypeDNames.add(((GenericHollowObject) hollowObject).getObject("value").toString());
        }

        Assert.assertFalse(finalTypeDNames.contains("two"));
    }

    private static class FakeAnnouncer implements HollowProducer.Announcer {
        @Override
        public void announce(long stateVersion) {
        }
    }

    private HollowProducer createInMemoryProducer() {
        return HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
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

    @SuppressWarnings("unused")
    @HollowPrimaryKey(fields = "id")
    private static class TypeC {
        int id;
        TypeD typeD;

        public TypeC(int id, TypeD typeD) {
            this.id = id;
            this.typeD = typeD;
        }
    }

    @SuppressWarnings("unused")
    private static class TypeD {
        int id;
        String value;

        public TypeD(int id, String name) {
            this.id = id;
            this.value = name;
        }
    }

    private Collection<HollowObject> getAllHollowObjects(HollowConsumer hollowConsumer, final String type) {
        final HollowReadStateEngine readStateEngine = hollowConsumer.getStateEngine();
        final HollowTypeDataAccess typeDataAccess = readStateEngine.getTypeDataAccess(type);
        final HollowTypeReadState typeState = typeDataAccess.getTypeState();
        return new AllHollowRecordCollection<HollowObject>(typeState) {
            @Override
            protected HollowObject getForOrdinal(int ordinal) {
                return new GenericHollowObject(readStateEngine, type, ordinal);
            }
        };
    }

}
