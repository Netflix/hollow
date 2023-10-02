/*
 *  Copyright 2021 Netflix, Inc.
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

import com.netflix.hollow.core.memory.ByteDataArray;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import org.junit.Assert;
import org.junit.Test;

public class GapEncodedVariableLengthIntegerReaderTest {

    @Test
    public void returnsValues() {
        GapEncodedVariableLengthIntegerReader reader = reader(1, 10, 100, 105, 107, 200);

        assertValues(reader, 1, 10, 100, 105, 107, 200);
        reader.reset();
        assertValues(reader, 1, 10, 100, 105, 107, 200);
    }

    @Test
    public void testEmpty() {
        GapEncodedVariableLengthIntegerReader reader = reader(20);
        Assert.assertFalse(reader.isEmpty());

        reader = reader();
        Assert.assertTrue(reader.isEmpty());

        Assert.assertTrue(GapEncodedVariableLengthIntegerReader.EMPTY_READER.isEmpty());
    }

    @Test
    public void testCombine() {
        GapEncodedVariableLengthIntegerReader reader1 = reader(1, 10, 100, 105, 107, 200);
        GapEncodedVariableLengthIntegerReader reader2 = reader(5, 76, 100, 102, 109, 197, 198, 199, 200, 201);

        GapEncodedVariableLengthIntegerReader combined = GapEncodedVariableLengthIntegerReader.combine(reader1, reader2, WastefulRecycler.SMALL_ARRAY_RECYCLER);

        assertValues(combined, 1, 5, 10, 76, 100, 102, 105, 107, 109, 197, 198, 199, 200, 201);
    }

    private GapEncodedVariableLengthIntegerReader reader(int... values) {
        ByteDataArray arr = new ByteDataArray(WastefulRecycler.SMALL_ARRAY_RECYCLER);

        int cur = 0;
        for(int i=0;i<values.length;i++) {
            VarInt.writeVInt(arr, values[i] - cur);
            cur = values[i];
        }

        return new GapEncodedVariableLengthIntegerReader(arr.getUnderlyingArray(), (int) arr.length());
    }

    private void assertValues(GapEncodedVariableLengthIntegerReader reader, int... expectedValues) {
        for(int i=0;i<expectedValues.length;i++) {
            Assert.assertEquals(expectedValues[i], reader.nextElement());
            reader.advance();
        }

        Assert.assertEquals(Integer.MAX_VALUE, reader.nextElement());
    }

    // SNAP: TODO: test split and join here

}
