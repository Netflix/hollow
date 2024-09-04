package com.netflix.hollow.api.producer.validation;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.test.InMemoryBlobStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
            Assert.fail(); //should not reach here
        }
    }

    @Test
    public void testPassThresholdNotSet() {
        try {
            testHelper(RecordCountPercentChangeValidator.Threshold.builder()
                    .withAddedPercentageThreshold(() -> 0.5f)
                    .build(), 0, 0, 3);
        } catch (Exception e) {
            Assert.fail(); //should not reach here
        }
    }

    @Test
    public void testAddExceedThreshold() {
        try {
            testHelper(RecordCountPercentChangeValidator.Threshold.builder()
                    .withAddedPercentageThreshold(() -> 0.5f)
                    .build(), 0, 4, 0);
            Assert.fail();
        } catch (ValidationStatusException expected) {
            Assert.assertEquals(1, expected.getValidationStatus().getResults().size());
        }
    }

    @Test
    public void testRemoveExceedThreshold() {
        try {
            testHelper(RecordCountPercentChangeValidator.Threshold.builder()
                    .withRemovedPercentageThreshold(() -> 0.5f)
                    .build(), 0, 0, 4);
            Assert.fail();
        } catch (ValidationStatusException expected) {
            Assert.assertEquals(1, expected.getValidationStatus().getResults().size());
        }
    }

    @Test
    public void testUpdateExceedThreshold() {
        try {
            testHelper(RecordCountPercentChangeValidator.Threshold.builder()
                    .withUpdatedPercentageThreshold(() ->0.5f)
                    .withAddedPercentageThreshold(() -> 0.5f)
                    .withAddedPercentageThreshold(() -> 0.5f)
                    .build(), 4, 1, 1);
            Assert.fail();
        } catch (ValidationStatusException expected) {
            Assert.assertEquals(1, expected.getValidationStatus().getResults().size());
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
