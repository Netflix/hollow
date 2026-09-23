package com.netflix.hollow.core.memory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import java.util.Arrays;
import org.junit.Test;

public class SegmentedByteArrayTest {

    @Test
    public void copiesToByteArrayBySegment() {
        SegmentedByteArray data = new SegmentedByteArray(WastefulRecycler.SMALL_ARRAY_RECYCLER);
        byte[] expected = new byte[96];
        for(int i = 0; i < expected.length; i++) {
            expected[i] = (byte)(i * 31);
            data.set(i, expected[i]);
        }

        for(int start = 0; start < expected.length; start++) {
            int length = expected.length - start;
            byte[] destination = new byte[length + 4];
            data.copyTo(start, destination, 2, length);

            byte[] actual = new byte[length];
            System.arraycopy(destination, 2, actual, 0, length);
            byte[] expectedRange = new byte[length];
            System.arraycopy(expected, start, expectedRange, 0, length);
            assertArrayEquals(expectedRange, actual);
        }
    }

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

    @Test
    public void readsContiguousAsciiAndMixedStrings() {
        SegmentedByteArray data = new SegmentedByteArray(new WastefulRecycler(10, 4));
        char[] characters = new char[256];
        Arrays.fill(characters, 'a');
        assertContiguousString(data, new String(characters));

        int[] positions = {0, 1, 7, 15, 16, 31, 32, 63, 64, 127, 128, 255};
        char[] nonAsciiCharacters = {'\u0080', '\u123e', '\uffff', '\ud83d', '\ude00'};
        for(int position : positions) {
            for(char character : nonAsciiCharacters) {
                characters[position] = character;
                assertContiguousString(data, new String(characters));
            }
            characters[position] = 'a';
        }
        assertContiguousString(data, "ascii \ud83d\ude00 suffix");
    }

    private void assertContiguousString(SegmentedByteArray data, String value) {
        byte[] encoded = encode(value);
        int start = 7;
        for(int i = 0; i < encoded.length; i++)
            data.set(start + i, encoded[i]);

        char[] decoded = new char[encoded.length];
        Arrays.fill(decoded, '\uffff');
        assertEquals(value, data.readVIntString(start, encoded.length, decoded));
        assertTrue(data.isVIntStringEqual(start, encoded.length, value));
        assertFalse(data.isVIntStringEqual(start, encoded.length, value + "x"));
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
