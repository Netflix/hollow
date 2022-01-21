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
package com.netflix.hollow.core.util;

import java.util.BitSet;
import org.junit.Assert;
import org.junit.Test;

public class RemovedOrdinalIteratorTest {

    @Test
    public void iteratesOverRemovedOrdinals() {
        BitSet previousOrdinals = bitSet(1, 2, 3, 4, 6, 7, 9, 10);
        BitSet currentOrdinals = bitSet(1, 3, 4, 5, 7, 8, 9);

        RemovedOrdinalIterator iter = new RemovedOrdinalIterator(previousOrdinals, currentOrdinals);

        Assert.assertEquals(2, iter.next());
        Assert.assertEquals(6, iter.next());
        Assert.assertEquals(10, iter.next());
        Assert.assertEquals(-1, iter.next());
    }

    @Test
    public void iteratesOverRemovedOrdinalsReverse() {
        BitSet previousOrdinals = bitSet(1, 2, 3, 4, 6, 10);
        BitSet currentOrdinals = bitSet(1, 3, 4, 5, 7, 8, 9);

        RemovedOrdinalIterator iter = new RemovedOrdinalIterator(previousOrdinals, currentOrdinals);

        Assert.assertEquals(2, iter.next());
        Assert.assertEquals(6, iter.next());
        Assert.assertEquals(10, iter.next());
        Assert.assertEquals(-1, iter.next());
    }

    @Test
    public void iteratesOverAddedOrdinals() {
        BitSet previousOrdinals = bitSet(1, 2, 3, 4, 6, 7, 9, 10);
        BitSet currentOrdinals = bitSet(1, 3, 4, 5, 7, 8, 9);

        RemovedOrdinalIterator iter = new RemovedOrdinalIterator(currentOrdinals, previousOrdinals);

        Assert.assertEquals(5, iter.next());
        Assert.assertEquals(8, iter.next());
        Assert.assertEquals(-1, iter.next());
    }

    @Test
    public void iteratesOverAddedOrdinalsReverse() {
        BitSet previousOrdinals = bitSet(1, 2, 3, 4, 6);
        BitSet currentOrdinals = bitSet(1, 3, 4, 5, 7, 8, 9);

        RemovedOrdinalIterator iter = new RemovedOrdinalIterator(currentOrdinals, previousOrdinals);

        Assert.assertEquals(5, iter.next());
        Assert.assertEquals(7, iter.next());
        Assert.assertEquals(8, iter.next());
        Assert.assertEquals(9, iter.next());
        Assert.assertEquals(-1, iter.next());
    }

    private BitSet bitSet(int... setBits) {
        BitSet bitSet = new BitSet();
        for(int bit : setBits) {
            bitSet.set(bit);
        }
        return bitSet;
    }

}
