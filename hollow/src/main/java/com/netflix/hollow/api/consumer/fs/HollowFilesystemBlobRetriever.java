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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HollowFilesystemBlobRetriever implements HollowConsumer.BlobRetriever {
    
    private final HollowConsumer.BlobRetriever fallbackBlobRetriever;
    private final Path blobStorePath;

    // TODO: deprecate in Hollow 3.0.0
    // @Deprecated
    public HollowFilesystemBlobRetriever(File blobStoreDir) {
        this(blobStoreDir.toPath());
    }

    /**
     * A new HollowFilesystemBlobRetriever which is not backed by a remote store.
     *
     * @param blobStorePath The directory from which to retrieve blobs
     * @since 2.12.0
     */
    public HollowFilesystemBlobRetriever(Path blobStorePath) {
        this(blobStorePath, null);
    }
    
    @Deprecated
    public HollowFilesystemBlobRetriever(File blobStoreDir, HollowConsumer.BlobRetriever fallbackBlobRetriever) {
        this(blobStoreDir.toPath(), fallbackBlobRetriever);
    }

    /**
     * A new HollowFileSystemBlobRetriever which is backed by a remote store.  When a blob from the remote store
     * is requested which exists locally, then the local copy is used.  When a blob from the remote store is
     * requested which does not exist locally, it is copied to the filesystem right before it is loaded.
     *
     * @param blobStorePath           The directory from which to retrieve blobs, if available
     * @param fallbackBlobRetriever  The remote blob retriever from which to retrieve blobs if they are not already available on the filesystem.
     * @since 2.12.0
     */
    public HollowFilesystemBlobRetriever(Path blobStorePath, HollowConsumer.BlobRetriever fallbackBlobRetriever) {
        this.blobStorePath = blobStorePath;
        this.fallbackBlobRetriever = fallbackBlobRetriever;

        try {
            if(!Files.exists(this.blobStorePath)){
                Files.createDirectories(this.blobStorePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create folder for blobRetriever; path=" + blobStorePath, e);
        }
    }

    @Override
    public HollowConsumer.Blob retrieveSnapshotBlob(long desiredVersion) {
        Path exactPath = blobStorePath.resolve("snapshot-" + desiredVersion);

        if(Files.exists(exactPath))
            return new FilesystemBlob(exactPath, desiredVersion);
        
        long maxVersionBeforeDesired = Long.MIN_VALUE;
        String maxVersionBeforeDesiredFilename = null;

        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(blobStorePath)) {
            for (Path path : directoryStream) {
                String filename = path.getFileName().toString();
                if(filename.startsWith("snapshot-")) {
                    long version = Long.parseLong(filename.substring(filename.lastIndexOf("-") + 1));
                    if(version < desiredVersion && version > maxVersionBeforeDesired) {
                        maxVersionBeforeDesired = version;
                        maxVersionBeforeDesiredFilename = filename;
                    }
                }
            }
        } catch(IOException ex) {
            throw new RuntimeException("Error listing snapshot files; path=" + blobStorePath, ex);
        }

        HollowConsumer.Blob filesystemBlob = null;
        if(maxVersionBeforeDesired > Long.MIN_VALUE)
            filesystemBlob = new FilesystemBlob(blobStorePath.resolve(maxVersionBeforeDesiredFilename), maxVersionBeforeDesired);

        if(fallbackBlobRetriever != null) {
            HollowConsumer.Blob remoteBlob = fallbackBlobRetriever.retrieveSnapshotBlob(desiredVersion);
            if(remoteBlob != null && (filesystemBlob == null || remoteBlob.getToVersion() != filesystemBlob.getToVersion()))
                return new BlobForBackupToFilesystem(remoteBlob, blobStorePath.resolve("snapshot-" + remoteBlob.getToVersion()));
        }
        
        return filesystemBlob;
    }

    @Override
    public HollowConsumer.Blob retrieveDeltaBlob(long currentVersion) {

        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(blobStorePath)) {
            for (Path path : directoryStream) {
                String filename = path.getFileName().toString();
                if(filename.startsWith("delta-" + currentVersion)) {
                    long destinationVersion = Long.parseLong(filename.substring(filename.lastIndexOf("-") + 1));
                    return new FilesystemBlob(blobStorePath.resolve(filename), currentVersion, destinationVersion);
                }
            }
        } catch(IOException ex) {
            throw new RuntimeException("Error listing delta files; path=" + blobStorePath, ex);
        }
        
        if(fallbackBlobRetriever != null) {
            HollowConsumer.Blob remoteBlob = fallbackBlobRetriever.retrieveDeltaBlob(currentVersion);
            if(remoteBlob != null)
                return new BlobForBackupToFilesystem(remoteBlob, blobStorePath.resolve("delta-" + remoteBlob.getFromVersion() + "-" + remoteBlob.getToVersion()));
        }
        
        return null;
    }

    @Override
    public HollowConsumer.Blob retrieveReverseDeltaBlob(long currentVersion) {
        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(blobStorePath)) {
            for (Path path : directoryStream) {
                String filename = path.getFileName().toString();
                if(filename.startsWith("reversedelta-" + currentVersion)) {
                    long destinationVersion = Long.parseLong(filename.substring(filename.lastIndexOf("-") + 1));
                    return new FilesystemBlob(blobStorePath.resolve(filename), currentVersion, destinationVersion);
                }
            }
        } catch(IOException ex) {
            throw new RuntimeException("Error listing reverse delta files; path=" + blobStorePath, ex);
        }

        if(fallbackBlobRetriever != null) {
            HollowConsumer.Blob remoteBlob = fallbackBlobRetriever.retrieveReverseDeltaBlob(currentVersion);
            if(remoteBlob != null)
                return new BlobForBackupToFilesystem(remoteBlob, blobStorePath.resolve("reversedelta-" + remoteBlob.getFromVersion() + "-" + remoteBlob.getToVersion()));
        }
        
        return null;
    }
    
    private static class FilesystemBlob extends HollowConsumer.Blob {

        private final Path path;

        @Deprecated
        public FilesystemBlob(File snapshotFile, long toVersion) {
            this(snapshotFile.toPath(), toVersion);
        }

        /**
         * @since 2.12.0
         */
        public FilesystemBlob(Path snapshotPath, long toVersion) {
            super(toVersion);
            this.path = snapshotPath;
        }

        public FilesystemBlob(File deltaFile, long fromVersion, long toVersion) {
           this(deltaFile.toPath(), fromVersion, toVersion);
        }

        /**
         * @since 2.12.0
         */
        public FilesystemBlob(Path deltaPath, long fromVersion, long toVersion) {
            super(fromVersion, toVersion);
            this.path = deltaPath;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new BufferedInputStream(Files.newInputStream(path));
        }
        
    }
    
    private static class BlobForBackupToFilesystem extends HollowConsumer.Blob {
        
        private final HollowConsumer.Blob remoteBlob;
        private final Path path;

        @Deprecated
        public BlobForBackupToFilesystem(HollowConsumer.Blob remoteBlob, File destinationFile) {
            this(remoteBlob, destinationFile.toPath());
        }

        public BlobForBackupToFilesystem(HollowConsumer.Blob remoteBlob, Path destinationPath) {
            super(remoteBlob.getFromVersion(), remoteBlob.getToVersion());
            this.path = destinationPath;
            this.remoteBlob = remoteBlob;
        }

        @Override
        public InputStream getInputStream() throws IOException {

            try(
                    InputStream is = remoteBlob.getInputStream();
                    OutputStream os = Files.newOutputStream(path)
            ) {
                byte buf[] = new byte[4096];
                int n = 0;
                while (-1 != (n = is.read(buf)))
                    os.write(buf, 0, n);
            }

            return new BufferedInputStream(Files.newInputStream(path));
        }
    }
}