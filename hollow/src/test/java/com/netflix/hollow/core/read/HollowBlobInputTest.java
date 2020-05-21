package com.netflix.hollow.core.read;

import static org.mockito.Mockito.mock;
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
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import org.junit.Assert;
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

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        testFile = Files.createTempFile(Paths.get(SCRATCH_DIR), "blobfile", "snapshot");
        Files.write(testFile, Arrays.asList("123"));
        when(mockBlob.getInputStream()).thenReturn(mockInputStream);
        when(mockBlob.getFile()).thenReturn(testFile.toFile());
    }

    @Test
    public void testModeBasedSelector() throws IOException {
        Assert.assertTrue((HollowBlobInput.modeBasedSelector(MemoryMode.ON_HEAP, mockBlob)).getInput() instanceof DataInputStream);
        Assert.assertTrue((HollowBlobInput.modeBasedSelector(MemoryMode.SHARED_MEMORY_LAZY, mockBlob)).getInput() instanceof RandomAccessFile);
    }
}
