package com.netflix.hollow.core.index;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
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
    public void testCustomPrefixIndex() throws Exception {

        for (Movie movie: getSimpleList()) {
            objectMapper.add(movie);
        }
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);
        HollowTokenizedPrefixIndex tokenizedPrefixIndex = new HollowTokenizedPrefixIndex(readStateEngine, "SimpleMovie", "name.value");

        Set<Integer> ordinals = toSet(tokenizedPrefixIndex.query("th"));
        Assert.assertTrue(ordinals.size() == 1);

        ordinals = toSet(tokenizedPrefixIndex.query("matrix"));
        Assert.assertTrue(ordinals.size() == 1);

        ordinals = toSet(tokenizedPrefixIndex.query("the "));// note the whitespace in query string.
        // expected result ordinals size is 0, since entire movie is not indexed. movie name is split by whitespace.
        Assert.assertTrue(ordinals.size() == 0);
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

        Set<Integer> ordinals = toSet(prefixIndex.query("龍"));
        Assert.assertTrue(ordinals.size() == 1);
        printResults(ordinals, "SimpleMovie", "name");

        ordinals = toSet(prefixIndex.query("00"));
        Assert.assertEquals(ordinals.size(), 1);
        printResults(ordinals, "SimpleMovie", "name");

        // update one movie
        ((SimpleMovie) (movies.get(3))).updateName("Rocky 2");
        // add new movie
        Movie m = new SimpleMovie(5, "As Good as It Gets", 1997);
        movies.add(m);
        m = new SimpleMovie(6, "0 dark thirty", 1997);
        movies.add(m);
        for (Movie movie : movies) {
            objectMapper.add(movie);
        }

        StateEngineRoundTripper.roundTripDelta(writeStateEngine, readStateEngine);
        ordinals = toSet(prefixIndex.query("as"));
        Assert.assertTrue(ordinals.size() == 1);

        ordinals = toSet(prefixIndex.query("R"));
        Assert.assertEquals(ordinals.size(), 2);
        printResults(ordinals, "SimpleMovie", "name");

        ordinals = toSet(prefixIndex.query("rocky 2"));
        Assert.assertTrue(ordinals.size() == 1);

        ordinals = toSet(prefixIndex.query("0"));
        Assert.assertTrue(ordinals.size() == 2);
        printResults(ordinals, "SimpleMovie", "name");

        prefixIndex.detachFromDeltaUpdates();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidType() throws Exception {
        for (Movie movie : getSimpleList()) {
            objectMapper.add(movie);
        }
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        // random type which does not exists in read state engine.
        new HollowPrefixIndex(readStateEngine, "randomType", "id");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFieldPath() throws Exception {
        for (Movie movie : getSimpleList()) {
            objectMapper.add(movie);
        }
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        // test invalid field path, basically field path does not lead to a string value.
        new HollowPrefixIndex(readStateEngine, "SimpleMovie", "id");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFieldPathReference() throws Exception {
        for (Movie movie : getReferenceList()) {
            objectMapper.add(movie);
        }
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        // test invalid field path, in this case reference type is referring toa field which is not present.
        // PrimaryKey class helps in catching this.
        new HollowPrefixIndex(readStateEngine, "MovieWithReferenceName", "name.randomField");
    }

    @Test
    public void testAutoExpandFieldPath() throws Exception {
        for (Movie movie : getReferenceList()) {
            objectMapper.add(movie);
        }
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);
        HollowPrefixIndex index = new HollowPrefixIndex(readStateEngine, "MovieWithReferenceName", "name.n");// no.value appended, it should work
        Set<Integer> ordinals = toSet(index.query("the"));
        Assert.assertTrue(ordinals.size() == 1);
        printResults(ordinals, "MovieWithReferenceName", "name");

    }

    private void test(List<Movie> movies, String type, String fieldPath) throws Exception {
        for (Movie movie : movies) {
            objectMapper.add(movie);
        }

        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        HollowPrefixIndex prefixIndex = new HollowPrefixIndex(readStateEngine, type, fieldPath);
        Set<Integer> ordinals = toSet(prefixIndex.query("R"));
        Assert.assertEquals(ordinals.size(), 2);

        ordinals = toSet(prefixIndex.query("R"));
        Assert.assertEquals(ordinals.size(), 2);

        ordinals = toSet(prefixIndex.query("th"));
        Assert.assertEquals(ordinals.size(), 1);

        ordinals = toSet(prefixIndex.query("the"));
        Assert.assertEquals(ordinals.size(), 1);

        ordinals = toSet(prefixIndex.query("blOO"));
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

    private static class HollowTokenizedPrefixIndex extends HollowPrefixIndex {

        public HollowTokenizedPrefixIndex(HollowReadStateEngine readStateEngine, String type, String fieldPath) {
            super(readStateEngine, type, fieldPath);
        }

        @Override
        public String[] getKey(int ordinal) {
            // split the key by " ";
            String[] keys = super.getKey(ordinal);
            List<String> tokens = new ArrayList<>();
            for (String key : keys) {
                String[] splits = key.split(" ");
                for (String split : splits)
                    tokens.add(split.toLowerCase());
            }
            return tokens.toArray(new String[tokens.size()]);
        }
    }

    private Set<Integer> toSet(HollowOrdinalIterator iterator) {
        Set<Integer> ordinals = new HashSet<>();
        int ordinal = iterator.next();
        while(ordinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            ordinals.add(ordinal);
            ordinal = iterator.next();
        }
        return ordinals;
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
