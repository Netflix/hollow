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

import static com.netflix.hollow.core.HollowConstants.VERSION_NONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.netflix.hollow.api.consumer.HollowConsumer.AnnouncementWatcher;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.test.HollowWriteStateEngineBuilder;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestHollowConsumerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

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

    @Test
    public void testDelta_versionTransition() throws Exception {
        long snapshotVersion = 1l;
        TestHollowConsumer consumer = new TestHollowConsumer.Builder()
                .withBlobRetriever(new TestBlobRetriever())
                .build();

        HollowWriteStateEngine state1 = new HollowWriteStateEngineBuilder().build();
        consumer.addSnapshot(snapshotVersion, state1);
        assertEquals("Should be no version", VERSION_NONE, consumer.getCurrentVersionId());
        consumer.triggerRefreshTo(snapshotVersion);
        assertEquals("Should be at snapshot version", snapshotVersion, consumer.getCurrentVersionId());

        long deltaToVersion = 2l;
        HollowWriteStateEngine state2 = new HollowWriteStateEngineBuilder().build();
        consumer.addDelta(snapshotVersion, deltaToVersion, state2);
        assertEquals("Should still be at snapshot version", snapshotVersion, consumer.getCurrentVersionId());
        consumer.triggerRefreshTo(deltaToVersion);
        assertEquals("Should be at delta To version", deltaToVersion, consumer.getCurrentVersionId());
    }

    @Test
    public void testSnapshotDeltaSnapshot_dataTransition() throws Exception {
        TestHollowConsumer consumer = new TestHollowConsumer.Builder()
                .withBlobRetriever(new TestBlobRetriever())
                .build();

        HollowWriteStateEngine state1 = new HollowWriteStateEngineBuilder(Collections.singletonList(Movie.class))
                .add(new Movie(1, "first"))
                .build();
        HollowWriteStateEngine state2 = new HollowWriteStateEngineBuilder(Collections.singletonList(Movie.class))
                .add(new Movie(2, "second"))
                .build();
        HollowWriteStateEngine state3 = new HollowWriteStateEngineBuilder(Collections.singletonList(Movie.class))
                .add(new Movie(3, "third"))
                .build();
        HollowWriteStateEngine state4 = new HollowWriteStateEngineBuilder(Collections.singletonList(Movie.class))
                .add(new Movie(4, "fourth"))
                .build();

        // SNAPSHOT
        consumer.applySnapshot(1l, state1);
        HollowDataAccess data = consumer.getAPI().getDataAccess();
        assertEquals("Should have Movie and String", new HashSet<>(Arrays.asList("Movie", "String")), data.getAllTypes());
        assertConsumerData(consumer, 0, new Movie(1, "first"));

        // DELTA
        consumer.applyDelta(2l, state2);
        assertConsumerData(consumer, 0, new Movie(1, "first"));   // ghost record
        assertConsumerData(consumer, 1, new Movie(2, "second"));  // new record

        // DELTA
        consumer.applyDelta(3l, state3);
        assertConsumerData(consumer, 0, new Movie(3, "third"));   // ordinal for ghost record is reclaimed in next cycle
        assertConsumerData(consumer, 1, new Movie(2, "second"));  // ghost record

        // SNAPSHOT
        consumer.applySnapshot(4l, state4);
        assertConsumerData(consumer, 0, new Movie(4, "fourth"));  // double snapshot; ordinals from previous state not presserved
    }

    @Test
    public void testDeltaBeforeSnapshot_Unsupported() throws IOException  {
        TestHollowConsumer consumer = new TestHollowConsumer.Builder().withBlobRetriever(new TestBlobRetriever()).build();
        HollowWriteStateEngine state = new HollowWriteStateEngineBuilder(Collections.singletonList(Movie.class))
                .add(new Movie(1, "first")).build();
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Delta can not be applied without first applying a snapshot");
        consumer.applyDelta(1l, state);
    }

    // Assert that consumer data contains given movie object at given ordinal
    private void assertConsumerData(TestHollowConsumer consumer, int ordinal, Movie m) {
        HollowDataAccess data = consumer.getAPI().getDataAccess();
        // The hydration of a movie POJO can be substituted with api.getMovie(ordinal) if generated api is available
        GenericHollowObject actualHollowObject = new GenericHollowObject(data, Movie.class.getSimpleName(), ordinal);
        Movie actualMovie = new Movie(actualHollowObject.getInt("id"),
                actualHollowObject.getObject("name").getString("value"));

        assertEquals(actualMovie, m);
    }

    @HollowPrimaryKey(fields = "id")
    static class Movie {
        int id;
        String name;

        Movie(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            Movie other = (Movie) o;
            return this.id == other.id
                    && this.name.equals(other.name);
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = 31 * result + id;
            result = 31 * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }
    }
}
