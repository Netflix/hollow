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
package com.netflix.hollow.api.objects.provider;

import static com.netflix.hollow.api.objects.provider.Util.memoize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import java.util.function.Supplier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class HollowObjectCacheProviderTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Mock
    HollowTypeReadState typeReadState;
    @Mock
    HollowTypeAPI typeAPI;
    @Mock
    HollowFactory<TypeA> factory;
    PopulatedOrdinalListener populatedOrdinalListener;
    Supplier<HollowObjectCacheProvider<TypeA>> subject;

    @Before
    public void before() {
        populatedOrdinalListener = new PopulatedOrdinalListener();

        when(typeReadState.getTypeState())
                .thenReturn(typeReadState);
        when(typeReadState.getListener(PopulatedOrdinalListener.class))
                .thenReturn(populatedOrdinalListener);

        subject = memoize(() -> new HollowObjectCacheProvider<>(typeReadState, typeAPI, factory));
    }

    @Test
    public void adding_noPreExisting() {
        TypeA a0 = typeA(0);
        TypeA a1 = typeA(1);
        TypeA a2 = typeA(2);

        notifyAdded(a0, a1, a2);

        assertEquals(a0, subject.get().getHollowObject(a0.ordinal));
        assertEquals(a1, subject.get().getHollowObject(a1.ordinal));
        assertEquals(a2, subject.get().getHollowObject(a2.ordinal));
    }

    @Test
    public void preExisting() {
        TypeA a0 = typeA(0);
        TypeA a1 = typeA(1);
        TypeA a2 = typeA(2);


        prepopulate(a0, a1, a2);

        assertEquals(a0, subject.get().getHollowObject(a0.ordinal));
        assertEquals(a1, subject.get().getHollowObject(a1.ordinal));
        assertEquals(a2, subject.get().getHollowObject(a2.ordinal));
    }

    @Test
    public void adding_withPreExisting() {
        TypeA a2 = typeA(2);

        prepopulate(typeA(0), typeA(1));
        notifyAdded(a2);

        assertEquals(a2, subject.get().getHollowObject(a2.ordinal));
    }

    @Test
    public void adding_withOrdinalGaps() {
        TypeA a = typeA(1);

        notifyAdded(a);

        assertNull(subject.get().getHollowObject(0));
        assertEquals(a, subject.get().getHollowObject(a.ordinal));
    }

    @Test
    public void notification_afterDetaching() {
        subject.get().detach();

        // FIXME(timt): assert that this shouldn't log an error
        notifyAdded(typeA(1));

        try {
            // asserting on the absence of side effects, in this case no gaps should have been
            // filled with null
            subject.get().getHollowObject(0);
            fail("expected exception to be thrown");
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    private void prepopulate(TypeA...population) {
        for(TypeA a : population)
            populatedOrdinalListener.addedOrdinal(a.ordinal);
    }

    private void notifyAdded(TypeA...added) {
        subject.get().beginUpdate();
        for(TypeA a : added)
            subject.get().addedOrdinal(a.ordinal);
        subject.get().endUpdate();
    }

    private TypeA typeA(int ordinal) {
        TypeA a = new TypeA(ordinal);
        when(factory.newCachedHollowObject(typeReadState, typeAPI, ordinal))
                .thenReturn(a);
        return a;
    }

    static class TypeA {
        final int ordinal;

        TypeA(int ordinal) {
            this.ordinal = ordinal;
        }
    }
}
