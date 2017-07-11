package com.netflix.hollow.core.index;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class HollowPrefixIndexTest extends AbstractStateEngineTest {

    @Test
    public void testHollowPrefixIndex() throws Exception {
        HollowObjectMapper objectMapper = new HollowObjectMapper(writeStateEngine);
        for (Movie movie : Movie.getMovieList()) {
            objectMapper.add(movie);
        }

        roundTripSnapshot();

        HollowPrefixIndex prefixIndex = new HollowPrefixIndex(readStateEngine, "Movie", "name");
        Set<Integer> ordinals = prefixIndex.query("R");
        Assert.assertEquals(ordinals.size(), 2);

    }

    private static class Movie {
        private int id;
        private String name;
        int yearRelease;

        public Movie(int id, String name, int yearRelease) {
            this.id = id;
            this.name = name;
            this.yearRelease = yearRelease;
        }

        public static List<Movie> getMovieList() {
            return Arrays.asList(
                    new Movie(1, "The Matrix", 1999),
                    new Movie(2, "Blood Diamond", 2006),
                    new Movie(3, "Rush", 2013),
                    new Movie(4, "Rocky", 1976));
        }
    }

    @Override
    protected void initializeTypeStates() {

    }
}
