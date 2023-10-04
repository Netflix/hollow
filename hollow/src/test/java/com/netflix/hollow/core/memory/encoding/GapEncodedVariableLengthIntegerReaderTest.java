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

import static com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader.EMPTY_READER;
import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.memory.ByteDataArray;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import java.util.function.Supplier;
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

        Assert.assertTrue(EMPTY_READER.isEmpty());
    }

    @Test
    public void testCombine() {
        GapEncodedVariableLengthIntegerReader reader1 = reader(1, 10, 100, 105, 107, 200);
        GapEncodedVariableLengthIntegerReader reader2 = reader(5, 76, 100, 102, 109, 197, 198, 199, 200, 201);

        GapEncodedVariableLengthIntegerReader combined = GapEncodedVariableLengthIntegerReader.combine(reader1, reader2, WastefulRecycler.SMALL_ARRAY_RECYCLER);

        assertValues(combined, 1, 5, 10, 76, 100, 102, 105, 107, 109, 197, 198, 199, 200, 201);
    }

    @Test
    public void testJoin() {
        GapEncodedVariableLengthIntegerReader[] from = new GapEncodedVariableLengthIntegerReader[2];
        from[0] = reader(1, 10, 100, 105, 107, 200);
        from[1] = reader(5, 76, 100, 102, 109, 200, 201);
        GapEncodedVariableLengthIntegerReader joined = GapEncodedVariableLengthIntegerReader.join(from);
        assertValues(joined, 2, 11, 20, 153, 200, 201, 205, 210, 214, 219, 400, 401, 403);

        GapEncodedVariableLengthIntegerReader[] from4 = new GapEncodedVariableLengthIntegerReader[4];
        from4[0] = reader(0, 1, 2, 3);
        from4[1] = null;
        from4[2] = reader(3);
        from4[3] = EMPTY_READER;
        GapEncodedVariableLengthIntegerReader joined4 = GapEncodedVariableLengthIntegerReader.join(from4);
        assertValues(joined4, 0, 4, 8, 12, 14); // splitOrdinal 3, in splitIndex 2 of numSplits 4 => 3*4+2

        GapEncodedVariableLengthIntegerReader[] empties = new GapEncodedVariableLengthIntegerReader[] {EMPTY_READER, EMPTY_READER};
        GapEncodedVariableLengthIntegerReader joinedEmpties = GapEncodedVariableLengthIntegerReader.join(empties);
        assertEquals(EMPTY_READER, joinedEmpties);

        GapEncodedVariableLengthIntegerReader[] nulls = new GapEncodedVariableLengthIntegerReader[] {null, null};
        GapEncodedVariableLengthIntegerReader joinedNulls = GapEncodedVariableLengthIntegerReader.join(nulls);
        assertEquals(EMPTY_READER, joinedNulls);

        GapEncodedVariableLengthIntegerReader[] from1 = new GapEncodedVariableLengthIntegerReader[1];
        from1[0] = reader(1, 10, 100, 105, 107, 200);
        GapEncodedVariableLengthIntegerReader joined1 = GapEncodedVariableLengthIntegerReader.join(from1);
        assertValues(joined1, 1, 10, 100, 105, 107, 200);

        assertIllegalStateException(() -> GapEncodedVariableLengthIntegerReader.join(null));
    }

    @Test
    public void testSplit() {
        GapEncodedVariableLengthIntegerReader reader = reader(1, 10, 100, 105, 107, 200);

        GapEncodedVariableLengthIntegerReader[] splitBy2 = reader.split(2);
        assertEquals(2, splitBy2.length);
        assertValues(splitBy2[0], 5, 50, 100); // (split[i]*numSplits + i) is the original ordinal
        assertValues(splitBy2[1], 0, 52, 53);

        GapEncodedVariableLengthIntegerReader[] splitBy256 = reader.split(256);
        assertEquals(256, splitBy256.length);
        assertValues(splitBy256[1], 0);
        assertValues(splitBy256[200], 0);
        assertEquals(EMPTY_READER, splitBy256[0]);
        assertEquals(EMPTY_READER, splitBy256[255]);

        GapEncodedVariableLengthIntegerReader[] splitBy2Empty = EMPTY_READER.split(2);
        assertEquals(2, splitBy2Empty.length);
        assertEquals(EMPTY_READER, splitBy2Empty[0]);
        assertEquals(EMPTY_READER, splitBy2Empty[1]);

        assertIllegalStateException(() ->reader.split(0));
        assertIllegalStateException(() -> reader.split(3));
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
            assertEquals(expectedValues[i], reader.nextElement());
            reader.advance();
        }

        assertEquals(Integer.MAX_VALUE, reader.nextElement());
    }

    private void assertIllegalStateException(Supplier<?> invocation) {
        try {
            invocation.get();
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }
}
