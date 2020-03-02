/*
 *  Copyright 2016-2019 Netflix, Inc.
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

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.HollowConstants;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class HollowFilesystemBlobRetriever implements HollowConsumer.BlobRetriever {
    private final Path blobStorePath;
    private final HollowConsumer.BlobRetriever fallbackBlobRetriever;
    private final boolean noFallBackForExistingSnapshot;

    /**
     * A new HollowFilesystemBlobRetriever which is not backed by a remote store.
     *
     * @param blobStorePath The directory from which to retrieve blobs
     * @since 2.12.0
     */
    @SuppressWarnings("unused")
    public HollowFilesystemBlobRetriever(Path blobStorePath) {
        this(blobStorePath, null);
    }

    /**
     * A new HollowFileSystemBlobRetriever which is backed by a remote store.  When a blob from the remote store
     * is requested which exists locally, then the local copy is used.  When a blob from the remote store is
     * requested which does not exist locally, it is copied to the filesystem right before it is loaded.
     *
     * @param blobStorePath          The directory from which to retrieve blobs, if available
     * @param fallbackBlobRetriever  The remote blob retriever from which to retrieve blobs if they are not already
     *                               available on the filesystem.
     * @since 2.12.0
     */
    public HollowFilesystemBlobRetriever(Path blobStorePath, HollowConsumer.BlobRetriever fallbackBlobRetriever) {
        this(blobStorePath, fallbackBlobRetriever, false);
    }

    /**
     * A new HollowFileSystemBlobRetriever which is backed by a remote store.  When a blob from the remote store
     * is requested which exists locally, then the local copy is used.  When a blob from the remote store is
     * requested which does not exist locally, it is copied to the filesystem right before it is loaded.
     *
     * @param blobStorePath          The directory from which to retrieve blobs, if available
     * @param fallbackBlobRetriever  The remote blob retriever from which to retrieve blobs if they are not already
     *                               available on the filesystem.
     * @param noFallBackForExistingSnapshot  If true and a snapshot blob is requested then if there exists a local snapshot
     *                               blob present for the desired version then that snapshot blob is returned and
     *                               the fallback blob retriever (if present) is not queried.
     */
    public HollowFilesystemBlobRetriever(Path blobStorePath, HollowConsumer.BlobRetriever fallbackBlobRetriever,
            boolean noFallBackForExistingSnapshot) {
        this.blobStorePath = blobStorePath;
        this.fallbackBlobRetriever = fallbackBlobRetriever;
        this.noFallBackForExistingSnapshot = noFallBackForExistingSnapshot;

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
        
        long maxVersionBeforeDesired = HollowConstants.VERSION_NONE;
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
        if (maxVersionBeforeDesired != HollowConstants.VERSION_NONE) {
            filesystemBlob = new FilesystemBlob(blobStorePath.resolve(maxVersionBeforeDesiredFilename),
                    maxVersionBeforeDesired);
            if (noFallBackForExistingSnapshot) {
                return filesystemBlob;
            }
        }

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
        FilesystemBlob(File snapshotFile, long toVersion) {
            this(snapshotFile.toPath(), toVersion);
        }

        /**
         * @since 2.12.0
         */
        FilesystemBlob(Path snapshotPath, long toVersion) {
            super(toVersion);
            this.path = snapshotPath;
        }

        /**
         * @since 2.12.0
         */
        FilesystemBlob(Path deltaPath, long fromVersion, long toVersion) {
            super(fromVersion, toVersion);
            this.path = deltaPath;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new BufferedInputStream(Files.newInputStream(path));
        }

        @Override
        public File getFile() throws IOException {
            return path.toFile();
        }
        
    }
    
    private static class BlobForBackupToFilesystem extends HollowConsumer.Blob {
        
        private final HollowConsumer.Blob remoteBlob;
        private final Path path;

        BlobForBackupToFilesystem(HollowConsumer.Blob remoteBlob, Path destinationPath) {
            super(remoteBlob.getFromVersion(), remoteBlob.getToVersion());
            this.path = destinationPath;
            this.remoteBlob = remoteBlob;
        }

        @Override
        public InputStream getInputStream() throws IOException {

            Path tempPath = path.resolveSibling(path.getName(path.getNameCount()-1) + "-" + UUID.randomUUID().toString());
            try(
                    InputStream is = remoteBlob.getInputStream();
                    OutputStream os = Files.newOutputStream(tempPath)
            ) {
                byte buf[] = new byte[4096];
                int n;
                while (-1 != (n = is.read(buf)))
                    os.write(buf, 0, n);
            }
            Files.move(tempPath, path, REPLACE_EXISTING);

            return new BufferedInputStream(Files.newInputStream(path));
        }
    }
}
