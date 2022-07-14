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
package com.netflix.hollow.api.producer;

import com.netflix.hollow.api.producer.fs.HollowFilesystemBlobStorageCleaner;
import com.netflix.hollow.api.producer.fs.HollowFilesystemPublisher;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowProducerBlobStorageCleanerTest {

    private static final String SCRATCH_DIR = System.getProperty("java.io.tmpdir");

    private File publishDir;

    @Before
    public void setUp() {
        publishDir = new File(SCRATCH_DIR, "publish-dir");
        publishDir.mkdir();
    }

    @Test
    public void cleanSnapshotsWithDefaultValue() {
        HollowProducer.Publisher publisher = new HollowFilesystemPublisher(publishDir.toPath());
        HollowProducer.BlobStorageCleaner blobStorageCleaner = new HollowFilesystemBlobStorageCleaner(publishDir);
        HollowProducer producer = HollowProducer.withPublisher(publisher)
                .withBlobStorageCleaner(blobStorageCleaner)
                .withVersionMinter(new TestVersionMinter())
                .build();

        /// initialize the data -- classic producer creates the first state in the delta chain. 
        producer.runCycle(state -> state.add(new TypeA(1, "one", 1)));

        HollowIncrementalProducer incrementalProducer = new HollowIncrementalProducer(producer);
        incrementalProducer.addOrModify(new TypeA(2, "two", 100));
        incrementalProducer.runCycle();
        incrementalProducer.addOrModify(new TypeA(3, "three", 1000));
        incrementalProducer.runCycle();
        incrementalProducer.addOrModify(new TypeA(4, "two", 100));
        incrementalProducer.runCycle();
        incrementalProducer.addOrModify(new TypeA(5, "three", 1000));
        incrementalProducer.runCycle();

        File[] files = listFiles(HollowProducer.Blob.Type.SNAPSHOT.prefix);
        List<String> fileNames = getFileNames(files);
        Assert.assertEquals(5, files.length);

        incrementalProducer.addOrModify(new TypeA(6, "three", 1000));
        incrementalProducer.runCycle();
        incrementalProducer.addOrModify(new TypeA(7, "three", 1000));
        incrementalProducer.runCycle();

        File[] filesAfterCleanup = listFiles(HollowProducer.Blob.Type.SNAPSHOT.prefix);
        List<String> fileNamesAfterCleanup = getFileNames(filesAfterCleanup);

        Assert.assertEquals(5, files.length);
        Assert.assertFalse(fileNamesAfterCleanup.contains("snapshot-1"));
        Assert.assertNotEquals(fileNamesAfterCleanup, fileNames);
    }

    @SuppressWarnings("unused")
    @HollowPrimaryKey(fields = {"id1", "id2"})
    private static class TypeA {
        int id1;
        String id2;
        long value;

        public TypeA(int id1, String id2, long value) {
            this.id1 = id1;
            this.id2 = id2;
            this.value = value;
        }
    }

    private File[] listFiles(final String blobType) {
        return publishDir.listFiles((dir, name) -> name.contains(blobType));
    }

    private List<String> getFileNames(File[] files) {
        List<String> fileNames = new ArrayList<>();
        for(File file : files) {
            fileNames.add(file.getName());
        }
        return fileNames;
    }

    @After
    public void removeAllFiles() {
        Arrays.stream(publishDir.listFiles()).forEach(File::delete);
    }

    private static final class TestVersionMinter implements HollowProducer.VersionMinter  {
        private static int versionCounter = 1;

        @Override
        public long mint() {
            return versionCounter++;
        }
    }
}
