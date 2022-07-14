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
package com.netflix.hollow.core.index;

import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for HollowSparseIntegerSet.
 */
public class HollowSparseIntegerSetTest {

    private HollowWriteStateEngine writeStateEngine;
    private HollowReadStateEngine readStateEngine;
    private HollowObjectMapper objectMapper;

    @Before
    public void beforeTestSetup() {
        writeStateEngine = new HollowWriteStateEngine();
        readStateEngine = new HollowReadStateEngine();
        objectMapper = new HollowObjectMapper(writeStateEngine);
    }

    @Test
    public void testEmptyAndDelta() throws Exception {
        List<Movie> emptyMovies = new ArrayList<>();
        objectMapper.initializeTypeState(Movie.class);
        for(Movie movie : emptyMovies)
            objectMapper.add(movie);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        HollowSparseIntegerSet hollowIntSet = new HollowSparseIntegerSet(readStateEngine, "Movie", "id.value", getPredicate());
        Assert.assertEquals(0, hollowIntSet.cardinality());

        hollowIntSet.listenForDeltaUpdates();

        for(Movie m : getMovies())
            objectMapper.add(m);
        objectMapper.add(new Movie(new Video(8192), "Random", 2009));
        StateEngineRoundTripper.roundTripDelta(writeStateEngine, readStateEngine);

        Assert.assertEquals(11, hollowIntSet.cardinality());// 11 movies released in 2009 as per predicate
    }


    @Test
    public void test() throws Exception {
        List<Movie> movies = getMovies();
        for(Movie movie : movies)
            objectMapper.add(movie);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        HollowSparseIntegerSet hollowIntSet = new HollowSparseIntegerSet(readStateEngine, "Movie", "id.value", getPredicate());
        Assert.assertFalse(hollowIntSet.get(1));
        Assert.assertFalse(hollowIntSet.get(2));
        Assert.assertFalse(hollowIntSet.get(3));
        Assert.assertFalse(hollowIntSet.get(4));

        Assert.assertTrue(hollowIntSet.get(77));
        Assert.assertTrue(hollowIntSet.get(66));
        Assert.assertTrue(hollowIntSet.get(55));
        Assert.assertTrue(hollowIntSet.get(512));
        Assert.assertTrue(hollowIntSet.get(513));
        Assert.assertTrue(hollowIntSet.get(Integer.MAX_VALUE));
        Assert.assertTrue(hollowIntSet.get(40));
        Assert.assertTrue(hollowIntSet.get(28));
        Assert.assertTrue(hollowIntSet.get(30));

        // listen for delta updates
        hollowIntSet.listenForDeltaUpdates();

        // apply delta
        Movie movie = movies.get(5);// change the movie release year for Avatar to 1999
        movie.releaseYear = 1999;

        for(Movie m : movies)
            objectMapper.add(m);
        StateEngineRoundTripper.roundTripDelta(writeStateEngine, readStateEngine);

        // now Avatar should not be there in index.
        Assert.assertFalse(hollowIntSet.get(512));
    }

    // this predicate only indexes movie released in 2009
    private HollowSparseIntegerSet.IndexPredicate getPredicate() {
        return new HollowSparseIntegerSet.IndexPredicate() {
            @Override
            public boolean shouldIndex(int ordinal) {
                HollowObjectTypeDataAccess objectTypeDataAccess = (HollowObjectTypeDataAccess) readStateEngine.getTypeDataAccess("Movie");
                int yearReleasedFieldPosition = objectTypeDataAccess.getSchema().getPosition("releaseYear");
                int yearReleased = objectTypeDataAccess.readInt(ordinal, yearReleasedFieldPosition);
                if(yearReleased == 2009)
                    return true;
                return false;
            }
        };
    }

    private List<Movie> getMovies() {
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie(new Video(1), "The Matrix", 1999));
        movies.add(new Movie(new Video(2), "Blood Diamond", 2006));
        movies.add(new Movie(new Video(3), "Rush", 2013));
        movies.add(new Movie(new Video(4), "Rocky", 1976));

        movies.add(new Movie(new Video(40), "Inglourious Basterds", 2009));
        movies.add(new Movie(new Video(512), "Avatar", 2009));
        movies.add(new Movie(new Video(513), "Harry Potter and the Half-Blood Prince", 2009));
        movies.add(new Movie(new Video(0), "The Hangover", 2009));
        movies.add(new Movie(new Video(Integer.MAX_VALUE), "Sherlock Holmes", 2009));
        movies.add(new Movie(new Video(28), "Up", 2009));
        movies.add(new Movie(new Video(30), "The Girl with the Dragon Tattoo", 2009));
        movies.add(new Movie(new Video(77), "District 9", 2009));
        movies.add(new Movie(new Video(66), "Law Abiding Citizen", 2009));
        movies.add(new Movie(new Video(55), "Moon", 2009));


        return movies;
    }

    private static class Movie {
        private Video id;
        private String name;
        private int releaseYear;

        public Movie(Video id, String name, int releaseYear) {
            this.id = id;
            this.name = name;
            this.releaseYear = releaseYear;
        }
    }

    private static class Video {
        private int value;

        public Video(int id) {
            this.value = id;
        }
    }
}
