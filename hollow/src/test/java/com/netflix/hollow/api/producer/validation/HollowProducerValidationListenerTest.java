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

import com.netflix.hollow.api.producer.AbstractHollowProducerListener;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.HollowProducer.Announcer;
import com.netflix.hollow.api.producer.HollowProducer.Blob;
import com.netflix.hollow.api.producer.HollowProducer.Builder;
import com.netflix.hollow.api.producer.HollowProducer.Populator;
import com.netflix.hollow.api.producer.HollowProducer.Publisher;
import com.netflix.hollow.api.producer.HollowProducer.WriteState;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowProducerValidationListenerTest {

    private HollowProducer hollowProducer;
    private TestValidationStatusListener validationListener;
    private TestCycleAndValidationStatusListener cycleAndValidationListener;
    private Publisher publisher;
    private Announcer announcer;

    @Before
    public void setup() {
        publisher = new Publisher() {
            @Override
            public void publish(Blob blob) {
                // TODO Auto-generated method stub
            }
        };
        announcer = new Announcer() {
            @Override
            public void announce(long stateVersion) {
                // TODO Auto-generated method stub
            }
        };
    }

    @Test
    public void testValidationListenerOnValidationSuccess() {
        createHollowProducerAndRunCycle("MovieWithPrimaryKey", true);
        assertOnValidationStatus(2, true, true, validationListener.getVersion());
        Assert.assertTrue(validationListener.getVersion() > 0);
        Assert.assertTrue(cycleAndValidationListener.getVersion() > 0);
        Assert.assertEquals(cycleAndValidationListener.getCycleVersion(), cycleAndValidationListener.getVersion());
        Assert.assertEquals(cycleAndValidationListener.getCycleVersion(), validationListener.getVersion());
    }


    @Test(expected = ValidationStatusException.class)
    public void testValidationListenerOnFailure() {
        createHollowProducerAndRunCycle("MovieWithoutPrimaryKey", true);
        assertOnValidationStatus(2, false, false, validationListener.getVersion());
    }

    @Test
    public void testValidationListenerWithOnlyRecordCountValidator() {
        createHollowProducerAndRunCycle("MovieWithPrimaryKey", false);
        assertOnValidationStatus(1, true, true, validationListener.getVersion());

        // Expecting only record count validator status
        ValidationResult validatorStatus = validationListener.getStatus().getResults().get(0);
        Assert.assertNotNull(validatorStatus);

        // ValidationStatus builds record validator status based toString of RecordCountValidatorStatus for now.
        Assert.assertEquals(ValidationResultType.PASSED, validatorStatus.getResultType());
        Assert.assertNull(validatorStatus.getThrowable());

        // Record count validator would have skipped validation because the previous record count is 0 in this test.
        // But that status for now is only passed as string through toString method of the validator.
        Assert.assertTrue(validatorStatus.getMessage().contains("MovieWithPrimaryKey"));
        Assert.assertTrue(validatorStatus.getMessage().contains("Previous record count is 0"));

        // Check details
        Assert.assertTrue(validatorStatus.getName().startsWith(RecordCountVarianceValidator.class.getName()));
        Assert.assertEquals("MovieWithPrimaryKey", validatorStatus.getDetails().get("Typename"));
        Assert.assertEquals("3.0", validatorStatus.getDetails().get("AllowableVariancePercent"));
    }

    private void createHollowProducerAndRunCycle(final String typeName, boolean addPrimaryKeyValidator) {
        ValidatorListener dupeValidator = new DuplicateDataDetectionValidator(typeName);
        ValidatorListener countValidator = new RecordCountVarianceValidator(typeName, 3.0f);
        validationListener = new TestValidationStatusListener();
        cycleAndValidationListener = new TestCycleAndValidationStatusListener();
        Builder builder = HollowProducer.withPublisher(publisher).withAnnouncer(announcer)
                .withListener(validationListener)
                .withListener(cycleAndValidationListener)
                .withListener(countValidator);
        if (addPrimaryKeyValidator) {
            builder = builder.withListener(dupeValidator);
        }

        hollowProducer = builder.build();
        if (typeName.equals("MovieWithPrimaryKey")) {
            hollowProducer.initializeDataModel(MovieWithPrimaryKey.class);
        } else {
            hollowProducer.initializeDataModel(MovieWithoutPrimaryKey.class);
        }

        hollowProducer.runCycle(new Populator() {
            @Override
            public void populate(WriteState newState) throws Exception {
                List<String> actors = Arrays.asList("Angelina Jolie", "Brad Pitt");
                if (typeName.equals("MovieWithPrimaryKey")) {
                    newState.add(new MovieWithPrimaryKey(123, "someTitle1", actors));
                    newState.add(new MovieWithPrimaryKey(123, "someTitle1", actors));
                } else {
                    newState.add(new MovieWithoutPrimaryKey(123, "someTitle1", actors));
                    newState.add(new MovieWithoutPrimaryKey(1233, "someTitle2", actors));
                }
            }
        });
    }

    private void assertOnValidationStatus(int size, boolean passed, boolean isThrowableNull, long version) {
        ValidationStatus status = validationListener.getStatus();
        Assert.assertNotNull(
                "Stats null indicates HollowValidationFakeListener.onValidationComplete() was not called on runCycle.",
                status);
        Assert.assertEquals(size, status.getResults().size());
        Assert.assertEquals(passed, status.passed());
    }


    @HollowPrimaryKey(fields = {"id"})
    static class MovieWithPrimaryKey extends MovieWithoutPrimaryKey {

        public MovieWithPrimaryKey(int id, String title, List<String> actors) {
            super(id, title, actors);
        }
    }

    static class MovieWithoutPrimaryKey {
        private final int id;
        private final String title;
        private final List<String> actors;

        public MovieWithoutPrimaryKey(int id, String title, List<String> actors) {
            this.id = id;
            this.title = title;
            this.actors = actors;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getActors() {
            return actors;
        }
    }

    static class TestValidationStatusListener implements ValidationStatusListener {
        private long version;
        private ValidationStatus status;

        @Override
        public void onValidationStatusStart(long version) {
            this.version = version;
        }

        @Override
        public void onValidationStatusComplete(
                ValidationStatus status, long version, Duration elapsed) {
            this.status = status;
        }

        public long getVersion() {
            return version;
        }

        public ValidationStatus getStatus() {
            return status;
        }

        public void reset() {
            version = -1;
            status = null;
        }
    }

    static class TestCycleAndValidationStatusListener extends AbstractHollowProducerListener
            implements ValidationStatusListener {
        private long cycleVersion;
        private long version;
        private ValidationStatus status;

        @Override public void onCycleStart(long version) {
            this.cycleVersion = version;
        }

        @Override
        public void onValidationStatusStart(long version) {
            this.version = version;
        }

        @Override
        public void onValidationStatusComplete(
                ValidationStatus status, long version, Duration elapsed) {
            this.status = status;
        }

        @Override
        public void onCycleComplete(ProducerStatus status, long elapsed, TimeUnit unit) {
        }

        public long getCycleVersion() {
            return cycleVersion;
        }

        public long getVersion() {
            return version;
        }

        public ValidationStatus getStatus() {
            return status;
        }

        public void reset() {
            version = -1;
            status = null;
        }
    }

}