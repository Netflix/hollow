package com.netflix.hollow.api.producer.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.test.InMemoryBlobStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class RecordCountPercentChangeValidatorTests {
    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    @Test
    public void testPassLessThanThreshold() {
        try {
            testHelper(RecordCountPercentChangeValidator.Threshold.builder()
                    .withAddedPercentageThreshold(() -> 0.5f)
                    .build(), 0, 0, 0);
        } catch (Exception e) {
            fail(); //should not reach here
        }
    }

    @Test
    public void testPassThresholdNotSet() {
        try {
            testHelper(RecordCountPercentChangeValidator.Threshold.builder()
                    .withAddedPercentageThreshold(() -> 0.5f)
                    .build(), 0, 0, 3);
        } catch (Exception e) {
            fail(); //should not reach here
        }
    }

    @Test
    public void testAddExceedThreshold() {
        try {
            testHelper(RecordCountPercentChangeValidator.Threshold.builder()
                    .withAddedPercentageThreshold(() -> 0.5f)
                    .build(), 0, 4, 0);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof ValidationStatusException);
            ValidationStatusException expected = (ValidationStatusException) e;
            assertEquals(1, expected.getValidationStatus().getResults().size());
            String message = expected.getValidationStatus().getResults().get(0).getMessage();
            assertTrue(message.startsWith("'TypeWithPrimaryKey' record count change exceeds threshold: "));
            assertTrue(message.contains("added 80.00% > 50.00%"));
        }
    }

    @Test
    public void testRemoveExceedThreshold() {
        try {
            testHelper(RecordCountPercentChangeValidator.Threshold.builder()
                    .withRemovedPercentageThreshold(() -> 0.5f)
                    .build(), 0, 0, 4);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof ValidationStatusException);
            ValidationStatusException expected = (ValidationStatusException) e;
            assertEquals(1, expected.getValidationStatus().getResults().size());
            String message = expected.getValidationStatus().getResults().get(0).getMessage();
            assertTrue(message.startsWith("'TypeWithPrimaryKey' record count change exceeds threshold: "));
            assertTrue(message.contains("removed 80.00% > 50.00%"));
        }
    }

    @Test
    public void testUpdateExceedThreshold() {
        try {
            testHelper(RecordCountPercentChangeValidator.Threshold.builder()
                    .withUpdatedPercentageThreshold(() ->0.5f)
                    .build(), 4, 0, 0);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof ValidationStatusException);
            ValidationStatusException expected = (ValidationStatusException) e;
            assertEquals(1, expected.getValidationStatus().getResults().size());
            String message = expected.getValidationStatus().getResults().get(0).getMessage();
            assertTrue(message.startsWith("'TypeWithPrimaryKey' record count change exceeds threshold: "));
            assertTrue(message.contains("updated 80.00% > 50.00%"));
        }
    }

    @Test
    public void testMultipleThresholdsExceeded() {
        try {
            testHelper(RecordCountPercentChangeValidator.Threshold.builder()
                    .withAddedPercentageThreshold(() -> 0.5f)
                    .withRemovedPercentageThreshold(() -> 0.3f)
                    .build(), 0, 4, 3);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof ValidationStatusException);
            ValidationStatusException expected = (ValidationStatusException) e;
            assertEquals(1, expected.getValidationStatus().getResults().size());
            String message = expected.getValidationStatus().getResults().get(0).getMessage();
            assertTrue(message.startsWith("'TypeWithPrimaryKey' record count change exceeds threshold: "));
            assertTrue(message.contains("added 80.00% > 50.00%"));
            assertTrue(message.contains("removed 60.00% > 30.00%"));
            assertTrue(message.contains(", "));
        }
    }

    private void testHelper(RecordCountPercentChangeValidator.Threshold threshold,
                            int updatedRecordCount,
                            int addedRecordCount,
                            int removedRecordCount) {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new RecordCountPercentChangeValidator("TypeWithPrimaryKey", threshold)).build();

        List<TypeWithPrimaryKey> previousItems = new ArrayList<>();
        previousItems.add(new TypeWithPrimaryKey(0, "a", "aa"));
        previousItems.add(new TypeWithPrimaryKey(1, "a", "aa"));
        previousItems.add(new TypeWithPrimaryKey(2, "a", "aa"));
        previousItems.add(new TypeWithPrimaryKey(3, "a", "aa"));
        previousItems.add(new TypeWithPrimaryKey(4, "a", "aa"));

        producer.runCycle(new HollowProducer.Populator() {
            public void populate(HollowProducer.WriteState newState) throws Exception {
                for (TypeWithPrimaryKey val : previousItems) {
                    newState.add(val);
                }
            }
        });

        List<TypeWithPrimaryKey> currentItems = new ArrayList<>();

        currentItems.addAll(previousItems);

        if (addedRecordCount > 0) {
            for (int i = 0; i < addedRecordCount; i++) {
                currentItems.add(new TypeWithPrimaryKey(i, String.valueOf(i), String.valueOf(i)));
            }
        }

        if (removedRecordCount > 0) {
            for (int i = 0; i < removedRecordCount; i++) {
                currentItems.remove(0);
            }
        }

        if (updatedRecordCount > 0) {
            for (int i = 0; i < updatedRecordCount; i++) {
                TypeWithPrimaryKey item = currentItems.get(0);
                TypeWithPrimaryKey newItem = new TypeWithPrimaryKey(item.id, item.name, "bb");
                currentItems.remove(0);
                currentItems.add(newItem);
            }
        }

        producer.runCycle(new HollowProducer.Populator() {
            public void populate(HollowProducer.WriteState newState) throws Exception {
                for (TypeWithPrimaryKey val : currentItems) {
                    newState.add(val);
                }
            }
        });
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
