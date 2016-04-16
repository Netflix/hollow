package com.netflix.vms.transformer.util;

import java.io.IOException;
import java.io.OutputStream;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;

// NOTE: Frozen code - do not change.  There is a cloned version in videometadata-snaphost library.  On next vip: need to use standard lz4 lib
public class LZ4VMSOutputStream extends OutputStream {
    private static final LZ4Factory lz4Factory = LZ4Factory.fastestInstance();
    private static final LZ4Compressor compressor = lz4Factory.fastCompressor();
    private static final int ONE_MEGABYTE = 1048576;
    private final byte[] compressionInputBuffer;
    private final byte[] compressionOutputBuffer;
    private final OutputStream underlyingOutputStream;
    private int bytesRemainingInCompressionInputBuffer = 0;
    private int currentCompressionInputBufferPosition = 0;

    public LZ4VMSOutputStream(OutputStream os) throws IOException {
        this(os, ONE_MEGABYTE);
    }

    public LZ4VMSOutputStream(OutputStream underlyingOutputStream, int blocksize) throws IOException {
        compressionInputBuffer = new byte[blocksize];
        this.underlyingOutputStream = underlyingOutputStream;
        this.bytesRemainingInCompressionInputBuffer = blocksize;
        this.currentCompressionInputBufferPosition = 0;
        this.compressionOutputBuffer = new byte[compressor.maxCompressedLength(blocksize)];
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (len <= bytesRemainingInCompressionInputBuffer) {
            System.arraycopy(b, off, compressionInputBuffer, currentCompressionInputBufferPosition, len);
            currentCompressionInputBufferPosition += len;
            bytesRemainingInCompressionInputBuffer -= len;
        } else {
            // len > bytesRemainingInCompressionInputBuffer
            while (len > 0) {
                int bytesToCopy = Math.min(bytesRemainingInCompressionInputBuffer, len);
                System.arraycopy(b, off, compressionInputBuffer, currentCompressionInputBufferPosition, bytesToCopy);
                currentCompressionInputBufferPosition += bytesToCopy;
                bytesRemainingInCompressionInputBuffer -= bytesToCopy;
                flush();
                len -= bytesToCopy;
                off += bytesToCopy;
            }
        }
    }

    @Override
    public void write(int i) throws IOException {
        byte b = (byte)i;
        if (0 == bytesRemainingInCompressionInputBuffer) {
            flush();
        }
        compressionInputBuffer[currentCompressionInputBufferPosition] = b;
        bytesRemainingInCompressionInputBuffer--;
        currentCompressionInputBufferPosition++;
    }

    @Override
    public void flush() throws IOException {
        if(currentCompressionInputBufferPosition > 0) {
            LZ4VMSStreamHelper.writeLength(currentCompressionInputBufferPosition, this.underlyingOutputStream);
            int bytesCompressed = compressor.compress(compressionInputBuffer, 0, currentCompressionInputBufferPosition, compressionOutputBuffer, 0, compressionOutputBuffer.length);
            LZ4VMSStreamHelper.writeLength(bytesCompressed, this.underlyingOutputStream);
            underlyingOutputStream.write(compressionOutputBuffer, 0, bytesCompressed);
            bytesRemainingInCompressionInputBuffer = compressionInputBuffer.length;
            currentCompressionInputBufferPosition = 0;
        }
    }

    @Override
    public void close() throws IOException {
        flush();
        underlyingOutputStream.close();
    }
}
