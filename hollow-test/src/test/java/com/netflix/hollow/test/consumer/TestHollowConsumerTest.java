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
package com.netflix.hollow.test.consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.netflix.hollow.api.consumer.HollowConsumer.AnnouncementWatcher;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.test.HollowWriteStateEngineBuilder;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;

public class TestHollowConsumerTest {
    @Test
    public void testAddSnapshot_version() throws Exception {
        long latestVersion = 1L;
        TestHollowConsumer consumer = new TestHollowConsumer.Builder()
            .withAnnouncementWatcher(new TestAnnouncementWatcher().setLatestVersion(latestVersion))
            .withBlobRetriever(new TestBlobRetriever())
            .build();
        consumer.addSnapshot(latestVersion, new HollowWriteStateEngineBuilder().build());
        assertEquals("Should be no version", AnnouncementWatcher.NO_ANNOUNCEMENT_AVAILABLE, consumer.getCurrentVersionId());
        consumer.triggerRefresh();
        assertEquals("Should be at latest version", latestVersion, consumer.getCurrentVersionId());
    }

    @Test
    public void testAddSnapshot_data() throws Exception {
        long latestVersion = 1L;
        TestHollowConsumer consumer = new TestHollowConsumer.Builder()
            .withAnnouncementWatcher(new TestAnnouncementWatcher().setLatestVersion(1L))
            .withBlobRetriever(new TestBlobRetriever())
            .build();
        consumer.addSnapshot(latestVersion,
                new HollowWriteStateEngineBuilder().add("foo").add(2).build());
        consumer.triggerRefresh();
        HollowDataAccess data = consumer.getAPI().getDataAccess();
        assertEquals("Should have string and int",
                new HashSet<>(Arrays.asList("String", "Integer")), data.getAllTypes());
        assertEquals("foo",
                new GenericHollowObject(data, "String", 0).getString("value"));
        assertEquals(2,
                new GenericHollowObject(data, "Integer", 0).getInt("value"));
    }

    @Test
    public void testAddSnapshot_triggerRefreshTo() throws Exception {
        long version = 2;
        TestHollowConsumer consumer = new TestHollowConsumer.Builder()
            .withBlobRetriever(new TestBlobRetriever())
            .build();
        consumer.addSnapshot(version, new HollowWriteStateEngineBuilder().build());
        try {
            consumer.triggerRefreshTo(version - 1);
            fail("Should have failed to create an update plan");
        } catch (RuntimeException e) { // we should make this a specific exception
        }
        consumer.triggerRefreshTo(version); // should succeed
    }

    @Test
    public void testAddSnapshot_afterUpdate() throws Exception {
        long version1 = 1L;
        long version2 = 2L;
        TestAnnouncementWatcher announcementWatcher =
            new TestAnnouncementWatcher().setLatestVersion(version1);
        TestHollowConsumer consumer = new TestHollowConsumer.Builder()
            .withAnnouncementWatcher(announcementWatcher)
            .withBlobRetriever(new TestBlobRetriever())
            .build();
        consumer.addSnapshot(version1, new HollowWriteStateEngineBuilder().build());
        consumer.triggerRefresh();
        assertEquals(version1, consumer.getCurrentVersionId());

        consumer.addSnapshot(version2, new HollowWriteStateEngineBuilder().build());
        consumer.triggerRefresh();
        assertEquals("We haven't told announcementWatcher about version2 yet", version1,
                consumer.getCurrentVersionId());

        announcementWatcher.setLatestVersion(version2);
        consumer.triggerRefresh();
        assertEquals(version2, consumer.getCurrentVersionId());
    }
}
