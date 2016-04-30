package com.netflix.vms.transformer.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Files {
    InputStream newBlobInputStream(File file) throws IOException;

    OutputStream newBlobOutputStream(File file) throws IOException;
}
