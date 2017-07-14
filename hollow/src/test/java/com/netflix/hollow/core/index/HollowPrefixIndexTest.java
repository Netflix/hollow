package com.netflix.hollow.core.index;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Test for Hollow Prefix Index.
 */
public class HollowPrefixIndexTest {

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
    public void testSimple() throws Exception {
        test(getSimpleList(), "SimpleMovie", "name.value");
    }

    @Test
    public void testInline() throws Exception {
        test(getInlineList(), "MovieInlineName", "name");
    }

    @Test
    public void testReference() throws Exception {
        test(getReferenceList(), "MovieWithReferenceName", "name.n.value");
    }

    @Test
    public void testReferenceInline() throws Exception {
        test(getReferenceToInlineList(), "MovieWithReferenceToInlineName", "name.n");
    }

    @Test
    public void testDeltaChange() throws Exception {
        List<Movie> movies = getSimpleList();
        ((SimpleMovie) movies.get(0)).updateName("007 James Bond");// test numbers
        ((SimpleMovie) movies.get(1)).updateName("龍爭虎鬥");// "Enter The Dragon"
        for (Movie movie : movies) {
            objectMapper.add(movie);
        }

        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);
        HollowPrefixIndex prefixIndex = new HollowPrefixIndex(readStateEngine, "SimpleMovie", "name.value");
        prefixIndex.listenForDeltaUpdates();

        Set<Integer> ordinals = prefixIndex.query("龍");
        Assert.assertTrue(ordinals.size() == 1);
        printResults(ordinals, "SimpleMovie", "name");

        ordinals = prefixIndex.query("00");
        Assert.assertEquals(ordinals.size(), 1);
        printResults(ordinals, "SimpleMovie", "name");

        // update one movie
        ((SimpleMovie)(movies.get(3))).updateName("Rocky 2");
        // add new movie
        Movie m = new SimpleMovie(5, "As Good as It Gets", 1997);
        movies.add(m);
        m = new SimpleMovie(6, "0 dark thirty", 1997);
        movies.add(m);
        for (Movie movie : movies) {
            objectMapper.add(movie);
        }

        StateEngineRoundTripper.roundTripDelta(writeStateEngine, readStateEngine);
        ordinals = prefixIndex.query("as");
        Assert.assertTrue(ordinals.size() == 1);

        ordinals = prefixIndex.query("R");
        Assert.assertEquals(ordinals.size(), 2);
        printResults(ordinals, "SimpleMovie", "name");

        ordinals = prefixIndex.query("rocky 2");
        Assert.assertTrue(ordinals.size() == 1);

        ordinals = prefixIndex.query("0");
        Assert.assertTrue(ordinals.size() == 2);
        printResults(ordinals, "SimpleMovie", "name");

        prefixIndex.stopDeltaUpdates();

    }

    private void test(List<Movie> movies, String type, String fieldPath) throws Exception {
        for (Movie movie : movies) {
            objectMapper.add(movie);
        }

        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        HollowPrefixIndex prefixIndex = new HollowPrefixIndex(readStateEngine, type, fieldPath);
        Set<Integer> ordinals = prefixIndex.query("R");
        Assert.assertEquals(ordinals.size(), 2);

        ordinals = prefixIndex.query("R");
        Assert.assertEquals(ordinals.size(), 2);

        ordinals = prefixIndex.query("th");
        Assert.assertEquals(ordinals.size(), 1);

        ordinals = prefixIndex.query("the");
        Assert.assertEquals(ordinals.size(), 1);

        ordinals = prefixIndex.query("blOO");
        Assert.assertEquals(ordinals.size(), 1);
    }

    public List<Movie> getSimpleList() {
        List<Movie> movies = new ArrayList<>();
        movies.add(new SimpleMovie(1, "The Matrix", 1999));
        movies.add(new SimpleMovie(2, "Blood Diamond", 2006));
        movies.add(new SimpleMovie(3, "Rush", 2013));
        movies.add(new SimpleMovie(4, "Rocky", 1976));
        return movies;
    }

    public List<Movie> getInlineList() {
        List<Movie> movies = new ArrayList<>();
        movies.add(new MovieInlineName(1, "The Matrix", 1999));
        movies.add(new MovieInlineName(2, "Blood Diamond", 2006));
        movies.add(new MovieInlineName(3, "Rush", 2013));
        movies.add(new MovieInlineName(4, "Rocky", 1976));
        return movies;
    }

    public List<Movie> getReferenceList() {
        List<Movie> movies = new ArrayList<>();
        movies.add(new MovieWithReferenceName(1, "The Matrix", 1999));
        movies.add(new MovieWithReferenceName(2, "Blood Diamond", 2006));
        movies.add(new MovieWithReferenceName(3, "Rush", 2013));
        movies.add(new MovieWithReferenceName(4, "Rocky", 1976));
        return movies;
    }

    public List<Movie> getReferenceToInlineList() {
        List<Movie> movies = new ArrayList<>();
        movies.add(new MovieWithReferenceToInlineName(1, "The Matrix", 1999));
        movies.add(new MovieWithReferenceToInlineName(2, "Blood Diamond", 2006));
        movies.add(new MovieWithReferenceToInlineName(3, "Rush", 2013));
        movies.add(new MovieWithReferenceToInlineName(4, "Rocky", 1976));
        return movies;
    }

    private static interface Movie {

    }

    private static class SimpleMovie implements Movie {

        private int id;
        private String name;
        int yearRelease;

        public SimpleMovie(int id, String name, int yearRelease) {
            this.id = id;
            this.name = name;
            this.yearRelease = yearRelease;
        }

        public void updateName(String n) {
            this.name = n;
        }
    }

    private static class MovieInlineName implements Movie {
        private int id;
        @HollowInline
        private String name;
        int yearRelease;

        public MovieInlineName(int id, String name, int yearRelease) {
            this.id = id;
            this.name = name;
            this.yearRelease = yearRelease;
        }
    }

    private static class MovieWithReferenceName implements Movie {
        private int id;
        private Name name;
        int yearRelease;

        public MovieWithReferenceName(int id, String name, int yearRelease) {
            this.id = id;
            this.name = new Name(name);
            this.yearRelease = yearRelease;
        }
    }

    private static class Name {
        String n;

        public Name(String n) {
            this.n = n;
        }
    }

    private static class MovieWithReferenceToInlineName implements Movie {
        private int id;
        private NameInline name;
        int yearRelease;

        public MovieWithReferenceToInlineName(int id, String name, int yearRelease) {
            this.id = id;
            this.name = new NameInline(name);
            this.yearRelease = yearRelease;
        }
    }

    private static class NameInline {
        @HollowInline
        String n;

        public NameInline(String n) {
            this.n = n;
        }
    }

    private void printResults(Set<Integer> ordinals, String type, String field) {
        HollowObjectTypeReadState movieReadState = (HollowObjectTypeReadState) readStateEngine.getTypeState(type);
        HollowObjectTypeReadState nameReadState = (HollowObjectTypeReadState) readStateEngine.getTypeState("String");
        int nameField = movieReadState.getSchema().getPosition(field);
        int valueField = nameReadState.getSchema().getPosition("value");
        for (int ordinal : ordinals) {
            int nameOrdinal = movieReadState.readOrdinal(ordinal, nameField);
            System.out.println(nameReadState.readString(nameOrdinal, valueField));
        }
    }
}
