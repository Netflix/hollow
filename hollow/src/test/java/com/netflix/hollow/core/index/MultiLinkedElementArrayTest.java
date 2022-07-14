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
package com.netflix.hollow.core.index;

import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import org.junit.Assert;
import org.junit.Test;

public class MultiLinkedElementArrayTest {

    @Test
    public void testIterators() {
        MultiLinkedElementArray arr = new MultiLinkedElementArray(WastefulRecycler.SMALL_ARRAY_RECYCLER);

        Assert.assertEquals(0, arr.newList());

        arr.add(0, 100);
        arr.add(0, 200);
        arr.add(0, 300);

        Assert.assertEquals(1, arr.newList());

        arr.add(1, 101);

        Assert.assertEquals(2, arr.newList());

        arr.add(2, 102);

        Assert.assertEquals(3, arr.newList());

        arr.add(0, 400);
        arr.add(0, 500);

        arr.add(2, 202);

        arr.add(3, 103);
        arr.add(3, 203);
        arr.add(3, 303);

        Assert.assertEquals(4, arr.newList());

        arr.add(4, 0);

        Assert.assertEquals(5, arr.newList());

        arr.add(5, 0);
        arr.add(5, 0);

        Assert.assertEquals(6, arr.newList());

        arr.add(6, 0);
        arr.add(6, 0);
        arr.add(6, 0);

        arr.add(0, 600);

        assertIteratorContents(arr.iterator(0), 600, 500, 400, 300, 200, 100);
        assertIteratorContents(arr.iterator(1), 101);
        assertIteratorContents(arr.iterator(2), 202, 102);
        assertIteratorContents(arr.iterator(3), 303, 203, 103);
        assertIteratorContents(arr.iterator(4), 0);
        assertIteratorContents(arr.iterator(5), 0, 0);
        assertIteratorContents(arr.iterator(6), 0, 0, 0);
    }

    private void assertIteratorContents(HollowOrdinalIterator iter, int... expectedOrdinals) {
        for(int i = 0; i < expectedOrdinals.length; i++) {
            Assert.assertEquals(expectedOrdinals[i], iter.next());
        }

        Assert.assertEquals(HollowOrdinalIterator.NO_MORE_ORDINALS, iter.next());
    }

}
