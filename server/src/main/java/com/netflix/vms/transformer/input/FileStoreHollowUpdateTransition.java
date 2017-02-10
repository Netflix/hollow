package com.netflix.vms.transformer.input;

import com.netflix.aws.S3.S3Object;
import com.netflix.aws.file.FileAccessItem;
import com.netflix.aws.file.FileStore;
import com.netflix.hollow.api.client.HollowBlob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import net.jpountz.lz4.LZ4BlockInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileStoreHollowUpdateTransition extends HollowBlob {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileStoreHollowUpdateTransition.class);

    private static final int NUM_RETRIES = 3;

    private final String fileStoreKeybase;
    private final String fileStoreVersion;

    private final FileStore fileStore;

    private String localFileLocation = System.getProperty("java.io.tmpdir");

    public FileStoreHollowUpdateTransition(FileAccessItem fileItem, FileStore fileStore) {
        super(FileStoreUtil.getFromVersion(fileItem), FileStoreUtil.getToVersion(fileItem));
        this.fileStoreKeybase = fileItem.getSimpleDBKeybase();
        this.fileStoreVersion = fileItem.getSimpleDBVersionString();
        this.fileStore = fileStore;
    }

    public FileStoreHollowUpdateTransition withLocalFileLocation(String localFileLocation) {
        this.localFileLocation = localFileLocation;
        return this;
    }

    @Override
    @SuppressWarnings("resource")
    public InputStream getInputStream() throws IOException {
        String filename = fileStoreKeybase + "-" + fileStoreVersion;

        File localFile = new File(localFileLocation, filename);

        if (localFile.exists()) {
            return new LZ4BlockInputStream(new FileInputStream(localFile));
        }

        int retryCount = 0;
        int randomIncompleteExtension = new Random().nextInt() & Integer.MAX_VALUE;
        final File localIncompleteFile = new File(localFileLocation, filename + "." + Integer.toString(randomIncompleteExtension, 16));

        while(retryCount < NUM_RETRIES) {
            retryCount++;

            try {
                S3Object s3Object = fileStore.getPublishedFile(fileStoreKeybase, fileStoreVersion);
                LOGGER.info("Copying object {} to {}", s3Object, localIncompleteFile);
                fileStore.copyFile(s3Object, localIncompleteFile);

                if(!localFile.exists()) {
                    localIncompleteFile.renameTo(localFile);
                }

                break;
            } catch(Exception e) {
                LOGGER.error("Retrieval of transition input stream failed", e);
                if(retryCount == NUM_RETRIES) {
                    if(localIncompleteFile.exists())
                        localIncompleteFile.delete();
                    throw new IOException(e);
                }
            }
        }

        return new LZ4BlockInputStream(new DeleteOnCloseFileInputStream(localFile));
    }
}