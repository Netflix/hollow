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
package com.netflix.hollow.api.producer.fs;

import com.netflix.hollow.api.producer.HollowProducer;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;

public class HollowFilesystemBlobStorageCleaner extends HollowProducer.BlobStorageCleaner {

    private final Logger log = Logger.getLogger(HollowFilesystemBlobStorageCleaner.class.getName());

    private int numOfSnapshotsToKeep;
    private final File blobStoreDir;

    public HollowFilesystemBlobStorageCleaner(File blobStoreDir) {
        this(blobStoreDir,5);
    }

    public HollowFilesystemBlobStorageCleaner(File blobStoreDir, int numOfSnapshotsToKeep) {
        this.blobStoreDir = blobStoreDir;
        this.numOfSnapshotsToKeep = numOfSnapshotsToKeep;
    }

    /**
     * Cleans snapshot to keep the last 'n' snapshots. Defaults to 5.
     */
    @Override
    public void cleanSnapshots() {
        File[] files = getFilesByType(HollowProducer.Blob.Type.SNAPSHOT.prefix);

        if(files == null || files.length <= numOfSnapshotsToKeep) {
            return;
        }

        sortByLastModified(files);

        for(int i= numOfSnapshotsToKeep; i < files.length; i++){
            File file = files[i];
            boolean deleted = file.delete();
            if(!deleted) {
                log.warning("Could not delete snapshot " + file.getPath());
            }
        }
    }

    @Override
    public void cleanHeader() {
    }

    @Override
    public void cleanDeltas() { }

    @Override
    public void cleanReverseDeltas() { }

    private void sortByLastModified(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                Long lastModifiedF2 = f2.lastModified();
                Long lastModifiedF1 = f1.lastModified();
                return lastModifiedF2.compareTo(lastModifiedF1);
            }
        });
        Arrays.sort(files, Collections.reverseOrder());
    }

    private File[] getFilesByType(final String blobType) {
        return blobStoreDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains(blobType);
            }
        });
    }
}
