/*
 *
 *  Copyright 2018 Netflix, Inc.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.netflix.hollow.api.consumer.InMemoryBlobStore;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.util.function.BiPredicate;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class ObjectModificationValidatorTest {
    @HollowPrimaryKey(fields = {"id", "code"})
    private static class TypeA {
        @SuppressWarnings("unused")
        private final int id;
        @SuppressWarnings("unused")
        private final String code;
        @SuppressWarnings("unused")
        private final String data;

        public TypeA(int id, String code, String data) {
            this.id = id;
            this.code = code;
            this.data = data;
        }
    }

    @Test
    public void testValidate_onlyAdditions() {
        BiPredicate<GenericHollowObject, GenericHollowObject> filter = getMockBiPredicate();
        when(filter.test(any(GenericHollowObject.class), any(GenericHollowObject.class))).thenReturn(true);
        ObjectModificationValidator<HollowAPI, GenericHollowObject> validator = createValidator(filter);
        HollowProducer producer = getProducer(validator);
        producer.runCycle(writeState -> writeState.add(new TypeA(1, "fo", "bar")));
        verify(filter, never()).test(
                any(GenericHollowObject.class), any(GenericHollowObject.class));
        producer.runCycle(writeState -> {
            writeState.add(new TypeA(1, "fo", "bar"));
            writeState.add(new TypeA(2, "fo", "baz"));
        });
        verify(filter, never()).test(
                any(GenericHollowObject.class), any(GenericHollowObject.class));
    }

    @Test
    public void testValidate_onlyRemovals() {
        @SuppressWarnings("unchecked")
        BiPredicate<GenericHollowObject, GenericHollowObject> filter = getMockBiPredicate();
        when(filter.test(any(GenericHollowObject.class), any(GenericHollowObject.class))).thenReturn(true);
        ObjectModificationValidator<HollowAPI, GenericHollowObject> validator = createValidator(filter);
        HollowProducer producer = getProducer(validator);
        producer.runCycle(writeState -> {
            writeState.add(new TypeA(1, "fo", "bar"));
            writeState.add(new TypeA(2, "fo", "baz"));
        });
        verify(filter, never()).test(
                any(GenericHollowObject.class), any(GenericHollowObject.class));
        producer.runCycle(writeState -> writeState.add(new TypeA(1, "fo", "bar")));
        verify(filter, never()).test(
                any(GenericHollowObject.class), any(GenericHollowObject.class));
    }

    @Test
    public void testValidate_onlyModifications() {
        BiPredicate<GenericHollowObject, GenericHollowObject> filter = getMockBiPredicate();
        when(filter.test(any(GenericHollowObject.class), any(GenericHollowObject.class))).thenReturn(true);
        ObjectModificationValidator<HollowAPI, GenericHollowObject> validator = createValidator(filter);
        HollowProducer producer = getProducer(validator);
        producer.runCycle(writeState -> {
            writeState.add(new TypeA(1, "fo", "bar"));
            writeState.add(new TypeA(2, "fo", "baz"));
        });
        verify(filter, never()).test(
                any(GenericHollowObject.class), any(GenericHollowObject.class));
        producer.runCycle(writeState -> {
            writeState.add(new TypeA(1, "fo", "baz"));
            writeState.add(new TypeA(2, "fo", "baz"));
        });
        ArgumentCaptor<GenericHollowObject> beforeCaptor =
                ArgumentCaptor.forClass(GenericHollowObject.class);
        ArgumentCaptor<GenericHollowObject> afterCaptor =
                ArgumentCaptor.forClass(GenericHollowObject.class);
        verify(filter).test(beforeCaptor.capture(), afterCaptor.capture());
        assertEquals("Before value should be correct", "bar",
                beforeCaptor.getValue().getObject("data").getString("value"));
        assertEquals("After value should be correct", "baz",
                afterCaptor.getValue().getObject("data").getString("value"));
    }

    @Test
    public void testValidate_validationFailure() {
        BiPredicate<GenericHollowObject, GenericHollowObject> filter =
                (a, b) -> a.getObject("data").getString("value").length()
                        <= b.getObject("data").getString("value").length();
        ObjectModificationValidator<HollowAPI, GenericHollowObject> validator = createValidator(filter);
        HollowProducer producer = getProducer(validator);
        producer.runCycle(writeState -> writeState.add(new TypeA(1, "fo", "bar")));
        try {
            producer.runCycle(writeState -> writeState.add(new TypeA(1, "fo", "ba")));
            fail("Expected validation exception");
        } catch (ValidationStatusException e) { // expected
        }
    }

    private static HollowProducer getProducer(ObjectModificationValidator validator) {
        return HollowProducer.withPublisher(new InMemoryBlobStore())
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(validator)
                .build();
    }

    @SuppressWarnings("unchecked")
    private BiPredicate<GenericHollowObject, GenericHollowObject> getMockBiPredicate() {
        return mock(BiPredicate.class);
    }

    private ObjectModificationValidator<HollowAPI, GenericHollowObject> createValidator(
            BiPredicate<GenericHollowObject, GenericHollowObject> filter) {
        return new ObjectModificationValidator<>(TypeA.class.getSimpleName(), filter,
                HollowAPI::new, (api, ordinal) -> new GenericHollowObject(api.getDataAccess(),
                TypeA.class.getSimpleName(), ordinal));
    }
}
