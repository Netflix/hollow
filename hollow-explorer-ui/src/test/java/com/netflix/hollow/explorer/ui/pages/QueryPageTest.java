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
package com.netflix.hollow.explorer.ui.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.explorer.ui.HollowExplorerUI;
import com.netflix.hollow.explorer.ui.model.QueryResult;
import com.netflix.hollow.ui.HollowUISession;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;

public class QueryPageTest {

    private HollowReadStateEngine stateEngine;
    private HollowExplorerUI ui;
    private QueryPage page;
    private HollowUISession session;

    @Before
    public void setUp() throws Exception {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        mapper.add(new Movie(1, "The Matrix", "Action"));
        mapper.add(new Movie(2, "Speed", "Action"));
        mapper.add(new Movie(3, "John Wick", "Action"));
        mapper.add(new Movie(4, "Toy Story", "Animation"));

        stateEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);
        ui = new HollowExplorerUI("", stateEngine);
        page = new QueryPage(ui);
        session = new HollowUISession();
    }

    private String render(HttpServletRequest req) throws Exception {
        StringWriter writer = new StringWriter();
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(resp.getWriter()).thenReturn(new PrintWriter(writer));
        page.render(req, resp, session);
        return writer.toString();
    }

    @Test
    public void testInitialPageShowsSearchOnly() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        String html = render(req);

        assertTrue(html.contains("Search:"));
        assertFalse(html.contains("Current Query:"));
        assertFalse(html.contains("Augment Current Query:"));
    }

    @Test
    public void testAddAndAugmentQueryClauses() throws Exception {
        HttpServletRequest req1 = mock(HttpServletRequest.class);
        when(req1.getParameter("type")).thenReturn("Movie");
        when(req1.getParameter("field")).thenReturn("genre");
        when(req1.getParameter("queryValue")).thenReturn("Action");

        String html1 = render(req1);
        assertTrue(html1.contains("Augment Current Query:"));
        assertTrue(html1.contains("Current Query:"));
        assertTrue(html1.contains("Movie.genre=&quot;Action&quot;"));
        assertTrue(html1.contains("removeClause=0"));
        assertTrue(html1.contains("clear=true"));

        QueryResult queryResult = (QueryResult) session.getAttribute("query-result");
        assertNotNull(queryResult);
        assertEquals(1, queryResult.getQueryClauses().size());

        // Augment with a second clause
        HttpServletRequest req2 = mock(HttpServletRequest.class);
        when(req2.getParameter("type")).thenReturn("Movie");
        when(req2.getParameter("field")).thenReturn("title");
        when(req2.getParameter("queryValue")).thenReturn("Speed");

        String html2 = render(req2);
        assertTrue(html2.contains("Movie.genre=&quot;Action&quot;"));
        assertTrue(html2.contains("removeClause=0"));
        assertTrue(html2.contains("Movie.title=&quot;Speed&quot;"));
        assertTrue(html2.contains("removeClause=1"));

        assertEquals(2, queryResult.getQueryClauses().size());
    }

    @Test
    public void testRemoveClauseFromQuery() throws Exception {
        // Add two clauses first
        HttpServletRequest req1 = mock(HttpServletRequest.class);
        when(req1.getParameter("type")).thenReturn("Movie");
        when(req1.getParameter("field")).thenReturn("genre");
        when(req1.getParameter("queryValue")).thenReturn("Action");
        render(req1);

        HttpServletRequest req2 = mock(HttpServletRequest.class);
        when(req2.getParameter("type")).thenReturn("Movie");
        when(req2.getParameter("field")).thenReturn("title");
        when(req2.getParameter("queryValue")).thenReturn("Speed");
        render(req2);

        // Remove clause 0 (genre=Action)
        HttpServletRequest reqRemove = mock(HttpServletRequest.class);
        when(reqRemove.getParameter("removeClause")).thenReturn("0");

        String html = render(reqRemove);
        assertFalse(html.contains("Movie.genre=&quot;Action&quot;"));
        assertTrue(html.contains("Movie.title=&quot;Speed&quot;"));
        assertTrue(html.contains("removeClause=0")); // now Speed is index 0

        QueryResult queryResult = (QueryResult) session.getAttribute("query-result");
        assertNotNull(queryResult);
        assertEquals(1, queryResult.getQueryClauses().size());
        assertEquals("Speed", queryResult.getQueryClauses().get(0).getValue());
    }

    @Test
    public void testRemoveLastRemainingClauseClearsQuery() throws Exception {
        // Add one clause
        HttpServletRequest req1 = mock(HttpServletRequest.class);
        when(req1.getParameter("type")).thenReturn("Movie");
        when(req1.getParameter("field")).thenReturn("genre");
        when(req1.getParameter("queryValue")).thenReturn("Action");
        render(req1);

        // Remove the only clause
        HttpServletRequest reqRemove = mock(HttpServletRequest.class);
        when(reqRemove.getParameter("removeClause")).thenReturn("0");

        String html = render(reqRemove);
        assertNull(session.getAttribute("query-result"));
        assertTrue(html.contains("Search:"));
        assertFalse(html.contains("Current Query:"));
        assertFalse(html.contains("Augment Current Query:"));
    }

    @Test
    public void testClearQueryParameter() throws Exception {
        // Add one clause
        HttpServletRequest req1 = mock(HttpServletRequest.class);
        when(req1.getParameter("type")).thenReturn("Movie");
        when(req1.getParameter("field")).thenReturn("genre");
        when(req1.getParameter("queryValue")).thenReturn("Action");
        render(req1);

        assertNotNull(session.getAttribute("query-result"));

        // Clear query
        HttpServletRequest reqClear = mock(HttpServletRequest.class);
        when(reqClear.getParameter("clear")).thenReturn("true");

        String html = render(reqClear);
        assertNull(session.getAttribute("query-result"));
        assertTrue(html.contains("Search:"));
        assertFalse(html.contains("Current Query:"));
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
