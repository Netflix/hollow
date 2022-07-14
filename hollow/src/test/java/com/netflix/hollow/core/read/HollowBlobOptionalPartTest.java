/*
 *  Copyright 2021 Netflix, Inc.
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
package com.netflix.hollow.core.read;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.DoubleSnapshotConfig;
import com.netflix.hollow.api.consumer.InMemoryBlobStore;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.ProducerOptionalBlobPartConfig;
import com.netflix.hollow.api.producer.fs.HollowFilesystemPublisher;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.TypeFilter;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.TypeA;
import com.netflix.hollow.core.write.objectmapper.TypeB;
import com.netflix.hollow.core.write.objectmapper.TypeC;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;

public class HollowBlobOptionalPartTest {

    @Test
    public void optionalPartsAreAvailableInLowLevelAPI() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.initializeTypeState(TypeA.class);

        mapper.add(new TypeA("1", 1, new TypeB((short) 1, 1L, 1f, new char[]{'1'}, new byte[]{1}), Collections.singleton(new TypeC('1', null))));
        mapper.add(new TypeA("2", 2, new TypeB((short) 2, 2L, 2f, new char[]{'2'}, new byte[]{2}), Collections.singleton(new TypeC('2', null))));
        mapper.add(new TypeA("3", 3, new TypeB((short) 3, 3L, 3f, new char[]{'3'}, new byte[]{3}), Collections.singleton(new TypeC('3', null))));

        ByteArrayOutputStream mainPart = new ByteArrayOutputStream();
        ByteArrayOutputStream bPart = new ByteArrayOutputStream();
        ByteArrayOutputStream cPart = new ByteArrayOutputStream();

        ProducerOptionalBlobPartConfig partConfig = newPartConfig();

        ProducerOptionalBlobPartConfig.OptionalBlobPartOutputStreams partStreams = partConfig.newStreams();
        partStreams.addOutputStream("B", bPart);
        partStreams.addOutputStream("C", cPart);

        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        writer.writeSnapshot(mainPart, partStreams);

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);

        HollowBlobInput mainPartInput = HollowBlobInput.serial(new ByteArrayInputStream(mainPart.toByteArray()));
        InputStream bPartInput = new ByteArrayInputStream(bPart.toByteArray());
        // HollowBlobInput cPartInput = HollowBlobInput.serial(new ByteArrayInputStream(cPart.toByteArray()));

        OptionalBlobPartInput optionalPartInput = new OptionalBlobPartInput();
        optionalPartInput.addInput("B", bPartInput);

        reader.readSnapshot(mainPartInput, optionalPartInput, TypeFilter.newTypeFilter().build());

        GenericHollowObject obj = new GenericHollowObject(readEngine, "TypeA", 1);

        Assert.assertEquals("2", obj.getObject("a1").getString("value"));
        Assert.assertEquals(2, obj.getInt("a2"));
        Assert.assertEquals(2L, obj.getObject("b").getLong("b2"));

        Assert.assertNull(readEngine.getTypeState("TypeC"));
    }

    @Test
    public void optionalPartsAreAvailableInHighLevelAPI() throws IOException {
        InMemoryBlobStore blobStore = new InMemoryBlobStore(Collections.singleton("B"));
        HollowInMemoryBlobStager stager = new HollowInMemoryBlobStager(newPartConfig());

        HollowProducer producer = HollowProducer
                .withPublisher(blobStore)
                .withNumStatesBetweenSnapshots(2)
                .withBlobStager(stager)
                .build();

        producer.initializeDataModel(TypeA.class);

        producer.runCycle(state -> {
            state.add(new TypeA("1", 1, new TypeB((short) 1, 1L, 1f, new char[]{'1'}, new byte[]{1}), Collections.singleton(new TypeC('1', null))));
            state.add(new TypeA("2", 2, new TypeB((short) 2, 2L, 2f, new char[]{'2'}, new byte[]{2}), Collections.singleton(new TypeC('2', null))));
            state.add(new TypeA("3", 3, new TypeB((short) 3, 3L, 3f, new char[]{'3'}, new byte[]{3}), Collections.singleton(new TypeC('3', null))));
        });

        producer.runCycle(state -> {
            state.add(new TypeA("1", 1, new TypeB((short) 1, 1L, 1f, new char[]{'1'}, new byte[]{1}), Collections.singleton(new TypeC('1', null))));
            // state.add(new TypeA("2", 2, new TypeB((short)2, 2L, 2f, new char[] {'2'}, new byte[] { 2 }), Collections.singleton(new TypeC('2', null))));
            state.add(new TypeA("3", 3, new TypeB((short) 3, 3L, 3f, new char[]{'3'}, new byte[]{3}), Collections.singleton(new TypeC('3', null))));
            state.add(new TypeA("4", 4, new TypeB((short) 4, 4L, 4f, new char[]{'4'}, new byte[]{4}), Collections.singleton(new TypeC('4', null))));
        });

        HollowConsumer consumer = HollowConsumer.newHollowConsumer()
                .withBlobRetriever(blobStore)
                .build();

        consumer.triggerRefresh();

        GenericHollowObject obj = new GenericHollowObject(consumer.getStateEngine(), "TypeA", 3);

        Assert.assertEquals("4", obj.getObject("a1").getString("value"));
        Assert.assertEquals(4, obj.getInt("a2"));
        Assert.assertEquals(4L, obj.getObject("b").getLong("b2"));

        Assert.assertFalse(consumer.getStateEngine().getTypeState("TypeB").getPopulatedOrdinals().get(1));
        Assert.assertNull(consumer.getStateEngine().getTypeState("TypeC"));
    }

    @Test
    public void refusesToApplyIncorrectPartSnapshot() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.initializeTypeState(TypeA.class);

        mapper.add(new TypeA("1", 1, new TypeB((short) 1, 1L, 1f, new char[]{'1'}, new byte[]{1}), Collections.singleton(new TypeC('1', null))));
        mapper.add(new TypeA("2", 2, new TypeB((short) 2, 2L, 2f, new char[]{'2'}, new byte[]{2}), Collections.singleton(new TypeC('2', null))));
        mapper.add(new TypeA("3", 3, new TypeB((short) 3, 3L, 3f, new char[]{'3'}, new byte[]{3}), Collections.singleton(new TypeC('3', null))));

        ByteArrayOutputStream mainPart = new ByteArrayOutputStream();
        ByteArrayOutputStream bPart = new ByteArrayOutputStream();
        ByteArrayOutputStream cPart = new ByteArrayOutputStream();

        ProducerOptionalBlobPartConfig partConfig = newPartConfig();

        ProducerOptionalBlobPartConfig.OptionalBlobPartOutputStreams partStreams = partConfig.newStreams();
        partStreams.addOutputStream("B", bPart);
        partStreams.addOutputStream("C", cPart);

        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        writer.writeSnapshot(mainPart, partStreams);

        writeEngine.prepareForNextCycle();

        mapper.add(new TypeA("1", 1, new TypeB((short) 1, 1L, 1f, new char[]{'1'}, new byte[]{1}), Collections.singleton(new TypeC('1', null))));
        mapper.add(new TypeA("3", 3, new TypeB((short) 3, 3L, 3f, new char[]{'3'}, new byte[]{3}), Collections.singleton(new TypeC('3', null))));
        mapper.add(new TypeA("4", 4, new TypeB((short) 2, 2L, 2f, new char[]{'2'}, new byte[]{2}), Collections.singleton(new TypeC('2', null))));

        ByteArrayOutputStream mainPart2 = new ByteArrayOutputStream();
        ByteArrayOutputStream bPart2 = new ByteArrayOutputStream();
        ByteArrayOutputStream cPart2 = new ByteArrayOutputStream();

        partStreams = partConfig.newStreams();
        partStreams.addOutputStream("B", bPart2);
        partStreams.addOutputStream("C", cPart2);

        writer = new HollowBlobWriter(writeEngine);
        writer.writeSnapshot(mainPart2, partStreams);

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);

        HollowBlobInput mainPartInput = HollowBlobInput.serial(new ByteArrayInputStream(mainPart.toByteArray()));
        InputStream bPartInput = new ByteArrayInputStream(bPart.toByteArray());
        InputStream cPartInput = new ByteArrayInputStream(cPart2.toByteArray()); /// wrong part state

        OptionalBlobPartInput optionalPartInput = new OptionalBlobPartInput();
        optionalPartInput.addInput("B", bPartInput);
        optionalPartInput.addInput("C", cPartInput);

        try {
            reader.readSnapshot(mainPartInput, optionalPartInput, TypeFilter.newTypeFilter().build());
            Assert.fail("Should have thrown Exception");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("Optional blob part C does not appear to be matched with the main input", ex.getMessage());
        }
    }

    @Test
    public void testFilesystemBlobRetriever() throws IOException {
        File localBlobStore = createLocalDir();
        HollowFilesystemPublisher publisher = new HollowFilesystemPublisher(localBlobStore.toPath());
        HollowInMemoryBlobStager stager = new HollowInMemoryBlobStager(newPartConfig());

        HollowProducer producer = HollowProducer.withPublisher(publisher).withBlobStager(stager).build();
        producer.initializeDataModel(TypeA.class);

        long v1 = producer.runCycle(state -> {
            state.add(new TypeA("1", 1, new TypeB((short) 1, 1L, 1f, new char[]{'1'}, new byte[]{1}), Collections.singleton(new TypeC('1', null))));
            state.add(new TypeA("2", 2, new TypeB((short) 2, 2L, 2f, new char[]{'2'}, new byte[]{2}), Collections.singleton(new TypeC('2', null))));
            state.add(new TypeA("3", 3, new TypeB((short) 3, 3L, 3f, new char[]{'3'}, new byte[]{3}), Collections.singleton(new TypeC('3', null))));
        });

        long v2 = producer.runCycle(state -> {
            state.add(new TypeA("1", 1, new TypeB((short) 1, 1L, 1f, new char[]{'1'}, new byte[]{1}), Collections.singleton(new TypeC('1', null))));
            state.add(new TypeA("3", 3, new TypeB((short) 3, 3L, 3f, new char[]{'3'}, new byte[]{3}), Collections.singleton(new TypeC('3', null))));
            state.add(new TypeA("4", 4, new TypeB((short) 2, 2L, 2f, new char[]{'2'}, new byte[]{2}), Collections.singleton(new TypeC('2', null))));
        });

        HollowFilesystemBlobRetriever blobRetriever = new HollowFilesystemBlobRetriever(localBlobStore.toPath(), new HashSet<>(Arrays.asList("B", "C")));

        HollowConsumer consumer = HollowConsumer.newHollowConsumer()
                .withBlobRetriever(blobRetriever)
                .withDoubleSnapshotConfig(new DoubleSnapshotConfig() {
                    @Override
                    public int maxDeltasBeforeDoubleSnapshot() {
                        return Integer.MAX_VALUE;
                    }
                    @Override
                    public boolean allowDoubleSnapshot() {
                        return false;
                    }
                }).build();

        consumer.triggerRefreshTo(v1);
        consumer.triggerRefreshTo(v2);
        consumer.triggerRefreshTo(v1);
    }

    @Test
    public void refusesToApplyIncorrectPartDelta() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.initializeTypeState(TypeA.class);

        mapper.add(new TypeA("1", 1, new TypeB((short) 1, 1L, 1f, new char[]{'1'}, new byte[]{1}), Collections.singleton(new TypeC('1', null))));
        mapper.add(new TypeA("2", 2, new TypeB((short) 2, 2L, 2f, new char[]{'2'}, new byte[]{2}), Collections.singleton(new TypeC('2', null))));
        mapper.add(new TypeA("3", 3, new TypeB((short) 3, 3L, 3f, new char[]{'3'}, new byte[]{3}), Collections.singleton(new TypeC('3', null))));

        ByteArrayOutputStream mainPart = new ByteArrayOutputStream();
        ByteArrayOutputStream bPart = new ByteArrayOutputStream();
        ByteArrayOutputStream cPart = new ByteArrayOutputStream();

        ProducerOptionalBlobPartConfig partConfig = newPartConfig();

        ProducerOptionalBlobPartConfig.OptionalBlobPartOutputStreams partStreams = partConfig.newStreams();
        partStreams.addOutputStream("B", bPart);
        partStreams.addOutputStream("C", cPart);

        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        writer.writeSnapshot(mainPart, partStreams);

        writeEngine.prepareForNextCycle();

        mapper.add(new TypeA("1", 1, new TypeB((short) 1, 1L, 1f, new char[]{'1'}, new byte[]{1}), Collections.singleton(new TypeC('1', null))));
        mapper.add(new TypeA("3", 3, new TypeB((short) 3, 3L, 3f, new char[]{'3'}, new byte[]{3}), Collections.singleton(new TypeC('3', null))));
        mapper.add(new TypeA("4", 4, new TypeB((short) 2, 2L, 2f, new char[]{'2'}, new byte[]{2}), Collections.singleton(new TypeC('2', null))));

        ByteArrayOutputStream mainPart2 = new ByteArrayOutputStream();
        ByteArrayOutputStream bPart2 = new ByteArrayOutputStream();
        ByteArrayOutputStream cPart2 = new ByteArrayOutputStream();

        partStreams = partConfig.newStreams();
        partStreams.addOutputStream("B", bPart2);
        partStreams.addOutputStream("C", cPart2);

        writer = new HollowBlobWriter(writeEngine);
        writer.writeDelta(mainPart2, partStreams);

        writeEngine.prepareForNextCycle();

        mapper.add(new TypeA("3", 3, new TypeB((short) 3, 3L, 3f, new char[]{'3'}, new byte[]{3}), Collections.singleton(new TypeC('3', null))));
        mapper.add(new TypeA("4", 4, new TypeB((short) 2, 2L, 2f, new char[]{'2'}, new byte[]{2}), Collections.singleton(new TypeC('2', null))));
        mapper.add(new TypeA("5", 5, new TypeB((short) 5, 5L, 5f, new char[]{'1'}, new byte[]{1}), Collections.singleton(new TypeC('5', null))));

        ByteArrayOutputStream mainPart3 = new ByteArrayOutputStream();
        ByteArrayOutputStream bPart3 = new ByteArrayOutputStream();
        ByteArrayOutputStream cPart3 = new ByteArrayOutputStream();

        partStreams = partConfig.newStreams();
        partStreams.addOutputStream("B", bPart3);
        partStreams.addOutputStream("C", cPart3);

        writer = new HollowBlobWriter(writeEngine);
        writer.writeDelta(mainPart3, partStreams);

        /// read snapshot
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);

        HollowBlobInput mainPartInput = HollowBlobInput.serial(new ByteArrayInputStream(mainPart.toByteArray()));
        InputStream bPartInput = new ByteArrayInputStream(bPart.toByteArray());
        InputStream cPartInput = new ByteArrayInputStream(cPart.toByteArray());

        OptionalBlobPartInput optionalPartInput = new OptionalBlobPartInput();
        optionalPartInput.addInput("B", bPartInput);
        optionalPartInput.addInput("C", cPartInput);

        reader.readSnapshot(mainPartInput, optionalPartInput, TypeFilter.newTypeFilter().build());

        /// apply delta
        mainPartInput = HollowBlobInput.serial(new ByteArrayInputStream(mainPart2.toByteArray()));
        bPartInput = new ByteArrayInputStream(bPart3.toByteArray()); /// wrong part state
        cPartInput = new ByteArrayInputStream(cPart2.toByteArray());

        optionalPartInput = new OptionalBlobPartInput();
        optionalPartInput.addInput("B", bPartInput);
        optionalPartInput.addInput("C", cPartInput);

        try {
            reader.applyDelta(mainPartInput, optionalPartInput);
            Assert.fail("Should have thrown Exception");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("Optional blob part B does not appear to be matched with the main input", ex.getMessage());
        }
    }

    @Test
    public void optionalPartsWithSharedMemoryLazy() throws IOException {
        File localBlobStore = createLocalDir();
        HollowFilesystemPublisher publisher = new HollowFilesystemPublisher(localBlobStore.toPath());
        HollowInMemoryBlobStager stager = new HollowInMemoryBlobStager(newPartConfig());

        HollowProducer producer = HollowProducer
                .withPublisher(publisher)
                .withNumStatesBetweenSnapshots(2)
                .withBlobStager(stager)
                .build();

        producer.initializeDataModel(TypeA.class);

        producer.runCycle(state -> {
            state.add(new TypeA("1", 1, new TypeB((short) 1, 1L, 1f, new char[]{'1'}, new byte[]{1}), Collections.singleton(new TypeC('1', null))));
            state.add(new TypeA("2", 2, new TypeB((short) 2, 2L, 2f, new char[]{'2'}, new byte[]{2}), Collections.singleton(new TypeC('2', null))));
            state.add(new TypeA("3", 3, new TypeB((short) 3, 3L, 3f, new char[]{'3'}, new byte[]{3}), Collections.singleton(new TypeC('3', null))));
        });

        HollowConsumer consumer = HollowConsumer.newHollowConsumer()
                .withBlobRetriever(new HollowFilesystemBlobRetriever(localBlobStore.toPath(), Collections.singleton("B")))
                .withMemoryMode(MemoryMode.SHARED_MEMORY_LAZY)
                .build();

        consumer.triggerRefresh();

        GenericHollowObject obj = new GenericHollowObject(consumer.getStateEngine(), "TypeA", 1);

        Assert.assertEquals("2", obj.getObject("a1").getString("value"));
        Assert.assertEquals(2, obj.getInt("a2"));
        Assert.assertEquals(2L, obj.getObject("b").getLong("b2"));

        Assert.assertNull(consumer.getStateEngine().getTypeState("TypeC"));
    }


    private ProducerOptionalBlobPartConfig newPartConfig() {
        ProducerOptionalBlobPartConfig partConfig = new ProducerOptionalBlobPartConfig();
        partConfig.addTypesToPart("B", "TypeB");
        partConfig.addTypesToPart("C", "SetOfTypeC", "TypeC", "MapOfStringToListOfInteger", "ListOfInteger", "Integer");
        return partConfig;
    }

    static File createLocalDir() throws IOException {
        File localDir = Files.createTempDirectory("hollow").toFile();
        localDir.deleteOnExit();
        return localDir;
    }

}
