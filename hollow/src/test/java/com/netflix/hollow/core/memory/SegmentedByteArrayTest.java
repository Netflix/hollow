package com.netflix.hollow.core.memory;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import org.junit.Test;

public class SegmentedByteArrayTest {

    @Test
    public void readsVIntsBySegment() {
        SegmentedByteArray data = new SegmentedByteArray(WastefulRecycler.SMALL_ARRAY_RECYCLER);
        String[] values = {
                "",
                "ascii",
                "characters spanning more than one deliberately small segment",
                "\u0080",
                "a\u123eb",
                "\uffff\u007f\u0080",
                "mixed ascii and unicode \u123e across several segments \uffff"
        };

        for(int start = 0; start < 96; start++) {
            for(String value : values) {
                byte[] encoded = encode(value);
                for(int i = 0; i < encoded.length; i++)
                    data.set(start + i, encoded[i]);

                char[] decoded = new char[encoded.length];
                int count = data.readVIntsInto(start, encoded.length, decoded);

                assertEquals(value, new String(decoded, 0, count));
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
