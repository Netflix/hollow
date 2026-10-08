package com.netflix.hollow.api.perfapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowListOrdinalIterator;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import org.junit.Test;

public class HollowPerfBackedListTest {

    @Test
    public void usesGetBasedTraversalWithoutOptIn() {
        HollowListTypePerfAPI typeAPI = mock(HollowListTypePerfAPI.class);
        HollowListTypeDataAccess dataAccess = mock(HollowListTypeDataAccess.class);
        when(typeAPI.typeAccess()).thenReturn(dataAccess);
        when(dataAccess.size(7)).thenReturn(2);
        when(dataAccess.getElementOrdinal(7, 0)).thenReturn(3);
        when(dataAccess.getElementOrdinal(7, 1)).thenReturn(5);
        when(dataAccess.ordinalIterator(7)).thenAnswer(invocation ->
                new HollowListOrdinalIterator(7, dataAccess));
        HollowPerfBackedList<Long> list = new HollowPerfBackedList<>(typeAPI, 7, ref -> ref);

        assertEquals(Long.valueOf(3), list.iterator().next());
        assertEquals(Long.valueOf(5), list.listIterator(1).next());
        assertEquals(java.util.Arrays.asList(3L, 5L), list.stream().collect(java.util.stream.Collectors.toList()));
        verify(dataAccess, never()).ordinalIterator(7);
    }

    @Test
    public void listIteratorAndSpliteratorUseOrdinalCursor() {
        HollowListTypePerfAPI typeAPI = mock(HollowListTypePerfAPI.class);
        HollowListTypeDataAccess dataAccess = enabledDataAccess();
        when(typeAPI.typeAccess()).thenReturn(dataAccess);
        when(dataAccess.size(7)).thenReturn(2);
        when(dataAccess.getElementOrdinal(7, 0)).thenReturn(3);
        when(dataAccess.getElementOrdinal(7, 1)).thenReturn(5);
        when(dataAccess.ordinalIterator(7)).thenAnswer(invocation ->
                new HollowListOrdinalIterator(7, dataAccess));
        HollowPerfBackedList<Long> list = new HollowPerfBackedList<>(typeAPI, 7, ref -> ref);

        ListIterator<Long> iterator = list.listIterator(1);
        clearInvocations(dataAccess);
        assertEquals(Long.valueOf(3), iterator.previous());
        assertEquals(Long.valueOf(3), iterator.next());
        assertEquals(Long.valueOf(5), iterator.next());
        assertFalse(iterator.hasNext());
        verify(dataAccess, never()).size(7);

        Spliterator<Long> suffix = list.spliterator();
        clearInvocations(dataAccess);
        Spliterator<Long> prefix = suffix.trySplit();
        assertEquals(1, prefix.estimateSize());
        assertEquals(1, suffix.estimateSize());
        verify(dataAccess, never()).size(7);
        verify(dataAccess, never()).getElementOrdinal(7, 0);
        verify(dataAccess, never()).getElementOrdinal(7, 1);
        assertTrue(prefix.tryAdvance(value -> assertEquals(Long.valueOf(3), value)));
        assertTrue(suffix.tryAdvance(value -> assertEquals(Long.valueOf(5), value)));
        assertFalse(suffix.tryAdvance(value -> { }));
    }

    @Test
    public void subclassRetainsItsGetBasedTraversal() {
        HollowListTypePerfAPI typeAPI = mock(HollowListTypePerfAPI.class);
        HollowListTypeDataAccess dataAccess = enabledDataAccess();
        when(typeAPI.typeAccess()).thenReturn(dataAccess);
        when(dataAccess.size(7)).thenReturn(1);
        when(dataAccess.getElementOrdinal(7, 0)).thenReturn(3);
        when(dataAccess.ordinalIterator(7)).thenAnswer(invocation ->
                new HollowListOrdinalIterator(7, dataAccess));
        HollowPerfBackedList<Long> list = new HollowPerfBackedList<Long>(typeAPI, 7, ref -> ref) {
            @Override
            public Long get(int index) {
                return 99L;
            }
        };
        java.util.List<Long> expected = Collections.singletonList(99L);
        assertEquals(Long.valueOf(99), list.get(0));
        assertEquals(Long.valueOf(99), list.listIterator().next());
        assertEquals(Long.valueOf(99), list.iterator().next());
        assertTrue(list.spliterator().tryAdvance(value -> assertEquals(Long.valueOf(99), value)));
        assertEquals(expected, list.stream().collect(java.util.stream.Collectors.toList()));
        assertEquals(expected, list.parallelStream().collect(java.util.stream.Collectors.toList()));
        assertEquals(expected, new java.util.ArrayList<>(list));
        assertEquals(expected, list);
        assertEquals(list, expected);
        assertEquals(expected.hashCode(), list.hashCode());
        assertEquals(expected.toString(), list.toString());
        verify(dataAccess, never()).ordinalIterator(7);
    }

    @Test
    public void iteratesUsingOrdinalIterator() {
        HollowListTypePerfAPI typeAPI = mock(HollowListTypePerfAPI.class);
        HollowListTypeDataAccess dataAccess = enabledDataAccess();
        HollowOrdinalIterator ordinalIterator = mock(HollowOrdinalIterator.class);
        when(typeAPI.typeAccess()).thenReturn(dataAccess);
        when(dataAccess.ordinalIterator(7)).thenReturn(ordinalIterator);
        when(ordinalIterator.next()).thenReturn(3, 5, HollowOrdinalIterator.NO_MORE_ORDINALS);

        HollowPerfBackedList<Long> list = new HollowPerfBackedList<>(typeAPI, 7, ref -> ref);

        Iterator<Long> iterator = list.iterator();
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertEquals(Long.valueOf(3), iterator.next());
        assertEquals(Long.valueOf(5), iterator.next());
        assertFalse(iterator.hasNext());
        verify(dataAccess).ordinalIterator(7);
    }

    @Test
    public void emptyCapturedCursorUsesJdkAdaptersWithoutAnotherSizeLookup() {
        HollowListTypePerfAPI typeAPI = mock(HollowListTypePerfAPI.class);
        HollowListTypeDataAccess dataAccess = enabledDataAccess();
        when(typeAPI.typeAccess()).thenReturn(dataAccess);
        HollowListOrdinalIterator cursor = new HollowListOrdinalIterator(7, dataAccess);
        when(dataAccess.ordinalIterator(7)).thenReturn(cursor);
        HollowPerfBackedList<Long> list = new HollowPerfBackedList<>(typeAPI, 7, ref -> {
            throw new AssertionError("Empty traversal must not instantiate an element");
        });
        clearInvocations(dataAccess);

        assertSame(Collections.emptyIterator(), list.iterator());
        assertSame(Collections.emptyListIterator(), list.listIterator());
        assertSame(Spliterators.emptySpliterator(), list.spliterator());
        assertThatThrownBy(() -> list.listIterator(-1)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> list.listIterator(1)).isInstanceOf(IndexOutOfBoundsException.class);
        verify(dataAccess, never()).size(7);
        verify(dataAccess, never()).getElementOrdinal(7, 0);
    }

    private HollowListTypeDataAccess enabledDataAccess() {
        HollowListTypeReadState type = mock(HollowListTypeReadState.class);
        HollowReadStateEngine engine = new HollowReadStateEngine();
        engine.setShardCursorIterators(true);
        when(type.getStateEngine()).thenReturn(engine);
        return type;
    }
}
