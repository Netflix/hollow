/*
 *
 *  Copyright 2017 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.api.consumer.fs;

import com.netflix.hollow.api.consumer.HollowConsumer;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HollowFilesystemBlobRetriever implements HollowConsumer.BlobRetriever {
    
    private final HollowConsumer.BlobRetriever fallbackBlobRetriever;
    private final File blobStoreDir;
    
    /**
     * A new HollowFilesystemBlobRetriever which is not backed by a remote store.
     * 
     * @param blobStoreDir The directory from which to retrieve blobs
     */
    public HollowFilesystemBlobRetriever(File blobStoreDir) {
        this(blobStoreDir, null);
    }
    
    /**
     * A new HollowFileSystemBlobRetriever which is backed by a remote store.  When a blob from the remote store
     * is requested which exists locally, then the local copy is used.  When a blob from the remote store is
     * requested which does not exist locally, it is copied to the filesystem right before it is loaded.
     * 
     * @param blobStoreDir           The directory from which to retrieve blobs, if available
     * @param fallbackBlobRetriever  The remote blob retriever from which to retrieve blobs if they are not already available on the filesystem.
     */
    public HollowFilesystemBlobRetriever(File blobStoreDir, HollowConsumer.BlobRetriever fallbackBlobRetriever) {
        this.blobStoreDir = blobStoreDir;
        this.fallbackBlobRetriever = fallbackBlobRetriever;
        
        blobStoreDir.mkdirs();
    }

    @Override
    public HollowConsumer.Blob retrieveSnapshotBlob(long desiredVersion) {
        File exactFile = new File(blobStoreDir, "snapshot-" + desiredVersion);
        
        if(exactFile.exists())
            return new FilesystemBlob(exactFile, desiredVersion);
        
        long maxVersionBeforeDesired = Long.MIN_VALUE;
        String maxVersionBeforeDesiredFilename = null;

        for(String filename : blobStoreDir.list()) {
            if(filename.startsWith("snapshot-")) {
                long version = Long.parseLong(filename.substring(filename.lastIndexOf("-") + 1));
                if(version < desiredVersion && version > maxVersionBeforeDesired) {
                    maxVersionBeforeDesired = version;
                    maxVersionBeforeDesiredFilename = filename;
                }
            }
        }
        
        HollowConsumer.Blob filesystemBlob = null;
        if(maxVersionBeforeDesired > Long.MIN_VALUE)
            filesystemBlob = new FilesystemBlob(new File(blobStoreDir, maxVersionBeforeDesiredFilename), maxVersionBeforeDesired);
        
        if(fallbackBlobRetriever != null) {
            HollowConsumer.Blob remoteBlob = fallbackBlobRetriever.retrieveSnapshotBlob(desiredVersion);
            if(remoteBlob != null && (filesystemBlob == null || remoteBlob.getToVersion() != filesystemBlob.getToVersion()))
                return new BlobForBackupToFilesystem(remoteBlob, new File(blobStoreDir, "snapshot-" + remoteBlob.getToVersion()));
        }
        
        return filesystemBlob;
    }

    @Override
    public HollowConsumer.Blob retrieveDeltaBlob(long currentVersion) {
        for(String filename : blobStoreDir.list()) {
            if(filename.startsWith("delta-" + currentVersion)) {
                long destinationVersion = Long.parseLong(filename.substring(filename.lastIndexOf("-") + 1));
                return new FilesystemBlob(new File(blobStoreDir, filename), currentVersion, destinationVersion);
            }
        }
        
        if(fallbackBlobRetriever != null) {
            HollowConsumer.Blob remoteBlob = fallbackBlobRetriever.retrieveDeltaBlob(currentVersion);
            if(remoteBlob != null)
                return new BlobForBackupToFilesystem(remoteBlob, new File(blobStoreDir, "delta-" + remoteBlob.getFromVersion() + "-" + remoteBlob.getToVersion()));
        }
        
        return null;
    }

    @Override
    public HollowConsumer.Blob retrieveReverseDeltaBlob(long currentVersion) {
        for(String filename : blobStoreDir.list()) {
            if(filename.startsWith("reversedelta-" + currentVersion)) {
                long destinationVersion = Long.parseLong(filename.substring(filename.lastIndexOf("-") + 1));
                return new FilesystemBlob(new File(blobStoreDir, filename), currentVersion, destinationVersion);
            }
        }
        
        if(fallbackBlobRetriever != null) {
            HollowConsumer.Blob remoteBlob = fallbackBlobRetriever.retrieveReverseDeltaBlob(currentVersion);
            if(remoteBlob != null)
                return new BlobForBackupToFilesystem(remoteBlob, new File(blobStoreDir, "reversedelta-" + remoteBlob.getFromVersion() + "-" + remoteBlob.getToVersion()));
        }
        
        return null;
    }
    
    private static class FilesystemBlob extends HollowConsumer.Blob {

        private final File file;

        public FilesystemBlob(File snapshotFile, long toVersion) {
            super(toVersion);
            this.file = snapshotFile;
        }
        
        public FilesystemBlob(File deltaFile, long fromVersion, long toVersion) {
            super(fromVersion, toVersion);
            this.file = deltaFile;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new BufferedInputStream(new FileInputStream(file));
        }
        
    }
    
    private static class BlobForBackupToFilesystem extends HollowConsumer.Blob {
        
        private final HollowConsumer.Blob remoteBlob;
        private final File file;

        public BlobForBackupToFilesystem(HollowConsumer.Blob remoteBlob, File destinationFile) {
            super(remoteBlob.getFromVersion(), remoteBlob.getToVersion());
            this.file = destinationFile;
            this.remoteBlob = remoteBlob;
        }

        @Override
        public InputStream getInputStream() throws IOException {

            try(
                    InputStream is = remoteBlob.getInputStream();
                    OutputStream os = new FileOutputStream(file);
            ) {
                byte buf[] = new byte[4096];
                int n = 0;
                while (-1 != (n = is.read(buf)))
                    os.write(buf, 0, n);
            }

            return new BufferedInputStream(new FileInputStream(file));
        }
    }
}