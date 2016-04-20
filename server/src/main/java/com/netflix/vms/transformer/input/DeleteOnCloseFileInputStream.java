package com.netflix.vms.transformer.input;

import com.netflix.logging.ILog;
import com.netflix.logging.LogManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DeleteOnCloseFileInputStream extends FileInputStream {

    private final ILog LOGGER = LogManager.getLogger(DeleteOnCloseFileInputStream.class);

    private final File file;

    public DeleteOnCloseFileInputStream(File file) throws FileNotFoundException {
        super(file);
        this.file = file;
    }

    @Override
    public void close() throws IOException {
        super.close();

        try {
            file.delete();
        } catch(Exception e) {
            LOGGER.warn("Could not delete local VMS blob file", e);
        }
    }


}
