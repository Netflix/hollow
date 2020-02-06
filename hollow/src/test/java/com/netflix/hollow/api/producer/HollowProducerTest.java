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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.delegate.HollowObjectGenericDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.producer.HollowProducer.Blob;
import com.netflix.hollow.api.producer.HollowProducer.Blob.Type;
import com.netflix.hollow.api.producer.HollowProducer.ReadState;
import com.netflix.hollow.api.producer.HollowProducerListener.ProducerStatus;
import com.netflix.hollow.api.producer.HollowProducerListener.RestoreStatus;
import com.netflix.hollow.api.producer.HollowProducerListener.Status;
import com.netflix.hollow.api.producer.enforcer.BasicSingleProducerEnforcer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemAnnouncer;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        HollowProducer producer = HollowProducer.withPublisher(new FakeBlobPublisher())
            .withAnnouncer(new HollowFilesystemAnnouncer(tmpFolder.toPath())).build();
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
        HollowProducer producer = createProducer(tmpFolder);
        long v1 = producer.runCycle(ws -> {
            ws.add(1);
        });

        // Run cycle with no changes
        long v2 = producer.runCycle(ws -> {
            ws.add(1);
        });

        long v3 = producer.runCycle(ws -> {
            ws.add(2);
        });

        Assert.assertEquals(v1, v2);
        Assert.assertTrue(v3 > v2);
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

        // Run cycle as the primary producer
        enforcer.enable();
        long v3 = producer.runCycle(ws -> {
            ws.add(2);
        });

        Assert.assertEquals(v1, v2);
        Assert.assertTrue(v3 > v2);
    }

    @Test
    public void testRestoreFailure() {
        HollowProducer producer = createProducer(tmpFolder, schema);
        long fakeVersion = 101;

        try {
            producer.restore(fakeVersion, blobRetriever);
            Assert.fail();
        } catch(Exception expected) { }

        Assert.assertNotNull(lastRestoreStatus);
        Assert.assertEquals(Status.FAIL, lastRestoreStatus.getStatus());
    }

    @Test
    public void testPublishAndRestore() {
        HollowProducer producer = createProducer(tmpFolder, schema);
        long version = testPublishV1(producer, 2, 10);

        producer.restore(version, blobRetriever);
        Assert.assertNotNull(lastRestoreStatus);
        Assert.assertEquals(Status.SUCCESS, lastRestoreStatus.getStatus());
        Assert.assertEquals("Version should be the same", version, lastRestoreStatus.getDesiredVersion());
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
    }

    @Test
    public void testRestoreToNonExact() {
        HollowProducer producer = createProducer(tmpFolder, schema);
        long version = testPublishV1(producer, 2, 7);

        producer = createProducer(tmpFolder, schema);
        producer.restore(version + 1, blobRetriever);
        Assert.assertNotNull(lastRestoreStatus);
        Assert.assertEquals(Status.SUCCESS, lastRestoreStatus.getStatus());
        Assert.assertEquals("Should have reached correct version", version,
                lastRestoreStatus.getVersionReached());
        Assert.assertEquals("Should have correct desired version", version + 1,
                lastRestoreStatus.getDesiredVersion());
    }

    @Test
    public void testRollsBackStateEngineOnPublishFailure() throws Exception {
        HollowProducer producer = spy(createProducer(tmpFolder, schema));
        Assert.assertEquals("Should have no populated ordinals", 0,
                producer.getWriteEngine().getTypeState("TestPojo").getPopulatedBitSet().cardinality());
        doThrow(new RuntimeException("Publish failed")).when(producer).publish(
                any(ProducerListenerSupport.ProducerListeners.class), any(Long.class), any(AbstractHollowProducer.Artifacts.class));
        try {
            producer.runCycle(newState -> newState.add(new TestPojoV1(1, 1)));
        } catch (RuntimeException e) { // expected
        }
        Assert.assertEquals("Should still have no populated ordinals", 0,
                producer.getWriteEngine().getTypeState("TestPojo").getPopulatedBitSet().cardinality());
    }

    private long testPublishV1(HollowProducer producer, final int size, final int valueMultiplier) {
        producer.runCycle(newState -> {
            for (int i = 1; i <= size; i++) {
                newState.add(new TestPojoV1(i, i * valueMultiplier));
            }
        });
        Assert.assertNotNull(lastProducerStatus);
        Assert.assertEquals(Status.SUCCESS, lastProducerStatus.getStatus());
        return lastProducerStatus.getVersion();
    }

    private long testPublishV2(HollowProducer producer, final int size, final int valueMultiplier) {
        producer.runCycle(newState -> {
            for (int i = 1; i <= size; i++) {
                newState.add(new TestPojoV2(i, i * valueMultiplier, i * valueMultiplier));
            }
        });
        Assert.assertNotNull(lastProducerStatus);
        Assert.assertEquals(Status.SUCCESS, lastProducerStatus.getStatus());
        return lastProducerStatus.getVersion();
    }

    private void restoreAndAssert(HollowProducer producer, long version, int size, int valueMultiplier) throws Exception {
        restoreAndAssert(producer, version, size, valueMultiplier, 1);
    }

    private void restoreAndAssert(HollowProducer producer, long version, int size, int valueMultiplier, int valueFieldCount) {
        ReadState readState = producer.restore(version, blobRetriever);
        Assert.assertNotNull(lastRestoreStatus);
        Assert.assertEquals(Status.SUCCESS, lastRestoreStatus.getStatus());
        Assert.assertEquals("Version should be the same", version, lastRestoreStatus.getDesiredVersion());

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readState.getStateEngine().getTypeState("TestPojo");
        BitSet populatedOrdinals = typeState.getPopulatedOrdinals();
        Assert.assertEquals(size, populatedOrdinals.cardinality());

        int ordinal = populatedOrdinals.nextSetBit(0);
        while (ordinal != -1) {
            GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(typeState), ordinal);
            System.out.println("ordinal=" + ordinal + obj);

            int id = obj.getInt("id");
            for (int i = 0; i < valueFieldCount; i++) {
                String valueFN = "v" + (i + 1);
                int value = id * valueMultiplier;
                Assert.assertEquals(valueFN, value, obj.getInt(valueFN));
            }

            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }
        System.out.println("Asserted Correctness of version:" + version + "\n\n");
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

    private class FakeBlobPublisher implements HollowProducer.Publisher {
        @Override
        public void publish(Blob blob) {
            File blobFile = blob.getFile();
            if (!blobFile.exists()) throw new RuntimeException("File does not existis: " + blobFile);

            if (!blob.getType().equals(Type.SNAPSHOT)) {
                return; // Only snapshot is needed for smoke Test
            }

            // Copy file
            File copiedFile = new File(tmpFolder, "copied_" + blobFile.getName());
            try {
                Files.copy(blobFile.toPath(), copiedFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException("Failed to publish:" + copiedFile, e);
            }

            blobMap.put(blob.getToVersion(), blob);
            blobFileMap.put(blob.getToVersion(), copiedFile);
            System.out.println("Published:" + copiedFile);
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
    }
}
