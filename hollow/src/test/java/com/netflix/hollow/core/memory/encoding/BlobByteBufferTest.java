package com.netflix.hollow.core.memory.encoding;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import org.junit.Test;

public class BlobByteBufferTest {

    @Test
    public void copiesAcrossMappedBufferBoundaries() throws Exception {
        byte[] data = new byte[100];
        for(int i = 0; i < data.length; i++)
            data[i] = (byte)(i * 31);

        File file = File.createTempFile("blob-byte-buffer", ".bin");
        file.deleteOnExit();
        try(FileOutputStream out = new FileOutputStream(file)) {
            out.write(data);
        }

        byte[] destination = new byte[84];
        try(FileInputStream in = new FileInputStream(file);
            FileChannel channel = in.getChannel()) {
            BlobByteBuffer buffer = BlobByteBuffer.mmapBlob(channel, 16);
            buffer.copyTo(10, destination, 2, 80);
        }

        byte[] expected = new byte[84];
        System.arraycopy(data, 10, expected, 2, 80);
        assertArrayEquals(expected, destination);
    }
}
