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

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.InMemoryBlobStore;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.api.producer.listener.IncrementalPopulateListener;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import com.netflix.hollow.core.write.objectmapper.RecordPrimaryKey;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HollowProducerIncrementalTest {

    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void publishAndLoadASnapshot() {
        HollowProducer.Incremental producer = createInMemoryIncrementalProducer();

        /// initialize the data
        initializeData(producer);

        long nextVersion = producer.runIncrementalCycle(iws -> {
            iws.addOrModify(new TypeA(1, "one", 100));
            iws.addOrModify(new TypeA(2, "two", 2));
            iws.addOrModify(new TypeA(3, "three", 300));
            iws.addOrModify(new TypeA(3, "three", 3));
            iws.addOrModify(new TypeA(4, "five", 6));
            iws.delete(new TypeA(5, "five", 5));

            iws.delete(new TypeB(2, "3"));
            iws.addOrModify(new TypeB(5, "5"));
            iws.addOrModify(new TypeB(5, "6"));
            iws.delete(new RecordPrimaryKey("TypeB", new Object[] {3}));
        });

        long finalVersion = producer.runIncrementalCycle(iws -> {
            iws.addOrModify(new TypeA(1, "one", 1000));
        });

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
    public void addIfAbsentWillInitializeNewRecordsButNotOverwriteExistingRecords() {
        HollowProducer.Incremental producer = createInMemoryIncrementalProducer();

        /// initialize the data -- classic producer creates the first state in the delta chain.
        initializeData(producer);

        long version = producer.runIncrementalCycle(iws -> {
            iws.addIfAbsent(new TypeA(100, "one hundred", 9999)); // new
            iws.addIfAbsent(new TypeA(101, "one hundred and one", 9998)); // new
            iws.addIfAbsent(new TypeA(1, "one", 9997)); // exists in prior state
            iws.addIfAbsent(new TypeA(2, "two", 9996)); // exists in prior state
            iws.addOrModify(new TypeA(102, "one hundred and two", 9995)); // new
            iws.addIfAbsent(new TypeA(102, "one hundred and two", 9994)); // new, but already added
            iws.addIfAbsent(new TypeA(103, "one hundred and three", 9993)); // new
            iws.addOrModify(new TypeA(103, "one hundred and three", 9992)); // overwrites prior call to addIfAbsent
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(version);

        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "TypeA", "id1", "id2");
        Assert.assertFalse(idx.containsDuplicates());

        assertTypeA(idx, 100, "one hundred", 9999L);
        assertTypeA(idx, 101, "one hundred and one", 9998L);
        assertTypeA(idx, 1, "one", 1L);
        assertTypeA(idx, 2, "two", 2L);
        assertTypeA(idx, 102, "one hundred and two", 9995L);
        assertTypeA(idx, 103, "one hundred and three", 9992L);
    }

    @Test
    public void publishDirectlyAndRestore() {
        HollowProducer.Incremental producer = createInMemoryIncrementalProducer();

        long nextVersion = producer.runIncrementalCycle(iws -> {
            iws.addOrModify(new TypeA(1, "one", 100));
            iws.addOrModify(new TypeA(2, "two", 2));
            iws.addOrModify(new TypeA(3, "three", 300));
            iws.addOrModify(new TypeA(3, "three", 3));
            iws.addOrModify(new TypeA(4, "five", 6));
            iws.delete(new TypeA(5, "five", 5));

            iws.delete(new TypeB(2, "2"));
            iws.addOrModify(new TypeB(4, "four"));
            iws.addOrModify(new TypeB(5, "6"));
            iws.addOrModify(new TypeB(5, "5"));
            iws.delete(new RecordPrimaryKey("TypeB", new Object[] {4}));
            iws.addOrModify(new TypeB(6, "6"));
        });

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
        HollowProducer.Incremental restoringProducer = createInMemoryIncrementalProducer();
        restoringProducer.initializeDataModel(TypeA.class, TypeB.class);
        restoringProducer.restore(nextVersion, blobStore);

        long finalVersion = producer.runIncrementalCycle(iws -> {
            iws.delete(new TypeA(1, "one", 100));
            iws.delete(new TypeA(2, "one", 100));
            iws.addOrModify(new TypeA(5, "five", 5));

            iws.addOrModify(new TypeB(1, "1"));
            iws.addOrModify(new TypeB(2, "2"));
            iws.addOrModify(new TypeB(3, "3"));
            iws.addOrModify(new TypeB(4, "4"));
            iws.delete(new TypeB(5, "ignored"));

        });

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
        long originalVersion = genesisProducer.runCycle(state -> {
            state.add(new TypeA(1, "one", 1));
        });

        /// now at some point in the future, we will start up and create a new classic producer
        /// to back the HollowIncrementalProducer.
        HollowProducer.Incremental restoringProducer = createInMemoryIncrementalProducer();
        /// adding a new type this time (TypeB).
        restoringProducer.initializeDataModel(TypeA.class, TypeB.class);
        restoringProducer.restore(originalVersion, blobStore);

        long version = restoringProducer.runIncrementalCycle(iws -> {
            iws.addOrModify(new TypeA(1, "one", 2));
            iws.addOrModify(new TypeA(2, "two", 2));
            iws.addOrModify(new TypeB(3, "three"));
        });


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
    public void canRemoveAndModifyNewTypesFromRestoredState() {
        HollowProducer genesisProducer = createInMemoryProducer();

        /// initialize the data -- classic producer creates the first state in the delta chain.
        long originalVersion = genesisProducer.runCycle(state -> {
            state.add(new TypeA(1, "one", 1));
        });

        /// now at some point in the future, we will start up and create a new classic producer
        /// to back the HollowIncrementalProducer.
        assertRemoveAndModifyNewTypes(originalVersion, createInMemoryIncrementalProducer());
        assertRemoveAndModifyNewTypes(originalVersion, createInMemoryIncrementalProducerWithoutIntegrityCheck());
    }

    private void assertRemoveAndModifyNewTypes(long originalVersion, HollowProducer.Incremental restoringProducer) {
        /// adding a new type this time (TypeB).
        restoringProducer.initializeDataModel(TypeA.class, TypeB.class);
        restoringProducer.restore(originalVersion, blobStore);

        restoringProducer.runIncrementalCycle(iws -> {
            iws.addOrModify(new TypeA(1, "one", 2));
            iws.addOrModify(new TypeA(2, "two", 2));
            iws.addOrModify(new TypeB(3, "three"));
            iws.addOrModify(new TypeB(4, "four"));
        });

        long version2 = restoringProducer.runIncrementalCycle(iws -> {
            iws.delete(new RecordPrimaryKey("TypeB", new Object[] { 3 }));
            iws.addOrModify(new TypeB(4, "four!"));
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(originalVersion);
        consumer.triggerRefreshTo(version2);

        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "TypeA", "id1", "id2");
        Assert.assertFalse(idx.containsDuplicates());

        assertTypeA(idx, 1, "one", 2L);
        assertTypeA(idx, 2, "two", 2L);

        /// consumers with established data models don't have visibility into new types.
        consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(version2);

        idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "TypeB", "id");
        Assert.assertFalse(idx.containsDuplicates());
        
        assertEquals(-1, idx.getMatchingOrdinal(3));
        assertTypeB(idx, 4, "four!");
    }


    @Test
    public void removeOrphanObjectsWithTypeInSnapshot() {
        HollowProducer.Incremental producer = createInMemoryIncrementalProducer();
        producer.runIncrementalCycle(iws -> {
            iws.addOrModify(new TypeC(1, new TypeD(1, "one")));
        });

        long nextVersion = producer.runIncrementalCycle(iws -> {
            TypeD typeD2 = new TypeD(2, "two");
            TypeC typeC2 = new TypeC(2, typeD2);
            iws.addOrModify(typeC2);
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(nextVersion);


        Collection<HollowObject> allHollowObjectsTypeD = getAllHollowObjects(consumer, "TypeD");
        List<String> typeDNames = new ArrayList<>();
        for (HollowObject hollowObject : allHollowObjectsTypeD) {
            typeDNames.add(((GenericHollowObject) hollowObject).getObject("value").toString());
        }

        Assert.assertTrue(typeDNames.contains("two"));

        long finalVersion = producer.runIncrementalCycle(iws -> {
            TypeD typeD3 = new TypeD(3, "three");
            TypeC typeC2 = new TypeC(2, typeD3);
            iws.addOrModify(typeC2);
        });

        consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(finalVersion);


        allHollowObjectsTypeD = getAllHollowObjects(consumer, "TypeD");
        List<String> finalTypeDNames = new ArrayList<>();
        for (HollowObject hollowObject : allHollowObjectsTypeD) {
            finalTypeDNames.add(((GenericHollowObject) hollowObject).getObject("value").toString());
        }

        Assert.assertFalse(finalTypeDNames.contains("two"));
    }

    @Test
    public void removeOrphanObjectsWithoutTypeInDelta() {
        HollowProducer.Incremental producer = createInMemoryIncrementalProducer();
        producer.initializeDataModel(TypeC.class);

        producer.runIncrementalCycle(iws -> {
            iws.addOrModify(new TypeA(1, "one", 1));
        });

        producer.runIncrementalCycle(iws -> {
            TypeD typeD2 = new TypeD(2, "two");
            TypeC typeC2 = new TypeC(2, typeD2);
            iws.addOrModify(typeC2);
        });

        long finalVersion = producer.runIncrementalCycle(iws -> {
            TypeD typeD3 = new TypeD(3, "three");
            TypeC typeC2 = new TypeC(2, typeD3);
            //Modify typeC2 to point to a new TypeD object
            iws.addOrModify(typeC2);
        });


        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(finalVersion);

        Collection<HollowObject> allHollowObjectsTypeD = getAllHollowObjects(consumer, "TypeD");
        List<String> finalTypeDNames = new ArrayList<>();
        for (HollowObject hollowObject : allHollowObjectsTypeD) {
            finalTypeDNames.add(((GenericHollowObject) hollowObject).getObject("value").toString());
        }

        Assert.assertFalse(finalTypeDNames.contains("two"));
    }

    @Test
    public void testOutOfScopeWriteStateAccess() {
        HollowProducer.Incremental producer = createInMemoryIncrementalProducer();
        producer.initializeDataModel(TypeC.class);

        AtomicReference<HollowProducer.Incremental.IncrementalWriteState> ar = new AtomicReference<>();
        producer.runIncrementalCycle(iws -> {
            ar.set(iws);
            iws.addOrModify(new TypeA(1, "one", 100));
            iws.addOrModify(new TypeA(2, "two", 2));
            iws.addOrModify(new TypeA(3, "three", 300));
            iws.delete(new TypeA(5, "five", 5));
        });

        try {
            ar.get().addOrModify(new TypeA(1, "one", 100));
            Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            ar.get().delete(new TypeA(1, "one", 100));
            Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            ar.get().addIfAbsent(new TypeA(1, "one", 100));
            Assert.fail();
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void testIncrementalPopulateListenerSuccess() {
        HollowProducer.Incremental producer = createInMemoryIncrementalProducer();
        producer.initializeDataModel(TypeC.class);

        class Listener implements IncrementalPopulateListener {
            long version;

            @Override
            public void onIncrementalPopulateStart(long version) {
                this.version = version;
            }

            long removed;
            long addedOrModified;

            @Override
            public void onIncrementalPopulateComplete(
                    Status status, long removed, long addedOrModified,
                    long version, Duration elapsed) {
                Assert.assertEquals(Status.StatusType.SUCCESS, status.getType());
                Assert.assertNull(status.getCause());
                Assert.assertEquals(this.version, version);

                this.removed = removed;
                this.addedOrModified = addedOrModified;
            }
        }

        Listener l = new Listener();
        producer.addListener(l);

        long version = producer.runIncrementalCycle(iws -> {
            iws.addOrModify(new TypeA(1, "one", 100));
            iws.addOrModify(new TypeA(2, "two", 2));
            iws.addOrModify(new TypeA(3, "three", 300));
            iws.delete(new TypeA(5, "five", 5));
        });
        Assert.assertEquals(version, l.version);
        Assert.assertEquals(1, l.removed);
        Assert.assertEquals(3, l.addedOrModified);


        version = producer.runIncrementalCycle(iws -> {
            iws.addOrModify(new TypeA(1, "one", 1000));
        });
        Assert.assertEquals(version, l.version);
        Assert.assertEquals(0, l.removed);
        Assert.assertEquals(1, l.addedOrModified);
    }

    @Test
    public void testIncrementalPopulateListenerFailure() {
        HollowProducer.Incremental producer = createInMemoryIncrementalProducer();
        producer.initializeDataModel(TypeC.class);

        class Listener implements IncrementalPopulateListener {
            @Override
            public void onIncrementalPopulateStart(long version) {
            }

            @Override
            public void onIncrementalPopulateComplete(
                    Status status, long removed, long addedOrModified,
                    long version, Duration elapsed) {
                Assert.assertEquals(Status.StatusType.FAIL, status.getType());
                Assert.assertNotNull(status.getCause());
            }
        }

        Listener l = new Listener();
        producer.addListener(l);

        try {
            producer.runIncrementalCycle(iws -> {
                iws.addOrModify(new TypeA(1, "one", 100));
                iws.addOrModify(new TypeA(2, "two", 2));
                iws.addOrModify(new TypeA(3, "three", 300));
                iws.delete(new TypeA(5, "five", 5));
                throw new RuntimeException("runIncrementalCycle failed");
            });
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertEquals("runIncrementalCycle failed", e.getMessage());
        }

    }

    private HollowProducer.Incremental createInMemoryIncrementalProducer() {
        return new HollowProducer.Builder<>()
                .withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .buildIncremental();
    }

    private HollowProducer.Incremental createInMemoryIncrementalProducerWithoutIntegrityCheck() {
        return new HollowProducer.Builder<>()
                .withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .noIntegrityCheck()
                .buildIncremental();
    }

    private HollowProducer createInMemoryProducer() {
        return new HollowProducer.Builder<>()
                .withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();
    }

    private long initializeData(HollowProducer.Incremental producer) {
        return producer.runIncrementalCycle(state -> {
            state.addOrModify(new TypeA(1, "one", 1));
            state.addOrModify(new TypeA(2, "two", 2));
            state.addOrModify(new TypeA(3, "three", 3));
            state.addOrModify(new TypeA(4, "four", 4));
            state.addOrModify(new TypeA(5, "five", 5));

            state.addOrModify(new TypeB(1, "1"));
            state.addOrModify(new TypeB(2, "2"));
            state.addOrModify(new TypeB(3, "3"));
            state.addOrModify(new TypeB(4, "4"));
        });
    }

    private void assertTypeA(
            HollowPrimaryKeyIndex typeAIdx, int id1,
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

    private void assertTypeB(
            HollowPrimaryKeyIndex typeBIdx, int id1,
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
    @HollowPrimaryKey(fields = {"id1", "id2"})
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