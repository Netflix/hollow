/*
 *  Copyright 2016-2024 Netflix, Inc.
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
package com.netflix.hollow.explorer.ui.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.explorer.ui.model.QueryResult.QueryClause;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

public class QueryResultTest {

    private HollowReadStateEngine stateEngine;

    @Before
    public void setUp() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        mapper.add(new Movie(1, "The Matrix", "Action"));
        mapper.add(new Movie(2, "Speed", "Action"));
        mapper.add(new Movie(3, "John Wick", "Action"));
        mapper.add(new Movie(4, "Toy Story", "Animation"));

        stateEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);
    }

    @Test
    public void testAugmentAndRemoveClauseByIndex() {
        QueryResult result = new QueryResult(stateEngine.getCurrentRandomizedTag());

        QueryClause clause1 = new QueryClause("Movie", "genre", "Action");
        QueryClause clause2 = new QueryClause("Movie", "title", "Speed");

        result.augmentQuery(clause1, stateEngine);
        assertEquals(1, result.getQueryClauses().size());
        assertEquals("Movie.genre=\"Action\"", result.getQueryDisplayString());
        assertEquals(3, result.getQueryMatches().get("Movie").cardinality());

        result.augmentQuery(clause2, stateEngine);
        assertEquals(2, result.getQueryClauses().size());
        assertEquals("Movie.genre=\"Action\" AND Movie.title=\"Speed\"", result.getQueryDisplayString());
        assertEquals(1, result.getQueryMatches().get("Movie").cardinality());

        // Remove clause 0 (genre=Action)
        result.removeClause(0, stateEngine);
        assertEquals(1, result.getQueryClauses().size());
        assertEquals(clause2, result.getQueryClauses().get(0));
        assertEquals("Movie.title=\"Speed\"", result.getQueryDisplayString());
        assertEquals(1, result.getQueryMatches().get("Movie").cardinality());

        // Remove remaining clause
        result.removeClause(0, stateEngine);
        assertEquals(0, result.getQueryClauses().size());
        assertEquals("", result.getQueryDisplayString());
        assertTrue(result.getQueryMatches().isEmpty());
    }

    @Test
    public void testRemoveMiddleClause() {
        QueryResult result = new QueryResult(stateEngine.getCurrentRandomizedTag());

        QueryClause clause1 = new QueryClause("Movie", "genre", "Action");
        QueryClause clause2 = new QueryClause("Movie", "id", "2");
        QueryClause clause3 = new QueryClause("Movie", "title", "Speed");

        result.augmentQuery(clause1, stateEngine);
        result.augmentQuery(clause2, stateEngine);
        result.augmentQuery(clause3, stateEngine);

        assertEquals(3, result.getQueryClauses().size());

        // Remove middle clause (id=2)
        result.removeClause(1, stateEngine);
        assertEquals(2, result.getQueryClauses().size());
        assertEquals(clause1, result.getQueryClauses().get(0));
        assertEquals(clause3, result.getQueryClauses().get(1));
        assertEquals("Movie.genre=\"Action\" AND Movie.title=\"Speed\"", result.getQueryDisplayString());
        assertEquals(1, result.getQueryMatches().get("Movie").cardinality());
    }

    @Test
    public void testRemoveClauseByObject() {
        QueryResult result = new QueryResult(stateEngine.getCurrentRandomizedTag());

        QueryClause clause1 = new QueryClause("Movie", "genre", "Action");
        QueryClause clause2 = new QueryClause("Movie", "title", "Speed");

        result.augmentQuery(clause1, stateEngine);
        result.augmentQuery(clause2, stateEngine);

        result.removeClause(clause1, stateEngine);
        assertEquals(1, result.getQueryClauses().size());
        assertEquals(clause2, result.getQueryClauses().get(0));
        assertEquals(1, result.getQueryMatches().get("Movie").cardinality());
    }

    @Test
    public void testRemoveOutOfBoundsClauseDoesNothing() {
        QueryResult result = new QueryResult(stateEngine.getCurrentRandomizedTag());
        QueryClause clause = new QueryClause("Movie", "genre", "Action");
        result.augmentQuery(clause, stateEngine);

        result.removeClause(-1, stateEngine);
        assertEquals(1, result.getQueryClauses().size());

        result.removeClause(5, stateEngine);
        assertEquals(1, result.getQueryClauses().size());
    }

    @Test
    public void testQueryClauseEqualsAndHashCode() {
        QueryClause c1 = new QueryClause("Movie", "title", "Speed");
        QueryClause c2 = new QueryClause("Movie", "title", "Speed");
        QueryClause c3 = new QueryClause("Movie", "title", "Matrix");
        QueryClause c4 = new QueryClause(null, "title", "Speed");

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotEquals(c1, c3);
        assertNotEquals(c1, c4);
    }

    public static class Movie {
        int id;
        String title;
        String genre;

        public Movie(int id, String title, String genre) {
            this.id = id;
            this.title = title;
            this.genre = genre;
        }
    }
}
