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
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.test.InMemoryBlobStore;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Verifies {@link HollowProducer.Builder#withSkipValidation} bypasses the entire validation stage for a
 * single cycle, covering every registered validator regardless of how it was added.
 */
public class SkipValidationTest {
    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    @Test
    public void skipTrue_bypassesFailingValidator() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator("TypeWithPrimaryKey"))
                .withSkipValidation(() -> true)
                .build();

        // Two records share primary key id=1; DuplicateDataDetectionValidator would normally fail this
        // cycle, but validation is skipped so the cycle publishes successfully.
        producer.runCycle(newState -> {
            newState.add(new TypeWithPrimaryKey(1, "a"));
            newState.add(new TypeWithPrimaryKey(1, "b"));
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefresh();
        Assert.assertEquals(2, consumer.getStateEngine().getTypeState("TypeWithPrimaryKey")
                .getPopulatedOrdinals().cardinality());
    }

    @Test
    public void skipFalse_stillValidates() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator("TypeWithPrimaryKey"))
                .withSkipValidation(() -> false)
                .build();

        try {
            producer.runCycle(newState -> {
                newState.add(new TypeWithPrimaryKey(1, "a"));
                newState.add(new TypeWithPrimaryKey(1, "b"));
            });
            Assert.fail("expected validation to fail when skip supplier returns false");
        } catch (ValidationStatusException expected) {
            // validators ran, as expected
        }
    }

    @Test
    public void defaultSupplier_alwaysValidates() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator("TypeWithPrimaryKey"))
                .build();

        try {
            producer.runCycle(newState -> {
                newState.add(new TypeWithPrimaryKey(1, "a"));
                newState.add(new TypeWithPrimaryKey(1, "b"));
            });
            Assert.fail("expected validation to fail when no skip supplier is configured");
        } catch (ValidationStatusException expected) {
            // validators ran, as expected
        }
    }

    @Test
    public void skipTogglesPerCycle_andCoversExternallyAddedValidator() {
        AtomicBoolean skip = new AtomicBoolean(true);
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withSkipValidation(skip::get)
                .build();

        // Validator added AFTER build (not via the builder) - the skip must still cover it.
        producer.addListener(new DuplicateDataDetectionValidator("TypeWithPrimaryKey"));

        // Cycle 1: skip=true, duplicate id=1 is allowed through.
        producer.runCycle(newState -> {
            newState.add(new TypeWithPrimaryKey(1, "a"));
            newState.add(new TypeWithPrimaryKey(1, "b"));
        });

        // Cycle 2: skip=false, the externally-added validator now runs and fails on the duplicate.
        skip.set(false);
        try {
            producer.runCycle(newState -> {
                newState.add(new TypeWithPrimaryKey(2, "a"));
                newState.add(new TypeWithPrimaryKey(2, "b"));
            });
            Assert.fail("expected externally-added validator to fail once skip disabled");
        } catch (ValidationStatusException expected) {
            // validators ran, as expected
        }
    }

    @Test
    public void skipTrue_reportsNoValidateStageEvent() {
        AtomicInteger startCount = new AtomicInteger();
        AtomicInteger completeCount = new AtomicInteger();
        ValidationStatusListener counter = new ValidationStatusListener() {
            @Override
            public void onValidationStatusStart(long version) {
                startCount.incrementAndGet();
            }

            @Override
            public void onValidationStatusComplete(ValidationStatus status, long version, Duration elapsed) {
                completeCount.incrementAndGet();
            }
        };

        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(counter)
                .withListener(new DuplicateDataDetectionValidator("TypeWithPrimaryKey"))
                .withSkipValidation(() -> true)
                .build();

        // Skipped cycle: neither the ValidationStatusListener nor the (failing) ValidatorListener
        // should observe any event -- the validate stage does not run at all, so tooling built on
        // these events (e.g. beacon/cycle log) sees no Validate stage entry for this cycle.
        producer.runCycle(newState -> {
            newState.add(new TypeWithPrimaryKey(1, "a"));
            newState.add(new TypeWithPrimaryKey(1, "b"));
        });
        Assert.assertEquals(0, startCount.get());
        Assert.assertEquals(0, completeCount.get());
    }

    @HollowPrimaryKey(fields = {"id"})
    static class TypeWithPrimaryKey {
        int id;
        String name;

        TypeWithPrimaryKey(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
