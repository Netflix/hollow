/*
 *  Copyright 2019 Netflix, Inc.
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

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.test.InMemoryBlobStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DuplicateDataDetectionValidatorTests {
    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    @Test
    public void failTestMissingSchema() {
        try {
            HollowProducer producer = HollowProducer.withPublisher(blobStore)
                    .withBlobStager(new HollowInMemoryBlobStager())
                    .withListener(new DuplicateDataDetectionValidator("FakeType")).build();
            producer.runCycle(writeState -> writeState.add("hello"));
        } catch (ValidationStatusException expected) {
            Assert.assertEquals(1, expected.getValidationStatus().getResults().size());
            Assert.assertTrue(expected.getValidationStatus().getResults().get(0).getMessage()
                    .endsWith("(see initializeDataModel)"));
        }
    }

    @Test
    public void test_errorMessageIsTruncatedWhenThereAreManyDuplicates() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class))
                .build();

        try {
            producer.runCycle(writeState -> {
                // Add 1000 records with the same primary key
                for (int i = 0; i < 1000; i++) {
                    writeState.add(new TypeWithPrimaryKey(1, "Duplicate" + i));
                }
            });
            Assert.fail("Expected ValidationStatusException");
        } catch (ValidationStatusException expected) {
            String message = expected.getValidationStatus().getResults().get(0).getMessage();
            Assert.assertTrue(message.contains("1 distinct keys that each have duplicate records affecting 1000 records")); // All 1000 records share the same primary key
        }
    }

    @Test
    public void deltaDetectsNewVsExistingDuplicate() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class))
                .build();

        // Snapshot: unique records
        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
            writeState.add(new TypeWithPrimaryKey(3, "C"));
        });

        // Delta: add a duplicate of existing key 2
        try {
            producer.runCycle(writeState -> {
                writeState.add(new TypeWithPrimaryKey(1, "A"));
                writeState.add(new TypeWithPrimaryKey(2, "B"));
                writeState.add(new TypeWithPrimaryKey(2, "B_dup"));
                writeState.add(new TypeWithPrimaryKey(3, "C"));
            });
            Assert.fail("Expected ValidationStatusException");
        } catch (ValidationStatusException expected) {
            String message = expected.getValidationStatus().getResults().get(0).getMessage();
            Assert.assertTrue("Should mention delta check",
                    message.contains("delta check"));
            Assert.assertTrue("Should contain the duplicate key",
                    message.contains("[2]"));
        }
    }

    @Test
    public void deltaDetectsNewVsNewDuplicate() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class))
                .build();

        // Snapshot: unique records
        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
        });

        // Delta: two new records with same key
        try {
            producer.runCycle(writeState -> {
                writeState.add(new TypeWithPrimaryKey(1, "A"));
                writeState.add(new TypeWithPrimaryKey(5, "E"));
                writeState.add(new TypeWithPrimaryKey(5, "E_dup"));
            });
            Assert.fail("Expected ValidationStatusException");
        } catch (ValidationStatusException expected) {
            String message = expected.getValidationStatus().getResults().get(0).getMessage();
            Assert.assertTrue("Should mention delta check",
                    message.contains("delta check"));
            Assert.assertTrue("Should contain the duplicate key",
                    message.contains("[5]"));
        }
    }

    @Test
    public void deltaWithNoNewOrdinalsPassesValidation() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class))
                .build();

        // Snapshot
        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
        });

        // Delta with identical data — no new ordinals, should pass
        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
        });
    }

    @Test
    public void snapshotDetectsPreExistingDuplicates() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class))
                .build();

        // Snapshot with duplicates — caught by full scan
        try {
            producer.runCycle(writeState -> {
                writeState.add(new TypeWithPrimaryKey(1, "A"));
                writeState.add(new TypeWithPrimaryKey(1, "A_dup"));
                writeState.add(new TypeWithPrimaryKey(2, "B"));
            });
            Assert.fail("Expected ValidationStatusException");
        } catch (ValidationStatusException expected) {
            String message = expected.getValidationStatus().getResults().get(0).getMessage();
            Assert.assertTrue("Snapshot path should provide full counts",
                    message.contains("distinct keys that each have duplicate records affecting"));
        }
    }

    @Test
    public void deltaWithModifiedRecordPassesValidation() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class))
                .build();

        // Snapshot
        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
        });

        // Delta: modify record 2 (same key, different non-key data) — should not be a duplicate
        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B_modified"));
        });
    }

    @Test
    public void deltaAfterFailedValidationPassesCorrectly() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class))
                .build();

        // Cycle 1 (snapshot): unique records — passes
        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
        });

        // Cycle 2 (delta): duplicate — fails
        try {
            producer.runCycle(writeState -> {
                writeState.add(new TypeWithPrimaryKey(1, "A"));
                writeState.add(new TypeWithPrimaryKey(2, "B"));
                writeState.add(new TypeWithPrimaryKey(2, "B_dup"));
            });
            Assert.fail("Expected ValidationStatusException");
        } catch (ValidationStatusException expected) {
            // expected
        }

        // Cycle 3 (delta): no duplicates — should pass with lagged index still aligned
        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
            writeState.add(new TypeWithPrimaryKey(3, "C"));
        });
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
