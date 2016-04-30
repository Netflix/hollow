package com.netflix.vms.transformer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.netflix.vms.transformer.common.TransformerFiles;

public class TransformerServerFiles implements TransformerFiles {
    @Override
    public InputStream newBlobInputStream(File file) throws IOException {
        return new LZ4VMSInputStream(new FileInputStream(file));
    }

    @Override
    public OutputStream newBlobOutputStream(File file) throws IOException {
        return new LZ4VMSOutputStream(new FileOutputStream(file));
    }
}
