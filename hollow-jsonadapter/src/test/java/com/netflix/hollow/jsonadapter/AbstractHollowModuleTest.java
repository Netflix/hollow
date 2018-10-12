package com.netflix.hollow.jsonadapter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hollow.api.codegen.AbstractHollowAPIGeneratorTest;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.InMemoryBlobStore;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;

/**
 * End-to-end tests to ensure generated API remains compatible with {@link HollowModule}.
 */
public abstract class AbstractHollowModuleTest extends AbstractHollowAPIGeneratorTest {
    private InMemoryBlobStore blobStore;
    private ObjectMapper objectMapper;
    private String apiName;
    private String apiClassName;

    @Before
    public void setup() {
        String packageName = "codegen.jackson";
        apiName = getClass().getSimpleName() + "API";
        apiClassName = packageName + "." + apiName;
        blobStore = new InMemoryBlobStore();
        objectMapper = new ObjectMapper()
                .registerModule(new HollowModule(true));
        objectMapper.setVisibility(objectMapper.getVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.NON_PRIVATE)
        );
    }

    public void emptyMovieTest() throws Exception {
        Movie movie = new Movie();

        // Hollow Maps and Sets are unordered, so we compare strings for the empty use case to check we get all null fields, and have the correct property order
        testSerialization(movie, true);
    }

    public void fullMovieTest() throws Exception {
        Actor actor1 = new Actor("Jack Black", new Role(1, "Jan Lewan"), ActorType.CLASSICAL);
        Actor actor2 = new Actor("Jenny Slate", new Role(1, "Marla Lewan"), ActorType.METHOD);
        List<Actor> actors = Arrays.asList(actor1, actor2);

        Map<String, Boolean> map = new HashMap<>();
        map.put("good", true);
        map.put("great", false);

        Map<Integer, Boolean> intMap = new HashMap<>();
        intMap.put(1, true);
        Map<Boolean, Boolean> boolMap = new HashMap<>();
        boolMap.put(true, true);
        Map<Float, Boolean> floatMap = new HashMap<>();
        floatMap.put(1f, true);
        Map<Double, Boolean> doubleMap = new HashMap<>();
        doubleMap.put(1d, true);

        Set<Long> rankings = new HashSet<>(Arrays.asList(1L, 2L, 3L));

        Movie movie = new Movie(Integer.MAX_VALUE, actors, map, intMap, boolMap, floatMap, doubleMap, rankings, 1, 2L, true, 3f, 4d, "Some string");
        movie.__assignedOrdinal = 123456;

        testSerialization(movie, false);
    }

    private void testSerialization(Movie originalMovie, boolean compareStrings) throws Exception {
        generateApi();
        runCycle(originalMovie);

        URL url = new File(clazzFolder).toURI().toURL();
        URL[] urls = {url};
        Thread currentThread = Thread.currentThread();
        ClassLoader originalClassLoader = currentThread.getContextClassLoader();
        URLClassLoader apiLoader = new URLClassLoader(urls, originalClassLoader);
        try {
            currentThread.setContextClassLoader(apiLoader);
            @SuppressWarnings("unchecked")
            Class<? extends HollowAPI> movieApiClass = (Class<? extends HollowAPI>) apiLoader.loadClass(apiClassName);
            HollowConsumer consumer = HollowConsumer
                    .withBlobRetriever(blobStore)
                    .withGeneratedAPIClass(movieApiClass)
                    .build();
            consumer.triggerRefresh();

            HollowAPI api = consumer.getAPI();
            Object movie = movieApiClass
                    .getDeclaredMethod("getMovie", int.class)
                    .invoke(api, 0);

            serialize(movie, originalMovie, compareStrings);
        } finally {
            currentThread.setContextClassLoader(originalClassLoader);
        }
    }

    private void generateApi() throws Exception {
        runGenerator(apiName, "codegen.jackson", Movie.class, false);
    }

    private void runCycle(Movie movie) {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();
        producer.runCycle(writeState -> writeState.add(movie));
    }

    private void serialize(Object movie, Movie originalMovie, boolean compareStrings) throws IOException {
        String value = objectMapper.writeValueAsString(movie);
        Movie deserializedMovie = objectMapper.readValue(value, Movie.class);

        Assert.assertEquals(originalMovie, deserializedMovie);
        Assert.assertEquals(HollowConstants.ORDINAL_NONE, deserializedMovie.__assignedOrdinal);

        if (compareStrings) {
            String expectedValue = objectMapper.writeValueAsString(originalMovie);
            Assert.assertEquals(expectedValue, value);
        }
    }

    @HollowPrimaryKey(fields = {"id"})
    static class Movie {
        Movie() {
        }

        public Movie(int id, List<Actor> actors, Map<String, Boolean> map, Map<Integer, Boolean> intMap, Map<Boolean, Boolean> boolMap, Map<Float, Boolean> floatMap, Map<Double, Boolean> doubleMap,
                     Set<Long> rankings, Integer i, Long l, Boolean b, Float f, Double d, String s) {
            this.id = id;
            this.actors = actors;
            this.map = map;
            this.intMap = intMap;
            this.boolMap = boolMap;
            this.floatMap = floatMap;
            this.doubleMap = doubleMap;
            this.rankings = rankings;
            this.i = i;
            this.l = l;
            this.b = b;
            this.f = f;
            this.d = d;
            this.s = s;
        }

        int id;

        // Collections
        List<Actor> actors;
        Map<String, Boolean> map;
        Map<Integer, Boolean> intMap;
        Map<Boolean, Boolean> boolMap;
        Map<Float, Boolean> floatMap;
        Map<Double, Boolean> doubleMap;
        Set<Long> rankings;

        // Native Types
        Integer i;
        Long l;
        Boolean b;
        Float f;
        Double d;
        String s;

        int __assignedOrdinal = HollowConstants.ORDINAL_NONE;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Movie movie = (Movie) o;
            return id == movie.id &&
                    Objects.equals(actors, movie.actors) &&
                    Objects.equals(map, movie.map) &&
                    Objects.equals(intMap, movie.intMap) &&
                    Objects.equals(boolMap, movie.boolMap) &&
                    Objects.equals(floatMap, movie.floatMap) &&
                    Objects.equals(doubleMap, movie.doubleMap) &&
                    Objects.equals(rankings, movie.rankings) &&
                    Objects.equals(i, movie.i) &&
                    Objects.equals(l, movie.l) &&
                    Objects.equals(b, movie.b) &&
                    Objects.equals(f, movie.f) &&
                    Objects.equals(d, movie.d) &&
                    Objects.equals(s, movie.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, actors, map, intMap, boolMap, floatMap, doubleMap, rankings, i, l, b, f, d, s);
        }
    }

    static class Actor {
        Actor() {
        }

        Actor(String name, Role role, ActorType type) {
            this.name = name;
            this.role = role;
            this.type = type;
        }

        @HollowInline
        String name;

        Role role;

        ActorType type;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Actor actor = (Actor) o;
            return Objects.equals(name, actor.name) &&
                    Objects.equals(role, actor.role) &&
                    type == actor.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, role, type);
        }
    }

    static class Role {
        Role() {
        }

        Role(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        Integer id;

        String name;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Role role = (Role) o;
            return Objects.equals(id, role.id) &&
                    Objects.equals(name, role.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    enum ActorType {
        CLASSICAL, METHOD
    }
}
