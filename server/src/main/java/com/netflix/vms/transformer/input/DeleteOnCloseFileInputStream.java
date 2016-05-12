package com.netflix.vms.transformer.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteOnCloseFileInputStream extends FileInputStream {

    private final Logger LOGGER = LoggerFactory.getLogger(DeleteOnCloseFileInputStream.class);

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
