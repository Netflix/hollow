package com.netflix.hollow.explorer.ui.pages;

import static com.netflix.hollow.ui.HollowDiffUtil.formatBytes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.explorer.ui.HollowExplorerUI;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;

public class ShowAllTypesPageTest {

    /**
     * Captures one row of the rendered table: type name, hole-footprint cell, heap-footprint cell.
     * The template lays out the cells as: type / records / holes / holeFootprint / heapFootprint / ...
     */
    private static final Pattern ROW = Pattern.compile(
            ">([^<]+)</a></th>\\s*"        // type name (anchor text)
                    + "<td>[^<]*</td>\\s*"  // records
                    + "<td>[^<]*</td>\\s*"  // holes
                    + "<td>([^<]*)</td>\\s*"// hole footprint
                    + "<td>([^<]*)</td>");  // heap footprint

    private HollowWriteStateEngine writeEngine;
    private HollowObjectMapper mapper;
    private HollowReadStateEngine readEngine;
    private ShowAllTypesPage page;

    @Before
    public void setUp() throws Exception {
        writeEngine = new HollowWriteStateEngine();
        mapper = new HollowObjectMapper(writeEngine);

        // Cycle 1: 10 SampleA + 3 SampleB records.
        for (int i = 0; i < 10; i++) {
            mapper.add(new SampleA(i));
        }
        for (int i = 0; i < 3; i++) {
            mapper.add(new SampleB(i * 100L));
        }
        readEngine = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeEngine, readEngine);

        // Cycle 2: keep only 5 SampleA, creating 5 holes. SampleB unchanged.
        for (int i = 0; i < 5; i++) {
            mapper.add(new SampleA(i));
        }
        for (int i = 0; i < 3; i++) {
            mapper.add(new SampleB(i * 100L));
        }
        StateEngineRoundTripper.roundTripDelta(writeEngine, readEngine);

        HollowExplorerUI ui = new HollowExplorerUI("", readEngine);
        page = new ShowAllTypesPage(ui);
    }

    @Test
    public void cacheHitProducesSameFootprintsAcrossRequests() throws Exception {
        Map<String, Footprints> first = parseRowsFromRender();
        Map<String, Footprints> second = parseRowsFromRender();
        assertEquals(first, second);
        assertFootprintsMatchEngine(second);
    }

    @Test
    public void cacheRebuildsAfterStateDelta() throws Exception {
        Map<String, Footprints> before = parseRowsFromRender();
        assertFootprintsMatchEngine(before);

        // Cycle 3: remove all remaining SampleA
        for (int i = 0; i < 3; i++) {
            mapper.add(new SampleB(i * 100L));
        }
        StateEngineRoundTripper.roundTripDelta(writeEngine, readEngine);

        Map<String, Footprints> after = parseRowsFromRender();
        assertFootprintsMatchEngine(after);
    }

    private void assertFootprintsMatchEngine(Map<String, Footprints> rendered) {
        for (HollowTypeReadState ts : readEngine.getTypeStates()) {
            String name = ts.getSchema().getName();
            Footprints fp = rendered.get(name);
            assertNotNull("missing rendered row for " + name, fp);
            assertEquals("heap footprint for " + name,
                    formatBytes(ts.getApproximateHeapFootprintInBytes()), fp.heap);
            assertEquals("hole footprint for " + name,
                    formatBytes(ts.getApproximateHoleCostInBytes()), fp.hole);
        }
    }

    private Map<String, Footprints> parseRowsFromRender() throws Exception {
        StringWriter body = new StringWriter();
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(resp.getWriter()).thenReturn(new PrintWriter(body));

        page.render(req, resp, null);

        Map<String, Footprints> rows = new HashMap<>();
        Matcher m = ROW.matcher(body.toString());
        while (m.find()) {
            rows.put(m.group(1).trim(),
                    new Footprints(m.group(2).trim(), m.group(3).trim()));
        }
        return rows;
    }

    private static final class Footprints {
        final String hole;
        final String heap;

        Footprints(String hole, String heap) {
            this.hole = hole;
            this.heap = heap;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Footprints)) return false;
            Footprints other = (Footprints) o;
            return hole.equals(other.hole) && heap.equals(other.heap);
        }

        @Override
        public int hashCode() {
            return hole.hashCode() ^ heap.hashCode();
        }
    }

    public static class SampleA {
        int num;
        SampleA(int num) { this.num = num; }
    }

    public static class SampleB {
        long value;
        SampleB(long value) { this.value = value; }
    }
}
