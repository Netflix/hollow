package com.netflix.hollow.core.memory.pool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ArraySegmentRecyclerTest {

    @Test
    public void recyclingRecyclerReportsArrayReuse() {
        assertTrue(new RecyclingRecycler().recyclesArrays());
    }

    @Test
    public void wastefulRecyclerReportsNoArrayReuse() {
        assertFalse(new WastefulRecycler(11, 8).recyclesArrays());
    }

}
