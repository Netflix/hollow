package com.netflix.hollow.core.read;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.memory.MemoryMode;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class HollowBlobInputTest {

    private static final String SCRATCH_DIR = System.getProperty("java.io.tmpdir");

    @Mock
    HollowConsumer.Blob mockBlob;

    @Mock
    InputStream mockInputStream;

    @Mock
    File mockFile;

    Path testFile;
    byte[] leadingBytes;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        testFile = Files.createTempFile(Paths.get(SCRATCH_DIR), "blobfile", "snapshot");
        // The test file looks like:
        // First 8 bytes for testing reading byte, short, and long
        // Then to test reading UTF, 2 bytes for UTF length (containing unsigned short value of 1) and then the string "test"
        leadingBytes = new byte[]{0, 1, 0, 1, 0, 1, 0, 1};
        Files.write(testFile, leadingBytes);
        byte[] utfLen = new byte[]{0, 1};   // bytes corresponding to a short of value 1
        Files.write(testFile, utfLen, StandardOpenOption.APPEND);
        Files.write(testFile, "test".getBytes(), StandardOpenOption.APPEND);
        when(mockBlob.getInputStream()).thenReturn(new FileInputStream(testFile.toFile()));
        when(mockBlob.getFile()).thenReturn(testFile.toFile());
    }

    @Test
    public void testModeBasedSelector() throws IOException {
        assertTrue((HollowBlobInput.modeBasedSelector(MemoryMode.ON_HEAP, mockBlob)).getInput() instanceof DataInputStream);
        assertTrue((HollowBlobInput.modeBasedSelector(MemoryMode.SHARED_MEMORY_LAZY, mockBlob)).getInput() instanceof RandomAccessFile);
        assertNotNull((HollowBlobInput.modeBasedSelector(MemoryMode.SHARED_MEMORY_LAZY, mockBlob)).getBuffer());
    }

    @Test
    public void testRead() throws IOException {
        HollowBlobInput inStream = HollowBlobInput.modeBasedSelector(MemoryMode.ON_HEAP, mockBlob);
        assertEquals(0, inStream.read()); // first byte is 0
        assertEquals(1, inStream.read()); // second byte is 1

        HollowBlobInput inBuffer = HollowBlobInput.modeBasedSelector(MemoryMode.SHARED_MEMORY_LAZY, mockBlob);
        assertEquals(0, inBuffer.read()); // first byte is 0
        assertEquals(1, inBuffer.read()); // second byte is 1
    }

    @Test
    public void testReadBytes() throws IOException {
        byte[] result = new byte[8];
        HollowBlobInput.modeBasedSelector(MemoryMode.ON_HEAP, mockBlob).read(result, 0, 8);
        assertTrue(Arrays.equals(leadingBytes, result));

        HollowBlobInput.modeBasedSelector(MemoryMode.SHARED_MEMORY_LAZY, mockBlob).read(result, 0, 8);
        assertTrue(Arrays.equals(leadingBytes, result));
    }

    @Test
    public void testSeek() throws IOException {
        try (HollowBlobInput inStream = HollowBlobInput.modeBasedSelector(MemoryMode.ON_HEAP, mockBlob)) {
            inStream.seek(3);
            fail();
        } catch (UnsupportedOperationException e) {
            // pass
        } catch (Exception e) {
            fail();
        }

        HollowBlobInput inBuffer = HollowBlobInput.modeBasedSelector(MemoryMode.SHARED_MEMORY_LAZY, mockBlob);
        inBuffer.seek(3);
        assertEquals(3, inBuffer.getFilePointer()); // first byte is 0
    }

    @Test
    public void testGetFilePointer() throws IOException {
        try (HollowBlobInput inStream = HollowBlobInput.modeBasedSelector(MemoryMode.ON_HEAP, mockBlob)) {
            inStream.getFilePointer();
            fail();
        } catch (UnsupportedOperationException e) {
            // pass
        } catch (Exception e) {
            fail();
        }

        HollowBlobInput inBuffer = HollowBlobInput.modeBasedSelector(MemoryMode.SHARED_MEMORY_LAZY, mockBlob);
        assertEquals(0, inBuffer.getFilePointer()); // first byte is 0
    }

    @Test
    public void testReadShort() throws IOException {
        HollowBlobInput inStream = HollowBlobInput.modeBasedSelector(MemoryMode.ON_HEAP, mockBlob);
        assertEquals(1, inStream.readShort()); // first short is 1
        assertEquals(1, inStream.readShort()); // second short is 1

        HollowBlobInput inBuffer = HollowBlobInput.modeBasedSelector(MemoryMode.SHARED_MEMORY_LAZY, mockBlob);
        assertEquals(1, inBuffer.readShort()); // first short is 1
        assertEquals(1, inBuffer.readShort()); // second short is 1
    }

    @Test
    public void testReadInt() throws IOException {
        HollowBlobInput inStream = HollowBlobInput.modeBasedSelector(MemoryMode.ON_HEAP, mockBlob);
        assertEquals(65537, inStream.readInt()); // first int

        HollowBlobInput inBuffer = HollowBlobInput.modeBasedSelector(MemoryMode.SHARED_MEMORY_LAZY, mockBlob);
        assertEquals(65537, inBuffer.readInt()); // first int
    }

    @Test
    public void testReadLong() throws IOException {
        HollowBlobInput inStream = HollowBlobInput.modeBasedSelector(MemoryMode.ON_HEAP, mockBlob);
        assertEquals(281479271743489l, inStream.readLong()); // first long

        HollowBlobInput inBuffer = HollowBlobInput.modeBasedSelector(MemoryMode.SHARED_MEMORY_LAZY, mockBlob);
        assertEquals(281479271743489l, inBuffer.readLong()); // first long
    }

    @Test
    public void testReadUTF() throws IOException {
        HollowBlobInput inStream = HollowBlobInput.modeBasedSelector(MemoryMode.ON_HEAP, mockBlob);
        inStream.readLong();    // skip 8 bytes
        assertEquals("t", inStream.readUTF()); // first UTF

        HollowBlobInput inBuffer = HollowBlobInput.modeBasedSelector(MemoryMode.SHARED_MEMORY_LAZY, mockBlob);
        inBuffer.seek(8);
        assertEquals("t", inBuffer.readUTF()); // first UTF
    }

    @Test
    public void testSkipBytes() throws IOException {
        HollowBlobInput inStream = HollowBlobInput.modeBasedSelector(MemoryMode.ON_HEAP, mockBlob);
        assertEquals(1l, inStream.skipBytes(1));
        assertEquals(1, inStream.read());   // next byte read is 1
        assertEquals(2000, inStream.skipBytes(2000));   // successfully skips past end of file for FileInputStream

        HollowBlobInput inBuffer = HollowBlobInput.modeBasedSelector(MemoryMode.SHARED_MEMORY_LAZY, mockBlob);
        assertEquals(1l, inBuffer.skipBytes(1));
        assertEquals(1, inBuffer.read());   // next byte read is 1
        assertEquals(12, inBuffer.skipBytes(2000));   // stops at end of file for RandomAccessFile
    }
}
