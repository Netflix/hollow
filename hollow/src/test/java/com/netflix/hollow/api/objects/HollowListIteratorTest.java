package com.netflix.hollow.api.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.netflix.hollow.api.objects.delegate.HollowListCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import java.util.Iterator;
import org.junit.Test;

public class HollowListIteratorTest {

    @Test
    public void iteratesUsingOrdinalIterator() {
        HollowListTypeDataAccess dataAccess = mock(HollowListTypeDataAccess.class);
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
        HollowListTypeDataAccess dataAccess = mock(HollowListTypeDataAccess.class);
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
