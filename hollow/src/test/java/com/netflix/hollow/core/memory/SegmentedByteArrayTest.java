package com.netflix.hollow.core.memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import org.junit.Test;

public class SegmentedByteArrayTest {

    @Test
    public void readsVIntsBySegment() {
        SegmentedByteArray data = new SegmentedByteArray(WastefulRecycler.SMALL_ARRAY_RECYCLER);
        String[] values = {
                "",
                "\u0000",
                "ascii",
                "characters spanning more than one deliberately small segment",
                "\u0080",
                "a\u123eb",
                "\uffff\u007f\u0080",
                "surrogate pair \ud83d\ude00",
                "mixed ascii and unicode \u123e across several segments \uffff"
        };

        for(int start = 0; start < 96; start++) {
            for(String value : values) {
                byte[] encoded = encode(value);
                for(int i = 0; i < encoded.length; i++)
                    data.set(start + i, encoded[i]);

                char[] decoded = new char[encoded.length];
                assertEquals(value, data.readVIntString(start, encoded.length, decoded));
                assertTrue(data.isVIntStringEqual(start, encoded.length, value));
                assertFalse(data.isVIntStringEqual(start, encoded.length, value + "x"));
                if(!value.isEmpty()) {
                    char replacement = value.charAt(0) == 'x' ? 'y' : 'x';
                    assertFalse(data.isVIntStringEqual(start, encoded.length,
                            replacement + value.substring(1)));
                    assertFalse(data.isVIntStringEqual(start, encoded.length,
                            value.substring(0, value.length() - 1)));
                }
            }
        }
    }

    private static byte[] encode(String value) {
        int length = 0;
        for(int i = 0; i < value.length(); i++)
            length += VarInt.sizeOfVInt(value.charAt(i));

        byte[] encoded = new byte[length];
        int position = 0;
        for(int i = 0; i < value.length(); i++)
            position = VarInt.writeVInt(encoded, position, value.charAt(i));
        return encoded;
    }
}
