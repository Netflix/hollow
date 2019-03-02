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
package com.netflix.hollow.core.index;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        test(getSimpleList(), "SimpleMovie", "name");// also tests, if field path auto expands to name.value.
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

        for (Movie movie : getSimpleList()) {
            objectMapper.add(movie);
        }
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);
        HollowTokenizedPrefixIndex tokenizedPrefixIndex = new HollowTokenizedPrefixIndex(readStateEngine, "SimpleMovie", "name.value");

        Set<Integer> ordinals = toSet(tokenizedPrefixIndex.findKeysWithPrefix("th"));
        Assert.assertTrue(ordinals.size() == 1);

        ordinals = toSet(tokenizedPrefixIndex.findKeysWithPrefix("matrix"));
        Assert.assertTrue(ordinals.size() == 1);

        ordinals = toSet(tokenizedPrefixIndex.findKeysWithPrefix("the "));// note the whitespace in findKeysWithPrefix string.
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
        HollowPrefixIndex prefixIndex = new HollowPrefixIndex(readStateEngine, "SimpleMovie", "name");
        prefixIndex.listenForDeltaUpdates();

        Set<Integer> ordinals = toSet(prefixIndex.findKeysWithPrefix("龍"));
        Set<String> movieNames = getMovieNames(ordinals, "SimpleMovie", "name");
        Assert.assertTrue(ordinals.size() == 1);
        Assert.assertTrue(movieNames.contains("龍爭虎鬥"));


        ordinals = toSet(prefixIndex.findKeysWithPrefix("00"));
        movieNames = getMovieNames(ordinals, "SimpleMovie", "name");
        Assert.assertTrue(ordinals.size() == 1);
        Assert.assertTrue(movieNames.contains("007 James Bond"));

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
        ordinals = toSet(prefixIndex.findKeysWithPrefix("as"));
        movieNames = getMovieNames(ordinals, "SimpleMovie", "name");
        Assert.assertTrue(ordinals.size() == 1);
        Assert.assertTrue(movieNames.contains("As Good as It Gets"));

        ordinals = toSet(prefixIndex.findKeysWithPrefix("R"));
        movieNames = getMovieNames(ordinals, "SimpleMovie", "name");
        Assert.assertEquals(ordinals.size(), 2);
        Assert.assertTrue(movieNames.contains("Rocky 2"));
        Assert.assertTrue(movieNames.contains("Rush"));

        ordinals = toSet(prefixIndex.findKeysWithPrefix("rocky 2"));
        movieNames = getMovieNames(ordinals, "SimpleMovie", "name");
        Assert.assertTrue(ordinals.size() == 1);
        Assert.assertTrue(movieNames.contains("Rocky 2"));

        ordinals = toSet(prefixIndex.findKeysWithPrefix("0"));
        movieNames = getMovieNames(ordinals, "SimpleMovie", "name");
        Assert.assertTrue(ordinals.size() == 2);
        Assert.assertTrue(movieNames.contains("007 James Bond"));
        Assert.assertTrue(movieNames.contains("0 dark thirty"));

        prefixIndex.detachFromDeltaUpdates();

    }

    @Test
    public void testListReference() throws Exception {
        MovieListReference movieListReference = new MovieListReference(1, 1999, "The Matrix", Arrays.asList("Keanu Reeves", "Laurence Fishburne", "Carrie-Anne Moss"));
        objectMapper.add(movieListReference);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);
        HollowPrefixIndex prefixIndex = new HollowPrefixIndex(readStateEngine, "MovieListReference", "actors.element.value");
        Set<Integer> ordinals = toSet(prefixIndex.findKeysWithPrefix("kea"));
        Assert.assertTrue(ordinals.size() == 1);
    }

    @Test
    public void testSetReference() throws Exception {
        MovieSetReference movieSetReference = new MovieSetReference(1, 1999, "The Matrix", new HashSet<String>(Arrays.asList("Keanu Reeves", "Laurence Fishburne", "Carrie-Anne Moss")));
        objectMapper.add(movieSetReference);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);
        HollowPrefixIndex prefixIndex = new HollowPrefixIndex(readStateEngine, "MovieSetReference", "actors.element");
        Set<Integer> ordinals = toSet(prefixIndex.findKeysWithPrefix("kea"));
        Assert.assertTrue(ordinals.size() == 1);
    }

    @Test
    public void testMovieActorReference() throws Exception {
        List<Actor> actors = Arrays.asList(new Actor("Keanu Reeves"), new Actor("Laurence Fishburne"), new Actor("Carrie-Anne Moss"));
        MovieActorReference movieSetReference = new MovieActorReference(1, 1999, "The Matrix", actors);
        objectMapper.add(movieSetReference);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);
        HollowPrefixIndex prefixIndex = new HollowPrefixIndex(readStateEngine, "MovieActorReference", "actors.element");
        Set<Integer> ordinals = toSet(prefixIndex.findKeysWithPrefix("kea"));
        Assert.assertTrue(ordinals.size() == 1);
    }

    @Test
    public void testMovieActorReference_duplicatesInList() throws Exception {
        List<Actor> actors = Collections.singletonList(new Actor("Keanu Reeves"));
        int numMovies = 10;
        IntStream.range(0, numMovies).mapToObj(index ->
                new MovieActorReference(index, 1999 + index, "The Matrix " + index, actors))
            .forEach(objectMapper::add);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);
        HollowPrefixIndex prefixIndex = new HollowPrefixIndex(readStateEngine, "MovieActorReference", "actors.element");
        Assert.assertEquals(numMovies, toSet(prefixIndex.findKeysWithPrefix("kea")).size());
    }

    @Test
    public void testMovieMapReference() throws Exception {
        Map<Integer, String> idActorMap = new HashMap<>();
        idActorMap.put(1, "Keanu Reeves");
        idActorMap.put(2, "Laurence Fishburne");
        idActorMap.put(3, "Carrie-Anne Moss");
        MovieMapReference movieMapReference = new MovieMapReference(1, 1999, "The Matrix", idActorMap);
        objectMapper.add(movieMapReference);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);
        HollowPrefixIndex prefixIndex = new HollowPrefixIndex(readStateEngine, "MovieMapReference", "idActorNameMap.value");
        Set<Integer> ordinals = toSet(prefixIndex.findKeysWithPrefix("kea"));
        Assert.assertTrue(ordinals.size() == 1);
    }

    @Test
    public void testMovieActorMapReference() throws Exception {
        Map<Integer, Actor> idActorMap = new HashMap<>();
        idActorMap.put(1, new Actor("Keanu Reeves"));
        idActorMap.put(2, new Actor("Laurence Fishburne"));
        idActorMap.put(3, new Actor("Carrie-Anne Moss"));
        MovieActorMapReference movieActorMapReference = new MovieActorMapReference(1, 1999, "The Matrix", idActorMap);
        objectMapper.add(movieActorMapReference);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);
        HollowPrefixIndex prefixIndex = new HollowPrefixIndex(readStateEngine, "MovieActorMapReference", "idActorNameMap.value");
        Set<Integer> ordinals = toSet(prefixIndex.findKeysWithPrefix("carr"));
        Assert.assertTrue(ordinals.size() == 1);
        ordinals = toSet(prefixIndex.findKeysWithPrefix("aaa"));
        Assert.assertTrue(ordinals.size() == 0);
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
        Set<Integer> ordinals = toSet(index.findKeysWithPrefix("the"));
        Assert.assertTrue(ordinals.size() == 1);

    }

    private void test(List<Movie> movies, String type, String fieldPath) throws Exception {
        for (Movie movie : movies) {
            objectMapper.add(movie);
        }

        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        HollowPrefixIndex prefixIndex = new HollowPrefixIndex(readStateEngine, type, fieldPath);
        Set<Integer> ordinals = toSet(prefixIndex.findKeysWithPrefix("R"));
        Assert.assertEquals(ordinals.size(), 2);


        ordinals = toSet(prefixIndex.findKeysWithPrefix("R"));
        Assert.assertEquals(ordinals.size(), 2);

        ordinals = toSet(prefixIndex.findKeysWithPrefix("th"));
        Assert.assertEquals(ordinals.size(), 1);

        ordinals = toSet(prefixIndex.findKeysWithPrefix("ttt"));
        Assert.assertEquals(ordinals.size(), 0);

        ordinals = toSet(prefixIndex.findKeysWithPrefix("the"));
        Assert.assertEquals(ordinals.size(), 1);

        ordinals = toSet(prefixIndex.findKeysWithPrefix("blOO"));
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

    private Set<Integer> toSet(HollowOrdinalIterator iterator) {
        Set<Integer> ordinals = new HashSet<>();
        int ordinal = iterator.next();
        while (ordinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            ordinals.add(ordinal);
            ordinal = iterator.next();
        }
        return ordinals;
    }

    private Set<String> getMovieNames(Set<Integer> ordinals, String type, String field) {
        Set<String> movieNames = new HashSet<>();
        HollowObjectTypeReadState movieReadState = (HollowObjectTypeReadState) readStateEngine.getTypeState(type);
        HollowObjectTypeReadState nameReadState = (HollowObjectTypeReadState) readStateEngine.getTypeState("String");
        int nameField = movieReadState.getSchema().getPosition(field);
        int valueField = nameReadState.getSchema().getPosition("value");
        for (int ordinal : ordinals) {
            int nameOrdinal = movieReadState.readOrdinal(ordinal, nameField);
            movieNames.add(nameReadState.readString(nameOrdinal, valueField));
        }
        return movieNames;
    }

    /**
     * Custom prefix index for splitting the key by white space.
     */
    private static class HollowTokenizedPrefixIndex extends HollowPrefixIndex {

        public HollowTokenizedPrefixIndex(HollowReadStateEngine readStateEngine, String type, String fieldPath) {
            super(readStateEngine, type, fieldPath);
        }

        @Override
        public String[] getKeys(int ordinal) {
            // split the key by " ";
            String[] keys = super.getKeys(ordinal);
            List<String> tokens = new ArrayList<>();
            for (String key : keys) {
                String[] splits = key.split(" ");
                for (String split : splits)
                    tokens.add(split.toLowerCase());
            }
            return tokens.toArray(new String[tokens.size()]);
        }
    }


    /**
     * Abstract Movie class for testing purposes.
     */
    private static abstract class Movie {
        private int id;
        private int yearRelease;

        public Movie(int id, int year) {
            this.id = id;
            this.yearRelease = year;
        }
    }

    /**
     * Movie class with String reference for movie name.
     */
    private static class SimpleMovie extends Movie {
        private String name;

        public SimpleMovie(int id, String name, int yearRelease) {
            super(id, yearRelease);
            this.name = name;
        }

        public void updateName(String n) {
            this.name = n;
        }
    }

    /**
     * Movie class with HollowInline attribute for movie name.
     */
    private static class MovieInlineName extends Movie {
        @HollowInline
        private String name;

        public MovieInlineName(int id, String name, int yearRelease) {
            super(id, yearRelease);
            this.name = name;
        }
    }

    /**
     * Movie class with name attribute being reference to another class with String reference.
     */
    private static class MovieWithReferenceName extends Movie {
        private Name name;

        public MovieWithReferenceName(int id, String name, int yearRelease) {
            super(id, yearRelease);
            this.name = new Name(name);
        }
    }

    private static class Name {
        String n;

        public Name(String n) {
            this.n = n;
        }
    }

    /**
     * Movie class with name attribute being reference to another class with HollowInline string value
     */
    private static class MovieWithReferenceToInlineName extends Movie {
        private NameInline name;

        public MovieWithReferenceToInlineName(int id, String name, int yearRelease) {
            super(id, yearRelease);
            this.name = new NameInline(name);
        }
    }

    private static class NameInline {
        @HollowInline
        String n;

        public NameInline(String n) {
            this.n = n;
        }
    }

    /**
     * Movie class with list of actors
     */
    private static class MovieListReference extends Movie {
        List<String> actors;
        String name;

        public MovieListReference(int id, int yearRelease, String name, List<String> actors) {
            super(id, yearRelease);
            this.name = name;
            this.actors = actors;
        }
    }

    /**
     * Movie class with set of actors
     */
    private static class MovieSetReference extends Movie {
        Set<String> actors;
        String name;

        public MovieSetReference(int id, int yearRelease, String name, Set<String> actors) {
            super(id, yearRelease);
            this.name = name;
            this.actors = actors;
        }
    }

    /**
     * Movie class with reference to List of Actor
     */
    private static class MovieActorReference extends Movie {
        List<Actor> actors;
        String name;

        public MovieActorReference(int id, int yearRelease, String name, List<Actor> actors) {
            super(id, yearRelease);
            this.actors = actors;
            this.name = name;
        }
    }

    private static class Actor {
        private String name;

        public Actor(String name) {
            this.name = name;
        }
    }

    private static class MovieMapReference extends Movie {
        private Map<Integer, String> idActorNameMap;
        private String name;

        public MovieMapReference(int id, int yearRelease, String name, Map<Integer, String> idActorMap) {
            super(id, yearRelease);
            this.name = name;
            this.idActorNameMap = idActorMap;
        }
    }

    private static class MovieActorMapReference extends Movie {
        private Map<Integer, Actor> idActorNameMap;
        private String name;

        public MovieActorMapReference(int id, int yearRelease, String name, Map<Integer, Actor> idActorMap) {
            super(id, yearRelease);
            this.name = name;
            this.idActorNameMap = idActorMap;
        }
    }
}
