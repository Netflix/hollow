package com.netflix.hollow.core.memory.encoding;

import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;

public class VarIntTest {
    private final static byte[] BYTES_EMPTY = new byte[]{};
    private final static byte[] BYTES_TRUNCATED = new byte[]{(byte) 0x81};
    private final static byte[] BYTES_VALUE_129 = new byte[]{(byte) 0x81, (byte) 0x01};

    @Test
    public void testReadVLongInputStream() throws IOException {
        InputStream is = new ByteArrayInputStream(BYTES_VALUE_129);

        Assert.assertEquals(129, VarInt.readVLong(is));
    }

    @Test(expected = EOFException.class)
    public void testReadVLongEmptyInputStream() throws IOException {
        InputStream is = new ByteArrayInputStream(BYTES_EMPTY);

        VarInt.readVLong(is);
    }

    @Test(expected = EOFException.class)
    public void testReadVLongTruncatedInputStream() throws IOException {
        InputStream is = new ByteArrayInputStream(BYTES_TRUNCATED);

        VarInt.readVLong(is);
    }

    @Test
    public void testReadVIntInputStream() throws IOException {
        InputStream is = new ByteArrayInputStream(BYTES_VALUE_129);

        Assert.assertEquals(129, VarInt.readVInt(is));
    }

    @Test(expected = EOFException.class)
    public void testReadVIntEmptyInputStream() throws IOException {
        InputStream is = new ByteArrayInputStream(BYTES_EMPTY);

        VarInt.readVInt(is);
    }

    @Test(expected = EOFException.class)
    public void testReadVIntTruncatedInputStream() throws IOException {
        InputStream is = new ByteArrayInputStream(BYTES_TRUNCATED);

        VarInt.readVInt(is);
    }

    @Test
    public void testReadVLongHollowBlobInput() throws IOException {
        HollowBlobInput hbi = HollowBlobInput.serial(BYTES_VALUE_129);

        Assert.assertEquals(129l, VarInt.readVLong(hbi));
    }

    @Test(expected = EOFException.class)
    public void testReadVLongEmptyHollowBlobInput() throws IOException {
        HollowBlobInput hbi = HollowBlobInput.serial(BYTES_EMPTY);

        VarInt.readVLong(hbi);
    }

    @Test(expected = EOFException.class)
    public void testReadVLongTruncatedHollowBlobInput() throws IOException {
        HollowBlobInput hbi = HollowBlobInput.serial(BYTES_TRUNCATED);

        VarInt.readVLong(hbi);
    }

    @Test
    public void testReadVIntHollowBlobInput() throws IOException {
        HollowBlobInput hbi = HollowBlobInput.serial(BYTES_VALUE_129);

        Assert.assertEquals(129l, VarInt.readVInt(hbi));
    }

    @Test(expected = EOFException.class)
    public void testReadVIntEmptyHollowBlobInput() throws IOException {
        HollowBlobInput hbi = HollowBlobInput.serial(BYTES_EMPTY);

        VarInt.readVInt(hbi);
    }

    @Test(expected = EOFException.class)
    public void testReadVIntTruncatedHollowBlobInput() throws IOException {
        HollowBlobInput hbi = HollowBlobInput.serial(BYTES_TRUNCATED);

        VarInt.readVInt(hbi);
    }
}
