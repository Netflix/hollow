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
import java.util.concurrent.atomic.AtomicBoolean;
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
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class, () -> true))
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
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class, () -> true))
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
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class, () -> true))
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
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class, () -> true))
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
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class, () -> true))
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
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class, () -> true))
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
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class, () -> true))
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

    @Test
    public void restoreToOlderVersionDropsLaggedIndex() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class, () -> true))
                .build();
        producer.initializeDataModel(TypeWithPrimaryKey.class);

        // Cycle 1 → v1: records {1, 2}
        long v1 = producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
        });

        // Cycle 2 → v2: adds key 3. Lagged index now reflects v2.
        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
            writeState.add(new TypeWithPrimaryKey(3, "C"));
        });

        // Restore to v1 — must drop the lagged index (which is aligned to v2 ordinal space).
        producer.restore(v1, blobStore);

        // Post-restore cycle on a fresh producer state. With the index dropped this runs the
        // snapshot (full-scan) path; introducing a duplicate must still be caught.
        try {
            producer.runCycle(writeState -> {
                writeState.add(new TypeWithPrimaryKey(1, "A"));
                writeState.add(new TypeWithPrimaryKey(2, "B"));
                writeState.add(new TypeWithPrimaryKey(2, "B_dup"));
            });
            Assert.fail("Expected ValidationStatusException");
        } catch (ValidationStatusException expected) {
            String message = expected.getValidationStatus().getResults().get(0).getMessage();
            Assert.assertTrue("Post-restore cycle should run snapshot path, not delta",
                    message.contains("distinct keys that each have duplicate records affecting"));
        }
    }

    @Test
    public void restoreThenCleanCyclePassesAndResumesIncrementalNextCycle() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class, () -> true))
                .build();
        producer.initializeDataModel(TypeWithPrimaryKey.class);

        long v1 = producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
        });
        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
            writeState.add(new TypeWithPrimaryKey(3, "C"));
        });

        producer.restore(v1, blobStore);

        // Clean post-restore cycle (snapshot path); rebuilds the lagged index for next cycle.
        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
            writeState.add(new TypeWithPrimaryKey(4, "D"));
        });

        // Next cycle should run the delta path against the rebuilt index and catch a new-vs-new dup.
        try {
            producer.runCycle(writeState -> {
                writeState.add(new TypeWithPrimaryKey(1, "A"));
                writeState.add(new TypeWithPrimaryKey(2, "B"));
                writeState.add(new TypeWithPrimaryKey(4, "D"));
                writeState.add(new TypeWithPrimaryKey(5, "E"));
                writeState.add(new TypeWithPrimaryKey(5, "E_dup"));
            });
            Assert.fail("Expected ValidationStatusException");
        } catch (ValidationStatusException expected) {
            String message = expected.getValidationStatus().getResults().get(0).getMessage();
            Assert.assertTrue("Should resume delta path after rebuilding index",
                    message.contains("delta check"));
            Assert.assertTrue(message.contains("[5]"));
        }
    }

    @Test
    public void defaultConstructorRunsFullScanEveryCycle() {
        // No-supplier constructor → every cycle is full-scan. Verify by introducing a duplicate on
        // cycle 2 and checking the message format (full-scan reports "distinct keys ... affecting N
        // records", delta reports "delta check").
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class))
                .build();

        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
        });

        try {
            producer.runCycle(writeState -> {
                writeState.add(new TypeWithPrimaryKey(1, "A"));
                writeState.add(new TypeWithPrimaryKey(2, "B"));
                writeState.add(new TypeWithPrimaryKey(2, "B_dup"));
            });
            Assert.fail("Expected ValidationStatusException");
        } catch (ValidationStatusException expected) {
            String message = expected.getValidationStatus().getResults().get(0).getMessage();
            Assert.assertTrue("Default mode should produce full-scan error message, not delta",
                    message.contains("distinct keys that each have duplicate records affecting"));
            Assert.assertFalse("Default mode should not be running the delta path",
                    message.contains("delta check"));
        }
    }

    @Test
    public void supplierReturningFalseRunsFullScan() {
        AtomicBoolean enabled = new AtomicBoolean(false);
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class, enabled::get))
                .build();

        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
        });

        try {
            producer.runCycle(writeState -> {
                writeState.add(new TypeWithPrimaryKey(1, "A"));
                writeState.add(new TypeWithPrimaryKey(2, "B"));
                writeState.add(new TypeWithPrimaryKey(2, "B_dup"));
            });
            Assert.fail("Expected ValidationStatusException");
        } catch (ValidationStatusException expected) {
            String message = expected.getValidationStatus().getResults().get(0).getMessage();
            Assert.assertTrue("Supplier returning false should force full-scan path",
                    message.contains("distinct keys that each have duplicate records affecting"));
            Assert.assertFalse(message.contains("delta check"));
        }
    }

    @Test
    public void supplierFlipResumesIncrementalOnNextCycle() {
        // With incremental enabled, flip the supplier off, run a full-scan cycle, flip it back on,
        // and confirm the next cycle returns to the delta path.
        AtomicBoolean enabled = new AtomicBoolean(true);
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class, enabled::get))
                .build();

        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
        });

        // Supplier off for one cycle.
        enabled.set(false);
        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
            writeState.add(new TypeWithPrimaryKey(3, "C"));
        });
        enabled.set(true);

        // The full-scan cycle dropped previousCycleIndex, so the next cycle rebuilds the baseline
        // (snapshot under incremental mode). The cycle after that is delta.
        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
            writeState.add(new TypeWithPrimaryKey(3, "C"));
            writeState.add(new TypeWithPrimaryKey(4, "D"));
        });

        try {
            producer.runCycle(writeState -> {
                writeState.add(new TypeWithPrimaryKey(1, "A"));
                writeState.add(new TypeWithPrimaryKey(2, "B"));
                writeState.add(new TypeWithPrimaryKey(3, "C"));
                writeState.add(new TypeWithPrimaryKey(4, "D"));
                writeState.add(new TypeWithPrimaryKey(5, "E"));
                writeState.add(new TypeWithPrimaryKey(5, "E_dup"));
            });
            Assert.fail("Expected ValidationStatusException");
        } catch (ValidationStatusException expected) {
            String message = expected.getValidationStatus().getResults().get(0).getMessage();
            Assert.assertTrue("Should be back on delta path after flipping supplier back on",
                    message.contains("delta check"));
            Assert.assertTrue(message.contains("[5]"));
        }
    }

    @Test
    public void deltaWithOrdinalReuseDoesNotProduceFalsePositive() {
        // Regression: when a record is modified across cycles its old ordinal is freed and may
        // be reused by Hollow's allocator for a brand-new record in a later cycle. The new-vs-
        // existing check in findDuplicateKeysInDelta probes the lagged index for the new record's
        // key and gates on populatedOrdinals.get(matchedOrdinal). The concern was that if the
        // matched (lagged) ordinal had been repopulated this cycle with an unrelated record, the
        // gate would pass and produce a false positive.
        //
        // In practice this is unreachable: compact() only frees ordinals that were NOT in the
        // prior cycle's populated set, so any ordinal the lagged index points to (which by
        // definition WAS populated last cycle) cannot enter the free pool for the current cycle.
        // This test pins that invariant.
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class, () -> true))
                .build();

        // Cycle 1 (snapshot): (1,"A") and (2,"B") get ordinals 0 and 1.
        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
        });

        // Cycle 2: (1,"A") is modified to (1,"A_v2"). The new bytes get a fresh ordinal;
        // (1,"A")'s old ordinal becomes unused-in-current and is eligible for the free pool
        // at the START of cycle 3 (when compact runs against cycle 2's populated set).
        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A_v2"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
        });

        // Cycle 3: modify (1,"A_v2") again and add a brand-new (3,"C"). At start-of-cycle
        // compact, ordinal 0 (originally (1,"A")) is freed. The allocator may hand ordinal 0
        // to (1,"A_v3"). The lagged index from cycle 2 maps key [1] to whatever ordinal
        // (1,"A_v2") landed on in cycle 2 — that ordinal is freed this cycle, so the
        // new-vs-existing check correctly sees populatedOrdinals.get(matched) == false and
        // does not report a duplicate. No real duplicates exist here.
        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A_v3"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
            writeState.add(new TypeWithPrimaryKey(3, "C"));
        });
    }

    @Test
    public void deltaWithOrdinalReuseStillDetectsRealDuplicate() {
        // Companion to deltaWithOrdinalReuseDoesNotProduceFalsePositive: the same cycle that
        // exercises ordinal reuse must still flag a genuine duplicate introduced alongside it.
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator(TypeWithPrimaryKey.class, () -> true))
                .build();

        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
        });

        producer.runCycle(writeState -> {
            writeState.add(new TypeWithPrimaryKey(1, "A_v2"));
            writeState.add(new TypeWithPrimaryKey(2, "B"));
        });

        // Cycle 3 mixes ordinal reuse (via (1,"A_v3")) with a real new-vs-existing duplicate
        // on key [2].
        try {
            producer.runCycle(writeState -> {
                writeState.add(new TypeWithPrimaryKey(1, "A_v3"));
                writeState.add(new TypeWithPrimaryKey(2, "B"));
                writeState.add(new TypeWithPrimaryKey(2, "B_dup"));
            });
            Assert.fail("Expected ValidationStatusException");
        } catch (ValidationStatusException expected) {
            String message = expected.getValidationStatus().getResults().get(0).getMessage();
            Assert.assertTrue("Should still detect the real duplicate on delta path",
                    message.contains("delta check"));
            Assert.assertTrue(message.contains("[2]"));
        }
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
