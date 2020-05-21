package com.netflix.hollow.core.read;

import static com.netflix.hollow.core.memory.MemoryMode.ON_HEAP;
import static com.netflix.hollow.core.memory.MemoryMode.SHARED_MEMORY_LAZY;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.encoding.BlobByteBuffer;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * This class provides an abstraction to help navigate between use of DataInputStream or RandomAccessFile
 * as the backing resource for Hollow Producer/Consumer Blob in order to work with the different memory modes.
 */
public class HollowBlobInput implements Closeable {
    private Object o;
    private BlobByteBuffer buffer;

    private HollowBlobInput() {}

    /**
     * Initialize the Hollow Blob Input object from the Hollow Consumer blob's Input Stream or Random Access File, depending on the configured memory mode
     * @param mode Configured memory mode
     * @param blob Hollow Consumer blob
     * @return the initialized Hollow Blob Input
     * @throws IOException if the Hollow Blob Input couldn't be initialized
     */
    public static HollowBlobInput modeBasedSelector(MemoryMode mode, HollowConsumer.Blob blob) throws IOException {
        if (mode.equals(ON_HEAP)) {
            return dataInputStream(blob.getInputStream());
        } else if (mode.equals(SHARED_MEMORY_LAZY)) {
            return randomAccessFile(blob.getFile());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    // SNAP: TODO: Comment about how returned handle must be closed
    public static HollowBlobInput randomAccessFile(File f) throws IOException {
        HollowBlobInput hbi = new HollowBlobInput();
        RandomAccessFile raf = new RandomAccessFile(f, "r");
        hbi.o = raf;
        FileChannel channel = ((RandomAccessFile) hbi.o).getChannel();
        hbi.buffer = BlobByteBuffer.mmapBlob(channel);
        return hbi;
    }

    public static HollowBlobInput dataInputStream(InputStream is) { // SNAP: TODO: Can everything be a RandomAccessFile?
        HollowBlobInput hbi = new HollowBlobInput();
        hbi.o = new DataInputStream(is);
        return hbi;
    }

    public BlobByteBuffer getBuffer() { // SNAP: TODO: Should I put a type check here? performance concerns?
        return buffer;
    }

    /**
     * Reads the next byte of data from the input stream by relaying the call to the underlying {@code DataInputStream} or
     * {@code RandomAccessFile}. The byte is returned as an integer in the range 0 to 255.
     *
     * @return an integer in the range 0 to 255
     * @throws IOException if underlying {@code DataInputStream} or {@code RandomAccessFile}
     * @throws UnsupportedOperationException if the input type wasn't  one of {@code DataInputStream} or {@code RandomAccessFile}
     */
    public int read() throws IOException {
        if (o instanceof RandomAccessFile) {
            return ((RandomAccessFile) o).read();
        } else if (o instanceof DataInputStream) {
            return ((DataInputStream) o).read();
        } else {
            throw new UnsupportedOperationException("Unknown Hollow Blob Input type");
        }
    }

    public int read(byte b[], int off, int len) throws IOException {
        if (o instanceof RandomAccessFile) {
            return ((RandomAccessFile) o).read(b, off, len);
        } else if (o instanceof DataInputStream) {
            return ((DataInputStream) o).read(b, off, len);
        } else {
            throw new UnsupportedOperationException("Unknown Hollow Blob Input type");
        }
    }

    /**
     * Sets the file-pointer to the desired offset measured from the beginning of the file by relaying the call to the
     * underlying {@code RandomAccessFile}. Operation not supported if the Hollow Blob Input is an {@code DataInputStream}.
     *
     * @param pos the position in bytes from the beginning of the file at which to set the file pointer to.
     * @exception IOException if originated in the underlying {@code RandomAccessFile} implementation
     * @exception UnsupportedOperationException if called when Hollow Blob Input is not a {@code RandomAccessFile}
     */
    public void seek(long pos) throws IOException {
        if (o instanceof RandomAccessFile) {
            ((RandomAccessFile) o).seek(pos);
        } else if (o instanceof DataInputStream) {
            throw new UnsupportedOperationException("Can not seek on Hollow Blob Input of type DataInputStream");
        } else {
            throw new UnsupportedOperationException("Unknown Hollow Blob Input type");
        }
    }

    public long getFilePointer() throws IOException {
        if (o instanceof RandomAccessFile) {
            return ((RandomAccessFile) o).getFilePointer();
        } else if (o instanceof DataInputStream) {
            throw new UnsupportedOperationException("Can not get file pointer for Hollow Blob Input of type DataInputStream");
        } else {
            throw new UnsupportedOperationException("Unknown Hollow Blob Input type");
        }
    }

    public final short readShort() throws IOException {
        if (o instanceof RandomAccessFile) {
            return ((RandomAccessFile) o).readShort();
        } else if (o instanceof DataInputStream) {
            return ((DataInputStream) o).readShort();
        } else {
            throw new UnsupportedOperationException("Unknown Hollow Blob Input type");
        }
    }

    public final int readInt() throws IOException {
        if (o instanceof RandomAccessFile) {
            return ((RandomAccessFile) o).readInt();
        } else if (o instanceof DataInputStream) {
            return ((DataInputStream) o).readInt();
        } else {
            throw new UnsupportedOperationException("Unknown Hollow Blob Input type");
        }
    }

    public final long readLong() throws IOException {
        if (o instanceof RandomAccessFile) {
            return ((RandomAccessFile) o).readLong();
        } else if (o instanceof DataInputStream) {
            return ((DataInputStream) o).readLong();
        } else {
            throw new UnsupportedOperationException("Unknown Hollow Blob Input type");
        }
    }

    public final String readUTF() throws IOException {
        if (o instanceof RandomAccessFile) {
            return ((RandomAccessFile) o).readUTF();
        } else if (o instanceof DataInputStream) {
            return ((DataInputStream) o).readUTF();
        } else {
            throw new UnsupportedOperationException("Unknown Hollow Blob Input type");
        }
    }

    public long skipBytes(long n) throws IOException {
        if (o instanceof RandomAccessFile) {
            long total = 0;
            int expected = 0;
            int actual = 0;
            do {
                expected = (n-total) > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) (n-total);
                actual = ((RandomAccessFile) o).skipBytes(expected);    // RandomAccessFile::skipBytes supports int values // SNAP: To Test
                total = total + actual;
            } while (total < n && actual > 0);
            return total;
        } else if (o instanceof DataInputStream) {
            return ((DataInputStream) o).skip(n); // invokes InputStream::skip which supports long values // SNAP: To Test
        } else {
            throw new UnsupportedOperationException("Unknown Hollow Blob Input type");
        }
    }

    @Override
    public void close() throws IOException {
        if (o instanceof RandomAccessFile) {
            ((RandomAccessFile) o).close();
        } else if (o instanceof DataInputStream) {
            ((DataInputStream) o).close();
        } else {
            throw new UnsupportedOperationException("Unknown Hollow Blob Input type");
        }
    }
}
