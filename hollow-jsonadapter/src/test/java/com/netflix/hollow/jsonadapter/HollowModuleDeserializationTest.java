package com.netflix.hollow.jsonadapter;

import static com.netflix.hollow.jsonadapter.AbstractHollowModuleSerializationTest.createEmptyMovie;
import static com.netflix.hollow.jsonadapter.AbstractHollowModuleSerializationTest.createFullMovie;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hollow.jsonadapter.AbstractHollowModuleSerializationTest.Movie;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowModuleDeserializationTest {
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        objectMapper = new ObjectMapper()
                .registerModule(new HollowModule(true, true));
        objectMapper.setVisibility(objectMapper.getVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.NON_PRIVATE)
        );
    }

    @Test
    public void emptyMovie() throws Exception {
        testInterner(createEmptyMovie());
    }

    @Test
    public void fullMove() throws Exception {
        testInterner(createFullMovie());
    }

    private void testInterner(Movie movie) throws IOException {
        String value = objectMapper.writeValueAsString(movie);
        Movie first = objectMapper.readValue(value, Movie.class);
        first.__assignedOrdinal = 1;
        Movie second = objectMapper.readValue(value, Movie.class);

        Assert.assertSame(first, second);
        Assert.assertEquals(1, second.__assignedOrdinal);
    }
}
