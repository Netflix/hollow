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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
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
        } catch (IllegalStateException expected) {}
    }

    @Test
    public void rotation_dropsRemovedOrdinal_byDefault() {
        TypeA a0 = typeA(0);
        TypeA a1 = typeA(1);
        TypeA a2 = typeA(2);
        prepopulate(a0, a1, a2);
        HollowObjectCacheProvider<TypeA> previous =
                new HollowObjectCacheProvider<>(typeReadState, typeAPI, factory);

        // delta: ordinal 2 removed
        removeOrdinals(2);

        HollowObjectCacheProvider<TypeA> current =
                new HollowObjectCacheProvider<>(typeReadState, typeAPI, factory, previous);

        assertEquals(a0, current.getHollowObject(0));
        assertEquals(a1, current.getHollowObject(1));
        // removed-only ordinal is holed out on rotation, so the before image is lost
        assertNull(current.getHollowObject(2));
    }

    @Test
    public void rotation_retainsRemovedOrdinalForOneCycle_whenEnabled() {
        TypeA a0 = typeA(0);
        TypeA a1 = typeA(1);
        TypeA a2 = typeA(2);
        prepopulate(a0, a1, a2);
        HollowObjectCacheProvider<TypeA> previous =
                new HollowObjectCacheProvider<>(typeReadState, typeAPI, factory);

        // delta: ordinal 2 removed
        removeOrdinals(2);

        HollowObjectCacheProvider<TypeA> current =
                new HollowObjectCacheProvider<>(typeReadState, typeAPI, factory, previous, true);

        assertEquals(a0, current.getHollowObject(0));
        assertEquals(a1, current.getHollowObject(1));
        // the before image of the removed record survives one cycle for change listeners
        assertEquals(a2, current.getHollowObject(2));
    }

    @Test
    public void rotation_repointsRetainedRemovedOrdinalToCurrentTypeApi() {
        @SuppressWarnings("unchecked")
        HollowFactory<HollowRecord> recordFactory = mock(HollowFactory.class);
        HollowRecord record = mock(HollowRecord.class);
        HollowCachedDelegate delegate = mock(HollowCachedDelegate.class);
        when(record.getDelegate()).thenReturn(delegate);
        when(recordFactory.newCachedHollowObject(typeReadState, typeAPI, 0)).thenReturn(record);

        // previous cycle: ordinal 0 populated
        populatedOrdinalListener.addedOrdinal(0);
        HollowObjectCacheProvider<HollowRecord> previous =
                new HollowObjectCacheProvider<>(typeReadState, typeAPI, recordFactory);

        // delta: ordinal 0 removed
        removeOrdinals(0);

        // rotate onto a new type api; the retained before image must be repointed to it so
        // that lazy reference fields resolve against the current read state
        HollowTypeAPI newTypeAPI = mock(HollowTypeAPI.class);
        HollowObjectCacheProvider<HollowRecord> current =
                new HollowObjectCacheProvider<>(typeReadState, newTypeAPI, recordFactory, previous, true);

        assertEquals(record, current.getHollowObject(0));
        verify(delegate).updateTypeAPI(newTypeAPI);
    }

    @Test
    public void rotation_retainedRemovedOrdinalIsDroppedAfterOneCycle() {
        TypeA a0 = typeA(0);
        TypeA a1 = typeA(1);
        TypeA a2 = typeA(2);
        TypeA a3 = typeA(3);
        prepopulate(a0, a1, a2, a3);
        HollowObjectCacheProvider<TypeA> v1 =
                new HollowObjectCacheProvider<>(typeReadState, typeAPI, factory);

        // cycle 1: ordinal 2 removed -> retained as the before image
        removeOrdinals(2);
        HollowObjectCacheProvider<TypeA> v2 =
                new HollowObjectCacheProvider<>(typeReadState, typeAPI, factory, v1, true);
        assertEquals(a2, v2.getHollowObject(2));

        // cycle 2: ordinal 2 stays removed -> retention window has elapsed, before image is gone
        // (ordinal 3 keeps the array sized so the slot is a hole rather than out of bounds)
        advanceCycleWithNoChanges();
        HollowObjectCacheProvider<TypeA> v3 =
                new HollowObjectCacheProvider<>(typeReadState, typeAPI, factory, v2, true);
        assertNull(v3.getHollowObject(2));
    }

    @Test
    public void rotation_reusedOrdinalReturnsFreshRecordNotStaleBeforeImage() {
        TypeA a0 = typeA(0);
        TypeA a1 = typeA(1);
        TypeA a2Old = typeA(2);
        prepopulate(a0, a1, a2Old);
        HollowObjectCacheProvider<TypeA> v1 =
                new HollowObjectCacheProvider<>(typeReadState, typeAPI, factory);

        // cycle 1: ordinal 2 removed -> retained as the before image
        removeOrdinals(2);
        HollowObjectCacheProvider<TypeA> v2 =
                new HollowObjectCacheProvider<>(typeReadState, typeAPI, factory, v1, true);
        assertEquals(a2Old, v2.getHollowObject(2));

        // cycle 2: ordinal 2 is reused for a different record -> the fresh record wins, not the before image
        TypeA a2New = typeA(2);
        addOrdinals(2);
        HollowObjectCacheProvider<TypeA> v3 =
                new HollowObjectCacheProvider<>(typeReadState, typeAPI, factory, v2, true);

        assertEquals(a2New, v3.getHollowObject(2));
        assertNotSame(a2Old, v3.getHollowObject(2));
    }

    @Test
    public void retainEnabled_withNoPreviousCache_behavesNormally() {
        TypeA a0 = typeA(0);
        TypeA a1 = typeA(1);
        prepopulate(a0, a1);

        HollowObjectCacheProvider<TypeA> provider =
                new HollowObjectCacheProvider<>(typeReadState, typeAPI, factory, null, true);

        assertEquals(a0, provider.getHollowObject(0));
        assertEquals(a1, provider.getHollowObject(1));
    }

    private void prepopulate(TypeA...population) {
        for (TypeA a : population)
            populatedOrdinalListener.addedOrdinal(a.ordinal);
    }

    private void removeOrdinals(int...removed) {
        populatedOrdinalListener.beginUpdate();
        for (int ordinal : removed)
            populatedOrdinalListener.removedOrdinal(ordinal);
        populatedOrdinalListener.endUpdate();
    }

    private void addOrdinals(int...added) {
        populatedOrdinalListener.beginUpdate();
        for (int ordinal : added)
            populatedOrdinalListener.addedOrdinal(ordinal);
        populatedOrdinalListener.endUpdate();
    }

    private void advanceCycleWithNoChanges() {
        populatedOrdinalListener.beginUpdate();
        populatedOrdinalListener.endUpdate();
    }

    private void notifyAdded(TypeA...added) {
        subject.get().beginUpdate();
        for (TypeA a : added)
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
