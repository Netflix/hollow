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
import java.io.IOException;
import java.io.InputStream;

public class HollowFilesystemBlobRetriever implements HollowConsumer.BlobRetriever {
    
    private final File blobStoreDir;
    
    public HollowFilesystemBlobRetriever(File blobStoreDir) {
        this.blobStoreDir = blobStoreDir;
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
        
        if(maxVersionBeforeDesired > Long.MIN_VALUE)
            return new FilesystemBlob(new File(blobStoreDir, maxVersionBeforeDesiredFilename), maxVersionBeforeDesired);
        
        return null;
    }

    @Override
    public HollowConsumer.Blob retrieveDeltaBlob(long currentVersion) {
        for(String filename : blobStoreDir.list()) {
            if(filename.startsWith("delta-" + currentVersion)) {
                long destinationVersion = Long.parseLong(filename.substring(filename.lastIndexOf("-") + 1));
                return new FilesystemBlob(new File(blobStoreDir, filename), currentVersion, destinationVersion);
            }
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
}