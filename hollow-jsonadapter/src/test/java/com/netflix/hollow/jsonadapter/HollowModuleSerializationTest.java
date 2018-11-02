package com.netflix.hollow.jsonadapter;

import org.junit.Test;

public class HollowModuleSerializationTest extends AbstractHollowModuleSerializationTest {
    @Test
    public void emptyMovie() throws Exception {
        emptyMovieTest();
    }

    @Test
    public void fullMovie() throws Exception {
        fullMovieTest();
    }
}
