package com.netflix.hollow.api.producer.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.test.InMemoryBlobStore;
import java.util.function.Supplier;
import org.junit.Before;
import org.junit.Test;

public class MinimumRecordCountValidatorTest {

    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    @Test
    public void testPassLessThanOrEqualToThresholdThreshold() {
        try {
            testHelper(1000000, () -> 500000);
            testHelper(10, () -> 10);
        } catch (Exception e) {
            fail(); //should not reach here
        }
    }

    @Test
    public void testFailInvalidThreshold() {
        try {
            testHelper(5, () -> null);
            fail(); //should not reach here
        } catch (Exception e) {
            assertTrue(e instanceof ValidationStatusException);
            ValidationStatusException expected = (ValidationStatusException) e;
            assertEquals(1, expected.getValidationStatus().getResults().size());
            assertEquals("Minimum record count validation for type Integer has failed due to invalid threshold: null", expected.getValidationStatus().getResults().get(0).getMessage());
        }

        try {
            testHelper(5, () -> -1);
            fail(); //should not reach here
        } catch (Exception e) {
            assertTrue(e instanceof ValidationStatusException);
            ValidationStatusException expected = (ValidationStatusException) e;
            assertEquals(1, expected.getValidationStatus().getResults().size());
            assertEquals("Minimum record count validation for type Integer has failed due to invalid threshold: -1", expected.getValidationStatus().getResults().get(0).getMessage());
        }

        try {
            testHelper(5, () -> (1<<29) + 1);
            fail(); //should not reach here
        } catch (Exception e) {
            assertTrue(e instanceof ValidationStatusException);
            ValidationStatusException expected = (ValidationStatusException) e;
            assertEquals(1, expected.getValidationStatus().getResults().size());
            assertEquals("Minimum record count validation for type Integer has failed due to invalid threshold: 536870913", expected.getValidationStatus().getResults().get(0).getMessage());
        }
    }

    @Test
    public void testFailThresholdBreached() {
        try {
            testHelper(5, () -> 6);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof ValidationStatusException);
            ValidationStatusException expected = (ValidationStatusException) e;
            assertEquals(1, expected.getValidationStatus().getResults().size());
            assertEquals("Minimum record count validation for type Integer has failed since the record count 5 is less than the configured threshold of 6", expected.getValidationStatus().getResults().get(0).getMessage());
        }
    }

    private void testHelper(int currentRecordCount,
                            Supplier<Integer> minRecordCountSupplier) {

        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new MinimumRecordCountValidator("Integer", minRecordCountSupplier)).build();

        producer.runCycle((state) -> {
                    for (int i = 0; i < currentRecordCount; i++) {
                        state.add(i);
                    }
        });
    }
}
