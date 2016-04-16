package com.netflix.vms.transformer.input;

import com.netflix.aws.S3.S3Object;
import com.netflix.aws.file.FileAccessItem;
import com.netflix.aws.file.FileStore;
import com.netflix.hollow.client.HollowUpdateTransition;
import com.netflix.logging.ILog;
import com.netflix.logging.LogManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileStoreHollowUpdateTransition extends HollowUpdateTransition {
    private static final ILog LOGGER = LogManager.getLogger(FileStoreHollowUpdateTransition.class);

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
    public InputStream getInputStream() throws IOException {
        String filename = fileStoreKeybase + "-" + fileStoreVersion;

        File localFile = new File(localFileLocation, filename);

        if(localFile.exists())
            return new FileInputStream(localFile);

        int retryCount = 0;

        while(retryCount < 3) {
            retryCount++;

            try {
                S3Object s3Object = fileStore.getPublishedFile(fileStoreKeybase, fileStoreVersion);
                LOGGER.infof("Copying object %s to %s", s3Object, localFile);
                fileStore.copyFile(s3Object, localFile);
            } catch(Exception e) {

            }
        }

        return new DeleteOnCloseFileInputStream(localFile);
    }

}
