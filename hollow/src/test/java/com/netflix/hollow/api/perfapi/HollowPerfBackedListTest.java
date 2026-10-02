package com.netflix.hollow.api.perfapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import java.util.Iterator;
import org.junit.Test;

public class HollowPerfBackedListTest {

    @Test
    public void iteratesUsingOrdinalIterator() {
        HollowListTypePerfAPI typeAPI = mock(HollowListTypePerfAPI.class);
        HollowListTypeDataAccess dataAccess = mock(HollowListTypeDataAccess.class);
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
}
