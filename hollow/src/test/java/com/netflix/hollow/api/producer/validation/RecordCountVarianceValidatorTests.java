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
package com.netflix.hollow.api.producer.validation;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.InMemoryBlobStore;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.HollowProducer.Populator;
import com.netflix.hollow.api.producer.HollowProducer.WriteState;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RecordCountVarianceValidatorTests {
    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    @Test
    public void failTestTooManyAdded() {
        try {
            HollowProducer producer = HollowProducer.withPublisher(blobStore)
                    .withBlobStager(new HollowInMemoryBlobStager())
                    .withListener(new RecordCountVarianceValidator("TypeWithPrimaryKey", 1f)).build();

            producer.runCycle(new Populator() {
                public void populate(WriteState newState) throws Exception {
                    newState.add(new TypeWithPrimaryKey(1, "Brad Pitt", "klsdjfla;sdjkf"));
                    newState.add(new TypeWithPrimaryKey(1, "Angelina Jolie", "as;dlkfjasd;l"));
                }
            });

            producer.runCycle(new Populator() {
                public void populate(WriteState newState) throws Exception {
                    newState.add(new TypeWithPrimaryKey(1, "Brad Pitt", "klsdjfla;sdjkf"));
                    newState.add(new TypeWithPrimaryKey(2, "Angelina Jolie", "as;dlkfjasd;l"));
                    newState.add(new TypeWithPrimaryKey(3, "Angelina Jolie1", "as;dlkfjasd;l"));
                    newState.add(new TypeWithPrimaryKey(4, "Angelina Jolie2", "as;dlkfjasd;l"));
                    newState.add(new TypeWithPrimaryKey(5, "Angelina Jolie3", "as;dlkfjasd;l"));
                    newState.add(new TypeWithPrimaryKey(6, "Angelina Jolie4", "as;dlkfjasd;l"));
                    newState.add(new TypeWithPrimaryKey(7, "Angelina Jolie5", "as;dlkfjasd;l"));
                }
            });
            Assert.fail();
        } catch (ValidationStatusException expected) {
            Assert.assertEquals(1, expected.getValidationStatus().getResults().size());
            //System.out.println("Message:"+expected.getIndividualFailures().get(0).getMessage());
            Assert.assertTrue(expected.getValidationStatus().getResults().get(0).getMessage()
                    .startsWith("Record count validation for type"));
        }
    }

    @Test
    public void failTestTooManyRemoved() {
        try {
            HollowProducer producer = HollowProducer.withPublisher(blobStore)
                    .withBlobStager(new HollowInMemoryBlobStager())
                    .withListener(new RecordCountVarianceValidator("TypeWithPrimaryKey", 1f)).build();

            producer.runCycle(new Populator() {
                public void populate(WriteState newState) throws Exception {
                    newState.add(new TypeWithPrimaryKey(1, "Brad Pitt", "klsdjfla;sdjkf"));
                    newState.add(new TypeWithPrimaryKey(1, "Angelina Jolie", "as;dlkfjasd;l"));
                }
            });

            producer.runCycle(new Populator() {
                public void populate(WriteState newState) throws Exception {
                    newState.add(new TypeWithPrimaryKey(1, "Brad Pitt", "klsdjfla;sdjkf"));
                    newState.add(new TypeWithPrimaryKey(1, "Angelina Jolie", "as;dlkfjasd;l"));
                    newState.add(new TypeWithPrimaryKey(1, "Bruce Willis", "as;dlkfjasd;l"));
                }
            });
            Assert.fail();
        } catch (ValidationStatusException expected) {
            //System.out.println("Message:"+expected.getIndividualFailures().get(0).getMessage());
            Assert.assertEquals(1, expected.getValidationStatus().getResults().size());
            Assert.assertTrue(expected.getValidationStatus().getResults().get(0).getMessage()
                    .startsWith("Record count validation for type"));
        }
    }

    @Test
    public void passTestNoMoreChangeThanExpected() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore).withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new RecordCountVarianceValidator("TypeWithPrimaryKey", 50f)).build();

        // runCycle(producer, 1);
        producer.runCycle(new Populator() {

            public void populate(WriteState newState) throws Exception {
                newState.add(new TypeWithPrimaryKey(1, "Brad Pitt", "klsdjfla;sdjkf"));
                newState.add(new TypeWithPrimaryKey(1, "Angelina Jolie", "as;dlkfjasd;l"));
            }
        });

        producer.runCycle(new Populator() {

            public void populate(WriteState newState) throws Exception {
                newState.add(new TypeWithPrimaryKey(1, "Brad Pitt", "klsdjfla;sdjkf"));
                newState.add(new TypeWithPrimaryKey(2, "Angelina Jolie", "as;dlkfjasd;l"));
                newState.add(new TypeWithPrimaryKey(7, "Bruce Willis", "as;dlkfjasd;l"));
            }
        });
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefresh();
        Assert.assertEquals(3, consumer.getStateEngine().getTypeState("TypeWithPrimaryKey").getPopulatedOrdinals()
                .cardinality());
    }

    @Test
    public void testGetChangePercent() {
        RecordCountVarianceValidator val = new RecordCountVarianceValidator("someType", 3.0f);
        Assert.assertTrue((Float.compare(99.99999652463547f, val.getChangePercent(0, 28382664)) == 0));
        Assert.assertTrue((Float.compare(99.646645f, val.getChangePercent(1, 283)) == 0));

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
}
