package com.netflix.hollow.api.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.netflix.hollow.api.custom.HollowListTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowListCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.disabled.HollowListDisabledDataAccess;
import com.netflix.hollow.core.read.dataaccess.proxy.HollowListProxyDataAccess;
import com.netflix.hollow.core.read.dataaccess.proxy.HollowProxyDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowListOrdinalIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import org.junit.Test;

public class HollowListIteratorTest {

    @Test
    public void usesGetBasedTraversalWithoutOptIn() {
        HollowListTypeDataAccess dataAccess = mock(HollowListTypeDataAccess.class);
        when(dataAccess.size(7)).thenReturn(2);
        when(dataAccess.getElementOrdinal(7, 0)).thenReturn(3);
        when(dataAccess.getElementOrdinal(7, 1)).thenReturn(5);
        when(dataAccess.ordinalIterator(7)).thenAnswer(invocation ->
                new HollowListOrdinalIterator(7, dataAccess));
        HollowList<Integer> list = list(new HollowListLookupDelegate<>(dataAccess));

        assertEquals(Integer.valueOf(3), list.iterator().next());
        assertEquals(Integer.valueOf(5), list.listIterator(1).next());
        List<Integer> values = new ArrayList<>();
        list.spliterator().forEachRemaining(values::add);
        assertEquals(Arrays.asList(3, 5), values);
        verify(dataAccess, never()).ordinalIterator(7);
    }

    @Test
    public void iteratesUsingOrdinalIterator() {
        HollowListTypeDataAccess dataAccess = enabledDataAccess();
        HollowOrdinalIterator ordinalIterator = mock(HollowOrdinalIterator.class);
        when(dataAccess.ordinalIterator(7)).thenReturn(ordinalIterator);
        when(ordinalIterator.next()).thenReturn(3, 5, HollowOrdinalIterator.NO_MORE_ORDINALS);

        HollowList<Integer> list = list(new HollowListLookupDelegate<>(dataAccess));

        Iterator<Integer> iterator = list.iterator();
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(3), iterator.next());
        assertEquals(Integer.valueOf(5), iterator.next());
        assertFalse(iterator.hasNext());
        verify(dataAccess).ordinalIterator(7);
    }

    @Test
    public void cachedListIteratesCachedOrdinals() {
        HollowListTypeDataAccess dataAccess = enabledDataAccess();
        when(dataAccess.size(7)).thenReturn(2);
        when(dataAccess.getElementOrdinal(7, 0)).thenReturn(3);
        when(dataAccess.getElementOrdinal(7, 1)).thenReturn(5);

        HollowList<Integer> list = list(new HollowListCachedDelegate<>(dataAccess, 7));

        Iterator<Integer> iterator = list.iterator();
        assertEquals(Integer.valueOf(3), iterator.next());
        assertEquals(Integer.valueOf(5), iterator.next());
        assertFalse(iterator.hasNext());
        verify(dataAccess, never()).ordinalIterator(7);
    }

    @Test
    public void listIteratorUsesCapturedBoundsInBothDirections() {
        HollowListTypeDataAccess dataAccess = enabledDataAccess();
        when(dataAccess.size(7)).thenReturn(2);
        when(dataAccess.getElementOrdinal(7, 0)).thenReturn(3);
        when(dataAccess.getElementOrdinal(7, 1)).thenReturn(5);
        when(dataAccess.ordinalIterator(7)).thenAnswer(invocation ->
                new HollowListOrdinalIterator(7, dataAccess));
        HollowList<Integer> list = list(new HollowListLookupDelegate<>(dataAccess));

        ListIterator<Integer> iterator = list.listIterator(1);
        clearInvocations(dataAccess);
        assertEquals(1, iterator.nextIndex());
        assertEquals(0, iterator.previousIndex());
        assertEquals(Integer.valueOf(3), iterator.previous());
        assertFalse(iterator.hasPrevious());
        assertThatThrownBy(iterator::previous).isInstanceOf(NoSuchElementException.class);
        assertEquals(Integer.valueOf(3), iterator.next());
        assertEquals(Integer.valueOf(5), iterator.next());
        assertFalse(iterator.hasNext());
        assertThatThrownBy(iterator::next).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(iterator::remove).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> iterator.set(9)).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> iterator.add(9)).isInstanceOf(UnsupportedOperationException.class);
        verify(dataAccess, never()).size(7);
        assertThatThrownBy(() -> list.listIterator(-1)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> list.listIterator(3)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void spliteratorSplitsRangesWithoutReadingElements() {
        HollowListTypeDataAccess dataAccess = enabledDataAccess();
        when(dataAccess.size(7)).thenReturn(2);
        when(dataAccess.getElementOrdinal(7, 0)).thenReturn(3);
        when(dataAccess.getElementOrdinal(7, 1)).thenReturn(5);
        when(dataAccess.ordinalIterator(7)).thenAnswer(invocation ->
                new HollowListOrdinalIterator(7, dataAccess));
        HollowList<Integer> list = list(new HollowListLookupDelegate<>(dataAccess));

        Spliterator<Integer> suffix = list.spliterator();
        clearInvocations(dataAccess);
        Spliterator<Integer> prefix = suffix.trySplit();
        assertEquals(1, prefix.estimateSize());
        assertEquals(1, suffix.estimateSize());
        assertTrue(suffix.hasCharacteristics(Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED));
        verify(dataAccess, never()).getElementOrdinal(7, 0);
        verify(dataAccess, never()).getElementOrdinal(7, 1);
        verify(dataAccess, never()).size(7);
        List<Integer> values = new ArrayList<>();
        prefix.forEachRemaining(values::add);
        suffix.forEachRemaining(values::add);
        assertEquals(Arrays.asList(3, 5), values);
        assertEquals(0, suffix.estimateSize());
        assertThatThrownBy(() -> suffix.tryAdvance(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void cachedListTraversalDoesNotReturnToLiveData() {
        HollowListTypeDataAccess dataAccess = enabledDataAccess();
        when(dataAccess.size(7)).thenReturn(2);
        when(dataAccess.getElementOrdinal(7, 0)).thenReturn(3);
        when(dataAccess.getElementOrdinal(7, 1)).thenReturn(5);
        HollowList<Integer> list = list(new HollowListCachedDelegate<>(dataAccess, 7));
        when(dataAccess.getElementOrdinal(7, 0)).thenReturn(99);
        clearInvocations(dataAccess);

        ListIterator<Integer> iterator = list.listIterator(2);
        assertEquals(Integer.valueOf(5), iterator.previous());
        assertEquals(Integer.valueOf(3), iterator.previous());
        List<Integer> values = new ArrayList<>();
        Spliterator<Integer> suffix = list.spliterator();
        suffix.trySplit().forEachRemaining(values::add);
        suffix.forEachRemaining(values::add);
        assertEquals(Arrays.asList(3, 5), values);
        verify(dataAccess, never()).size(7);
        verify(dataAccess, never()).getElementOrdinal(7, 0);
        verify(dataAccess, never()).ordinalIterator(7);
    }

    @Test
    public void lookupSubclassRetainsItsGetBasedListIterator() {
        HollowListTypeDataAccess dataAccess = enabledDataAccess();
        when(dataAccess.size(7)).thenReturn(1);
        when(dataAccess.getElementOrdinal(7, 0)).thenReturn(3);
        when(dataAccess.ordinalIterator(7)).thenAnswer(invocation ->
                new HollowListOrdinalIterator(7, dataAccess));
        HollowList<Integer> list = list(new HollowListLookupDelegate<Integer>(dataAccess) {
            @Override
            public Integer get(HollowList<Integer> list, int ordinal, int index) {
                return 99;
            }
        });
        assertEquals(Integer.valueOf(99), list.listIterator().next());
    }

    @Test
    public void proxyTraversalContinuesToUseTheProxyAfterSplitting() {
        HollowListTypeDataAccess original = enabledDataAccess();
        when(original.size(7)).thenReturn(2);
        HollowListProxyDataAccess proxy = new HollowListProxyDataAccess(new HollowProxyDataAccess());
        proxy.setCurrentDataAccess(original);
        HollowList<Integer> list = list(new HollowListLookupDelegate<>(proxy));
        ListIterator<Integer> iterator = list.listIterator();
        Spliterator<Integer> suffix = list.spliterator();
        Spliterator<Integer> prefix = suffix.trySplit();

        HollowListTypeDataAccess replacement = mock(HollowListTypeDataAccess.class);
        when(replacement.getElementOrdinal(7, 0)).thenReturn(9);
        when(replacement.getElementOrdinal(7, 1)).thenReturn(11);
        proxy.setCurrentDataAccess(replacement);
        assertEquals(Integer.valueOf(9), iterator.next());
        List<Integer> values = new ArrayList<>();
        prefix.forEachRemaining(values::add);
        suffix.forEachRemaining(values::add);
        assertEquals(Arrays.asList(9, 11), values);
        verify(replacement, never()).size(7);
    }

    @Test
    public void emptyCursorPreservesTraversalContracts() {
        HollowListTypeDataAccess dataAccess = enabledDataAccess();
        HollowList<Integer> list = list(new HollowListCachedDelegate<>(dataAccess, 7));
        clearInvocations(dataAccess);
        assertSame(Collections.emptyIterator(), list.iterator());
        ListIterator<Integer> iterator = list.listIterator();
        assertSame(Collections.emptyListIterator(), iterator);
        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasPrevious());
        assertEquals(0, iterator.nextIndex());
        assertEquals(-1, iterator.previousIndex());
        assertThatThrownBy(iterator::next).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(iterator::previous).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(iterator::remove).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> iterator.set(9)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> iterator.add(9)).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> list.listIterator(-1)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> list.listIterator(1)).isInstanceOf(IndexOutOfBoundsException.class);
        Spliterator<Integer> spliterator = list.spliterator();
        assertSame(Spliterators.emptySpliterator(), spliterator);
        assertEquals(0, spliterator.estimateSize());
        assertEquals(null, spliterator.trySplit());
        assertFalse(spliterator.tryAdvance(value -> { throw new AssertionError(); }));
        assertThatThrownBy(() -> spliterator.tryAdvance(null)).isInstanceOf(NullPointerException.class);
        verify(dataAccess, never()).size(7);
        verify(dataAccess, never()).getElementOrdinal(7, 0);
    }

    @Test
    public void cachedTraversalWorksAfterLiveDataAccessIsDisabled() {
        HollowListTypeDataAccess dataAccess = enabledDataAccess();
        when(dataAccess.size(7)).thenReturn(2);
        when(dataAccess.getElementOrdinal(7, 0)).thenReturn(3);
        when(dataAccess.getElementOrdinal(7, 1)).thenReturn(5);
        HollowListCachedDelegate<Integer> delegate = new HollowListCachedDelegate<>(dataAccess, 7);
        HollowList<Integer> list = list(delegate);
        HollowListTypeAPI replacement = mock(HollowListTypeAPI.class);
        when(replacement.getTypeDataAccess()).thenReturn(HollowListDisabledDataAccess.INSTANCE);
        delegate.updateTypeAPI(replacement);

        assertEquals(Integer.valueOf(3), list.iterator().next());
        assertEquals(Integer.valueOf(5), list.listIterator(2).previous());
        assertEquals(Arrays.asList(3, 5), list.stream().collect(java.util.stream.Collectors.toList()));
    }

    private HollowListTypeDataAccess enabledDataAccess() {
        HollowListTypeReadState type = mock(HollowListTypeReadState.class);
        HollowReadStateEngine engine = new HollowReadStateEngine();
        engine.setShardCursorIterators(true);
        when(type.getStateEngine()).thenReturn(engine);
        return type;
    }

    private HollowList<Integer> list(HollowListDelegate<Integer> delegate) {
        return new HollowList<Integer>(delegate, 7) {
            @Override
            public Integer instantiateElement(int elementOrdinal) {
                return elementOrdinal;
            }

            @Override
            public boolean equalsElement(int elementOrdinal, Object testObject) {
                return Integer.valueOf(elementOrdinal).equals(testObject);
            }
        };
    }
}
