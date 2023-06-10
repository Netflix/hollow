package com.netflix.hollow.core.memory.encoding;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class EncodedByteBufferTest {

    @Test
    public void growUsingBuffer() throws IOException  {
        File f = new File("/tmp/growme");
        RandomAccessFile raf = new RandomAccessFile(f, "rws");
        FileChannel channel = raf.getChannel();

        BlobByteBuffer buffer = BlobByteBuffer.mmapBlob(channel, 1024 * 1024);
        System.out.println("Original size: " + buffer.getChannel().size());
        buffer.putByte(0, new Byte("0"));
        buffer.putByte(1, new Byte("1"));

        raf.setLength(10);
        BlobByteBuffer newBuffer = BlobByteBuffer.mmapBlob(channel, 1024 * 1024);

        System.out.println("New size: " + buffer.getChannel().size());
        newBuffer.putByte(8, new Byte("0"));
        newBuffer.putByte(9, new Byte("1"));
    }

    @Test
    public void growUsingFile() throws IOException {
        byte[] bytes = new byte[2];
        bytes[0] = new Byte("1");
        bytes[1] = new Byte("0");

        File f = new File("/tmp/writeMe");
        RandomAccessFile raf = new RandomAccessFile(f, "rws");
        raf.writeByte(0);
        raf.writeByte(1);
        System.out.println("Old size: " + raf.length());
        System.out.println("Position before resize: " + raf.getFilePointer());

        raf.setLength(10);
        System.out.println("Position after resize: " + raf.getFilePointer());
        raf.seek(8);

        raf.write(bytes, 0, 2) ;

        System.out.println("New size: " + raf.length());
        FileChannel channel = raf.getChannel();
        BlobByteBuffer buffer = BlobByteBuffer.mmapBlob(channel, 1024 * 1024);
        System.out.println("Mapped buffer size: " + buffer.getChannel().size());
    }

    @Test
    public void testOrderedCopyToRaf() {
        // MappedByteBuffer bulk read bytes
        //  public ByteBuffer get(byte[] dst, int offset, int length) {
        // Raf build write bytes
        //  public void write(byte b[], int off, int len) throws IOException {
        MappedByteBuffer mappedByteBuffer;
        byte[] tmp;
        // mappedByteBuffer.get(tmp, 1, 4);

        RandomAccessFile raf;
        // raf.write(tmp, 1, 4);

    }
}
