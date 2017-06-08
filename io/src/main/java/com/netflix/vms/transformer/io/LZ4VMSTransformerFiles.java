package com.netflix.vms.transformer.io;

import com.netflix.vms.transformer.common.TransformerFiles;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;

public class LZ4VMSTransformerFiles implements TransformerFiles {
    @Override
    public InputStream newBlobInputStream(File file) throws IOException {
        return new LZ4BlockInputStream(new FileInputStream(file));
    }

    @Override
    public OutputStream newBlobOutputStream(File file) throws IOException {
        return new LZ4BlockOutputStream(new FileOutputStream(file));
    }
}
