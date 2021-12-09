/*
 *  Copyright 2016-2021 Netflix, Inc.
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
import com.netflix.hollow.api.consumer.HollowConsumer.Blob.BlobType;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.read.OptionalBlobPartInput;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class HollowFilesystemBlobRetriever implements HollowConsumer.BlobRetriever {
    private final Path blobStorePath;
    private final HollowConsumer.BlobRetriever fallbackBlobRetriever;
    private final boolean useExistingStaleSnapshot;
    private final Set<String> optionalBlobParts;

    /**
     * A new HollowFilesystemBlobRetriever which is not backed by a remote store.
     *
     * @param blobStorePath The directory from which to retrieve blobs
     * @since 2.12.0
     */
    @SuppressWarnings("unused")
    public HollowFilesystemBlobRetriever(Path blobStorePath) {
        this(blobStorePath, null, false);
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
     * @param useExistingStaleSnapshot  If true and a snapshot blob is requested then if there exists a local snapshot
     *                               blob present for the desired version then that snapshot blob is returned and
     *                               the fallback blob retriever (if present) is not queried.
     */
    public HollowFilesystemBlobRetriever(Path blobStorePath, HollowConsumer.BlobRetriever fallbackBlobRetriever, boolean useExistingStaleSnapshot) {
        this.blobStorePath = blobStorePath;
        this.fallbackBlobRetriever = fallbackBlobRetriever;
        this.useExistingStaleSnapshot = useExistingStaleSnapshot;
        this.optionalBlobParts = fallbackBlobRetriever == null ? null : fallbackBlobRetriever.configuredOptionalBlobParts();

        ensurePathExists(blobStorePath);
    }

    /**
     * A new HollowFilesystemBlobRetriever which is not backed by a remote store.
     * 
     * Uses the configured optional blob parts
     *
     * @param blobStorePath The directory from which to retrieve blobs
     * @since 2.12.0
     */
    public HollowFilesystemBlobRetriever(Path blobStorePath, Set<String> optionalBlobParts) {
        this.blobStorePath = blobStorePath;
        this.optionalBlobParts = optionalBlobParts;
        this.useExistingStaleSnapshot = true;
        this.fallbackBlobRetriever = null;

        ensurePathExists(blobStorePath);
    }

    private void ensurePathExists(Path blobStorePath) {
        try {
            if(!Files.exists(this.blobStorePath)){
                Files.createDirectories(this.blobStorePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create folder for blobRetriever; path=" + blobStorePath, e);
        }
    }

    @Override
    public HollowConsumer.HeaderBlob retrieveHeaderBlob(long desiredVersion) {
        Path exactPath = blobStorePath.resolve("header-" + desiredVersion);
        if (Files.exists(exactPath))
            return new FilesystemHeaderBlob(exactPath, -1L, desiredVersion);

        long maxVersionBeforeDesired = HollowConstants.VERSION_NONE;
        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(blobStorePath)) {
            for (Path path : directoryStream) {
                String filename = path.getFileName().toString();
                if(filename.startsWith("header-")) {
                    long version = Long.parseLong(filename.substring(filename.lastIndexOf("-") + 1));
                    if(version < desiredVersion && version > maxVersionBeforeDesired) {
                        maxVersionBeforeDesired = version;
                    }
                }
            }
        } catch(IOException ex) {
            throw new RuntimeException("Error listing header files; path=" + blobStorePath, ex);
        }
        HollowConsumer.HeaderBlob filesystemBlob = null;
        if (maxVersionBeforeDesired != HollowConstants.VERSION_NONE) {
            filesystemBlob = new FilesystemHeaderBlob(blobStorePath.resolve("snapshot-" + maxVersionBeforeDesired), -1L, maxVersionBeforeDesired);
            if (useExistingStaleSnapshot) {
                return filesystemBlob;
            }
        }

        if(fallbackBlobRetriever != null) {
            HollowConsumer.HeaderBlob remoteBlob = fallbackBlobRetriever.retrieveHeaderBlob(desiredVersion);
            if(remoteBlob != null && (filesystemBlob == null || remoteBlob.getToVersion() != filesystemBlob.getToVersion()))
                return new HeaderBlobFromBackupToFilesystem(remoteBlob, blobStorePath.resolve("header-" + remoteBlob.getToVersion()));
        }

        return filesystemBlob;
    }

    @Override
    public HollowConsumer.Blob retrieveSnapshotBlob(long desiredVersion) {
        Path exactPath = blobStorePath.resolve("snapshot-" + desiredVersion);

        if(Files.exists(exactPath) && allRequestedPartsExist(BlobType.SNAPSHOT, -1L, desiredVersion))
            return filesystemBlob(BlobType.SNAPSHOT, -1L, desiredVersion);
        
        long maxVersionBeforeDesired = HollowConstants.VERSION_NONE;

        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(blobStorePath)) {
            for (Path path : directoryStream) {
                String filename = path.getFileName().toString();
                if(filename.startsWith("snapshot-")) {
                    long version = Long.parseLong(filename.substring(filename.lastIndexOf("-") + 1));
                    if(version < desiredVersion && version > maxVersionBeforeDesired && allRequestedPartsExist(BlobType.SNAPSHOT, -1L, version)) {
                        maxVersionBeforeDesired = version;
                    }
                }
            }
        } catch(IOException ex) {
            throw new RuntimeException("Error listing snapshot files; path=" + blobStorePath, ex);
        }

        HollowConsumer.Blob filesystemBlob = null;
        if (maxVersionBeforeDesired != HollowConstants.VERSION_NONE) {
            filesystemBlob = filesystemBlob(BlobType.SNAPSHOT, -1L, maxVersionBeforeDesired);
            if (useExistingStaleSnapshot) {
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

    private HollowConsumer.Blob filesystemBlob(HollowConsumer.Blob.BlobType type, long currentVersion, long destinationVersion) {
        Path path;
        Map<String, Path> optionalPartPaths = null;
        switch(type) {
        case SNAPSHOT:
            path = blobStorePath.resolve("snapshot-" + destinationVersion);
            if(optionalBlobParts != null && !optionalBlobParts.isEmpty()) {
                optionalPartPaths = new HashMap<>(optionalBlobParts.size());
                for(String part : optionalBlobParts) {
                    optionalPartPaths.put(part, blobStorePath.resolve("snapshot_"+part+"-"+destinationVersion));
                }
            }
            
            return new FilesystemBlob(path, destinationVersion, optionalPartPaths);
        case DELTA:
            path = blobStorePath.resolve("delta-" + currentVersion + "-" + destinationVersion);
            if(optionalBlobParts != null && !optionalBlobParts.isEmpty()) {
                optionalPartPaths = new HashMap<>(optionalBlobParts.size());
                for(String part : optionalBlobParts) {
                    optionalPartPaths.put(part, blobStorePath.resolve("delta_"+part+"-"+currentVersion+"-"+destinationVersion));
                }
            }
            
            return new FilesystemBlob(path, currentVersion, destinationVersion, optionalPartPaths);
        case REVERSE_DELTA:
            path = blobStorePath.resolve("reversedelta-" + currentVersion + "-" + destinationVersion);
            if(optionalBlobParts != null && !optionalBlobParts.isEmpty()) {
                optionalPartPaths = new HashMap<>(optionalBlobParts.size());
                for(String part : optionalBlobParts) {
                    optionalPartPaths.put(part, blobStorePath.resolve("reversedelta_"+part+"-"+currentVersion+"-"+destinationVersion));
                }
            }
            
            return new FilesystemBlob(path, currentVersion, destinationVersion, optionalPartPaths);
        default:
            throw new IllegalArgumentException("Unknown BlobType: " + type.toString());
        }
    }

    @Override
    public HollowConsumer.Blob retrieveDeltaBlob(long currentVersion) {

        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(blobStorePath)) {
            for (Path path : directoryStream) {
                String filename = path.getFileName().toString();
                if(filename.startsWith("delta-" + currentVersion)) {
                    long destinationVersion = Long.parseLong(filename.substring(filename.lastIndexOf("-") + 1));
                    if(allRequestedPartsExist(BlobType.DELTA, currentVersion, destinationVersion))
                        return filesystemBlob(BlobType.DELTA, currentVersion, destinationVersion);
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
                    if(allRequestedPartsExist(BlobType.REVERSE_DELTA, currentVersion, destinationVersion))
                        return filesystemBlob(BlobType.REVERSE_DELTA, currentVersion, destinationVersion);
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

    private boolean allRequestedPartsExist(HollowConsumer.Blob.BlobType type, long currentVersion, long destinationVersion) {
        if(optionalBlobParts == null || optionalBlobParts.isEmpty())
            return true;

        for(String part : optionalBlobParts) {
            String filename = null;
            switch(type) {
            case SNAPSHOT:
                filename = "snapshot_" + part + "-" + destinationVersion;
                break;
            case DELTA:
                filename = "delta_" + part + "-" + currentVersion + "-" + destinationVersion;
                break;
            case REVERSE_DELTA:
                filename = "reversedelta_" + part + "-" + currentVersion + "-" + destinationVersion;
                break;
            }

            if(!Files.exists(blobStorePath.resolve(filename)))
                return false;
        }

        return true;
    }    

    private static class FilesystemHeaderBlob extends HollowConsumer.HeaderBlob {
        private final Path path;

        protected FilesystemHeaderBlob(Path headerPath,long fromVersion, long toVersion) {
            super(fromVersion, toVersion);
            this.path = headerPath;
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

    private static class FilesystemBlob extends HollowConsumer.Blob {

        private final Path path;
        private final Map<String, Path> optionalPartPaths;

        @Deprecated
        FilesystemBlob(File snapshotFile, long toVersion) {
            this(snapshotFile.toPath(), toVersion);
        }

        /**
         * @since 2.12.0
         */
        FilesystemBlob(Path snapshotPath, long toVersion) {
            this(snapshotPath, toVersion, null);
        }

        /**
         * @since 2.12.0
         */
        FilesystemBlob(Path deltaPath, long fromVersion, long toVersion) {
            this(deltaPath, fromVersion, toVersion, null);
        }

        FilesystemBlob(Path snapshotPath, long toVersion, Map<String, Path> optionalPartPaths) {
            super(toVersion);
            this.path = snapshotPath;
            this.optionalPartPaths = optionalPartPaths;
        }

        FilesystemBlob(Path deltaPath, long fromVersion, long toVersion, Map<String, Path> optionalPartPaths) {
            super(fromVersion, toVersion);
            this.path = deltaPath;
            this.optionalPartPaths = optionalPartPaths;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new BufferedInputStream(Files.newInputStream(path));
        }

        @Override
        public OptionalBlobPartInput getOptionalBlobPartInputs() throws IOException {
            if(optionalPartPaths == null || optionalPartPaths.isEmpty())
                return null;
            
            OptionalBlobPartInput input = new OptionalBlobPartInput();
            for(Map.Entry<String, Path> pathEntry : optionalPartPaths.entrySet()) {
                input.addInput(pathEntry.getKey(), pathEntry.getValue().toFile());
            }
            return input;
        }

        @Override
        public File getFile() throws IOException {
            return path.toFile();
        }
        
    }

    private static class HeaderBlobFromBackupToFilesystem extends HollowConsumer.HeaderBlob {
        private final HollowConsumer.HeaderBlob remoteHeaderBlob;
        private final Path path;

        protected HeaderBlobFromBackupToFilesystem(HollowConsumer.HeaderBlob remoteHeaderBlob, Path destinationPath) {
            super(remoteHeaderBlob.getFromVersion(), remoteHeaderBlob.getToVersion());
            this.path = destinationPath;
            this.remoteHeaderBlob = remoteHeaderBlob;
        }

        @Override
        public InputStream getInputStream() throws IOException {

            Path tempPath = path.resolveSibling(path.getName(path.getNameCount()-1) + "-" + UUID.randomUUID().toString());
            try(
                    InputStream is = remoteHeaderBlob.getInputStream();
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

        @Override
        public File getFile() throws IOException {
            Path tempPath = path.resolveSibling(path.getName(path.getNameCount()-1) + "-" + UUID.randomUUID().toString());
            try(
                    InputStream is = remoteHeaderBlob.getInputStream();
                    OutputStream os = Files.newOutputStream(tempPath)
            ) {
                byte buf[] = new byte[4096];
                int n;
                while (-1 != (n = is.read(buf)))
                    os.write(buf, 0, n);
            }
            Files.move(tempPath, path, REPLACE_EXISTING);

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


        @Override
        public File getFile() throws IOException {
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

            return path.toFile();
        }

        @Override
        public OptionalBlobPartInput getOptionalBlobPartInputs() throws IOException {
            OptionalBlobPartInput remoteOptionalParts = remoteBlob.getOptionalBlobPartInputs();
            if(remoteOptionalParts == null)
                return null;

            OptionalBlobPartInput localOptionalParts = new OptionalBlobPartInput();

            for(Map.Entry<String, InputStream> entry : remoteOptionalParts.getInputStreamsByPartName().entrySet()) {
                Path tempPath = path.resolveSibling(path.getName(path.getNameCount()-1) + "_" + entry.getKey() + "-" + UUID.randomUUID().toString());
                Path destPath = getBlobType() == BlobType.SNAPSHOT ?
                        path.resolveSibling(getBlobType().getType() + "_" + entry.getKey() + "-" + getToVersion())
                            : path.resolveSibling(getBlobType().getType() + "_" + entry.getKey() + "-" + getFromVersion() + "-" + getToVersion());
                try(
                        InputStream is = entry.getValue();
                        OutputStream os = Files.newOutputStream(tempPath)
                ) {
                    byte buf[] = new byte[4096];
                    int n;
                    while (-1 != (n = is.read(buf, 0, buf.length)))
                        os.write(buf, 0, n);
                }
                Files.move(tempPath, destPath, REPLACE_EXISTING);
                
                localOptionalParts.addInput(entry.getKey(), new BufferedInputStream(Files.newInputStream(destPath)));
            }

            return localOptionalParts;
        }

    }
}