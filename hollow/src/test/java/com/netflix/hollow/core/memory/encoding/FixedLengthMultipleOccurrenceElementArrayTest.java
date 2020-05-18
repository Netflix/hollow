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
package com.netflix.hollow.core.memory.encoding;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.junit.Before;
import org.junit.Test;

public class FixedLengthMultipleOccurrenceElementArrayTest {
    private FixedLengthMultipleOccurrenceElementArray array;

    @Before
    public void setUp() {
        array = new FixedLengthMultipleOccurrenceElementArray(
                WastefulRecycler.SMALL_ARRAY_RECYCLER, 10000L, 5, 4);
    }

    @Test
    public void testAddAndGet_returnsMultipleNoZero() {
        List<Long> elements = LongStream.range(1, 4).boxed().collect(Collectors.toList());
        elements.forEach(v -> array.addElement(0, v));
        assertEquals(elements, array.getElements(0));
    }

    @Test
    public void testAddAndGet_returnsMultipleWithZero() {
        List<Long> elements = LongStream.range(0, 3).boxed().collect(Collectors.toList());
        elements.forEach(v -> array.addElement(0, v));
        assertEquals(elements, array.getElements(0));
    }

    @Test
    public void testAddAndGet_resizes() {
        List<Long> elements = LongStream.range(1, 4).boxed().collect(Collectors.toList());
        elements.forEach(v -> array.addElement(0, v));
        assertEquals(elements, array.getElements(0));
    }

    @Test
    public void testAddAndGet_multipleNodes() {
        List<Long> values0 = LongStream.range(0, 4).boxed().collect(Collectors.toList());
        List<Long> values1 = LongStream.range(2, 15).boxed().collect(Collectors.toList());
        List<Long> values2 = LongStream.range(1, 2).boxed().collect(Collectors.toList());
        values0.forEach(v -> array.addElement(0, v));
        values1.forEach(v -> array.addElement(1, v));
        values2.forEach(v -> array.addElement(2, v));
        assertEquals(values0, array.getElements(0));
        assertEquals(values1, array.getElements(1));
        assertEquals(values2, array.getElements(2));
    }

    @Test
    public void testLargeNumberOfNodes() {
        LongStream.range(0, 10000).forEach(nodeIndex -> {
            List<Long> elements = LongStream.range(0, 31).boxed().collect(Collectors.toList());
            elements.forEach(ordinal -> array.addElement(nodeIndex, ordinal));
            assertEquals("nodeIndex " + nodeIndex + " should have correct elements", elements,
                    array.getElements(nodeIndex));
        });
    }
}
