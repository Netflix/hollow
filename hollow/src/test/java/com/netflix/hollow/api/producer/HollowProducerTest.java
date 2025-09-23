/*
 *  Copyright 2016-2019 Netflix, Inc.
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.delegate.HollowObjectGenericDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.producer.HollowProducer.Blob;
import com.netflix.hollow.api.producer.HollowProducer.Blob.Type;
import com.netflix.hollow.api.producer.HollowProducer.HeaderBlob;
import com.netflix.hollow.api.producer.HollowProducer.ReadState;
import com.netflix.hollow.api.producer.HollowProducerListener.ProducerStatus;
import com.netflix.hollow.api.producer.HollowProducerListener.RestoreStatus;
import com.netflix.hollow.api.producer.HollowProducerListener.Status;
import com.netflix.hollow.api.producer.enforcer.BasicSingleProducerEnforcer;
import com.netflix.hollow.api.producer.enforcer.SingleProducerEnforcer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemAnnouncer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.api.producer.listener.VetoableListener;
import com.netflix.hollow.api.producer.model.CustomReferenceType;
import com.netflix.hollow.api.producer.model.HasAllTypeStates;
import com.netflix.hollow.core.HollowBlobHeader;
import com.netflix.hollow.core.read.engine.HollowBlobHeaderReader;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import com.netflix.hollow.test.InMemoryBlobStore;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowProducerTest {
    private static final String NAMESPACE = "hollowProducerTest";

    private File tmpFolder;
    private HollowObjectSchema schema;
    private HollowConsumer.BlobRetriever blobRetriever;

    private Map<Long, Blob> blobMap = new HashMap<>();
    private Map<Long, File> blobFileMap = new HashMap<>();
    private Map<Long, HeaderBlob> headerBlobMap = new HashMap<>();
    private Map<Long, File> headerFileMap = new HashMap<>();
    private ProducerStatus lastProducerStatus;
    private RestoreStatus lastRestoreStatus;

    @Before
    public void setUp() throws IOException {
        schema = new HollowObjectSchema("TestPojo", 2, "id");
        schema.addField("id", FieldType.INT);
        schema.addField("v1", FieldType.INT);

        tmpFolder = Files.createTempDirectory(null).toFile();
        blobRetriever = new FakeBlobRetriever(NAMESPACE, tmpFolder.getAbsolutePath());
    }

    private HollowProducer createProducer(File tmpFolder, HollowObjectSchema... schemas) {
        return createProducer(tmpFolder, null, null, schemas);
    }

    private HollowProducer createProducer(File tmpFolder,
                                          HollowConsumer.UpdatePlanBlobVerifier updatePlanBlobVerifier,
                                          HollowProducer.VersionMinter versionMinter,
                                          HollowObjectSchema... schemas) {
         HollowProducer.Builder producerBuilder = HollowProducer.withPublisher(new FakeBlobPublisher())
            .withAnnouncer(new HollowFilesystemAnnouncer(tmpFolder.toPath()));
        if(updatePlanBlobVerifier != null) {
            producerBuilder = producerBuilder.withUpdatePlanVerifier(updatePlanBlobVerifier);
        }
        if (versionMinter != null) {
            producerBuilder = producerBuilder.withVersionMinter(versionMinter);
        }
        HollowProducer producer = producerBuilder.build();
        if (schemas != null && schemas.length > 0) {
            producer.initializeDataModel(schemas);
        }
        producer.addListener(new FakeProducerListener());
        return producer;
    }

    @After
    public void tearDown() {
        for (File file : blobFileMap.values()) {
            System.out.println("\t deleting: " + file);
            file.delete();
        }
    }

    @Test
    public void testPopulateNoChangesVersion() {
        HollowProducer producer = createProducer(tmpFolder, schema);
        long v0 = producer.runCycle(ws -> {});
        assertEquals(0, v0); // must not publish an empty snapshot

        producer = createProducer(tmpFolder);
        long v1 = producer.runCycle(ws -> {
            ws.add(1);
        });
        assertEquals(producer.getCycleCountWithPrimaryStatus(), 1);
        // Run cycle with no changes
        long v2 = producer.runCycle(ws -> {
            ws.add(1);
        });
        assertEquals(producer.getCycleCountWithPrimaryStatus(), 2);
        long v3 = producer.runCycle(ws -> {
            ws.add(2);
        });
        assertEquals(producer.getCycleCountWithPrimaryStatus(), 3);

        assertEquals(v1, v2);
        assertTrue(v3 > v2);
    }

    @Test
    public void testNotPrimaryProducerVersion() {
        BasicSingleProducerEnforcer enforcer = new BasicSingleProducerEnforcer();
        HollowProducer producer = HollowProducer.withPublisher(new FakeBlobPublisher())
                .withSingleProducerEnforcer(enforcer)
                .withAnnouncer(new HollowFilesystemAnnouncer(tmpFolder.toPath()))
                .build();
        producer.addListener(new FakeProducerListener());

        long v1 = producer.runCycle(ws -> {
            ws.add(1);
        });

        enforcer.disable();
        // Run cycle as not the primary producer
        long v2 = producer.runCycle(ws -> {
            ws.add(1);
        });
        assertEquals(producer.getCycleCountWithPrimaryStatus(), 0);
        // Run cycle as the primary producer
        enforcer.enable();
        long v3 = producer.runCycle(ws -> {
            ws.add(2);
        });

        assertEquals(v1, v2);
        assertTrue(v3 > v2);
        assertEquals(producer.getCycleCountWithPrimaryStatus(), 1);
    }

    @Test
    public void testNonPrimaryCantPublish() {
        // when producer starts cycle as primary but relinquishes primary status sometime before publish
        BasicSingleProducerEnforcer enforcer = new BasicSingleProducerEnforcer();
        HollowProducer producer = HollowProducer.withPublisher(new FakeBlobPublisher())
                .withSingleProducerEnforcer(enforcer)
                .withAnnouncer(new HollowFilesystemAnnouncer(tmpFolder.toPath()))
                .build();

        producer.runCycle(ws -> {
            ws.add(1);
        });

        producer.addListener(new ProducerYieldsPrimaryBeforePublish(enforcer));
        try {
            producer.runCycle(ws -> {
                ws.add(2);
            });
        } catch (IllegalStateException e) {
            assertTrue(e instanceof HollowProducer.NotPrimaryMidCycleException);
            assertEquals("Publish failed primary (aka leader) check", e.getMessage());
            assertEquals(producer.getCycleCountWithPrimaryStatus(), 2); // counted as cycle ran for the producer with primary status but lost status mid cycle. Doesn't matter as the next cycle result in a no-op.
            return;
        }
        fail();
    }

    @Test
    public void testNonPrimaryProducerCantAnnounce() {
        // when producer starts cycle as primary but relinquishes primary status sometime before announcement
        BasicSingleProducerEnforcer enforcer = new BasicSingleProducerEnforcer();
        HollowProducer producer = HollowProducer.withPublisher(new FakeBlobPublisher())
                .withSingleProducerEnforcer(enforcer)
                .withAnnouncer(new HollowFilesystemAnnouncer(tmpFolder.toPath()))
                .build();
        producer.addListener(new ProducerYieldsPrimaryBeforeAnnounce(enforcer));
        try {
            producer.runCycle(ws -> {
                ws.add(1);
            });
        } catch (HollowProducer.NotPrimaryMidCycleException e) {
            assertEquals("Announcement failed primary (aka leader) check", e.getMessage());
            assertEquals(producer.getCycleCountWithPrimaryStatus(), 1); // counted as cycle ran for producer with primary status
            return;
        }
        fail();
    }

    @Test
    public void testRestoreFailure() {
        HollowProducer producer = createProducer(tmpFolder, schema);
        long fakeVersion = 101;

        try {
            producer.restore(fakeVersion, blobRetriever);
            fail();
        } catch(Exception expected) { }

        Assert.assertNotNull(lastRestoreStatus);
        assertEquals(Status.FAIL, lastRestoreStatus.getStatus());
        assertEquals(producer.getCycleCountWithPrimaryStatus(), 0);
    }

    @Test
    public void testPublishAndRestore() {
        HollowProducer producer = createProducer(tmpFolder, schema);
        long version = testPublishV1(producer, 2, 10);

        producer.restore(version, blobRetriever);
        Assert.assertNotNull(lastRestoreStatus);
        assertEquals(Status.SUCCESS, lastRestoreStatus.getStatus());
        assertEquals("Version should be the same", version, lastRestoreStatus.getDesiredVersion());
        assertEquals(producer.getCycleCountWithPrimaryStatus(), 1);
    }

    @Test
    public void testHeaderPublish() throws IOException {
        HollowProducer producer = createProducer(tmpFolder, schema);
        long version = testPublishV1(producer, 2, 10);
        HollowConsumer.HeaderBlob headerBlob = blobRetriever.retrieveHeaderBlob(version);
        Assert.assertNotNull(headerBlob);
        assertEquals(version, headerBlob.getVersion());
        HollowBlobHeader header = new HollowBlobHeaderReader().readHeader(headerBlob.getInputStream());
        Assert.assertNotNull(header);
        assertEquals(1, header.getSchemas().size());
        assertEquals(schema, header.getSchemas().get(0));
    }

    @Test
    public void testMultipleRestores() throws Exception {
        HollowProducer producer = createProducer(tmpFolder, schema);

        System.out.println("\n\n------------ Publish a few versions ------------\n");
        List<Long> versions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int size = i + 1;
            int valueMultiplier = i + 10;
            long version = testPublishV1(producer, size, valueMultiplier);
            versions.add(version);
        }
        assertEquals(producer.getCycleCountWithPrimaryStatus(), 5);

        System.out.println("\n\n------------ Restore and validate ------------\n");
        for (int i = 0; i < versions.size(); i++) {
            long version = versions.get(i);
            int size = i + 1;
            int valueMultiplier = i + 10;

            restoreAndAssert(producer, version, size, valueMultiplier);
        }

        System.out.println("\n\n------------ Restore in reverse order and validate ------------\n");
        for (int i = versions.size() - 1; i >= 0; i--) {
            long version = versions.get(i);
            int size = i + 1;
            int valueMultiplier = i + 10;

            restoreAndAssert(producer, version, size, valueMultiplier);
        }
    }

    @Test
    public void testAlternatingPublishAndRestores() throws Exception {
        HollowProducer producer = createProducer(tmpFolder, schema);

        List<Long> versions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int size = i + 10;
            int valueMultiplier = i + 10;
            long version = testPublishV1(producer, size, valueMultiplier);
            versions.add(version);

            restoreAndAssert(producer, version, size, valueMultiplier);
        }
    }

    @Test
    public void testPublishAndRestoreWithSchemaChanges() throws Exception {
        int sizeV1 = 3;
        int valueMultiplierV1 = 20;

        long v1 = 0;
        { // Publish V1
            HollowProducer producer = createProducer(tmpFolder, schema);
            v1 = testPublishV1(producer, sizeV1, valueMultiplierV1);
            assertEquals(producer.getCycleCountWithPrimaryStatus(), 1);
        }

        // Publish V2;
        int sizeV2 = sizeV1 * 2;
        int valueMultiplierV2 = valueMultiplierV1 * 2;
        HollowObjectSchema schemaV2 = new HollowObjectSchema("TestPojo", 3, "id");
        schemaV2.addField("id", FieldType.INT);
        schemaV2.addField("v1", FieldType.INT);
        schemaV2.addField("v2", FieldType.INT);
        HollowProducer producerV2 = createProducer(tmpFolder, schemaV2);
        long v2 = testPublishV2(producerV2, sizeV2, valueMultiplierV2);

        { // Restore V1
            int valueFieldCount = 1;
            restoreAndAssert(producerV2, v1, sizeV1, valueMultiplierV1, valueFieldCount);
        }

        // Publish V3
        int sizeV3 = sizeV1 * 3;
        int valueMultiplierV3 = valueMultiplierV1 * 3;
        long v3 = testPublishV2(producerV2, sizeV3, valueMultiplierV3);

        { // Restore V2
            int valueFieldCount = 2;
            restoreAndAssert(producerV2, v2, sizeV2, valueMultiplierV2, valueFieldCount);
        }

        { // Restore V3
            int valueFieldCount = 2;
            restoreAndAssert(producerV2, v3, sizeV3, valueMultiplierV3, valueFieldCount);
        }
        assertEquals(producerV2.getCycleCountWithPrimaryStatus(), 2);
    }

    @Test
    public void testRestoreToNonExact() {
        HollowProducer producer = createProducer(tmpFolder, schema);
        long version = testPublishV1(producer, 2, 7);

        producer = createProducer(tmpFolder, schema);
        producer.restore(version + 1, blobRetriever);
        Assert.assertNotNull(lastRestoreStatus);
        assertEquals(Status.SUCCESS, lastRestoreStatus.getStatus());
        assertEquals("Should have reached correct version", version,
                lastRestoreStatus.getVersionReached());
        assertEquals("Should have correct desired version", version + 1,
                lastRestoreStatus.getDesiredVersion());
        assertEquals(producer.getCycleCountWithPrimaryStatus(), 0); // no cycle run
    }

    @Test
    public void testRollsBackStateEngineOnPublishFailure() throws Exception {
        HollowProducer producer = spy(createProducer(tmpFolder, schema));
        assertEquals("Should have no populated ordinals", 0,
                producer.getWriteEngine().getTypeState("TestPojo").getPopulatedBitSet().cardinality());
        doThrow(new RuntimeException("Publish failed")).when(producer).publish(
                any(ProducerListenerSupport.ProducerListeners.class), any(Long.class), any(AbstractHollowProducer.Artifacts.class));
        try {
            producer.runCycle(newState -> newState.add(new TestPojoV1(1, 1)));
        } catch (RuntimeException e) { // expected
        }
        assertEquals("Should still have no populated ordinals", 0,
                producer.getWriteEngine().getTypeState("TestPojo").getPopulatedBitSet().cardinality());
        assertEquals(producer.getCycleCountWithPrimaryStatus(), 1); // counted as cycle ran for producer with primary status
    }

    private long testPublishV1(HollowProducer producer, final int size, final int valueMultiplier) {
        producer.runCycle(newState -> {
            for (int i = 1; i <= size; i++) {
                newState.add(new TestPojoV1(i, i * valueMultiplier));
            }
        });
        Assert.assertNotNull(lastProducerStatus);
        assertEquals(Status.SUCCESS, lastProducerStatus.getStatus());
        return lastProducerStatus.getVersion();
    }

    private long testPublishV2(HollowProducer producer, final int size, final int valueMultiplier) {
        producer.runCycle(newState -> {
            for (int i = 1; i <= size; i++) {
                newState.add(new TestPojoV2(i, i * valueMultiplier, i * valueMultiplier));
            }
        });
        Assert.assertNotNull(lastProducerStatus);
        assertEquals(Status.SUCCESS, lastProducerStatus.getStatus());
        return lastProducerStatus.getVersion();
    }

    private void restoreAndAssert(HollowProducer producer, long version, int size, int valueMultiplier) throws Exception {
        restoreAndAssert(producer, version, size, valueMultiplier, 1);
    }

    private void restoreAndAssert(HollowProducer producer, long version, int size, int valueMultiplier, int valueFieldCount) {
        ReadState readState = producer.restore(version, blobRetriever);
        Assert.assertNotNull(lastRestoreStatus);
        assertEquals(Status.SUCCESS, lastRestoreStatus.getStatus());
        assertEquals("Version should be the same", version, lastRestoreStatus.getDesiredVersion());

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readState.getStateEngine().getTypeState("TestPojo");
        BitSet populatedOrdinals = typeState.getPopulatedOrdinals();
        assertEquals(size, populatedOrdinals.cardinality());

        int ordinal = populatedOrdinals.nextSetBit(0);
        while (ordinal != -1) {
            GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(typeState), ordinal);
            System.out.println("ordinal=" + ordinal + obj);

            int id = obj.getInt("id");
            for (int i = 0; i < valueFieldCount; i++) {
                String valueFN = "v" + (i + 1);
                int value = id * valueMultiplier;
                assertEquals(valueFN, value, obj.getInt(valueFN));
            }

            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }
        System.out.println("Asserted Correctness of version:" + version + "\n\n");
    }

    @Test
    public void testReshardingAllTypes() {
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        for (boolean allowResharding : Arrays.asList(true, false)) {
            HollowProducer.Builder producerBuilder = HollowProducer.withPublisher(blobStore)
                    .withBlobStager(blobStager);
            if (allowResharding)
                    producerBuilder = producerBuilder.withTypeResharding(true);
            HollowProducer producer = producerBuilder.withTargetMaxTypeShardSize(32).build();
            producer.initializeDataModel(HasAllTypeStates.class);
            long v0 = producer.runCycle(ws -> {
                // At shard size 32 results in 2 shards for Integer, 4 for ListOfInteger, etc.
                for (int i=0;i<50;i++) {
                    final long val = new Long(i);
                    Set<String> set = new HashSet<>(Arrays.asList("e" + val));
                    List<Integer> list = Arrays.asList(i);
                    Map<String, Long> map = new HashMap<String, Long>(){{put("k"+val, new Long(val));}};
                    ws.add(new HasAllTypeStates(
                            new CustomReferenceType(val),
                            set,
                            list,
                            map
                    ));
                }
            });
            assertEquals(2, producer.getWriteEngine().getTypeState("Long").getNumShards());
            assertEquals(2, producer.getWriteEngine().getTypeState("CustomReferenceType").getNumShards());
            assertEquals(8, producer.getWriteEngine().getTypeState("SetOfString").getNumShards());
            assertEquals(4, producer.getWriteEngine().getTypeState("ListOfInteger").getNumShards());
            assertEquals(8, producer.getWriteEngine().getTypeState("MapOfStringToLong").getNumShards());
            HollowConsumer c = HollowConsumer.withBlobRetriever(blobStore)
                    .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
                        @Override
                        public boolean allowDoubleSnapshot() {
                            return false;
                        }
                        @Override
                        public int maxDeltasBeforeDoubleSnapshot() {
                            return Integer.MAX_VALUE;
                        }
                    })
                    .build();
            c.triggerRefreshTo(v0);
            assertEquals(2, c.getStateEngine().getTypeState("Long").numShards());
            assertEquals(2, c.getStateEngine().getTypeState("CustomReferenceType").numShards());
            assertEquals(8, c.getStateEngine().getTypeState("SetOfString").numShards());
            assertEquals(4, c.getStateEngine().getTypeState("ListOfInteger").numShards());
            assertEquals(8, c.getStateEngine().getTypeState("MapOfStringToLong").numShards());

            long v1 = producer.runCycle(ws -> {

                // 2x the data, causes more num shards at same shard size if resharding is enabled
                for (int i=0;i<100;i++) {
                    final long val = new Long(i);
                    ws.add(new HasAllTypeStates(
                            new CustomReferenceType(val),
                            new HashSet<>(Arrays.asList("e" + val)),
                            Arrays.asList(i),
                            new HashMap<String, Long>(){{put("k"+val, new Long(val));}}
                    ));
                }
            });
            c.triggerRefreshTo(v1);
            if (allowResharding) {
                assertTrue(2 < producer.getWriteEngine().getTypeState("Long").getNumShards());
                assertTrue(2 < producer.getWriteEngine().getTypeState("CustomReferenceType").getNumShards());
                assertTrue(8 < producer.getWriteEngine().getTypeState("SetOfString").getNumShards());
                assertTrue(4 < producer.getWriteEngine().getTypeState("ListOfInteger").getNumShards());
                assertTrue(8 < producer.getWriteEngine().getTypeState("MapOfStringToLong").getNumShards());

                assertTrue(2 < c.getStateEngine().getTypeState("Long").numShards());
                assertTrue(2 < c.getStateEngine().getTypeState("CustomReferenceType").numShards());
                assertTrue(8 < c.getStateEngine().getTypeState("SetOfString").numShards());
                assertTrue(4 < c.getStateEngine().getTypeState("ListOfInteger").numShards());
                assertTrue(8 < c.getStateEngine().getTypeState("MapOfStringToLong").numShards());
            } else {
                assertEquals(2, producer.getWriteEngine().getTypeState("Long").getNumShards());
                assertEquals(2, producer.getWriteEngine().getTypeState("CustomReferenceType").getNumShards());
                assertEquals(8, producer.getWriteEngine().getTypeState("SetOfString").getNumShards());
                assertEquals(4, producer.getWriteEngine().getTypeState("ListOfInteger").getNumShards());
                assertEquals(8, producer.getWriteEngine().getTypeState("MapOfStringToLong").getNumShards());

                assertEquals(2, c.getStateEngine().getTypeState("Long").numShards());
                assertEquals(2, c.getStateEngine().getTypeState("CustomReferenceType").numShards());
                assertEquals(8, c.getStateEngine().getTypeState("SetOfString").numShards());
                assertEquals(4, c.getStateEngine().getTypeState("ListOfInteger").numShards());
                assertEquals(8, c.getStateEngine().getTypeState("MapOfStringToLong").numShards());
            }

            long v2 = producer.runCycle(ws -> {
                // still same num shards, because ghost records are accounted for in determining num shards
                for (int i=0;i<50;i++) {
                    final long val = new Long(i);
                    ws.add(new HasAllTypeStates(
                            new CustomReferenceType(val),
                            new HashSet<>(Arrays.asList("e" + val)),
                            Arrays.asList(i),
                            new HashMap<String, Long>(){{put("k"+val, new Long(val));}}
                    ));
                }
            });
            c.triggerRefreshTo(v2);
            if (allowResharding) {
                assertTrue(2 < producer.getWriteEngine().getTypeState("Long").getNumShards());
                assertTrue(2 < producer.getWriteEngine().getTypeState("CustomReferenceType").getNumShards());
                assertTrue(8 < producer.getWriteEngine().getTypeState("SetOfString").getNumShards());
                assertTrue(4 < producer.getWriteEngine().getTypeState("ListOfInteger").getNumShards());
                assertTrue(8 < producer.getWriteEngine().getTypeState("MapOfStringToLong").getNumShards());

                assertTrue(2 < c.getStateEngine().getTypeState("Long").numShards());
                assertTrue(2 < c.getStateEngine().getTypeState("CustomReferenceType").numShards());
                assertTrue(8 < c.getStateEngine().getTypeState("SetOfString").numShards());
                assertTrue(4 < c.getStateEngine().getTypeState("ListOfInteger").numShards());
                assertTrue(8 < c.getStateEngine().getTypeState("MapOfStringToLong").numShards());
            } else {
                assertEquals(2, producer.getWriteEngine().getTypeState("Long").getNumShards());
                assertEquals(2, producer.getWriteEngine().getTypeState("CustomReferenceType").getNumShards());
                assertEquals(8, producer.getWriteEngine().getTypeState("SetOfString").getNumShards());
                assertEquals(4, producer.getWriteEngine().getTypeState("ListOfInteger").getNumShards());
                assertEquals(8, producer.getWriteEngine().getTypeState("MapOfStringToLong").getNumShards());

                assertEquals(2, c.getStateEngine().getTypeState("Long").numShards());
                assertEquals(2, c.getStateEngine().getTypeState("CustomReferenceType").numShards());
                assertEquals(8, c.getStateEngine().getTypeState("SetOfString").numShards());
                assertEquals(4, c.getStateEngine().getTypeState("ListOfInteger").numShards());
                assertEquals(8, c.getStateEngine().getTypeState("MapOfStringToLong").numShards());
            }

            long v3 = producer.runCycle(ws -> {
                // back to original shard count
                for (int i=0;i<49;i++) {    // one change in runCycle
                    final long val = new Long(i);
                    ws.add(new HasAllTypeStates(
                            new CustomReferenceType(val),
                            new HashSet<>(Arrays.asList("e" + val)),
                            Arrays.asList(i),
                            new HashMap<String, Long>(){{put("k"+val, new Long(val));}}
                    ));
                }
            });
            c.triggerRefreshTo(v3);
            assertEquals(2, producer.getWriteEngine().getTypeState("Long").getNumShards());
            assertEquals(2, producer.getWriteEngine().getTypeState("CustomReferenceType").getNumShards());
            assertEquals(8, producer.getWriteEngine().getTypeState("SetOfString").getNumShards());
            assertEquals(4, producer.getWriteEngine().getTypeState("ListOfInteger").getNumShards());
            assertEquals(8, producer.getWriteEngine().getTypeState("MapOfStringToLong").getNumShards());
            assertEquals(2, c.getStateEngine().getTypeState("Long").numShards());
            assertEquals(2, c.getStateEngine().getTypeState("CustomReferenceType").numShards());
            assertEquals(8, c.getStateEngine().getTypeState("SetOfString").numShards());
            assertEquals(4, c.getStateEngine().getTypeState("ListOfInteger").numShards());
            assertEquals(8, c.getStateEngine().getTypeState("MapOfStringToLong").numShards());
        }
    }

    @Test
    public void testDeltaChainWithMultipleReshardingInvocations() {

        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();
        int numCycles = 100;
        int numRecordsMin = 1;
        long v = 0;

        HollowProducer p = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager)
                .withTypeResharding(true).withTargetMaxTypeShardSize(32).build();

        HollowConsumer c = HollowConsumer.withBlobRetriever(blobStore)
                .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
                    @Override
                    public boolean allowDoubleSnapshot() {
                        return false;
                    }
                    @Override
                    public int maxDeltasBeforeDoubleSnapshot() {
                        return Integer.MAX_VALUE;
                    }
                })
                .build();

        long startVersion = 0;
        Map<String, Set<Integer>> numShardsExercised = new HashMap<>();
        for (int n = 0; n < numCycles; n ++) {
            int numRecords = numRecordsMin + new Random().nextInt(100);
            v = p.runCycle(state -> {
                for (int i = 0; i < numRecords; i ++) {
                    final long val = new Long(i);
                    state.add(new HasAllTypeStates(
                            new CustomReferenceType(val),
                            new HashSet<>(Arrays.asList("e" + val)),
                            Arrays.asList(i),
                            new HashMap<String, Long>(){{put("k"+val, new Long(val));}}
                    ));

                }
            });
            if (n == 0)
                startVersion = v;

            c.triggerRefreshTo(v);

            for (HollowTypeReadState type : c.getStateEngine().getTypeStates()) {
                String t = type.getSchema().getName();
                if (!numShardsExercised.containsKey(t)) {
                    numShardsExercised.put(t, new HashSet<>());
                }
                numShardsExercised.get(t).add(new Integer(type.numShards()));

            }
        }
        long endVersion = v;


        assertEquals(8, numShardsExercised.size());
        for (HollowTypeReadState type : c.getStateEngine().getTypeStates()) {
            assertTrue(numShardsExercised.get(type.getSchema().getName()).size() > 1);
        }

        c.triggerRefreshTo(startVersion);
        assertEquals(startVersion, c.getCurrentVersionId());
        c.triggerRefreshTo(endVersion);
        assertEquals(endVersion, c.getCurrentVersionId());
        c.triggerRefreshTo(startVersion);
        assertEquals(startVersion, c.getCurrentVersionId());
    }

    @Test
    public void testNumShardsMaintainedWhenNoResharding() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();
        HollowProducer nonReshardingProducer1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager)
                .withTypeResharding(false).withTargetMaxTypeShardSize(32).build();
        long v1_1 = nonReshardingProducer1.runCycle(ws -> {
            // At target shard size 32, causes 2 shards for Integer, 4 for ListOfInteger, etc.
            for (int i=0;i<50;i++) {
                final long val = new Long(i);
                ws.add(new HasAllTypeStates(
                        new CustomReferenceType(val),
                        new HashSet<>(Arrays.asList("e" + val)),
                        Arrays.asList(i),
                        new HashMap<String, Long>(){{put("k"+val, new Long(val));}}
                ));
            }
        });
        assertEquals(2, nonReshardingProducer1.getWriteEngine().getTypeState("Long").getNumShards());
        assertEquals(2, nonReshardingProducer1.getWriteEngine().getTypeState("CustomReferenceType").getNumShards());
        assertEquals(8, nonReshardingProducer1.getWriteEngine().getTypeState("SetOfString").getNumShards());
        assertEquals(4, nonReshardingProducer1.getWriteEngine().getTypeState("ListOfInteger").getNumShards());
        assertEquals(8, nonReshardingProducer1.getWriteEngine().getTypeState("MapOfStringToLong").getNumShards());

        try {
            long v1_2 = nonReshardingProducer1.runCycle(ws -> {
                throw new RuntimeException("failed population");
            });
            fail("exception expected");
        } catch (Exception e){
        }

        long v1_3 = nonReshardingProducer1.runCycle(ws -> {
            for (int i=0;i<100;i++) {
                final long val = new Long(i);
                ws.add(new HasAllTypeStates(
                        new CustomReferenceType(val),
                        new HashSet<>(Arrays.asList("e" + val)),
                        Arrays.asList(i),
                        new HashMap<String, Long>(){{put("k"+val, new Long(val));}}
                ));
            }
        });
        // no change in num shards
        assertEquals(2, nonReshardingProducer1.getWriteEngine().getTypeState("Long").getNumShards());
        assertEquals(2, nonReshardingProducer1.getWriteEngine().getTypeState("CustomReferenceType").getNumShards());
        assertEquals(8, nonReshardingProducer1.getWriteEngine().getTypeState("SetOfString").getNumShards());
        assertEquals(4, nonReshardingProducer1.getWriteEngine().getTypeState("ListOfInteger").getNumShards());
        assertEquals(8, nonReshardingProducer1.getWriteEngine().getTypeState("MapOfStringToLong").getNumShards());

        HollowProducer nonReshardingProducer2 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager)
                .withTypeResharding(false).withTargetMaxTypeShardSize(32).build();
        nonReshardingProducer2.initializeDataModel(HasAllTypeStates.class);

        assertEquals(-1, nonReshardingProducer2.getWriteEngine().getTypeState("Long").getNumShards());
        assertEquals(-1, nonReshardingProducer2.getWriteEngine().getTypeState("CustomReferenceType").getNumShards());
        assertEquals(-1, nonReshardingProducer2.getWriteEngine().getTypeState("SetOfString").getNumShards());
        assertEquals(-1, nonReshardingProducer2.getWriteEngine().getTypeState("ListOfInteger").getNumShards());
        assertEquals(-1, nonReshardingProducer2.getWriteEngine().getTypeState("MapOfStringToLong").getNumShards());

        nonReshardingProducer2.restore(v1_1, blobStore);
        assertEquals(2, nonReshardingProducer2.getWriteEngine().getTypeState("Long").getNumShards());
        assertEquals(2, nonReshardingProducer2.getWriteEngine().getTypeState("CustomReferenceType").getNumShards());
        assertEquals(8, nonReshardingProducer2.getWriteEngine().getTypeState("SetOfString").getNumShards());
        assertEquals(4, nonReshardingProducer2.getWriteEngine().getTypeState("ListOfInteger").getNumShards());
        assertEquals(8, nonReshardingProducer2.getWriteEngine().getTypeState("MapOfStringToLong").getNumShards());

        try {
            nonReshardingProducer2.runCycle(ws -> {
                // causes 4 shards for Integer at shard size 32
                throw new RuntimeException("failed population");
            });
            fail("exception expected");
        } catch (Exception e){
        }
        // still no change in num shards
        assertEquals(2, nonReshardingProducer2.getWriteEngine().getTypeState("Long").getNumShards());
        assertEquals(2, nonReshardingProducer2.getWriteEngine().getTypeState("CustomReferenceType").getNumShards());
        assertEquals(8, nonReshardingProducer2.getWriteEngine().getTypeState("SetOfString").getNumShards());
        assertEquals(4, nonReshardingProducer2.getWriteEngine().getTypeState("ListOfInteger").getNumShards());
        assertEquals(8, nonReshardingProducer2.getWriteEngine().getTypeState("MapOfStringToLong").getNumShards());

        nonReshardingProducer2.runCycle(ws -> {
            for (int i=0;i<100;i++) {
                final long val = new Long(i);
                ws.add(new HasAllTypeStates(
                        new CustomReferenceType(val),
                        new HashSet<>(Arrays.asList("e" + val)),
                        Arrays.asList(i),
                        new HashMap<String, Long>(){{put("k"+val, new Long(val));}}
                ));
            }
        });
        // still no change in num shards
        assertEquals(2, nonReshardingProducer2.getWriteEngine().getTypeState("Long").getNumShards());
        assertEquals(2, nonReshardingProducer2.getWriteEngine().getTypeState("CustomReferenceType").getNumShards());
        assertEquals(8, nonReshardingProducer2.getWriteEngine().getTypeState("SetOfString").getNumShards());
        assertEquals(4, nonReshardingProducer2.getWriteEngine().getTypeState("ListOfInteger").getNumShards());
        assertEquals(8, nonReshardingProducer2.getWriteEngine().getTypeState("MapOfStringToLong").getNumShards());
    }

    // Delta should serialize type with num shard change even if there is no populated ordinal change, this is needed for
    // consistent num shards across snapshot and delta to a version, and without this producer will fail integrity check.
    // This can happen for e.g. when ghost records got dropped, or when a large change in type size results in num shards
    // adjusting over a few cycles
    @Test
    public void testChangingNumShardsWithoutChangesInPopulatedOrdinals() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager inMemoryBlobStager = new HollowInMemoryBlobStager();
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(inMemoryBlobStager)
                .withTypeResharding(true)
                .build();

        HollowObjectSchema testObjectSchema = new HollowObjectSchema("TestObject", 1);
        testObjectSchema.addField("intField", HollowObjectSchema.FieldType.INT);
        HollowMapSchema testMapSchema = new HollowMapSchema("TestMap", "TestObject", "String", "intField");

        p.initializeDataModel(testObjectSchema, testMapSchema);
        int targetSize = 16;
        p.getWriteEngine().setTargetMaxTypeShardSize(targetSize);
        long v1 = p.runCycle(state -> {
                for (int i = 0; i <= 1; i++) {
                    HollowObjectWriteRecord rec = new HollowObjectWriteRecord(testObjectSchema);
                    rec.setInt("intField", i);
                    state.getStateEngine().add("TestObject", rec);
                }
                HollowMapWriteRecord rec = new HollowMapWriteRecord();
                rec.addEntry(1, 1);
                state.getStateEngine().add("TestMap", rec);
            });

        HollowConsumer c = HollowConsumer
                .withBlobRetriever(blobStore)
                .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
                    @Override
                    public boolean allowDoubleSnapshot() {
                        return false;
                    }
                    @Override
                    public int maxDeltasBeforeDoubleSnapshot() {
                        return Integer.MAX_VALUE;
                    }
                })
                .build();
        c.triggerRefreshTo(v1);

        assertEquals(1, c.getStateEngine().getTypeState("TestMap").numShards());
        assertEquals(1, c.getStateEngine().getTypeState("TestObject").numShards());

        long v2 = p.runCycle(state -> {
            for (int i=0; i<=1001; i++) {
                HollowObjectWriteRecord rec = new HollowObjectWriteRecord(testObjectSchema);
                rec.setInt("intField", i);
                state.getStateEngine().add("TestObject", rec);
            }
            HollowMapWriteRecord rec = new HollowMapWriteRecord();
            rec.addEntry(1, 1);
            state.getStateEngine().add("TestMap", rec);
            rec = new HollowMapWriteRecord();
            rec.addEntry(1000, 1001);
            state.getStateEngine().add("TestMap", rec);
        });
        c.triggerRefreshTo(v2);
        assertEquals(1, c.getStateEngine().getTypeState("TestMap").numShards());
        assertEquals(2, c.getStateEngine().getTypeState("TestObject").numShards());

        long v3 = p.runCycle(state -> {
            for (int i = 0; i <= 1001; i++) {
                HollowObjectWriteRecord rec = new HollowObjectWriteRecord(testObjectSchema);
                rec.setInt("intField", i);
                state.getStateEngine().add("TestObject", rec);
            }
            HollowMapWriteRecord rec = new HollowMapWriteRecord();
            rec.addEntry(1, 1);
            state.getStateEngine().add("TestMap", rec);
            rec = new HollowMapWriteRecord();
            rec.addEntry(1000, 1001);
            state.getStateEngine().add("TestMap", rec);
            rec = new HollowMapWriteRecord();
            rec.addEntry(1, 2);
            state.getStateEngine().add("TestMap", rec);
        });
        c.triggerRefreshTo(v3);
        assertEquals(v3, c.getCurrentVersionId());
    }

    // @Test Disabled until producer allows both resharding and focusHolesInFewestShards features
    public void testReshardingWithFocusHoleFillInFewestShards() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();
        HollowProducer producer = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager)
                .withTypeResharding(true).withTargetMaxTypeShardSize(64)
                .withFocusHoleFillInFewestShards(true)
                .build();
        long v1 = producer.runCycle(state -> {
            for(int i=1;i<=36;i++) {
                add(state, "val" + i, i);
            }
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
                .withSkipTypeShardUpdateWithNoAdditions()
                .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
                    @Override
                    public boolean allowDoubleSnapshot() {
                        return false;
                    }
                    @Override
                    public int maxDeltasBeforeDoubleSnapshot() {
                        return Integer.MAX_VALUE;
                    }
                })
                .build();
        consumer.triggerRefreshTo(v1);
        Assert.assertEquals(4, consumer.getStateEngine().getTypeState("TestRec").numShards());

        /// remove 4 from S0: 13, 21, 25, 29
        /// remove 5 from S1:  6, 14, 18, 26, 30
        /// remove 2 from S2: 11, 19
        /// remove 1 from S3: 24
        Set<Integer> removeSet = new HashSet<>(Arrays.asList(13, 21, 25, 29, 6, 14, 18, 26, 30, 11, 19, 24));
        long v2 = producer.runCycle(state -> {
            for(int i=1;i<=36;i++) {
                if(!removeSet.contains(i))
                    add(state, "val" + i, i);
            }

            add(state, "newval37", 37);
        });
        consumer.triggerRefreshTo(v2);
        removeSet.add(5);

        long v3 = producer.runCycle(state -> {
            for(int i=1;i<=36;i++) {
                if(!removeSet.contains(i))
                    add(state, "val" + i, i);
            }
            add(state, "newval37", 37);
            for(int i=1000;i<1005;i++) {
                add(state, "bigval"+i, i);
            }
        });
        consumer.triggerRefreshTo(v3);

        /// all changes focused in shard 1
        assertRecordOrdinal(consumer,  5, "bigval1000", 1000);
        assertRecordOrdinal(consumer, 13, "bigval1001", 1001);
        assertRecordOrdinal(consumer, 17, "bigval1002", 1002);
        assertRecordOrdinal(consumer, 25, "bigval1003", 1003);
        assertRecordOrdinal(consumer, 29, "bigval1004", 1004);

        assertRecordOrdinal(consumer, 32, "val33", 33); // shard1
        assertRecordOrdinal(consumer,  9, "val10", 10); // shard2
        assertRecordOrdinal(consumer, 14, "val15", 15);
        assertRecordOrdinal(consumer, 11, "val12", 12);

        // numShards doubled to 8
        assertEquals(8, consumer.getStateEngine().getTypeState("TestRec").numShards());
        assertEquals(v3, consumer.getCurrentVersionId());

        FailingValidationListener cycleFailingListener = new FailingValidationListener();
        producer.addListener(cycleFailingListener);

        // exercise resetToLastNumShards
        try {
            producer.runCycle(state -> {
                add(state, "newVal", 9999);
            });
            fail("Cycle expected to fail at validation");
        } catch (Exception e) {
        }
        try {
            producer.runCycle(state -> {
                add(state, "anotherNewVal", 9998);
            });
            fail("Cycle expected to fail at validation");
        } catch (Exception e) {
        }

        producer.removeListener(cycleFailingListener);
        long v4 = producer.runCycle(state -> {
            for(int i=1;i<=36;i++) {
                if(!removeSet.contains(i))
                    add(state, "val" + i, i);
            }

            add(state, "newval37", 37);

            for(int i=1000;i<1009;i++) {
                add(state, "bigval"+i, i);
            }
        });
        consumer.triggerRefreshTo(v4);

        /// all changes focused in shard 0
        assertRecordOrdinal(consumer,  4, "bigval1005", 1005);
        assertRecordOrdinal(consumer, 12, "bigval1006", 1006);
        assertRecordOrdinal(consumer, 20, "bigval1007", 1007);
        assertRecordOrdinal(consumer, 28, "bigval1008", 1008);

        assertRecordOrdinal(consumer, 32, "val33", 33); // shard1
        assertRecordOrdinal(consumer,  9, "val10", 10); // shard2
        assertRecordOrdinal(consumer, 14, "val15", 15);
        assertRecordOrdinal(consumer, 11, "val12", 12);

        assertEquals(8, consumer.getStateEngine().getTypeState("TestRec").numShards());
        assertEquals(v4, consumer.getCurrentVersionId());

        consumer.triggerRefreshTo(v1);
        assertEquals(v1, consumer.getCurrentVersionId());

        consumer.triggerRefreshTo(v4);
        assertEquals(v4, consumer.getCurrentVersionId());
        assertRecordOrdinal(consumer, 28, "bigval1008", 1008);
        assertRecordOrdinal(consumer, 32, "val33", 33); // shard1
        assertRecordOrdinal(consumer,  9, "val10", 10); // shard2
        assertRecordOrdinal(consumer, 11, "val12", 12);
    }

    @Test
    public void testHollowProducerRestoreWithBlobVerifier() {
        HollowConsumer.UpdatePlanBlobVerifier blobVerifier = new HollowConsumer.UpdatePlanBlobVerifier() {
            @Override
            public boolean announcementVerificationEnabled() {
                return true;
            }

            @Override
            public int announcementVerificationMaxLookback() {
                return 1;
            }

            @Override
            public HollowConsumer.AnnouncementWatcher announcementWatcher() {
                HollowConsumer.AnnouncementWatcher watcher = mock(HollowConsumer.AnnouncementWatcher.class);
                when(watcher.getVersionAnnouncementStatus(1001L)).thenReturn(HollowConsumer.AnnouncementStatus.NOT_ANNOUNCED);
                when(watcher.getVersionAnnouncementStatus(1000L)).thenReturn(HollowConsumer.AnnouncementStatus.ANNOUNCED);
                return null;
            }
        };
        HollowProducer.VersionMinter mockVersionMinter = mock(HollowProducer.VersionMinter.class);
        when(mockVersionMinter.mint()).thenReturn(1000L).thenReturn(1001L);
        HollowProducer producer = createProducer(tmpFolder,
                blobVerifier, mockVersionMinter, schema);
        long version1 = testPublishV1(producer, 2, 10);
        long version2 = testPublishV1(producer, 2, 10);

        HollowProducer.ReadState readState = producer.restore(version2, blobRetriever);
        assertEquals(version1, readState.getVersion());
    }

    private void add(HollowProducer.WriteState state, String sVal, int iVal) {
        TestRec rec = new TestRec(sVal, iVal);
        state.add(rec);
    }

    private static class TestRec {
        @HollowInline
        private final String strVal;
        private final int intVal;

        public TestRec(String strVal, int intVal) {
            this.strVal = strVal;
            this.intVal = intVal;
        }
    }

    private void assertRecordOrdinal(HollowConsumer consumer, int ordinal, String sVal, int iVal) {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState)consumer.getStateEngine().getTypeState("TestRec");

        Assert.assertEquals(iVal, typeState.readInt(ordinal, typeState.getSchema().getPosition("intVal")));
        Assert.assertEquals(sVal, typeState.readString(ordinal, typeState.getSchema().getPosition("strVal")));
    }

    @SuppressWarnings("unused")
    @HollowTypeName(name = "TestPojo")
    private static class TestPojoV1 {
        public int id;
        public int v1;

        TestPojoV1(int id, int v1) {
            super();
            this.id = id;
            this.v1 = v1;
        }
    }

    @SuppressWarnings("unused")
    @HollowTypeName(name = "TestPojo")
    private static class TestPojoV2 {
        int id;
        int v1;
        int v2;

        TestPojoV2(int id, int v1, int v2) {
            this.id = id;
            this.v1 = v1;
            this.v2 = v2;
        }
    }

    private class FakeProducerListener extends AbstractHollowProducerListener {
        @Override
        public void onCycleComplete(ProducerStatus status, long elapsed, TimeUnit unit) {
            lastProducerStatus = status;
        }

        @Override
        public void onProducerRestoreComplete(RestoreStatus status, long elapsed, TimeUnit unit) {
            lastRestoreStatus = status;
        }
    }

    private class FailingValidationListener extends AbstractHollowProducerListener implements VetoableListener {
        @Override
        public void onValidationStart(long version) {
            throw new RuntimeException("This listener fails validation");
        }
    }

    private class ProducerYieldsPrimaryBeforePublish extends AbstractHollowProducerListener {
        private SingleProducerEnforcer singleProducerEnforcer;

        ProducerYieldsPrimaryBeforePublish(SingleProducerEnforcer singleProducerEnforcer) {
            this.singleProducerEnforcer = singleProducerEnforcer;
        }

        @Override
        public void onPopulateStart(long version) {
            singleProducerEnforcer.disable();
        }
    }

    private class ProducerYieldsPrimaryBeforeAnnounce extends AbstractHollowProducerListener {
        private SingleProducerEnforcer singleProducerEnforcer;

        ProducerYieldsPrimaryBeforeAnnounce(SingleProducerEnforcer singleProducerEnforcer) {
            this.singleProducerEnforcer = singleProducerEnforcer;
        }

        @Override
        public void onValidationStart(long version) {
            singleProducerEnforcer.disable();
        }
    }

    private class FakeBlobPublisher implements HollowProducer.Publisher {
        private void publishBlob(Blob blob) {
            File blobFile = blob.getFile();
            if (!blob.getType().equals(Type.SNAPSHOT)) {
                // Only snapshot is needed for smoke Test
                return;
            }
            File copiedFile = copyFile(blobFile);

            blobMap.put(blob.getToVersion(), blob);
            blobFileMap.put(blob.getToVersion(), copiedFile);
            System.out.println("Published:" + copiedFile);
        }

        private File copyFile(File blobFile) {
            if (!blobFile.exists()) throw new RuntimeException("File does not exists: " + blobFile);

            // Copy file
            File copiedFile = new File(tmpFolder, "copied_" + blobFile.getName());
            try {
                Files.copy(blobFile.toPath(), copiedFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException("Failed to publish:" + copiedFile, e);
            }
            return copiedFile;
        }

        private void publishHeader(HeaderBlob headerBlob) {
            File headerBlobFile = headerBlob.getFile();
            File copiedFile = copyFile(headerBlobFile);

            headerBlobMap.put(headerBlob.getVersion(), headerBlob);
            headerFileMap.put(headerBlob.getVersion(), copiedFile);
            System.out.println("Published Header:" + copiedFile);
        }

        @Override
        public void publish(HollowProducer.PublishArtifact publishArtifact) {
            if (publishArtifact instanceof HeaderBlob) {
                publishHeader((HeaderBlob) publishArtifact);
            } else {
                publishBlob((Blob) publishArtifact);
            }
        }
    }

    @SuppressWarnings("unused")
    private class FakeBlobRetriever implements HollowConsumer.BlobRetriever {
        private String namespace;
        private String tmpDir;

        FakeBlobRetriever(String namespace, String tmpDir) {
            this.namespace = namespace;
            this.tmpDir = tmpDir;
        }

        @Override
        public HollowConsumer.Blob retrieveSnapshotBlob(long desiredVersion) {
            long blobVersion = desiredVersion;
            File blobFile = blobFileMap.get(desiredVersion);
            if (blobFile == null) {
                // find the closest one
                blobVersion = blobFileMap.keySet().stream()
                        .filter(l -> l < desiredVersion)
                        .reduce(Long.MIN_VALUE, Math::max);
                if (blobVersion == Long.MIN_VALUE) {
                    return null;
                } else {
                    blobFile = blobFileMap.get(blobVersion);
                }
            }
            final File blobFileFinal = blobFile;

            System.out.println("Restored: " + blobFile);
            return new HollowConsumer.Blob(blobVersion) {
                @Override
                public InputStream getInputStream() throws IOException {
                    return new FileInputStream(blobFileFinal);
                }
            };
        }

        @Override
        public HollowConsumer.Blob retrieveDeltaBlob(long currentVersion) {
            // no delta available
            return null;
        }

        @Override
        public HollowConsumer.Blob retrieveReverseDeltaBlob(long currentVersion) {
            throw new UnsupportedOperationException();
        }

        @Override
        public HollowConsumer.HeaderBlob retrieveHeaderBlob(long currentVersion) {
            final File blobFile = headerFileMap.get(currentVersion);
            System.out.println("Restored: " + blobFile);
            return new HollowConsumer.HeaderBlob(currentVersion) {
                @Override
                public InputStream getInputStream() throws IOException {
                    return new FileInputStream(blobFile);
                }
            };
        }
    }
}
