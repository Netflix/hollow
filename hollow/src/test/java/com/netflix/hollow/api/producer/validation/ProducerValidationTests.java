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
package com.netflix.hollow.api.producer.validation;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.InMemoryBlobStore;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProducerValidationTests {
    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    @Test
    public void duplicateDetectionFailureTest() {
        duplicateDetectionFailureTest(new DuplicateDataDetectionValidator("TypeWithPrimaryKey"));
        duplicateDetectionFailureTest(
                new DuplicateDataDetectionValidator("TypeWithPrimaryKey", new String[] {"id", "name"}));

        duplicateDetectionFailureTest(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class));
        duplicateDetectionFailureTest(new DuplicateDataDetectionValidator(TypeWithPrimaryKey2.class));

        duplicateDetectionFailureTest(null, true);
        duplicateDetectionFailureTest(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class), true);
    }

    void duplicateDetectionFailureTest(DuplicateDataDetectionValidator v) {
        duplicateDetectionFailureTest(v, false);
    }

    void duplicateDetectionFailureTest(DuplicateDataDetectionValidator v, boolean auto) {
        HollowProducer.Builder<?> b = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager());
        if (v != null) {
            b.withListener(v);
        }
        HollowProducer producer = b.build();

        if (auto) {
            producer.initializeDataModel(TypeWithPrimaryKey.class);
            DuplicateDataDetectionValidator.addValidatorsForSchemaWithPrimaryKey(producer);
        }

        try {
            //runCycle(producer, 1);
            producer.runCycle(newState -> {
                newState.add(new TypeWithPrimaryKey(1, "Brad Pitt", "klsdjfla;sdjkf"));
                newState.add(new TypeWithPrimaryKey(1, "Angelina Jolie", "as;dlkfjasd;l"));
                newState.add(new TypeWithPrimaryKey(1, "Brad Pitt", "as;dlkfjasd;l"));
            });
            Assert.fail();
        } catch (ValidationStatusException expected) {
            Assert.assertEquals(1, expected.getValidationStatus().getResults().size());
            Assert.assertTrue(expected.getValidationStatus().getResults().get(0).getMessage()
                    .startsWith("Duplicate keys found for type TypeWithPrimaryKey"));
        }
    }

    @Test
    public void duplicateDetectionSuccessTest() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator("TypeWithPrimaryKey"))
                .build();

        //runCycle(producer, 1);
        producer.runCycle(newState -> {
            newState.add(new TypeWithPrimaryKey(1, "Brad Pitt", "klsdjfla;sdjkf"));
            newState.add(new TypeWithPrimaryKey(1, "Angelina Jolie", "as;dlkfjasd;l"));
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefresh();
        Assert.assertEquals(2, consumer.getStateEngine().getTypeState("TypeWithPrimaryKey").getPopulatedOrdinals()
                .cardinality());
    }

    @Test
    public void duplicateDetectionWithRestoreAndNewType() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator("TypeWithPrimaryKey"))
                .build();

        // run an initial cycle to get data in, without Type3WithPrimaryKey
        long initialVersion =
                producer.runCycle(newState -> newState.add(new TypeWithPrimaryKey(1, "Foo", "bar")));

        producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator("TypeWithPrimaryKey"))
                .withListener(new DuplicateDataDetectionValidator("TypeWithPrimaryKey3"))
                .build();
        producer.initializeDataModel(TypeWithPrimaryKey.class, TypeWithPrimaryKey3.class);
        producer.restore(initialVersion, blobStore);

        producer.runCycle(newState -> newState.add(new TypeWithPrimaryKey3(1, "Bar")));

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefresh();
    }

    @Test
    public void duplicateDetectionNewType() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator("TypeWithPrimaryKey"))
                .build();
        producer.runCycle(newState -> newState.add(new TypeWithPrimaryKey(1, "Bar", "x")));
        producer.addListener(new DuplicateDataDetectionValidator("TypeWithPrimaryKey3"));
        // this adds the type state for TypeWithPrimarkyKey3
        producer.runCycle(newState -> newState.add(new TypeWithPrimaryKey3(1, "Bar")));
    }

    @HollowPrimaryKey(fields = {"id", "name"})
    static class TypeWithPrimaryKey {
        int id;
        String name;
        String desc;

        TypeWithPrimaryKey(int id, String name, String desc) {
            this.id = id;
            this.name = name;
            this.desc = desc;
        }
    }

    @HollowPrimaryKey(fields = {"id", "name"})
    @HollowTypeName(name = "TypeWithPrimaryKey")
    static class TypeWithPrimaryKey2 {
        int id;
        String name;
        String desc;

        TypeWithPrimaryKey2(int id, String name, String desc) {
            this.id = id;
            this.name = name;
            this.desc = desc;
        }
    }

    @HollowPrimaryKey(fields = {"id"})
    static class TypeWithPrimaryKey3 {
        int id;
        String name;

        TypeWithPrimaryKey3(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
