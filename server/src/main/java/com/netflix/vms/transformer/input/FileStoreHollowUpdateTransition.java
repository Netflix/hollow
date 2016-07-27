package com.netflix.vms.transformer.input;

import com.netflix.vms.transformer.io.LZ4VMSInputStream;
import com.netflix.aws.S3.S3Object;
import com.netflix.aws.file.FileAccessItem;
import com.netflix.aws.file.FileStore;
import com.netflix.hollow.client.HollowUpdateTransition;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import net.jpountz.lz4.LZ4BlockInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileStoreHollowUpdateTransition extends HollowUpdateTransition {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileStoreHollowUpdateTransition.class);

    private final String fileStoreKeybase;
    private final String fileStoreVersion;

    private final FileStore fileStore;
    
    private final boolean useVMSLZ4;

    private String localFileLocation = System.getProperty("java.io.tmpdir");

    public FileStoreHollowUpdateTransition(FileAccessItem fileItem, FileStore fileStore, boolean useVMSLZ4) {
        super(FileStoreUtil.getFromVersion(fileItem), FileStoreUtil.getToVersion(fileItem));
        this.fileStoreKeybase = fileItem.getSimpleDBKeybase();
        this.fileStoreVersion = fileItem.getSimpleDBVersionString();
        this.fileStore = fileStore;
        this.useVMSLZ4 = useVMSLZ4;
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

        if(localFile.exists())
            return useVMSLZ4 ? new LZ4VMSInputStream(new FileInputStream(localFile)) : new LZ4BlockInputStream(new FileInputStream(localFile));

        int retryCount = 0;

        while(retryCount < 3) {
            retryCount++;

            try {
                S3Object s3Object = fileStore.getPublishedFile(fileStoreKeybase, fileStoreVersion);
                LOGGER.info("Copying object {} to {}", s3Object, localFile);
                fileStore.copyFile(s3Object, localFile);
                break;
            } catch(Exception e) {
                LOGGER.error("Retrieval of transition input stream failed", e);
                if(retryCount == 2)
                    throw new IOException(e);
            }
        }

        return useVMSLZ4 ? new LZ4VMSInputStream(new DeleteOnCloseFileInputStream(localFile)) : new LZ4BlockInputStream(new DeleteOnCloseFileInputStream(localFile));
    }

}
